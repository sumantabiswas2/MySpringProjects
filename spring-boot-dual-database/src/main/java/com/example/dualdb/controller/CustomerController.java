package com.example.dualdb.controller;

import com.example.dualdb.model.mysql.Customer;
import com.example.dualdb.model.neo4j.Neo4jCustomer;
import com.example.dualdb.repository.mysql.CustomerRepository;
import com.example.dualdb.repository.neo4j.Neo4jCustomerRepository;
import com.example.dualdb.service.CustomerSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository mysqlCustomerRepository;
    private final Neo4jCustomerRepository neo4jCustomerRepository;
    private final CustomerSyncService customerSyncService;

    // ========== MySQL Endpoints ==========

    @GetMapping("/mysql")
    public ResponseEntity<List<Customer>> getAllMySqlCustomers() {
        return ResponseEntity.ok(mysqlCustomerRepository.findAll());
    }

    @GetMapping("/mysql/{id}")
    public ResponseEntity<Customer> getMySqlCustomer(@PathVariable Long id) {
        return mysqlCustomerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/mysql")
    public ResponseEntity<Customer> createMySqlCustomer(@RequestBody Customer customer) {
        Customer saved = mysqlCustomerRepository.save(customer);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/mysql/search")
    public ResponseEntity<List<Customer>> searchMySqlCustomers(@RequestParam String name) {
        return ResponseEntity.ok(mysqlCustomerRepository.searchByName(name));
    }

    // ========== Neo4j Endpoints ==========

    @GetMapping("/neo4j")
    public ResponseEntity<List<Neo4jCustomer>> getAllNeo4jCustomers() {
        return ResponseEntity.ok(neo4jCustomerRepository.findAll());
    }

    @GetMapping("/neo4j/{id}")
    public ResponseEntity<Neo4jCustomer> getNeo4jCustomer(@PathVariable Long id) {
        return neo4jCustomerRepository.findByCustomerId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/neo4j/recent")
    public ResponseEntity<List<Neo4jCustomer>> getRecentNeo4jCustomers() {
        return ResponseEntity.ok(neo4jCustomerRepository.findRecentCustomers());
    }

    // ========== Sync Endpoints ==========

    @PostMapping("/sync/{id}")
    public ResponseEntity<String> syncCustomerToNeo4j(@PathVariable Long id) {
        try {
            customerSyncService.syncCustomerToNeo4j(id);
            return ResponseEntity.ok("Customer " + id + " synced to Neo4j successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to sync customer: " + e.getMessage());
        }
    }

    @PostMapping("/sync/all")
    public ResponseEntity<String> syncAllCustomersToNeo4j() {
        try {
            customerSyncService.syncAllCustomersToNeo4j();
            return ResponseEntity.ok("All customers synced to Neo4j successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to sync customers: " + e.getMessage());
        }
    }

    @GetMapping("/sync/check/{id}")
    public ResponseEntity<Boolean> checkCustomerInNeo4j(@PathVariable Long id) {
        return ResponseEntity.ok(customerSyncService.isCustomerInNeo4j(id));
    }
}