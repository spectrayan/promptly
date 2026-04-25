package com.promptly.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

/**
 * Reactive MongoDB configuration.
 * Enables auditing for automatic createdAt/updatedAt population.
 * Removes _class discriminator so seed data is correctly deserialized.
 * Explicitly sets the database name to prevent the default 'test' database.
 */
@Configuration
@EnableReactiveMongoAuditing
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    @Value("${spring.data.mongodb.database:promptly}")
    private String databaseName;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

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
