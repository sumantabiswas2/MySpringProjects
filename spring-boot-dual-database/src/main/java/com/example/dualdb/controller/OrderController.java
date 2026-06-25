package com.example.dualdb.controller;

import com.example.dualdb.model.mysql.Order;
import com.example.dualdb.model.neo4j.Neo4jProduct;
import com.example.dualdb.repository.mysql.OrderRepository;
import com.example.dualdb.service.OrderAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository mysqlOrderRepository;
    private final OrderAnalysisService orderAnalysisService;

    // ========== MySQL Endpoints ==========

    @GetMapping("/mysql")
    public ResponseEntity<List<Order>> getAllMySqlOrders() {
        return ResponseEntity.ok(mysqlOrderRepository.findAll());
    }

    @GetMapping("/mysql/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(mysqlOrderRepository.findByCustomerCustomerId(customerId));
    }

    @GetMapping("/mysql/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable Order.OrderStatus status) {
        return ResponseEntity.ok(mysqlOrderRepository.findByOrderStatus(status));
    }

    @GetMapping("/mysql/high-spenders")
    public ResponseEntity<List<Object[]>> getHighSpendingCustomers(
            @RequestParam(defaultValue = "500") BigDecimal minSpend) {
        return ResponseEntity.ok(mysqlOrderRepository.findHighSpendingCustomers(minSpend));
    }

    // ========== Neo4j Analysis Endpoints ==========

    @GetMapping("/neo4j/recommendations/{customerId}")
    public ResponseEntity<List<Neo4jProduct>> getProductRecommendations(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(
                orderAnalysisService.getProductRecommendations(customerId)
        );
    }

    @GetMapping("/neo4j/product-buyers/{productId}")
    public ResponseEntity<?> getCustomersWhoBoughtProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(
                orderAnalysisService.getCustomersWhoBoughtProduct(productId)
        );
    }

    @GetMapping("/neo4j/top-customers")
    public ResponseEntity<?> getTopCustomers(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(
                orderAnalysisService.getTopCustomersByOrderCount(limit)
        );
    }
}