@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {"shared", "prompt", "prompt :: domain-model", "workflow", "scanner"}
)
package com.promptly.audit;
