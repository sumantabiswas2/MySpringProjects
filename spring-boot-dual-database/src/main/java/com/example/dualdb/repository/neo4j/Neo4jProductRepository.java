package com.example.dualdb.repository.neo4j;

import com.example.dualdb.model.neo4j.Neo4jProduct;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Neo4jProductRepository extends Neo4jRepository<Neo4jProduct, Long> {

    @Query("MATCH (p:Product {productId: $productId}) RETURN p")
    Neo4jProduct findByProductId(@Param("productId") Long productId);

    @Query("MATCH (p:Product) WHERE p.category = $category RETURN p")
    List<Neo4jProduct> findByCategory(@Param("category") String category);

    @Query("MATCH (p:Product)<-[:CONTAINS]-(o:Order)<-[:PLACED_ORDER]-(c:Customer) " +
           "WHERE c.customerId = $customerId " +
           "RETURN DISTINCT p")
    List<Neo4jProduct> findProductsPurchasedByCustomer(@Param("customerId") Long customerId);

    @Query("MATCH (p1:Product {productId: $productId})<-[r1:CONTAINS]-(o:Order)-[r2:CONTAINS]->(p2:Product) " +
           "WHERE p1.productId <> p2.productId " +
           "RETURN p2, COUNT(o) as frequency " +
           "ORDER BY frequency DESC LIMIT 5")
    List<Neo4jProduct> findFrequentlyBoughtTogether(@Param("productId") Long productId);
}