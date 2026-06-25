package com.example.dualdb.repository.mysql;

import com.example.dualdb.model.mysql.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(String category);

    List<Product> findByQuantityInStockLessThan(Integer reorderLevel);

    List<Product> findByUnitPriceBetween(BigDecimal min, BigDecimal max);

    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.unitPrice DESC")
    List<Product> findActiveProductsOrderedByPriceDesc();

    @Query("SELECT p.category, COUNT(p) as productCount, AVG(p.unitPrice) as avgPrice " +
           "FROM Product p GROUP BY p.category")
    List<Object[]> getCategoryStats();
}