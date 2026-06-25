package com.example.dualdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class SpringBootDualDatabaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootDualDatabaseApplication.class, args);
		log.info("=========================================");
        log.info("Spring Boot Dual Database Application");
        log.info("MySQL: http://localhost:8080/api/customers/mysql");
        log.info("Neo4j: http://localhost:8080/api/customers/neo4j");
        log.info("=========================================");
	}

}
