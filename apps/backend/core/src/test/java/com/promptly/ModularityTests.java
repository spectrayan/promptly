package com.promptly;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Verifies Spring Modulith module structure.
 * <p>
 * This test FAILS the build if:
 * - A module accesses another module's internal packages
 * - Circular dependencies exist between modules
 * - A module depends on a module not listed in its allowedDependencies
 */
class ModularityTests {

    private final ApplicationModules modules = ApplicationModules.of(PromptlyApplication.class);

    @Test
    void verifyModularStructure() {
        modules.verify();
    }

    @Test
    void generateModuleDocumentation() {
        new Documenter(modules).writeDocumentation();
    }

}
