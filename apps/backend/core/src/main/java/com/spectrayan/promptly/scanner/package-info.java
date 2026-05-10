@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {"shared", "shared :: config", "infrastructure", "prompt :: domain-model", "prompt"}
)
package com.spectrayan.promptly.scanner;
