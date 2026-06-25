package com.example.dualdb.repository.mysql;

import com.example.dualdb.model.mysql.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // ============================================
    // DERIVED QUERY METHODS (Spring Data JPA)
    // ============================================

    /**
     * Find customer by email (unique)
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Find customers by customer type
     */
    List<Customer> findByCustomerType(Customer.CustomerType customerType);

    /**
     * Find active customers
     */
    List<Customer> findByIsActiveTrue();

    /**
     * Find inactive customers
     */
    List<Customer> findByIsActiveFalse();

    /**
     * Find customers by first name containing
     */
    List<Customer> findByFirstNameContaining(String firstName);

    /**
     * Find customers by last name containing
     */
    List<Customer> findByLastNameContaining(String lastName);

    /**
     * Find customers by email containing
     */
    List<Customer> findByEmailContaining(String email);

    /**
     * Find customers by phone
     */
    Optional<Customer> findByPhone(String phone);

    /**
     * Count customers by type
     */
    long countByCustomerType(Customer.CustomerType customerType);

    /**
     * Count active customers
     */
    long countByIsActiveTrue();

    /**
     * Count inactive customers
     */
    long countByIsActiveFalse();

    /**
     * Find customers registered after a specific date
     */
    List<Customer> findByRegistrationDateAfter(java.time.LocalDateTime date);

    /**
     * Find customers registered before a specific date
     */
    List<Customer> findByRegistrationDateBefore(java.time.LocalDateTime date);

    /**
     * Find customers by registration date between
     */
    List<Customer> findByRegistrationDateBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

    /**
     * Find customers by type and active status
     */
    List<Customer> findByCustomerTypeAndIsActiveTrue(Customer.CustomerType customerType);

    /**
     * Find customers by type and active status (inactive)
     */
    List<Customer> findByCustomerTypeAndIsActiveFalse(Customer.CustomerType customerType);

    /**
     * Find top customers by number of orders (using JPQL)
     */
    @Query("SELECT c FROM Customer c " +
           "JOIN Order o ON c.customerId = o.customer.customerId " +
           "GROUP BY c.customerId " +
           "ORDER BY COUNT(o.orderId) DESC")
    List<Customer> findTopCustomersByOrderCount();

    /**
     * Find top customers by total spent
     */
    @Query("SELECT c FROM Customer c " +
           "JOIN Order o ON c.customerId = o.customer.customerId " +
           "WHERE o.orderStatus = 'Delivered' " +
           "GROUP BY c.customerId " +
           "ORDER BY SUM(o.finalAmount) DESC")
    List<Customer> findTopCustomersByTotalSpent();

    // ============================================
    // CUSTOM QUERIES (JPQL)
    // ============================================

    /**
     * Search customers by name (first or last) containing the search term
     */
    @Query("SELECT c FROM Customer c " +
           "WHERE LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Customer> searchByName(@Param("searchTerm") String searchTerm);

    /**
     * Search customers by email or phone
     */
    @Query("SELECT c FROM Customer c " +
           "WHERE LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR c.phone LIKE CONCAT('%', :searchTerm, '%')")
    List<Customer> searchByEmailOrPhone(@Param("searchTerm") String searchTerm);

    /**
     * Find customers who haven't placed any orders
     */
    @Query("SELECT c FROM Customer c " +
           "WHERE c.customerId NOT IN (SELECT DISTINCT o.customer.customerId FROM Order o)")
    List<Customer> findCustomersWithoutOrders();

    /**
     * Find customers who have placed at least one order
     */
    @Query("SELECT DISTINCT c FROM Customer c " +
           "JOIN Order o ON c.customerId = o.customer.customerId")
    List<Customer> findCustomersWithOrders();

    /**
     * Find customers with minimum order count
     */
    @Query("SELECT c FROM Customer c " +
           "WHERE (SELECT COUNT(o) FROM Order o WHERE o.customer.customerId = c.customerId) >= :minOrders")
    List<Customer> findCustomersWithMinOrders(@Param("minOrders") int minOrders);

    /**
     * Find customers with minimum total spent
     */
    @Query("SELECT c FROM Customer c " +
           "WHERE (SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o " +
           "WHERE o.customer.customerId = c.customerId AND o.orderStatus = 'Delivered') >= :minSpent")
    List<Customer> findCustomersWithMinSpent(@Param("minSpent") java.math.BigDecimal minSpent);

    /**
     * Get customer order statistics
     */
    @Query("SELECT c.customerId, c.firstName, c.lastName, " +
           "COUNT(o.orderId) as orderCount, " +
           "COALESCE(SUM(o.finalAmount), 0) as totalSpent " +
           "FROM Customer c " +
           "LEFT JOIN Order o ON c.customerId = o.customer.customerId AND o.orderStatus = 'Delivered' " +
           "GROUP BY c.customerId")
    List<Object[]> getCustomerOrderStatistics();

    /**
     * Find customers by customer type with pagination
     */
    @Query("SELECT c FROM Customer c WHERE c.customerType = :customerType")
    List<Customer> findCustomersByType(@Param("customerType") Customer.CustomerType customerType);

    // ============================================
    // NATIVE QUERIES (when JPQL is not enough)
    // ============================================

    /**
     * Find customers who bought a specific product
     */
    @Query(value = "SELECT DISTINCT c.* FROM customers c " +
                   "JOIN orders o ON c.customer_id = o.customer_id " +
                   "JOIN order_items oi ON o.order_id = oi.order_id " +
                   "WHERE oi.product_id = :productId", 
           nativeQuery = true)
    List<Customer> findCustomersWhoBoughtProduct(@Param("productId") Long productId);

    /**
     * Find customers by registration year
     */
    @Query(value = "SELECT * FROM customers " +
                   "WHERE YEAR(registration_date) = :year", 
           nativeQuery = true)
    List<Customer> findCustomersByRegistrationYear(@Param("year") int year);

    /**
     * Get total revenue by customer
     */
    @Query(value = "SELECT c.customer_id, c.first_name, c.last_name, " +
                   "COALESCE(SUM(o.final_amount), 0) as total_spent " +
                   "FROM customers c " +
                   "LEFT JOIN orders o ON c.customer_id = o.customer_id " +
                   "AND o.order_status IN ('Delivered', 'Shipped') " +
                   "GROUP BY c.customer_id " +
                   "HAVING total_spent > 0 " +
                   "ORDER BY total_spent DESC", 
           nativeQuery = true)
    List<Object[]> getCustomerRevenue();

    // ============================================
    // BULK OPERATIONS
    // ============================================

    /**
     * Update customer type in bulk
     */
    @Query("UPDATE Customer c SET c.customerType = :newType WHERE c.customerType = :oldType")
    int updateCustomerTypeByType(@Param("oldType") Customer.CustomerType oldType, 
                                  @Param("newType") Customer.CustomerType newType);

    /**
     * Deactivate customers who haven't logged in for a while
     */
    @Query("UPDATE Customer c SET c.isActive = false " +
           "WHERE c.lastLogin < :date AND c.isActive = true")
    int deactivateInactiveCustomers(@Param("date") java.time.LocalDateTime date);

    /**
     * Delete customers without orders (use with caution!)
     */
    @Query("DELETE FROM Customer c " +
           "WHERE c.customerId NOT IN (SELECT DISTINCT o.customer.customerId FROM Order o)")
    int deleteCustomersWithoutOrders();
}