package com.example.dualdb.controller.graphql;

import com.example.dualdb.model.mysql.Customer;
import com.example.dualdb.model.mysql.Order;
import com.example.dualdb.model.mysql.OrderItem;
import com.example.dualdb.model.mysql.Product;
import com.example.dualdb.repository.mysql.CustomerRepository;
import com.example.dualdb.repository.mysql.OrderRepository;
import com.example.dualdb.repository.mysql.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class OrderGraphQLController {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    // ============================================
    // QUERY MAPPINGS
    // ============================================

    @QueryMapping
    public List<Order> orders() {
        log.info("GraphQL Query: orders");
        return orderRepository.findAll();
    }

    @QueryMapping
    public Order orderById(@Argument Long orderId) {
        log.info("GraphQL Query: orderById - {}", orderId);
        return orderRepository.findById(orderId).orElse(null);
    }

    @QueryMapping
    public List<Order> ordersByCustomer(@Argument Long customerId) {
        log.info("GraphQL Query: ordersByCustomer - {}", customerId);
        return orderRepository.findByCustomerCustomerId(customerId);
    }

    @QueryMapping
    public List<Order> ordersByStatus(@Argument Order.OrderStatus orderStatus) {
        log.info("GraphQL Query: ordersByStatus - {}", orderStatus);
        return orderRepository.findByOrderStatus(orderStatus);
    }

    // ============================================
    // MUTATION MAPPINGS
    // ============================================

    @MutationMapping
    public Order createOrder(@Argument OrderInput input) {
        log.info("GraphQL Mutation: createOrder - customerId: {}", input.getCustomerId());
        
        Customer customer = customerRepository.findById(input.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found: " + input.getCustomerId()));
        
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(Order.OrderStatus.Pending);
        order.setPaymentStatus(Order.PaymentStatus.Pending);
        order.setNotes(input.getNotes());
        
        // Calculate totals
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemInput itemInput : input.getItems()) {
            Product product = productRepository.findById(itemInput.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemInput.getProductId()));
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemInput.getQuantity());
            orderItem.setUnitPriceAtOrder(product.getUnitPrice());
            orderItem.setDiscountPercent(BigDecimal.ZERO);
            orderItem.setTaxPercent(new BigDecimal("8.0"));
            
            BigDecimal itemTotal = product.getUnitPrice()
                    .multiply(new BigDecimal(itemInput.getQuantity()));
            orderItem.setTotalPrice(itemTotal);
            
            totalAmount = totalAmount.add(itemTotal);
        }
        
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setTaxAmount(totalAmount.multiply(new BigDecimal("0.08")));
        order.setShippingCharge(new BigDecimal("5.99"));
        order.setFinalAmount(totalAmount.add(order.getTaxAmount()).add(order.getShippingCharge()));
        
        return orderRepository.save(order);
    }

    @MutationMapping
    public Order updateOrderStatus(@Argument Long orderId, @Argument Order.OrderStatus status) {
        log.info("GraphQL Mutation: updateOrderStatus - orderId: {}, status: {}", orderId, status);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }

    // ============================================
    // SCHEMA MAPPINGS
    // ============================================

    @SchemaMapping(typeName = "Order", field = "orderDate")
    public String getOrderDateFormatted(Order order) {
        return order.getOrderDate() != null 
                ? order.getOrderDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) 
                : null;
    }

    // ============================================
    // INPUT CLASSES
    // ============================================

    public static class OrderInput {
        private Long customerId;
        private Long shippingAddressId;
        private Long billingAddressId;
        private List<OrderItemInput> items;
        private String notes;

        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public Long getShippingAddressId() { return shippingAddressId; }
        public void setShippingAddressId(Long shippingAddressId) { this.shippingAddressId = shippingAddressId; }
        public Long getBillingAddressId() { return billingAddressId; }
        public void setBillingAddressId(Long billingAddressId) { this.billingAddressId = billingAddressId; }
        public List<OrderItemInput> getItems() { return items; }
        public void setItems(List<OrderItemInput> items) { this.items = items; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class OrderItemInput {
        private Long productId;
        private Integer quantity;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}