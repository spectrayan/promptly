@org.springframework.modulith.ApplicationModule(
        type = org.springframework.modulith.ApplicationModule.Type.OPEN,
        allowedDependencies = {"shared", "infrastructure", "auth::api", "auth::model"}
)
package com.spectrayan.promptly.project;
