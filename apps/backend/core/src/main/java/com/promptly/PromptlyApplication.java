package com.promptly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PromptlyApplication {

    public static void main(String[] args) {
        SpringApplication.run(PromptlyApplication.class, args);
    }

}
