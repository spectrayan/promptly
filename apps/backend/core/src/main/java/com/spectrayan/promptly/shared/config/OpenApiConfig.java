package com.spectrayan.promptly.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI documentation configuration.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI promptlyOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Promptly API")
                        .description("Enterprise AI Prompt Governance Platform — REST API")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Promptly Team")
                                .url("https://promptly.dev"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://promptly.dev/license"))
                );
    }

}
