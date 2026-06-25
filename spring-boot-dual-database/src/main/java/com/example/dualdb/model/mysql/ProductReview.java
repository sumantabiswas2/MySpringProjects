package com.example.dualdb.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ProductReview entity representing customer reviews for products
 * Maps to the product_reviews table in MySQL
 */
@Entity
@Table(name = "product_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private Integer rating;

    @Column(name = "review_title", length = 200)
    private String reviewTitle;

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;

    @Column(name = "is_verified_purchase")
    private Boolean isVerifiedPurchase = false;

    @Column(name = "helpful_count")
    private Integer helpfulCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Lifecycle callback to set timestamps before persist
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (helpfulCount == null) {
            helpfulCount = 0;
        }
        if (isVerifiedPurchase == null) {
            isVerifiedPurchase = false;
        }
    }

    /**
     * Lifecycle callback to update timestamp before update
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Helper method to increment helpful count
     */
    public void incrementHelpfulCount() {
        if (this.helpfulCount == null) {
            this.helpfulCount = 0;
        }
        this.helpfulCount++;
    }

    /**
     * Helper method to decrement helpful count
     */
    public void decrementHelpfulCount() {
        if (this.helpfulCount != null && this.helpfulCount > 0) {
            this.helpfulCount--;
        }
    }

    /**
     * Check if review is verified purchase
     */
    public boolean isVerifiedPurchase() {
        return Boolean.TRUE.equals(isVerifiedPurchase);
    }

    @Override
    public String toString() {
        return "ProductReview{" +
                "reviewId=" + reviewId +
                ", productId=" + (product != null ? product.getProductId() : null) +
                ", customerId=" + (customer != null ? customer.getCustomerId() : null) +
                ", rating=" + rating +
                ", reviewTitle='" + reviewTitle + '\'' +
                ", isVerifiedPurchase=" + isVerifiedPurchase +
                ", helpfulCount=" + helpfulCount +
                ", createdAt=" + createdAt +
                '}';
    }
}