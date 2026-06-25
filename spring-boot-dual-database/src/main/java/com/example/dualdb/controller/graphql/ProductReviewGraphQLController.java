package com.example.dualdb.controller.graphql;

import com.example.dualdb.model.mysql.Customer;
import com.example.dualdb.model.mysql.Product;
import com.example.dualdb.model.mysql.ProductReview;
import com.example.dualdb.repository.mysql.CustomerRepository;
import com.example.dualdb.repository.mysql.ProductRepository;
import com.example.dualdb.repository.mysql.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ProductReviewGraphQLController {

    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    // ============================================
    // QUERY MAPPINGS
    // ============================================

    @QueryMapping
    public List<ProductReview> productReviews(@Argument Long productId) {
        log.info("GraphQL Query: productReviews - productId: {}", productId);
        return productReviewRepository.findByProductProductId(productId);
    }

    @QueryMapping
    public ProductReview productReviewById(@Argument Long reviewId) {
        log.info("GraphQL Query: productReviewById - {}", reviewId);
        return productReviewRepository.findById(reviewId).orElse(null);
    }

    @QueryMapping
    public List<ProductReview> productReviewsByCustomer(@Argument Long customerId) {
        log.info("GraphQL Query: productReviewsByCustomer - {}", customerId);
        return productReviewRepository.findByCustomerCustomerId(customerId);
    }

    @QueryMapping
    public List<ProductReview> productReviewsByRating(@Argument Integer rating) {
        log.info("GraphQL Query: productReviewsByRating - {}", rating);
        return productReviewRepository.findByRating(rating);
    }

    @QueryMapping
    public Double productAverageRating(@Argument Long productId) {
        log.info("GraphQL Query: productAverageRating - {}", productId);
        return productReviewRepository.findAverageRatingByProductId(productId).orElse(0.0);
    }

    @QueryMapping
    public Map<Integer, Long> productRatingDistribution(@Argument Long productId) {
        log.info("GraphQL Query: productRatingDistribution - {}", productId);
        List<Object[]> distribution = productReviewRepository.getRatingDistribution(productId);
        return distribution.stream()
                .collect(Collectors.toMap(
                        arr -> (Integer) arr[0],
                        arr -> (Long) arr[1]
                ));
    }

    // ============================================
    // MUTATION MAPPINGS
    // ============================================

    @MutationMapping
    public ProductReview addProductReview(@Argument ProductReviewInput input) {
        log.info("GraphQL Mutation: addProductReview - productId: {}, customerId: {}", 
                input.getProductId(), input.getCustomerId());
        
        // Check if customer and product exist
        Product product = productRepository.findById(input.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + input.getProductId()));
        Customer customer = customerRepository.findById(input.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found: " + input.getCustomerId()));
        
        // Check if customer already reviewed this product
        productReviewRepository.findByCustomerCustomerIdAndProductProductId(
                customer.getCustomerId(), product.getProductId()
        ).ifPresent(review -> {
            throw new RuntimeException("Customer has already reviewed this product");
        });
        
        // Create new review
        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setCustomer(customer);
        review.setRating(input.getRating());
        review.setReviewTitle(input.getReviewTitle());
        review.setReviewText(input.getReviewText());
        review.setIsVerifiedPurchase(false); // Could be set based on order history
        review.setHelpfulCount(0);
        
        return productReviewRepository.save(review);
    }

    @MutationMapping
    public ProductReview updateProductReview(@Argument Long reviewId, @Argument ProductReviewInput input) {
        log.info("GraphQL Mutation: updateProductReview - {}", reviewId);
        
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found: " + reviewId));
        
        review.setRating(input.getRating());
        review.setReviewTitle(input.getReviewTitle());
        review.setReviewText(input.getReviewText());
        
        return productReviewRepository.save(review);
    }

    @MutationMapping
    public DeleteResult deleteProductReview(@Argument Long reviewId) {
        log.info("GraphQL Mutation: deleteProductReview - {}", reviewId);
        try {
            productReviewRepository.deleteById(reviewId);
            return new DeleteResult(true, "Review deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting review: {}", e.getMessage());
            return new DeleteResult(false, "Failed to delete review: " + e.getMessage());
        }
    }

    @MutationMapping
    public ProductReview markReviewHelpful(@Argument Long reviewId) {
        log.info("GraphQL Mutation: markReviewHelpful - {}", reviewId);
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found: " + reviewId));
        review.incrementHelpfulCount();
        return productReviewRepository.save(review);
    }

    @MutationMapping
    public ProductReview markReviewNotHelpful(@Argument Long reviewId) {
        log.info("GraphQL Mutation: markReviewNotHelpful - {}", reviewId);
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found: " + reviewId));
        review.decrementHelpfulCount();
        return productReviewRepository.save(review);
    }

    // ============================================
    // SCHEMA MAPPINGS (Field Resolvers)
    // ============================================

    @SchemaMapping(typeName = "ProductReview", field = "createdAt")
    public String getCreatedAtFormatted(ProductReview review) {
        return review.getCreatedAt() != null 
                ? review.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) 
                : null;
    }

    @SchemaMapping(typeName = "ProductReview", field = "updatedAt")
    public String getUpdatedAtFormatted(ProductReview review) {
        return review.getUpdatedAt() != null 
                ? review.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) 
                : null;
    }

    // ============================================
    // INPUT CLASSES
    // ============================================

    public static class ProductReviewInput {
        private Long productId;
        private Long customerId;
        private Integer rating;
        private String reviewTitle;
        private String reviewText;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        public String getReviewTitle() { return reviewTitle; }
        public void setReviewTitle(String reviewTitle) { this.reviewTitle = reviewTitle; }
        public String getReviewText() { return reviewText; }
        public void setReviewText(String reviewText) { this.reviewText = reviewText; }
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