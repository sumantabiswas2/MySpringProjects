package com.example.dualdb.controller;

import com.example.dualdb.model.mysql.Customer;
import com.example.dualdb.model.neo4j.Neo4jCustomer;
import com.example.dualdb.repository.mysql.CustomerRepository;
import com.example.dualdb.repository.neo4j.Neo4jCustomerRepository;
import com.example.dualdb.service.CustomerSyncService;
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
public class CustomerController {

    private final CustomerRepository mysqlCustomerRepository;
    private final Neo4jCustomerRepository neo4jCustomerRepository;
    private final CustomerSyncService customerSyncService;
    private final CacheManager cacheManager;

    // ========== MySQL Endpoints with Caching ==========

    @GetMapping("/mysql")
    public ResponseEntity<List<Customer>> getAllMySqlCustomers() {
        return ResponseEntity.ok(customerSyncService.getAllCustomers());
    }

    @GetMapping("/mysql/{id}")
    public ResponseEntity<Customer> getMySqlCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerSyncService.getCustomerById(id));
    }

    @PostMapping("/mysql")
    public ResponseEntity<Customer> createMySqlCustomer(@RequestBody Customer customer) {
        return ResponseEntity.ok(customerSyncService.createOrUpdateCustomer(customer));
    }

    @PutMapping("/mysql/{id}")
    public ResponseEntity<Customer> updateMySqlCustomer(@PathVariable Long id, 
                                                         @RequestBody Customer customer) {
        customer.setCustomerId(id);
        return ResponseEntity.ok(customerSyncService.createOrUpdateCustomer(customer));
    }

    @DeleteMapping("/mysql/{id}")
    public ResponseEntity<String> deleteMySqlCustomer(@PathVariable Long id) {
        customerSyncService.deleteCustomer(id);
        return ResponseEntity.ok("Customer " + id + " deleted successfully");
    }

    @GetMapping("/mysql/search")
    public ResponseEntity<List<Customer>> searchMySqlCustomers(@RequestParam String name) {
        // This method doesn't use caching (search results aren't cached)
        return ResponseEntity.ok(mysqlCustomerRepository.searchByName(name));
    }

    // ========== Neo4j Endpoints with Caching ==========

    @GetMapping("/neo4j")
    public ResponseEntity<List<Neo4jCustomer>> getAllNeo4jCustomers() {
        return ResponseEntity.ok(customerSyncService.getAllNeo4jCustomers());
    }

    @GetMapping("/neo4j/{id}")
    public ResponseEntity<Neo4jCustomer> getNeo4jCustomer(@PathVariable Long id) {
        return neo4jCustomerRepository.findByCustomerId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/neo4j/recent")
    public ResponseEntity<List<Neo4jCustomer>> getRecentNeo4jCustomers() {
        return ResponseEntity.ok(customerSyncService.getRecentNeo4jCustomers());
    }

    // ========== Sync Endpoints ==========

    @PostMapping("/sync/{id}")
    public ResponseEntity<Map<String, String>> syncCustomerToNeo4j(@PathVariable Long id) {
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
    public ResponseEntity<Map<String, Object>> checkCustomerInNeo4j(@PathVariable Long id) {
        boolean exists = customerSyncService.isCustomerInNeo4j(id);
        Map<String, Object> response = new HashMap<>();
        response.put("customerId", id);
        response.put("existsInNeo4j", exists);
        return ResponseEntity.ok(response);
    }
}