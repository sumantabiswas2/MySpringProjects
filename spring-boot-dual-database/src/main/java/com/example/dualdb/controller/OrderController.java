package com.example.dualdb.controller;

import com.example.dualdb.model.mysql.Order;
import com.example.dualdb.model.neo4j.Neo4jProduct;
import com.example.dualdb.repository.mysql.OrderRepository;
import com.example.dualdb.service.OrderAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Order management and Neo4j-based order analytics")
public class OrderController {

    private final OrderRepository mysqlOrderRepository;
    private final OrderAnalysisService orderAnalysisService;

    // ========== MySQL Endpoints ==========

    @GetMapping("/mysql")
    @Operation(summary = "Get all MySQL orders", description = "Retrieves all orders from the MySQL database")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
            content = @Content(schema = @Schema(implementation = Order.class)))
    public ResponseEntity<List<Order>> getAllMySqlOrders() {
        return ResponseEntity.ok(mysqlOrderRepository.findAll());
    }

    @GetMapping("/mysql/customer/{customerId}")
    @Operation(summary = "Get orders by customer", description = "Retrieves all orders for a specific customer from MySQL")
    @ApiResponse(responseCode = "200", description = "Customer orders retrieved successfully",
            content = @Content(schema = @Schema(implementation = Order.class)))
    public ResponseEntity<List<Order>> getOrdersByCustomer(
            @Parameter(description = "Customer ID", required = true) @PathVariable Long customerId) {
        return ResponseEntity.ok(mysqlOrderRepository.findByCustomerCustomerId(customerId));
    }

    @GetMapping("/mysql/status/{status}")
    @Operation(summary = "Get orders by status", description = "Retrieves all orders with the given order status")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
            content = @Content(schema = @Schema(implementation = Order.class)))
    public ResponseEntity<List<Order>> getOrdersByStatus(
            @Parameter(description = "Order status (Pending, Processing, Shipped, Delivered, Cancelled)",
                    required = true) @PathVariable Order.OrderStatus status) {
        return ResponseEntity.ok(mysqlOrderRepository.findByOrderStatus(status));
    }

    @GetMapping("/mysql/high-spenders")
    @Operation(summary = "Get high-spending customers", description = "Retrieves customers whose total spend exceeds the minimum threshold")
    @ApiResponse(responseCode = "200", description = "High-spending customers retrieved successfully")
    public ResponseEntity<List<Object[]>> getHighSpendingCustomers(
            @Parameter(description = "Minimum total spend threshold", example = "500")
            @RequestParam(defaultValue = "500") BigDecimal minSpend) {
        return ResponseEntity.ok(mysqlOrderRepository.findHighSpendingCustomers(minSpend));
    }

    // ========== Neo4j Analysis Endpoints ==========

    @GetMapping("/neo4j/recommendations/{customerId}")
    @Operation(summary = "Get product recommendations", description = "Returns product recommendations for a customer based on Neo4j graph analysis", tags = {"Analytics"})
    @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully",
            content = @Content(schema = @Schema(implementation = Neo4jProduct.class)))
    public ResponseEntity<List<Neo4jProduct>> getProductRecommendations(
            @Parameter(description = "Customer ID", required = true) @PathVariable Long customerId) {
        return ResponseEntity.ok(
                orderAnalysisService.getProductRecommendations(customerId)
        );
    }

    @GetMapping("/neo4j/product-buyers/{productId}")
    @Operation(summary = "Get customers who bought a product", description = "Returns customers who purchased a specific product based on Neo4j graph relationships", tags = {"Analytics"})
    @ApiResponse(responseCode = "200", description = "Customers retrieved successfully")
    public ResponseEntity<?> getCustomersWhoBoughtProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Long productId) {
        return ResponseEntity.ok(
                orderAnalysisService.getCustomersWhoBoughtProduct(productId)
        );
    }

    @GetMapping("/neo4j/top-customers")
    @Operation(summary = "Get top customers by order count", description = "Returns the top customers ranked by number of orders from Neo4j graph analysis", tags = {"Analytics"})
    @ApiResponse(responseCode = "200", description = "Top customers retrieved successfully")
    public ResponseEntity<?> getTopCustomers(
            @Parameter(description = "Maximum number of customers to return", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(
                orderAnalysisService.getTopCustomersByOrderCount(limit)
        );
    }
}
