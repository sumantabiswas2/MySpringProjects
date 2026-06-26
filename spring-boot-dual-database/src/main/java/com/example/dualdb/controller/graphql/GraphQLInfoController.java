package com.example.dualdb.controller.graphql;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/graphql-info")
@Tag(name = "GraphQL", description = "GraphQL API endpoint information")
public class GraphQLInfoController {

    @GetMapping
    @Operation(summary = "GraphQL API info", description = "Information about the GraphQL API")
    public Map<String, Object> getGraphQLInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("endpoint", "/graphql");
        info.put("ide", "/graphiql");
        info.put("schema", "Available types: Customer, Product, Order, ProductReview, ShoppingCart");
        info.put("sampleQueries", getSampleQueries());
        return info;
    }

    private Map<String, String> getSampleQueries() {
        Map<String, String> queries = new HashMap<>();
        queries.put("getCustomers", 
            "query { customers { customerId firstName lastName email } }");
        queries.put("getCustomerById", 
            "query { customerById(customerId: 1) { firstName lastName email orders { orderId finalAmount } } }");
        queries.put("getProductRecommendations", 
            "query { productRecommendations(customerId: 1, limit: 5) { productId productName unitPrice } }");
        return queries;
    }
}
