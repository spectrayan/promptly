package com.spectrayan.promptly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class PromptlyApplication {

    public static void main(String[] args) {
        SpringApplication.run(PromptlyApplication.class, args);
    }

}

