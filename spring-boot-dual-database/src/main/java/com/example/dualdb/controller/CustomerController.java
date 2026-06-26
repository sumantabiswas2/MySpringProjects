package com.example.dualdb.controller;

import com.example.dualdb.model.mysql.Customer;
import com.example.dualdb.model.neo4j.Neo4jCustomer;
import com.example.dualdb.repository.mysql.CustomerRepository;
import com.example.dualdb.repository.neo4j.Neo4jCustomerRepository;
import com.example.dualdb.service.CustomerSyncService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Customer management operations across MySQL and Neo4j")
public class CustomerController {

    private final CustomerRepository mysqlCustomerRepository;
    private final Neo4jCustomerRepository neo4jCustomerRepository;
    private final CustomerSyncService customerSyncService;
    private final CacheManager cacheManager;

    // ========== MySQL Endpoints with Caching ==========

    @GetMapping("/mysql")
    @Operation(summary = "Get all MySQL customers", description = "Retrieves all customers from MySQL with caching enabled")
    @ApiResponse(responseCode = "200", description = "Customers retrieved successfully",
            content = @Content(schema = @Schema(implementation = Customer.class)))
    public ResponseEntity<List<Customer>> getAllMySqlCustomers() {
        return ResponseEntity.ok(customerSyncService.getAllCustomers());
    }

    @GetMapping("/mysql/{id}")
    @Operation(summary = "Get MySQL customer by ID", description = "Retrieves a single customer from MySQL by customer ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found",
                    content = @Content(schema = @Schema(implementation = Customer.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Customer> getMySqlCustomer(
            @Parameter(description = "Customer ID", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(customerSyncService.getCustomerById(id));
    }

    @PostMapping("/mysql")
    @Operation(summary = "Create MySQL customer", description = "Creates a new customer in MySQL and syncs to cache")
    @ApiResponse(responseCode = "200", description = "Customer created successfully",
            content = @Content(schema = @Schema(implementation = Customer.class)))
    public ResponseEntity<Customer> createMySqlCustomer(@RequestBody Customer customer) {
        return ResponseEntity.ok(customerSyncService.createOrUpdateCustomer(customer));
    }

    @PutMapping("/mysql/{id}")
    @Operation(summary = "Update MySQL customer", description = "Updates an existing customer in MySQL by ID")
    @ApiResponse(responseCode = "200", description = "Customer updated successfully",
            content = @Content(schema = @Schema(implementation = Customer.class)))
    public ResponseEntity<Customer> updateMySqlCustomer(
            @Parameter(description = "Customer ID", required = true) @PathVariable Long id,
            @RequestBody Customer customer) {
        customer.setCustomerId(id);
        return ResponseEntity.ok(customerSyncService.createOrUpdateCustomer(customer));
    }

    @DeleteMapping("/mysql/{id}")
    @Operation(summary = "Delete MySQL customer", description = "Deletes a customer from MySQL and evicts related cache entries")
    @ApiResponse(responseCode = "200", description = "Customer deleted successfully")
    public ResponseEntity<String> deleteMySqlCustomer(
            @Parameter(description = "Customer ID", required = true) @PathVariable Long id) {
        customerSyncService.deleteCustomer(id);
        return ResponseEntity.ok("Customer " + id + " deleted successfully");
    }

    @GetMapping("/mysql/search")
    @Operation(summary = "Search MySQL customers by name", description = "Retrieves customers whose name matches the search term (not cached)")
    @ApiResponse(responseCode = "200", description = "Search results returned",
            content = @Content(schema = @Schema(implementation = Customer.class)))
    public ResponseEntity<List<Customer>> searchMySqlCustomers(
            @Parameter(description = "Name search term", required = true) @RequestParam String name) {
        return ResponseEntity.ok(mysqlCustomerRepository.searchByName(name));
    }

    // ========== Neo4j Endpoints with Caching ==========

    @GetMapping("/neo4j")
    @Operation(summary = "Get all Neo4j customers", description = "Retrieves all customer nodes from the Neo4j graph database")
    @ApiResponse(responseCode = "200", description = "Neo4j customers retrieved successfully",
            content = @Content(schema = @Schema(implementation = Neo4jCustomer.class)))
    public ResponseEntity<List<Neo4jCustomer>> getAllNeo4jCustomers() {
        return ResponseEntity.ok(customerSyncService.getAllNeo4jCustomers());
    }

    @GetMapping("/neo4j/{id}")
    @Operation(summary = "Get Neo4j customer by ID", description = "Retrieves a single customer node from Neo4j by customer ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found",
                    content = @Content(schema = @Schema(implementation = Neo4jCustomer.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found in Neo4j")
    })
    public ResponseEntity<Neo4jCustomer> getNeo4jCustomer(
            @Parameter(description = "Customer ID", required = true) @PathVariable Long id) {
        return neo4jCustomerRepository.findByCustomerId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/neo4j/recent")
    @Operation(summary = "Get recently registered Neo4j customers", description = "Retrieves customers registered within the last 30 days from Neo4j")
    @ApiResponse(responseCode = "200", description = "Recent customers retrieved successfully",
            content = @Content(schema = @Schema(implementation = Neo4jCustomer.class)))
    public ResponseEntity<List<Neo4jCustomer>> getRecentNeo4jCustomers() {
        return ResponseEntity.ok(customerSyncService.getRecentNeo4jCustomers());
    }

    // ========== Sync Endpoints ==========

    @PostMapping("/sync/{id}")
    @Operation(summary = "Sync customer to Neo4j", description = "Syncs a single MySQL customer to the Neo4j graph database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer synced successfully"),
            @ApiResponse(responseCode = "500", description = "Sync operation failed")
    })
    public ResponseEntity<Map<String, String>> syncCustomerToNeo4j(
            @Parameter(description = "Customer ID to sync", required = true) @PathVariable Long id) {
        try {
            customerSyncService.syncCustomerToNeo4j(id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Customer " + id + " synced to Neo4j successfully");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to sync customer: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/sync/all")
    @Operation(summary = "Sync all customers to Neo4j", description = "Syncs all MySQL customers to the Neo4j graph database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All customers synced successfully"),
            @ApiResponse(responseCode = "500", description = "Bulk sync operation failed")
    })
    public ResponseEntity<Map<String, String>> syncAllCustomersToNeo4j() {
        try {
            customerSyncService.syncAllCustomersToNeo4j();
            Map<String, String> response = new HashMap<>();
            response.put("message", "All customers synced to Neo4j successfully");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to sync customers: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/sync/check/{id}")
    @Operation(summary = "Check customer in Neo4j", description = "Checks whether a customer exists in the Neo4j graph database")
    @ApiResponse(responseCode = "200", description = "Existence check completed")
    public ResponseEntity<Map<String, Object>> checkCustomerInNeo4j(
            @Parameter(description = "Customer ID to check", required = true) @PathVariable Long id) {
        boolean exists = customerSyncService.isCustomerInNeo4j(id);
        Map<String, Object> response = new HashMap<>();
        response.put("customerId", id);
        response.put("existsInNeo4j", exists);
        return ResponseEntity.ok(response);
    }
}
