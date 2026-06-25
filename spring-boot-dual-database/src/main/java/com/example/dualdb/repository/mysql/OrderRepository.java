package com.example.dualdb.repository.mysql;

import com.example.dualdb.model.mysql.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerCustomerId(Long customerId);

    List<Order> findByOrderStatus(Order.OrderStatus status);

    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT o FROM Order o WHERE o.finalAmount >= :minAmount")
    List<Order> findOrdersAboveAmount(@Param("minAmount") BigDecimal minAmount);

    @Query("SELECT o.customer.customerId, COUNT(o) as orderCount, SUM(o.finalAmount) as totalSpent " +
           "FROM Order o GROUP BY o.customer.customerId HAVING SUM(o.finalAmount) >= :minSpend")
    List<Object[]> findHighSpendingCustomers(@Param("minSpend") BigDecimal minSpend);
}