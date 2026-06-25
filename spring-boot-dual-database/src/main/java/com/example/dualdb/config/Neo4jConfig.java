package com.example.dualdb.config;


import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableNeo4jRepositories(
        basePackages = "com.example.dualdb.repository.neo4j",
        transactionManagerRef = "neo4jTransactionManager"
)
public class Neo4jConfig {

    @Value("${spring.neo4j.uri}")
    private String uri;

    @Value("${spring.neo4j.authentication.username}")
    private String username;

    @Value("${spring.neo4j.authentication.password}")
    private String password;

    @Bean(name = "neo4jDriver")
    public Driver neo4jDriver() {
        return GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }

    @Bean(name = "neo4jTransactionManager")
    public org.springframework.transaction.PlatformTransactionManager neo4jTransactionManager(
            @Qualifier("neo4jDriver") Driver driver) {
        return new org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager(driver);
    }
}