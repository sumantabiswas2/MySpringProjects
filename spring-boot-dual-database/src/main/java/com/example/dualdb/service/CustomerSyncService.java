package com.example.dualdb.service;

import com.example.dualdb.model.mysql.Customer;
import com.example.dualdb.model.mysql.Order;
import com.example.dualdb.model.mysql.OrderItem;
import com.example.dualdb.model.mysql.Product;
import com.example.dualdb.model.neo4j.*;
import com.example.dualdb.repository.mysql.CustomerRepository;
import com.example.dualdb.repository.mysql.OrderRepository;
import com.example.dualdb.repository.mysql.ProductRepository;
import com.example.dualdb.repository.neo4j.Neo4jCustomerRepository;
import com.example.dualdb.repository.neo4j.Neo4jOrderRepository;
import com.example.dualdb.repository.neo4j.Neo4jProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerSyncService {

    private final CustomerRepository mysqlCustomerRepository;
    private final OrderRepository mysqlOrderRepository;
    private final ProductRepository mysqlProductRepository;
    private final Neo4jCustomerRepository neo4jCustomerRepository;
    private final Neo4jOrderRepository neo4jOrderRepository;
    private final Neo4jProductRepository neo4jProductRepository;

    
    
    /**
     * Get customer with caching
     */
    @Cacheable(value = "customerById", key = "#customerId", unless = "#result == null")
    public Customer getCustomerById(Long customerId) {
        log.info("Fetching customer {} from MySQL (not from cache)", customerId);
        return mysqlCustomerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));
    }
    
    

    /**
     * Get all customers with caching
     */
    @Cacheable(value = "customers")
    public List<Customer> getAllCustomers() {
        log.info("Fetching all customers from MySQL (not from cache)");
        return mysqlCustomerRepository.findAll();
    }
    
    

    /**
     * Get all Neo4j customers with caching
     */
    @Cacheable(value = "neojCustomer", unless = "#result == null || #result.isEmpty()")
    public List<Neo4jCustomer> getAllNeo4jCustomers() {
        log.info("Fetching all Neo4j customers (not from cache)");
        return neo4jCustomerRepository.findAll();
    }
    
    

    /**
     * Get recent Neo4j customers with caching
     */
    @Cacheable(value = "neojCustomer", key = "'recent'", unless = "#result == null || #result.isEmpty()")
    public List<Neo4jCustomer> getRecentNeo4jCustomers() {
        log.info("Fetching recent Neo4j customers (not from cache)");
        return neo4jCustomerRepository.findRecentCustomers();
    }
    
    

    /**
     * Create or update customer - evicts old cache entries
     */
    @CachePut(value = "customerById", key = "#customer.customerId")
    @CacheEvict(value = "customers", allEntries = true)
    public Customer createOrUpdateCustomer(Customer customer) {
        log.info("Creating/updating customer {} - evicting cache", customer.getCustomerId());
        return mysqlCustomerRepository.save(customer);
    }
    
    

    /**
     * Delete customer - evicts all related cache entries
     */
    @CacheEvict(value = {"customerById", "customers", "customerOrders"}, 
                key = "#customerId")
    public void deleteCustomer(Long customerId) {
        log.info("Deleting customer {} - evicting cache", customerId);
        mysqlCustomerRepository.deleteById(customerId);
    }
    
    

    /**
     * Sync customer to Neo4j and evict Neo4j caches
     */
    @CacheEvict(value = {"neojCustomer", "neojProducts", "customerRecommendations"}, 
                allEntries = true)
    @Transactional
    public void syncCustomerToNeo4j(Long customerId) {
        log.info("Syncing customer {} to Neo4j - evicting Neo4j caches", customerId);

        // 1. Fetch from MySQL
        Customer mysqlCustomer = mysqlCustomerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        List<Order> mysqlOrders = mysqlOrderRepository.findByCustomerCustomerId(customerId);

        // 2. Create Neo4j entities
        Neo4jCustomer neo4jCustomer = new Neo4jCustomer(mysqlCustomer);
        List<Neo4jOrder> neo4jOrders = new ArrayList<>();

        for (Order mysqlOrder : mysqlOrders) {
            Neo4jOrder neo4jOrder = new Neo4jOrder();
            neo4jOrder.setOrderId(mysqlOrder.getOrderId());
            neo4jOrder.setOrderDate(mysqlOrder.getOrderDate());
            neo4jOrder.setOrderStatus(mysqlOrder.getOrderStatus().name());
            neo4jOrder.setTotalAmount(mysqlOrder.getTotalAmount());
            neo4jOrder.setDiscountAmount(mysqlOrder.getDiscountAmount());
            neo4jOrder.setTaxAmount(mysqlOrder.getTaxAmount());
            neo4jOrder.setShippingCharge(mysqlOrder.getShippingCharge());
            neo4jOrder.setFinalAmount(mysqlOrder.getFinalAmount());
            neo4jOrder.setPaymentMethod(mysqlOrder.getPaymentMethod());
            neo4jOrder.setPaymentStatus(mysqlOrder.getPaymentStatus().name());

            // Process order items
            List<Neo4jOrderItem> neo4jItems = new ArrayList<>();
            for (OrderItem mysqlItem : mysqlOrder.getOrderItems()) {
                Neo4jOrderItem neo4jItem = new Neo4jOrderItem();
                neo4jItem.setQuantity(mysqlItem.getQuantity());
                neo4jItem.setUnitPriceAtOrder(mysqlItem.getUnitPriceAtOrder());
                neo4jItem.setDiscountPercent(mysqlItem.getDiscountPercent());
                neo4jItem.setTaxPercent(mysqlItem.getTaxPercent());
                neo4jItem.setTotalPrice(mysqlItem.getTotalPrice());

                // Get or create Neo4j product
                Product mysqlProduct = mysqlItem.getProduct();
                Neo4jProduct neo4jProduct = neo4jProductRepository.findByProductId(mysqlProduct.getProductId());
                if (neo4jProduct == null) {
                    neo4jProduct = new Neo4jProduct(mysqlProduct);
                    neo4jProduct = neo4jProductRepository.save(neo4jProduct);
                }
                neo4jItem.setProduct(neo4jProduct);
                neo4jItems.add(neo4jItem);
            }

            neo4jOrder.setItems(neo4jItems);
            neo4jOrders.add(neo4jOrder);
        }

        // 3. Save to Neo4j
        neo4jCustomer.setOrders(neo4jOrders);
        neo4jCustomerRepository.save(neo4jCustomer);

        log.info("Successfully synced customer {} with {} orders to Neo4j", 
                customerId, neo4jOrders.size());
    }
    
    
    

    @CacheEvict(value = {"neojCustomer", "neojProducts", "customerRecommendations"}, 
                allEntries = true)
    @Transactional
    public void syncAllCustomersToNeo4j() {
        log.info("Syncing all customers to Neo4j - evicting Neo4j caches");
        List<Customer> customers = mysqlCustomerRepository.findAll();
        
        for (Customer customer : customers) {
            try {
                syncCustomerToNeo4j(customer.getCustomerId());
            } catch (Exception e) {
                log.error("Failed to sync customer {}: {}", customer.getCustomerId(), e.getMessage());
            }
        }
        
        log.info("Completed syncing {} customers to Neo4j", customers.size());
    }
    
    

    public boolean isCustomerInNeo4j(Long customerId) {
        return neo4jCustomerRepository.findByCustomerId(customerId).isPresent();
    }



	public List<Customer> getCustomersByType(String customerType) {
		// TODO Auto-generated method stub
		return null;
	}
}