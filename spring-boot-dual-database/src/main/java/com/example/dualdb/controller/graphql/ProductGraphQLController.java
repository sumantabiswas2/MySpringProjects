package com.example.dualdb.controller.graphql;

import com.example.dualdb.model.mysql.Product;
import com.example.dualdb.model.mysql.ProductReview;
import com.example.dualdb.model.neo4j.Neo4jProduct;
import com.example.dualdb.repository.mysql.ProductRepository;
import com.example.dualdb.service.ProductService;
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
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ProductGraphQLController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    // ============================================
    // QUERY MAPPINGS
    // ============================================

    @QueryMapping
    public List<Product> products() {
        log.info("GraphQL Query: products");
        return productRepository.findAll();
    }

    @QueryMapping
    public Product productById(@Argument Long productId) {
        log.info("GraphQL Query: productById - {}", productId);
        return productService.getProductById(productId);
    }

    @QueryMapping
    public List<Product> productsByCategory(@Argument String category) {
        log.info("GraphQL Query: productsByCategory - {}", category);
        return productService.getProductsByCategory(category);
    }

    @QueryMapping
    public List<Product> productsByPriceRange(@Argument BigDecimal minPrice, @Argument BigDecimal maxPrice) {
        log.info("GraphQL Query: productsByPriceRange - {} to {}", minPrice, maxPrice);
        return productRepository.findProductsByPriceRange(minPrice, maxPrice);
    }

    @QueryMapping
    public List<Product> productsInStock() {
        log.info("GraphQL Query: productsInStock");
        return productRepository.findProductsInStock();
    }

    @QueryMapping
    public List<Product> productsOutOfStock() {
        log.info("GraphQL Query: productsOutOfStock");
        return productRepository.findProductsOutOfStock();
    }

    @QueryMapping
    public List<Product> activeProductsInStock() {
        log.info("GraphQL Query: activeProductsInStock");
        return productRepository.findActiveProductsInStock();
    }

    @QueryMapping
    public List<Product> productsNeedingReorder() {
        log.info("GraphQL Query: productsNeedingReorder");
        return productRepository.findProductsNeedingReorder();
    }

    @QueryMapping
    public List<Product> activeProductsNeedingReorder() {
        log.info("GraphQL Query: activeProductsNeedingReorder");
        return productRepository.findActiveProductsNeedingReorder();
    }

    @QueryMapping
    public List<Product> searchProducts(@Argument String query) {
        log.info("GraphQL Query: searchProducts - {}", query);
        return productRepository.searchProducts(query);
    }

    @QueryMapping
    public List<Product> topSellingProducts(@Argument Integer limit) {
        log.info("GraphQL Query: topSellingProducts - limit: {}", limit);
        return productRepository.findTopSellingProducts(limit != null ? limit : 10);
    }

    @QueryMapping
    public List<Product> productsWithMinRating(@Argument Double minRating) {
        log.info("GraphQL Query: productsWithMinRating - {}", minRating);
        return productRepository.findProductsWithMinRating(minRating);
    }

    @QueryMapping
    public Product mostExpensiveProduct() {
        log.info("GraphQL Query: mostExpensiveProduct");
        return productRepository.findMostExpensiveProduct().orElse(null);
    }

    @QueryMapping
    public Product cheapestProduct() {
        log.info("GraphQL Query: cheapestProduct");
        return productRepository.findCheapestProduct().orElse(null);
    }

    @QueryMapping
    public List<Object[]> categoryStatistics() {
        log.info("GraphQL Query: categoryStatistics");
        return productRepository.getCategoryStats();
    }

    @QueryMapping
    public Long totalStockQuantity() {
        log.info("GraphQL Query: totalStockQuantity");
        return productRepository.getTotalStockQuantity();
    }

    @QueryMapping
    public List<Product> productsByStockRange(@Argument Integer minStock, @Argument Integer maxStock) {
        log.info("GraphQL Query: productsByStockRange - {} to {}", minStock, maxStock);
        return productRepository.findProductsWithStockBetween(minStock, maxStock);
    }

   
    // ============================================
    // MUTATION MAPPINGS
    // ============================================

    @MutationMapping
    public Product updateProductStock(@Argument Long productId, @Argument Integer quantity) {
        log.info("GraphQL Mutation: updateProductStock - productId: {}, quantity: {}", productId, quantity);
        Product product = productService.getProductById(productId);
        product.setQuantityInStock(quantity);
        return productService.createOrUpdateProduct(product);
    }

    @MutationMapping
    public Product incrementProductStock(@Argument Long productId, @Argument Integer quantity) {
        log.info("GraphQL Mutation: incrementProductStock - productId: {}, quantity: {}", productId, quantity);
        productRepository.incrementProductStock(productId, quantity);
        return productService.getProductById(productId);
    }

    @MutationMapping
    public Product decrementProductStock(@Argument Long productId, @Argument Integer quantity) {
        log.info("GraphQL Mutation: decrementProductStock - productId: {}, quantity: {}", productId, quantity);
        int updated = productRepository.decrementProductStock(productId, quantity);
        if (updated == 0) {
            throw new RuntimeException("Insufficient stock for product: " + productId);
        }
        return productService.getProductById(productId);
    }

    @MutationMapping
    public Product updateProductPrice(@Argument Long productId, @Argument BigDecimal price) {
        log.info("GraphQL Mutation: updateProductPrice - productId: {}, price: {}", productId, price);
        Product product = productService.getProductById(productId);
        product.setUnitPrice(price);
        return productService.createOrUpdateProduct(product);
    }

    @MutationMapping
    public Product toggleProductActive(@Argument Long productId) {
        log.info("GraphQL Mutation: toggleProductActive - {}", productId);
        Product product = productService.getProductById(productId);
        product.setIsActive(!product.getIsActive());
        return productService.createOrUpdateProduct(product);
    }

    @MutationMapping
    public Product updateProductCategory(@Argument Long productId, @Argument String category) {
        log.info("GraphQL Mutation: updateProductCategory - productId: {}, category: {}", productId, category);
        Product product = productService.getProductById(productId);
        product.setCategory(category);
        return productService.createOrUpdateProduct(product);
    }

    @MutationMapping
    public int applyDiscountToCategory(@Argument String category, @Argument Double discountPercentage) {
        log.info("GraphQL Mutation: applyDiscountToCategory - {}, {}%", category, discountPercentage);
        return productRepository.applyDiscountByCategory(category, discountPercentage);
    }

    @MutationMapping
    public int deactivateOutOfStockProducts() {
        log.info("GraphQL Mutation: deactivateOutOfStockProducts");
        return productRepository.deactivateOutOfStockProducts();
    }

    // ============================================
    // SCHEMA MAPPINGS (Field Resolvers)
    // ============================================

    @SchemaMapping(typeName = "Product", field = "createdAt")
    public String getCreatedAtFormatted(Product product) {
        return product.getCreatedAt() != null 
                ? product.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) 
                : null;
    }

    @SchemaMapping(typeName = "Product", field = "updatedAt")
    public String getUpdatedAtFormatted(Product product) {
        return product.getUpdatedAt() != null 
                ? product.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) 
                : null;
    }

    @SchemaMapping(typeName = "Product", field = "averageRating")
    public Double getAverageRating(Product product) {
        // In a real implementation, you'd calculate this from reviews
        // For now, return a default or fetch from a service
        return 4.5;
    }

    @SchemaMapping(typeName = "Product", field = "reviewCount")
    public Long getReviewCount(Product product) {
        // In a real implementation, you'd count reviews from the repository
        return 0L;
    }

    @SchemaMapping(typeName = "Product", field = "frequentlyBoughtTogether")
    public List<Neo4jProduct> getFrequentlyBoughtTogether(Product product) {
        return productService.getFrequentlyBoughtTogether(product.getProductId());
    }
}