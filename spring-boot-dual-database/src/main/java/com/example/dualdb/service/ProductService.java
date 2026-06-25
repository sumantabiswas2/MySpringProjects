package com.example.dualdb.service;

import com.example.dualdb.model.mysql.Product;
import com.example.dualdb.model.neo4j.Neo4jProduct;
import com.example.dualdb.repository.mysql.ProductRepository;
import com.example.dualdb.repository.neo4j.Neo4jProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository mysqlProductRepository;
    private final Neo4jProductRepository neo4jProductRepository;

    /**
     * Get product by ID with caching
     */
    @Cacheable(value = "products", key = "#productId")
    public Product getProductById(Long productId) {
        log.info("Fetching product {} from MySQL (not from cache)", productId);
        return mysqlProductRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
    }

    /**
     * Get products by category with caching
     */
    @Cacheable(value = "productByCategory", key = "#category")
    public List<Product> getProductsByCategory(String category) {
        log.info("Fetching products in category '{}' from MySQL (not from cache)", category);
        return mysqlProductRepository.findByCategory(category);
    }

    /**
     * Get active products with caching
     */
    @Cacheable(value = "productByCategory", key = "'active'")
    public List<Product> getActiveProducts() {
        log.info("Fetching active products from MySQL (not from cache)");
        return mysqlProductRepository.findActiveProductsOrderedByPriceDesc();
    }

    /**
     * Get Neo4j product with caching
     */
    @Cacheable(value = "neojProducts", key = "#productId")
    public Neo4jProduct getNeo4jProductById(Long productId) {
        log.info("Fetching Neo4j product {} (not from cache)", productId);
        return neo4jProductRepository.findByProductId(productId);
    }

    /**
     * Create or update product - manages cache
     */
    @CachePut(value = "products", key = "#product.productId")
    @CacheEvict(value = {"productByCategory"}, allEntries = true)
    public Product createOrUpdateProduct(Product product) {
        log.info("Creating/updating product {} - updating cache", product.getProductId());
        return mysqlProductRepository.save(product);
    }

    /**
     * Delete product - evicts caches
     */
    @CacheEvict(value = {"products", "productByCategory", "neojProducts"}, 
                key = "#productId")
    public void deleteProduct(Long productId) {
        log.info("Deleting product {} - evicting caches", productId);
        mysqlProductRepository.deleteById(productId);
    }

    /**
     * Get category statistics with caching (long-lived)
     */
    @Cacheable(value = "categoryStats", key = "'all'")
    public List<Object[]> getCategoryStats() {
        log.info("Fetching category stats from MySQL (not from cache)");
        return mysqlProductRepository.getCategoryStats();
    }

    /**
     * Get frequently bought together products
     */
    @Cacheable(value = "neojProducts", key = "'frequentlyBought_' + #productId")
    public List<Neo4jProduct> getFrequentlyBoughtTogether(Long productId) {
        log.info("Finding frequently bought together with product {} (not from cache)", productId);
        return neo4jProductRepository.findFrequentlyBoughtTogether(productId);
    }

    /**
     * Get products purchased by customer
     */
    @Cacheable(value = "customerPurchasedProducts", key = "#customerId")
    public List<Neo4jProduct> getProductsPurchasedByCustomer(Long customerId) {
        log.info("Finding products purchased by customer {} (not from cache)", customerId);
        return neo4jProductRepository.findProductsPurchasedByCustomer(customerId);
    }
}