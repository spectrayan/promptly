package com.spectrayan.promptly.shared.config.postgres;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Manually registers a JDBC {@link DataSource} bean when running in SQL mode.
 * <p>
 * Spring Boot 4's {@code DataSourceAutoConfiguration} is mutually exclusive with
 * R2DBC auto-configuration — when the R2DBC {@code ConnectionFactory} is present,
 * the JDBC DataSource auto-config is suppressed. However, Spring Modulith's
 * {@code JdbcEventPublicationAutoConfiguration} requires a JDBC DataSource for
 * persisting domain event publications. This configuration fills that gap.
 * <p>
 * This class is placed under {@code config/postgres/} so that it is excluded from
 * compilation under the default MongoDB profile (which lacks HikariCP on the classpath).
 */
@Configuration
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "sql")
public class JdbcDataSourceConfig {

    @Bean
    public DataSource dataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        return new HikariDataSource(config);
    }
}
