package com.example.dualdb.repository.neo4j;

import com.example.dualdb.model.neo4j.Neo4jOrder;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Neo4jOrderRepository extends Neo4jRepository<Neo4jOrder, Long> {

    @Query("MATCH (c:Customer {customerId: $customerId})-[:PLACED_ORDER]->(o:Order) " +
           "RETURN o ORDER BY o.orderDate DESC")
    List<Neo4jOrder> findOrdersByCustomer(@Param("customerId") Long customerId);

    @Query("MATCH (o:Order)-[:CONTAINS]->(p:Product) " +
           "WHERE p.productId = $productId " +
           "RETURN o")
    List<Neo4jOrder> findOrdersContainingProduct(@Param("productId") Long productId);

    @Query("MATCH (o:Order) " +
           "WHERE o.orderStatus = $status " +
           "RETURN o")
    List<Neo4jOrder> findOrdersByStatus(@Param("status") String status);
}