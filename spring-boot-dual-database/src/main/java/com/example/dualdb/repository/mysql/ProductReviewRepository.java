package com.example.dualdb.repository.mysql;

import com.example.dualdb.model.mysql.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    /**
     * Find all reviews for a specific product
     */
    List<ProductReview> findByProductProductId(Long productId);

    /**
     * Find all reviews by a specific customer
     */
    List<ProductReview> findByCustomerCustomerId(Long customerId);

    /**
     * Find all reviews with a specific rating
     */
    List<ProductReview> findByRating(Integer rating);

    /**
     * Find reviews by product and rating
     */
    List<ProductReview> findByProductProductIdAndRating(Long productId, Integer rating);

    /**
     * Find verified purchase reviews for a product
     */
    List<ProductReview> findByProductProductIdAndIsVerifiedPurchaseTrue(Long productId);

    /**
     * Find top helpful reviews for a product
     */
    List<ProductReview> findByProductProductIdOrderByHelpfulCountDesc(Long productId);

    /**
     * Get average rating for a product
     */
    @Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.product.productId = :productId")
    Optional<Double> findAverageRatingByProductId(@Param("productId") Long productId);

    /**
     * Get review count for a product
     */
    @Query("SELECT COUNT(r) FROM ProductReview r WHERE r.product.productId = :productId")
    Long countReviewsByProductId(@Param("productId") Long productId);

    /**
     * Get rating distribution for a product
     */
    @Query("SELECT r.rating, COUNT(r) FROM ProductReview r WHERE r.product.productId = :productId GROUP BY r.rating ORDER BY r.rating DESC")
    List<Object[]> getRatingDistribution(@Param("productId") Long productId);

    /**
     * Find recent reviews for a product
     */
    @Query("SELECT r FROM ProductReview r WHERE r.product.productId = :productId ORDER BY r.createdAt DESC")
    List<ProductReview> findRecentReviewsByProductId(@Param("productId") Long productId);

    /**
     * Increment helpful count for a review
     */
    @Modifying
    @Transactional
    @Query("UPDATE ProductReview r SET r.helpfulCount = r.helpfulCount + 1 WHERE r.reviewId = :reviewId")
    void incrementHelpfulCount(@Param("reviewId") Long reviewId);

    /**
     * Decrement helpful count for a review
     */
    @Modifying
    @Transactional
    @Query("UPDATE ProductReview r SET r.helpfulCount = r.helpfulCount - 1 WHERE r.reviewId = :reviewId AND r.helpfulCount > 0")
    void decrementHelpfulCount(@Param("reviewId") Long reviewId);

    /**
     * Find reviews with a minimum rating
     */
    @Query("SELECT r FROM ProductReview r WHERE r.rating >= :minRating ORDER BY r.rating DESC")
    List<ProductReview> findByMinRating(@Param("minRating") Integer minRating);

    /**
     * Find reviews with specific text in title or content
     */
    @Query("SELECT r FROM ProductReview r WHERE LOWER(r.reviewTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(r.reviewText) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ProductReview> searchReviewsByKeyword(@Param("keyword") String keyword);

    /**
     * Find reviews by customer and product (for checking if already reviewed)
     */
    Optional<ProductReview> findByCustomerCustomerIdAndProductProductId(Long customerId, Long productId);

    /**
     * Delete all reviews for a product
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ProductReview r WHERE r.product.productId = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}