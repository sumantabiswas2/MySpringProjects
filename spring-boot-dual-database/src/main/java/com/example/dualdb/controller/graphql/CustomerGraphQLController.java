package com.example.dualdb.controller.graphql;

import com.example.dualdb.model.mysql.Customer;
import com.example.dualdb.model.mysql.Order;
import com.example.dualdb.model.neo4j.Neo4jCustomer;
import com.example.dualdb.model.neo4j.Neo4jProduct;
import com.example.dualdb.repository.mysql.CustomerRepository;
import com.example.dualdb.repository.neo4j.Neo4jCustomerRepository;
import com.example.dualdb.service.CustomerSyncService;
import com.example.dualdb.service.OrderAnalysisService;
import com.example.dualdb.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CustomerGraphQLController {

    private final CustomerSyncService customerSyncService;
    private final OrderAnalysisService orderAnalysisService;
    private final ProductService productService;
    private final CustomerRepository customerRepository;
    private final Neo4jCustomerRepository neo4jCustomerRepository;

    // ============================================
    // QUERY MAPPINGS
    // ============================================

    @QueryMapping
    public List<Customer> customers() {
        log.info("GraphQL Query: customers");
        return customerSyncService.getAllCustomers();
    }

    @QueryMapping
    public Customer customerById(@Argument Long customerId) {
        log.info("GraphQL Query: customerById - {}", customerId);
        return customerSyncService.getCustomerById(customerId);
    }

    @QueryMapping
    public Customer customersByEmail(@Argument String email) {
        log.info("GraphQL Query: customersByEmail - {}", email);
        return customerRepository.findByEmail(email).orElse(null);
    }

    @QueryMapping
    public List<Customer> customersByType(@Argument Customer.CustomerType customerType) {
        log.info("GraphQL Query: customersByType - {}", customerType);
        return customerRepository.findByCustomerType(customerType);
    }

    @QueryMapping
    public List<Customer> customersByActiveStatus(@Argument Boolean isActive) {
        log.info("GraphQL Query: customersByActiveStatus - {}", isActive);
        if (Boolean.TRUE.equals(isActive)) {
            return customerRepository.findByIsActiveTrue();
        } else {
            return customerRepository.findByIsActiveFalse();
        }
    }

    @QueryMapping
    public List<Customer> searchCustomers(@Argument String query) {
        log.info("GraphQL Query: searchCustomers - {}", query);
        return customerRepository.searchByName(query);
    }

    @QueryMapping
    public List<Customer> customersWithoutOrders() {
        log.info("GraphQL Query: customersWithoutOrders");
        return customerRepository.findCustomersWithoutOrders();
    }

    @QueryMapping
    public List<Customer> customersWithOrders() {
        log.info("GraphQL Query: customersWithOrders");
        return customerRepository.findCustomersWithOrders();
    }

    @QueryMapping
    public List<Customer> topCustomersByOrderCount() {
        log.info("GraphQL Query: topCustomersByOrderCount");
        return customerRepository.findTopCustomersByOrderCount();
    }

    @QueryMapping
    public List<Customer> topCustomersByTotalSpent() {
        log.info("GraphQL Query: topCustomersByTotalSpent");
        return customerRepository.findTopCustomersByTotalSpent();
    }

    @QueryMapping
    public List<Neo4jProduct> productRecommendations(@Argument Long customerId, @Argument Integer limit) {
        log.info("GraphQL Query: productRecommendations - {}, limit: {}", customerId, limit);
        List<Neo4jProduct> recommendations = orderAnalysisService.getProductRecommendations(customerId);
        if (limit != null && limit > 0) {
            return recommendations.stream().limit(limit).collect(Collectors.toList());
        }
        return recommendations;
    }

    @QueryMapping
    public List<Neo4jCustomer> customersWhoBoughtProduct(@Argument Long productId) {
        log.info("GraphQL Query: customersWhoBoughtProduct - {}", productId);
        return orderAnalysisService.getCustomersWhoBoughtProduct(productId);
    }

    @QueryMapping
    public List<Neo4jProduct> frequentlyBoughtTogether(@Argument Long productId, @Argument Integer limit) {
        log.info("GraphQL Query: frequentlyBoughtTogether - {}, limit: {}", productId, limit);
        List<Neo4jProduct> products = productService.getFrequentlyBoughtTogether(productId);
        if (limit != null && limit > 0) {
            return products.stream().limit(limit).collect(Collectors.toList());
        }
        return products;
    }

    @QueryMapping
    public CustomerOrderAnalytics customerOrderAnalytics(@Argument Long customerId) {
        log.info("GraphQL Query: customerOrderAnalytics - {}", customerId);
        Customer customer = customerSyncService.getCustomerById(customerId);
        List<Order> orders = customer.getOrders() != null ? customer.getOrders() : Collections.emptyList();
        
        CustomerOrderAnalytics analytics = new CustomerOrderAnalytics();
        analytics.setCustomerId(customerId);
        analytics.setFirstName(customer.getFirstName());
        analytics.setLastName(customer.getLastName());
        analytics.setTotalOrders(orders.size());
        
        BigDecimal totalSpent = orders.stream()
                .filter(o -> o.getOrderStatus() == Order.OrderStatus.Delivered)
                .map(Order::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        analytics.setTotalSpent(totalSpent);
        
        analytics.setAverageOrderValue(orders.isEmpty() ? BigDecimal.ZERO : 
                totalSpent.divide(BigDecimal.valueOf(orders.size()), 2, java.math.RoundingMode.HALF_UP));
        
        if (!orders.isEmpty()) {
            analytics.setLastOrderDate(orders.get(0).getOrderDate());
        }
        
        return analytics;
    }

    // ============================================
    // MUTATION MAPPINGS
    // ============================================

    @MutationMapping
    public Customer createCustomer(@Argument CustomerInput input) {
        log.info("GraphQL Mutation: createCustomer - {}", input.getEmail());
        Customer customer = new Customer();
        customer.setFirstName(input.getFirstName());
        customer.setLastName(input.getLastName());
        customer.setEmail(input.getEmail());
        customer.setPhone(input.getPhone());
        customer.setCustomerType(input.getCustomerType());
        customer.setRegistrationDate(LocalDateTime.now());
        customer.setIsActive(true);
        return customerSyncService.createOrUpdateCustomer(customer);
    }

    @MutationMapping
    public Customer updateCustomer(@Argument Long customerId, @Argument UpdateCustomerInput input) {
        log.info("GraphQL Mutation: updateCustomer - {}", customerId);
        Customer customer = customerSyncService.getCustomerById(customerId);
        
        if (input.getFirstName() != null) {
            customer.setFirstName(input.getFirstName());
        }
        if (input.getLastName() != null) {
            customer.setLastName(input.getLastName());
        }
        if (input.getPhone() != null) {
            customer.setPhone(input.getPhone());
        }
        if (input.getCustomerType() != null) {
            customer.setCustomerType(input.getCustomerType());
        }
        if (input.getIsActive() != null) {
            customer.setIsActive(input.getIsActive());
        }
        
        return customerSyncService.createOrUpdateCustomer(customer);
    }

    @MutationMapping
    public DeleteResult deleteCustomer(@Argument Long customerId) {
        log.info("GraphQL Mutation: deleteCustomer - {}", customerId);
        try {
            customerSyncService.deleteCustomer(customerId);
            return new DeleteResult(true, "Customer deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting customer: {}", e.getMessage());
            return new DeleteResult(false, "Failed to delete customer: " + e.getMessage());
        }
    }

    @MutationMapping
    public Customer deactivateCustomer(@Argument Long customerId) {
        log.info("GraphQL Mutation: deactivateCustomer - {}", customerId);
        Customer customer = customerSyncService.getCustomerById(customerId);
        customer.setIsActive(false);
        return customerSyncService.createOrUpdateCustomer(customer);
    }

    @MutationMapping
    public Customer activateCustomer(@Argument Long customerId) {
        log.info("GraphQL Mutation: activateCustomer - {}", customerId);
        Customer customer = customerSyncService.getCustomerById(customerId);
        customer.setIsActive(true);
        return customerSyncService.createOrUpdateCustomer(customer);
    }

    @MutationMapping
    public Customer updateCustomerType(@Argument Long customerId, @Argument Customer.CustomerType customerType) {
        log.info("GraphQL Mutation: updateCustomerType - {}, {}", customerId, customerType);
        Customer customer = customerSyncService.getCustomerById(customerId);
        customer.setCustomerType(customerType);
        return customerSyncService.createOrUpdateCustomer(customer);
    }

    // ============================================
    // SCHEMA MAPPINGS (Field Resolvers)
    // ============================================

 

    @BatchMapping(typeName = "Customer", field = "orders")
    public Map<Customer, List<Order>> getOrdersForCustomers(List<Customer> customers) {
        log.info("Batch fetching orders for {} customers", customers.size());
        Map<Customer, List<Order>> result = new HashMap<>();
        
        for (Customer customer : customers) {
            if (customer.getOrders() != null) {
                result.put(customer, customer.getOrders());
            } else {
                result.put(customer, Collections.emptyList());
            }
        }
        return result;
    }

    @SchemaMapping(typeName = "Customer", field = "totalSpent")
    public BigDecimal getTotalSpentForCustomer(Customer customer) {
        if (customer.getOrders() == null || customer.getOrders().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return customer.getOrders().stream()
                .filter(o -> o.getOrderStatus() == Order.OrderStatus.Delivered)
                .map(Order::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @SchemaMapping(typeName = "Customer", field = "orderCount")
    public Integer getOrderCountForCustomer(Customer customer) {
        return customer.getOrders() != null ? customer.getOrders().size() : 0;
    }

    @SchemaMapping(typeName = "Customer", field = "registrationDate")
    public String getRegistrationDateFormatted(Customer customer) {
        return customer.getRegistrationDate() != null 
                ? customer.getRegistrationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) 
                : null;
    }

    // ============================================
    // ANALYTICS CLASS
    // ============================================

    public static class CustomerOrderAnalytics {
        private Long customerId;
        private String firstName;
        private String lastName;
        private Integer totalOrders;
        private BigDecimal totalSpent;
        private BigDecimal averageOrderValue;
        private LocalDateTime lastOrderDate;

        // Getters and setters
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public Integer getTotalOrders() { return totalOrders; }
        public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
        public BigDecimal getTotalSpent() { return totalSpent; }
        public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }
        public BigDecimal getAverageOrderValue() { return averageOrderValue; }
        public void setAverageOrderValue(BigDecimal averageOrderValue) { this.averageOrderValue = averageOrderValue; }
        public LocalDateTime getLastOrderDate() { return lastOrderDate; }
        public void setLastOrderDate(LocalDateTime lastOrderDate) { this.lastOrderDate = lastOrderDate; }
    }

    // ============================================
    // INPUT/OUTPUT CLASSES
    // ============================================

    public static class CustomerInput {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String dateOfBirth;
        private Customer.CustomerType customerType;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
        public Customer.CustomerType getCustomerType() { return customerType; }
        public void setCustomerType(Customer.CustomerType customerType) { this.customerType = customerType; }
    }

    public static class UpdateCustomerInput {
        private String firstName;
        private String lastName;
        private String phone;
        private Customer.CustomerType customerType;
        private Boolean isActive;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public Customer.CustomerType getCustomerType() { return customerType; }
        public void setCustomerType(Customer.CustomerType customerType) { this.customerType = customerType; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }

    public static class DeleteResult {
        private boolean success;
        private String message;

        public DeleteResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}