package com.example.dualdb.controller.health;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {

    @GetMapping
    @Operation(summary = "Health check", description = "Check if the application is running")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Application is running");
        return response;
    }

    @GetMapping("/info")
    @Operation(summary = "Application info", description = "Get application information")
    public Map<String, String> info() {
        Map<String, String> info = new HashMap<>();
        info.put("name", "Spring Boot Dual Database Application");
        info.put("version", "1.0.0");
        info.put("description", "Application with MySQL, Neo4j, Redis, and Observability");
        return info;
    }
}