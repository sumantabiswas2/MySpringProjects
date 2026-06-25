package com.example.dualdb.repository.neo4j;

import com.example.dualdb.model.neo4j.Neo4jCustomer;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Neo4jCustomerRepository extends Neo4jRepository<Neo4jCustomer, Long> {

    Optional<Neo4jCustomer> findByEmail(String email);

    @Query("MATCH (c:Customer) RETURN c ORDER BY c.registrationDate DESC LIMIT 10")
    List<Neo4jCustomer> findRecentCustomers();

    @Query("MATCH (c:Customer {customerId: $customerId}) RETURN c")
    Optional<Neo4jCustomer> findByCustomerId(@Param("customerId") Long customerId);

    @Query("MATCH (c:Customer)-[:PLACED_ORDER]->(:Order)-[:CONTAINS]->(p:Product) " +
           "WHERE p.productId = $productId " +
           "RETURN DISTINCT c")
    List<Neo4jCustomer> findCustomersWhoBoughtProduct(@Param("productId") Long productId);

    @Query("MATCH (c:Customer) " +
           "OPTIONAL MATCH (c)-[:PLACED_ORDER]->(o:Order) " +
           "RETURN c, COUNT(o) as orderCount " +
           "ORDER BY orderCount DESC LIMIT $limit")
    List<Neo4jCustomer> findTopCustomersByOrderCount(@Param("limit") int limit);
}