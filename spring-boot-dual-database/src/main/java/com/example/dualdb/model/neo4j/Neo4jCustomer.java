package com.example.dualdb.model.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Node("Customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Neo4jCustomer {

    @Id
    private Long customerId;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Date dateOfBirth;
    private LocalDateTime registrationDate;
    private LocalDateTime lastLogin;
    private Boolean isActive;
    private String customerType;

    @Relationship(type = "PLACED_ORDER", direction = Relationship.Direction.OUTGOING)
    private List<Neo4jOrder> orders;

    public Neo4jCustomer(com.example.dualdb.model.mysql.Customer mysqlCustomer) {
        this.customerId = mysqlCustomer.getCustomerId();
        this.firstName = mysqlCustomer.getFirstName();
        this.lastName = mysqlCustomer.getLastName();
        this.email = mysqlCustomer.getEmail();
        this.phone = mysqlCustomer.getPhone();
        this.dateOfBirth = mysqlCustomer.getDateOfBirth();
        this.registrationDate = mysqlCustomer.getRegistrationDate();
        this.lastLogin = mysqlCustomer.getLastLogin();
        this.isActive = mysqlCustomer.getIsActive();
        this.customerType = mysqlCustomer.getCustomerType() != null ? 
                mysqlCustomer.getCustomerType().name() : null;
    }
}