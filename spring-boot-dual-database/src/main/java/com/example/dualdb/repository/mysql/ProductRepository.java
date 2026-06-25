package com.example.dualdb.repository.mysql;

import com.example.dualdb.model.mysql.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ============================================
    // DERIVED QUERY METHODS (Spring Data JPA)
    // ============================================

    /**
     * Find product by SKU (unique)
     */
    Optional<Product> findBySku(String sku);

    /**
     * Find products by category
     */
    List<Product> findByCategory(String category);

    /**
     * Find products by category and sub-category
     */
    List<Product> findByCategoryAndSubCategory(String category, String subCategory);

    /**
     * Find products by unit price between min and max
     */
    List<Product> findByUnitPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Find products with quantity in stock less than reorder level
     */
    List<Product> findByQuantityInStockLessThan(Integer reorderLevel);

    /**
     * Find products with quantity in stock greater than specified value
     */
    List<Product> findByQuantityInStockGreaterThan(Integer quantity);

    /**
     * Find products with quantity in stock greater than or equal to specified value
     */
    List<Product> findByQuantityInStockGreaterThanEqual(Integer quantity);

    /**
     * Find products with quantity in stock less than or equal to specified value
     */
    List<Product> findByQuantityInStockLessThanEqual(Integer quantity);

    /**
     * Find products with quantity in stock between min and max
     */
    List<Product> findByQuantityInStockBetween(Integer min, Integer max);

    /**
     * Find active products
     */
    List<Product> findByIsActiveTrue();

    /**
     * Find inactive products
     */
    List<Product> findByIsActiveFalse();

    /**
     * Find active products by category
     */
    List<Product> findByCategoryAndIsActiveTrue(String category);

    /**
     * Find products by product name containing
     */
    List<Product> findByProductNameContaining(String productName);

    /**
     * Find products by product name or description containing
     */
    List<Product> findByProductNameContainingOrProductDescriptionContaining(String name, String description);

    /**
     * Find products where stock is below reorder level
     */
    List<Product> findByQuantityInStockLessThanAndIsActiveTrue(Integer reorderLevel);

    /**
     * Count products by category
     */
    long countByCategory(String category);

    /**
     * Count active products by category
     */
    long countByCategoryAndIsActiveTrue(String category);

    /**
     * Sum of all stock quantity
     */
    @Query("SELECT SUM(p.quantityInStock) FROM Product p")
    Long getTotalStockQuantity();

    // ============================================
    // CUSTOM QUERIES (JPQL)
    // ============================================

    /**
     * Find products with stock above a certain threshold
     */
    @Query("SELECT p FROM Product p WHERE p.quantityInStock > :threshold")
    List<Product> findProductsWithStockAbove(@Param("threshold") Integer threshold);

    /**
     * Find products with stock below a certain threshold
     */
    @Query("SELECT p FROM Product p WHERE p.quantityInStock < :threshold")
    List<Product> findProductsWithStockBelow(@Param("threshold") Integer threshold);

    /**
     * Find products with stock between min and max
     */
    @Query("SELECT p FROM Product p WHERE p.quantityInStock BETWEEN :min AND :max")
    List<Product> findProductsWithStockBetween(@Param("min") Integer min, @Param("max") Integer max);

    /**
     * Find products needing reorder (stock <= reorder level)
     */
    @Query("SELECT p FROM Product p WHERE p.quantityInStock <= p.reorderLevel")
    List<Product> findProductsNeedingReorder();

    /**
     * Find active products needing reorder
     */
    @Query("SELECT p FROM Product p WHERE p.quantityInStock <= p.reorderLevel AND p.isActive = true")
    List<Product> findActiveProductsNeedingReorder();

    /**
     * Find active products ordered by price descending
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.unitPrice DESC")
    List<Product> findActiveProductsOrderedByPriceDesc();

    /**
     * Find active products ordered by price ascending
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.unitPrice ASC")
    List<Product> findActiveProductsOrderedByPriceAsc();

    /**
     * Find products by price range
     */
    @Query("SELECT p FROM Product p WHERE p.unitPrice >= :minPrice AND p.unitPrice <= :maxPrice")
    List<Product> findProductsByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                            @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Search products by name, description, or SKU
     */
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.productDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Product> searchProducts(@Param("searchTerm") String searchTerm);

    /**
     * Search products by name
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Product> searchProductsByName(@Param("searchTerm") String searchTerm);

    /**
     * Search products by category and name
     */
    @Query("SELECT p FROM Product p WHERE p.category = :category AND " +
           "LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Product> searchProductsByCategoryAndName(@Param("category") String category, 
                                                   @Param("searchTerm") String searchTerm);

    /**
     * Get category statistics
     */
    @Query("SELECT p.category, COUNT(p) as productCount, AVG(p.unitPrice) as avgPrice, " +
           "SUM(p.quantityInStock) as totalStock " +
           "FROM Product p GROUP BY p.category")
    List<Object[]> getCategoryStats();

    /**
     * Get product with highest price
     */
    @Query("SELECT p FROM Product p WHERE p.unitPrice = (SELECT MAX(p2.unitPrice) FROM Product p2)")
    Optional<Product> findMostExpensiveProduct();

    /**
     * Get product with lowest price
     */
    @Query("SELECT p FROM Product p WHERE p.unitPrice = (SELECT MIN(p2.unitPrice) FROM Product p2)")
    Optional<Product> findCheapestProduct();

    /**
     * Get products with stock greater than 0
     */
    @Query("SELECT p FROM Product p WHERE p.quantityInStock > 0")
    List<Product> findProductsInStock();

    /**
     * Get products with stock equal to 0
     */
    @Query("SELECT p FROM Product p WHERE p.quantityInStock = 0")
    List<Product> findProductsOutOfStock();

    /**
     * Get products with stock greater than 0 and active
     */
    @Query("SELECT p FROM Product p WHERE p.quantityInStock > 0 AND p.isActive = true")
    List<Product> findActiveProductsInStock();

    /**
     * Get products by category with stock greater than 0
     */
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.quantityInStock > 0")
    List<Product> findProductsInStockByCategory(@Param("category") String category);

    /**
     * Find products with high rating (based on reviews)
     */
    @Query("SELECT p FROM Product p " +
           "WHERE p.productId IN " +
           "(SELECT r.product.productId FROM ProductReview r " +
           "GROUP BY r.product.productId " +
           "HAVING AVG(r.rating) >= :minRating)")
    List<Product> findProductsWithMinRating(@Param("minRating") Double minRating);

    // ============================================
    // NATIVE QUERIES
    // ============================================

    /**
     * Get top selling products (by quantity sold)
     */
    @Query(value = "SELECT p.* " +
                   "FROM products p " +
                   "LEFT JOIN order_items oi ON p.product_id = oi.product_id " +
                   "LEFT JOIN orders o ON oi.order_id = o.order_id " +
                   "AND o.order_status IN ('Delivered', 'Shipped') " +
                   "GROUP BY p.product_id " +
                   "ORDER BY COALESCE(SUM(oi.quantity), 0) DESC " , 
           nativeQuery = true)
    List<Product> findTopSellingProducts(@Param("limit1") int limit);

    /**
     * Get products by category with stock
     */
    @Query(value = "SELECT * FROM products WHERE category = :category AND quantity_in_stock > 0", 
           nativeQuery = true)
    List<Product> findAvailableProductsByCategory(@Param("category") String category);

    /**
     * Get products by price range with stock > 0
     */
    @Query(value = "SELECT * FROM products " +
                   "WHERE unit_price BETWEEN :minPrice AND :maxPrice " +
                   "AND quantity_in_stock > 0", 
           nativeQuery = true)
    List<Product> findAvailableProductsByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                                      @Param("maxPrice") BigDecimal maxPrice);

    // ============================================
    // BULK OPERATIONS
    // ============================================

    /**
     * Update product stock
     */
    @Query("UPDATE Product p SET p.quantityInStock = :newStock WHERE p.productId = :productId")
    int updateProductStock(@Param("productId") Long productId, 
                           @Param("newStock") Integer newStock);

    /**
     * Increment product stock
     */
    @Query("UPDATE Product p SET p.quantityInStock = p.quantityInStock + :quantity WHERE p.productId = :productId")
    int incrementProductStock(@Param("productId") Long productId, 
                              @Param("quantity") Integer quantity);

    /**
     * Decrement product stock (with check for negative)
     */
    @Query("UPDATE Product p SET p.quantityInStock = p.quantityInStock - :quantity " +
           "WHERE p.productId = :productId AND p.quantityInStock >= :quantity")
    int decrementProductStock(@Param("productId") Long productId, 
                              @Param("quantity") Integer quantity);

    /**
     * Mark products as inactive if stock is 0
     */
    @Query("UPDATE Product p SET p.isActive = false WHERE p.quantityInStock = 0 AND p.isActive = true")
    int deactivateOutOfStockProducts();

    /**
     * Apply discount to products by category
     */
    @Query("UPDATE Product p SET p.unitPrice = p.unitPrice * (1 - :discountPercentage / 100) " +
           "WHERE p.category = :category")
    int applyDiscountByCategory(@Param("category") String category, 
                                 @Param("discountPercentage") Double discountPercentage);

    /**
     * Update reorder level for products by category
     */
    @Query("UPDATE Product p SET p.reorderLevel = :newReorderLevel " +
           "WHERE p.category = :category")
    int updateReorderLevelByCategory(@Param("category") String category, 
                                      @Param("newReorderLevel") Integer newReorderLevel);
}