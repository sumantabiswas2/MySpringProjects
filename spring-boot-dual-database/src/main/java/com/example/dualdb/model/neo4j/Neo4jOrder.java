package com.example.dualdb.model.neo4j;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Node("Order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Neo4jOrder {

    @Id
    private Long orderId;

    private LocalDateTime orderDate;
    private String orderStatus;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal shippingCharge;
    private BigDecimal finalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String notes;

    @Relationship(type = "CONTAINS", direction = Relationship.Direction.OUTGOING)
    private List<Neo4jOrderItem> items;
}