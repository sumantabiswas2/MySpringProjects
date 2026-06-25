package com.example.dualdb.model.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.math.BigDecimal;

@Node("Product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Neo4jProduct {

    @Id
    private Long productId;

    private String productName;
    private String sku;
    private String category;
    private String subCategory;
    private BigDecimal unitPrice;
    private BigDecimal costPrice;
    private Integer quantityInStock;
    private Boolean isActive;

    public Neo4jProduct(com.example.dualdb.model.mysql.Product mysqlProduct) {
        this.productId = mysqlProduct.getProductId();
        this.productName = mysqlProduct.getProductName();
        this.sku = mysqlProduct.getSku();
        this.category = mysqlProduct.getCategory();
        this.subCategory = mysqlProduct.getSubCategory();
        this.unitPrice = mysqlProduct.getUnitPrice();
        this.costPrice = mysqlProduct.getCostPrice();
        this.quantityInStock = mysqlProduct.getQuantityInStock();
        this.isActive = mysqlProduct.getIsActive();
    }
}