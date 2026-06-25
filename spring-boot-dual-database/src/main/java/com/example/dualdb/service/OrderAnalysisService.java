package com.example.dualdb.service;

import com.example.dualdb.model.neo4j.Neo4jCustomer;
import com.example.dualdb.model.neo4j.Neo4jProduct;
import com.example.dualdb.repository.neo4j.Neo4jCustomerRepository;
import com.example.dualdb.repository.neo4j.Neo4jProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderAnalysisService {

    private final Neo4jCustomerRepository neo4jCustomerRepository;
    private final Neo4jProductRepository neo4jProductRepository;

    /**
     * Get product recommendations with caching - expires after 1 hour
     */
    @Cacheable(value = "customerRecommendations", 
               key = "#customerId", 
               unless = "#result == null || #result.isEmpty()")
    public List<Neo4jProduct> getProductRecommendations(Long customerId) {
        log.info("Calculating product recommendations for customer {} (not from cache)", customerId);
        
        // Get all products this customer purchased
        List<Neo4jProduct> purchasedProducts = neo4jProductRepository
                .findProductsPurchasedByCustomer(customerId);
        
        if (purchasedProducts.isEmpty()) {
            log.info("No products found for customer {}", customerId);
            return List.of();
        }
        
        // For each product, find frequently bought together
        Map<Long, Neo4jProduct> recommendations = new HashMap<>();
        for (Neo4jProduct product : purchasedProducts) {
            List<Neo4jProduct> frequentlyBought = neo4jProductRepository
                    .findFrequentlyBoughtTogether(product.getProductId());
            for (Neo4jProduct recommended : frequentlyBought) {
                recommendations.putIfAbsent(recommended.getProductId(), recommended);
            }
        }
        
        // Remove products the customer already bought
        purchasedProducts.forEach(p -> recommendations.remove(p.getProductId()));
        
        log.info("Found {} product recommendations for customer {}", 
                recommendations.size(), customerId);
        
        return List.copyOf(recommendations.values());
    }

    /**
     * Get customers who bought a product with caching
     */
    @Cacheable(value = "customersByProduct", 
               key = "#productId", 
               unless = "#result == null || #result.isEmpty()")
    public List<Neo4jCustomer> getCustomersWhoBoughtProduct(Long productId) {
        log.info("Finding customers who bought product {} (not from cache)", productId);
        return neo4jCustomerRepository.findCustomersWhoBoughtProduct(productId);
    }

    /**
     * Get top customers with caching
     */
    @Cacheable(value = "topCustomers", 
               key = "#limit", 
               unless = "#result == null || #result.isEmpty()")
    public List<Neo4jCustomer> getTopCustomersByOrderCount(int limit) {
        log.info("Finding top {} customers by order count (not from cache)", limit);
        return neo4jCustomerRepository.findTopCustomersByOrderCount(limit);
    }

    /**
     * Update recommendations for a customer (evict cache)
     */
    @CacheEvict(value = "customerRecommendations", key = "#customerId")
    public void refreshCustomerRecommendations(Long customerId) {
        log.info("Refreshing recommendations cache for customer {}", customerId);
        // The actual refresh happens on the next call to getProductRecommendations
    }

    /**
     * Clear all recommendation caches
     */
    @CacheEvict(value = {"customerRecommendations", "customersByProduct", "topCustomers"}, 
                allEntries = true)
    public void clearAllRecommendationCaches() {
        log.info("Clearing all recommendation caches");
    }

    /**
     * Get customers by category with caching
     */
    @Cacheable(value = "customersByCategory", 
               key = "#category", 
               unless = "#result == null || #result.isEmpty()")
    public List<Neo4jCustomer> findCustomersByCategory(String category) {
        log.info("Finding customers who bought products in category: {} (not from cache)", category);
        return neo4jCustomerRepository.findCustomersWhoBoughtProduct(
                neo4jProductRepository.findByCategory(category)
                        .stream()
                        .findFirst()
                        .map(Neo4jProduct::getProductId)
                        .orElse(null)
        );
    }
}