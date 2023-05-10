package com.epam.esm.config;

import com.epam.esm.util.GiftCertificateSortMap;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@PropertySource("classpath:datasource.properties")
@PropertySource("classpath:sort.properties")
public class RestApiConfig {
    @Autowired
    private Environment env;

    @Value("#{${gift.certificate.sort.config}}")
    private Map<String, String> map;

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

    @Bean
    public GiftCertificateSortMap sortMap() {
        return new GiftCertificateSortMap(map);
    }

/*    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder().serializers(LOCAL_DATETIME_SERIALIZER);
//                .serializationInclusion(JsonInclude.Include.NON_NULL);
    }*/
}
