package com.example.dualdb.service;

import com.example.dualdb.model.neo4j.Neo4jCustomer;
import com.example.dualdb.model.neo4j.Neo4jProduct;
import com.example.dualdb.repository.neo4j.Neo4jCustomerRepository;
import com.example.dualdb.repository.neo4j.Neo4jProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * Find product recommendations for a customer
     * (Frequently bought together by customers who bought the same products)
     */
    public List<Neo4jProduct> getProductRecommendations(Long customerId) {
        log.info("Finding product recommendations for customer {}", customerId);
        
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
     * Find top customers who bought a specific product
     */
    public List<Neo4jCustomer> getCustomersWhoBoughtProduct(Long productId) {
        log.info("Finding customers who bought product {}", productId);
        return neo4jCustomerRepository.findCustomersWhoBoughtProduct(productId);
    }

    /**
     * Find the most active customers (by order count)
     */
    public List<Neo4jCustomer> getTopCustomersByOrderCount(int limit) {
        log.info("Finding top {} customers by order count", limit);
        return neo4jCustomerRepository.findTopCustomersByOrderCount(limit);
    }

    /**
     * Find customers who bought products in a specific category
     */
    public List<Neo4jCustomer> findCustomersByCategory(String category) {
        log.info("Finding customers who bought products in category: {}", category);
        return neo4jCustomerRepository.findCustomersWhoBoughtProduct(
                neo4jProductRepository.findByCategory(category)
                        .stream()
                        .findFirst()
                        .map(Neo4jProduct::getProductId)
                        .orElse(null)
        );
    }
}