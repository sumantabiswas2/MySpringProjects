package com.example.dualdb.model.neo4j;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.math.BigDecimal;

@RelationshipProperties
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Neo4jOrderItem {

    @RelationshipId
    private Long id;

    @TargetNode
    private Neo4jProduct product;

    private Integer quantity;
    private BigDecimal unitPriceAtOrder;
    private BigDecimal discountPercent;
    private BigDecimal taxPercent;
    private BigDecimal totalPrice;
}
