package com.example.dualdb.config.swaggr;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-Commerce API Documentation")
                        .version("1.0.0")
                        .description("""
                                Complete API documentation for the E-Commerce Application.
                                
                                ## Features:
                                - **GraphQL API**: Flexible query language for frontend
                                - **REST APIs**: Traditional REST endpoints for simpler integrations
                                - **Authentication**: JWT-based authentication
                                - **Data Sources**: MySQL (relational) + Neo4j (graph)
                                - **Caching**: Redis-backed caching layer
                                - **Observability**: Prometheus, Grafana, Tempo, Loki
                                """)
                        .contact(new Contact()
                                .name("Your Name")
                                .email("your.email@example.com")
                                .url("https://github.com/yourusername"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://staging-api.example.com")
                                .description("Staging Server"),
                        new Server()
                                .url("https://api.example.com")
                                .description("Production Server")
                ))
                .tags(List.of(
                        new Tag().name("Customer").description("Customer management operations"),
                        new Tag().name("Product").description("Product management operations"),
                        new Tag().name("Order").description("Order management operations"),
                        new Tag().name("Review").description("Product review operations"),
                        new Tag().name("GraphQL").description("GraphQL API endpoint"),
                        new Tag().name("Cache").description("Cache management operations"),
                        new Tag().name("Analytics").description("Analytics and reporting operations")
                ));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**", "/graphql/**")
                .packagesToScan("com.example.dualdb.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi graphQlApi() {
        return GroupedOpenApi.builder()
                .group("graphql")
                .pathsToMatch("/graphql")
                .build();
    }

    @Bean
    public GroupedOpenApi restApi() {
        return GroupedOpenApi.builder()
                .group("rest")
                .pathsToMatch("/api/**")
                .packagesToScan("com.example.dualdb.controller")
                .build();
    }
}