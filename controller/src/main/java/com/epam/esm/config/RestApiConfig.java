package com.epam.esm.config;

import com.epam.esm.util.CaseInsensitiveEnumConverter;
import com.epam.esm.util.SortBy;
import com.epam.esm.util.SortDir;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@ComponentScan("com.epam.esm")
@EnableWebMvc
@PropertySource("classpath:datasource.properties")
public class RestApiConfig implements WebMvcConfigurer {
    @Autowired
    private Environment env;
    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(env.getProperty("spring.datasource.hikari.driver-class-name"));
        hikariConfig.setJdbcUrl(env.getProperty("spring.datasource.hikari.jdbc-url"));
        hikariConfig.setUsername(env.getProperty("spring.datasource.hikari.username"));
        hikariConfig.setPassword(env.getProperty("spring.datasource.hikari.password"));

        hikariConfig.setMaximumPoolSize(Integer.parseInt(env.getProperty("spring.datasource.hikari.maximum-pool-size")));
        hikariConfig.setConnectionTestQuery(env.getProperty("spring.datasource.hikari.connection-test-query"));

        hikariConfig.setTransactionIsolation(env.getProperty("spring.datasource.hikari.transaction-isolation"));
        hikariConfig.setAutoCommit(Boolean.parseBoolean(env.getProperty("spring.datasource.hikari.auto-commit")));

        hikariConfig.addDataSourceProperty("dataSource.cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(hikariConfig);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        List<Class<? extends Enum>> enums = List.of(SortBy.class, SortDir.class);
        enums.forEach(enumClass -> registry.addConverter(String.class, enumClass,
                new CaseInsensitiveEnumConverter<>(enumClass)));
    }
}
