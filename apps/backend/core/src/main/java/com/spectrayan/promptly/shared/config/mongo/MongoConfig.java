package com.spectrayan.promptly.shared.config.mongo;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

/**
 * Reactive MongoDB configuration.
 * Enables auditing for automatic createdAt/updatedAt population.
 * Removes _class discriminator so seed data is correctly deserialized.
 *
 * <p>Connection settings (uri, database, host, port) are managed by
 * Spring Boot auto-configuration via {@code spring.data.mongodb.*} properties
 * and can be overridden with environment variables (e.g. {@code MONGODB_URI}).
 */
@Configuration
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "mongo", matchIfMissing = true)
@EnableReactiveMongoAuditing
public class MongoConfig {

    /**
     * Post-processes the MappingMongoConverter bean to remove _class discriminator.
     * This allows seed data (without _class field) to be deserialized correctly.
     */
    @Bean
    public BeanPostProcessor removeTypeMapperPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if (bean instanceof MappingMongoConverter converter) {
                    converter.setTypeMapper(new DefaultMongoTypeMapper(null));
                }
                return bean;
            }
        };
    }
}
