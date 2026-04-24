<#
.SYNOPSIS
    Post-processes bundled OpenAPI YAML to remove duplicate inline schemas
    and replace inlined enum properties with $ref pointers.

.PARAMETER BundledSpecPath
    Path to the bundled openapi.yaml file to post-process.
#>
param(
    [Parameter(Mandatory=$true)]
    [string]$BundledSpecPath
)

if (-not (Test-Path $BundledSpecPath)) {
    Write-Host "Bundled spec not found at: $BundledSpecPath - skipping deduplication"
    exit 0
}

$content = Get-Content $BundledSpecPath -Raw
$lines = $content -split "`n"
$schemasStartLine = -1
$schemaNames = @()

# Find all schema names under components/schemas
for ($i = 0; $i -lt $lines.Count; $i++) {
    if ($lines[$i] -match '^\s{2}schemas:') {
        $schemasStartLine = $i
    }
    if ($schemasStartLine -ge 0 -and $i -gt $schemasStartLine) {
        if ($lines[$i] -match '^\s{4}(\w+):\s*$') {
            $schemaNames += $Matches[1]
        }
        # Stop at next top-level key
        if ($i -gt ($schemasStartLine + 1) -and $lines[$i] -match '^\s{2}[A-Za-z]' -and $lines[$i] -notmatch '^\s{4}') {
            break
        }
    }
}

# Separate canonical (PascalCase) from inline (snake_case) schemas
$canonicalSchemas = $schemaNames | Where-Object { $_ -cmatch '^[A-Z]' }
$inlineSchemas = $schemaNames | Where-Object { $_ -cmatch '^[a-z]' }

Write-Host "Found $($canonicalSchemas.Count) canonical schemas, $($inlineSchemas.Count) inline schemas"

# ── Phase 1: Deduplicate inline object schemas ──

function Get-SchemaProperties {
    param([string]$SchemaName, [string[]]$Lines)
    $props = [System.Collections.ArrayList]::new()
    $found = $false
    $inProperties = $false

    for ($i = 0; $i -lt $Lines.Count; $i++) {
        if (-not $found -and $Lines[$i] -match "^\s{4}${SchemaName}:\s*$") {
            $found = $true
            continue
        }
        if ($found) {
            if ($Lines[$i] -match '^\s{6}properties:\s*$') {
                $inProperties = $true
                continue
            }
            if ($inProperties) {
                if ($Lines[$i] -match '^\s{8}(\w+):\s*$') {
                    [void]$props.Add($Matches[1])
                }
                if ($Lines[$i] -match '^\s{6}\w' -and $Lines[$i] -notmatch '^\s{8}') {
                    break
                }
            }
            if ($Lines[$i] -match '^\s{4}[A-Za-z]' -and $Lines[$i] -notmatch '^\s{6}' -and $Lines[$i] -notmatch "^\s{4}${SchemaName}:") {
                break
            }
        }
    }

    $sorted = $props | Sort-Object
    return ($sorted -join ',')
}

$canonicalProps = @{}
foreach ($schema in $canonicalSchemas) {
    $sig = Get-SchemaProperties -SchemaName $schema -Lines $lines
    if ($sig -ne '') {
        $canonicalProps[$sig] = $schema
    }
}

$mappings = @{}
foreach ($inline in $inlineSchemas) {
    $sig = Get-SchemaProperties -SchemaName $inline -Lines $lines
    if ($sig -ne '' -and $canonicalProps.ContainsKey($sig)) {
        $canonical = $canonicalProps[$sig]
        $mappings[$inline] = $canonical
        Write-Host "  Mapping: $inline -> $canonical"
    }
}

if ($mappings.Count -gt 0) {
    foreach ($key in $mappings.Keys) {
        $val = $mappings[$key]
        $search = [regex]::Escape("""#/components/schemas/$key""")
        $replace = """#/components/schemas/$val"""
        $content = [regex]::Replace($content, $search, $replace)
    }

    foreach ($key in $mappings.Keys) {
        $escapedKey = [regex]::Escape($key)
        $removePattern = "(?m)^    ${escapedKey}:\s*\r?\n(?:      .*\r?\n)*"
        $content = [regex]::Replace($content, $removePattern, '')
    }

    Write-Host "Phase 1: removed $($mappings.Count) inline object schemas"
}

# ── Phase 2: Replace inlined enum properties with $ref pointers ──
# Build a map of canonical enum schemas: enum values -> schema name
$enumSchemas = @{}
$reloadedLines = $content -split "`n"

foreach ($schema in $canonicalSchemas) {
    $found = $false
    $isEnum = $false
    $enumValues = @()

    for ($i = 0; $i -lt $reloadedLines.Count; $i++) {
        if (-not $found -and $reloadedLines[$i] -match "^\s{4}${schema}:\s*$") {
            $found = $true
            continue
        }
        if ($found) {
            if ($reloadedLines[$i] -match '^\s{6}enum:\s*$') {
                $isEnum = $true
                continue
            }
            if ($isEnum -and $reloadedLines[$i] -match '^\s{6}-\s+(\w+)') {
                $enumValues += $Matches[1]
            }
            if ($isEnum -and $reloadedLines[$i] -notmatch '^\s{6}-' -and $reloadedLines[$i].Trim() -ne '') {
                break
            }
            if ($reloadedLines[$i] -match '^\s{4}[A-Za-z]' -and $reloadedLines[$i] -notmatch '^\s{6}') {
                break
            }
        }
    }

    if ($isEnum -and $enumValues.Count -gt 0) {
        $key = ($enumValues | Sort-Object) -join ','
        $enumSchemas[$key] = $schema
    }
}

Write-Host "Found $($enumSchemas.Count) canonical enum schemas"

# Now scan ALL property definitions in the spec for inlined enums
# Pattern: a property that has enum: with matching values should be replaced with $ref
$outputLines = [System.Collections.ArrayList]::new()
$i = 0
$replacedEnums = 0

while ($i -lt $reloadedLines.Count) {
    $line = $reloadedLines[$i]

    # Detect a property with inlined enum (e.g., "        contentFormat:")
    # followed by lines like "          description:", "          enum:", "          - VALUE"
    if ($line -match '^(\s{8})(\w+):\s*$') {
        $propIndent = $Matches[1]
        $propName = $Matches[2]
        $childIndent = $propIndent + "  "

        # Look ahead to see if this property has an inline enum
        $j = $i + 1
        $propEnumValues = @()
        $propHasEnum = $false
        $propEndLine = $j

        while ($j -lt $reloadedLines.Count) {
            $nextLine = $reloadedLines[$j]
            # Lines belonging to this property (deeper indent or blank)
            if ($nextLine -match "^${childIndent}" -or $nextLine.Trim() -eq '') {
                if ($nextLine -match "^${childIndent}enum:\s*$") {
                    $propHasEnum = $true
                }
                if ($propHasEnum -and $nextLine -match "^${childIndent}-\s+(\w+)") {
                    $propEnumValues += $Matches[1]
                }
                $propEndLine = $j
                $j++
            } else {
                break
            }
        }

        if ($propHasEnum -and $propEnumValues.Count -gt 0) {
            $enumKey = ($propEnumValues | Sort-Object) -join ','
            if ($enumSchemas.ContainsKey($enumKey)) {
                $canonicalEnum = $enumSchemas[$enumKey]
                # Replace the entire inlined enum property with a $ref
                [void]$outputLines.Add("${propIndent}${propName}:")
                [void]$outputLines.Add("${childIndent}`$ref: ""#/components/schemas/${canonicalEnum}""")
                $replacedEnums++
                $i = $propEndLine + 1
                continue
            }
        }
    }

    [void]$outputLines.Add($line)
    $i++
}

if ($replacedEnums -gt 0) {
    $content = $outputLines -join "`n"
    Write-Host "Phase 2: replaced $replacedEnums inlined enum properties with `$ref pointers"
}

Set-Content -Path $BundledSpecPath -Value $content -NoNewline
$total = $mappings.Count + $replacedEnums
Write-Host "Deduplication complete: $($mappings.Count) inline schemas removed, $replacedEnums enum refs restored"
