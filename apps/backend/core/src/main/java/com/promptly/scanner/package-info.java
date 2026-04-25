@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {"shared", "infrastructure", "prompt :: domain-model", "prompt"}
)
package com.promptly.scanner;
