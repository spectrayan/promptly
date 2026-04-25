@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {"shared", "infrastructure", "shared::config", "prompt", "prompt :: domain-model", "workflow", "scanner"}
)
package com.promptly.export;
