@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {"shared", "infrastructure", "prompt", "prompt :: domain-model", "workflow", "scanner"}
)
package com.promptly.audit;
