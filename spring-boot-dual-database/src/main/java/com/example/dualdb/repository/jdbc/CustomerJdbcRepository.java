package com.example.dualdb.repository.jdbc;


import com.example.dualdb.model.mysql.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CustomerJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // RowMapper for Customer
    private static final RowMapper<Customer> CUSTOMER_ROW_MAPPER = (rs, rowNum) -> {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getLong("customer_id"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        
        Timestamp dob = rs.getTimestamp("date_of_birth");
        if (dob != null) {
            customer.setDateOfBirth(new java.util.Date(dob.getTime()));
        }
        
        Timestamp regDate = rs.getTimestamp("registration_date");
        if (regDate != null) {
            customer.setRegistrationDate(regDate.toLocalDateTime());
        }
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            customer.setLastLogin(lastLogin.toLocalDateTime());
        }
        
        customer.setIsActive(rs.getBoolean("is_active"));
        customer.setCustomerType(Customer.CustomerType.valueOf(rs.getString("customer_type")));
        return customer;
    };

    // ============================================
    // CRUD Operations
    // ============================================

    /**
     * Find all customers
     */
    public List<Customer> findAll() {
        log.debug("JDBC: Finding all customers");
        String sql = "SELECT * FROM customers";
        return jdbcTemplate.query(sql, CUSTOMER_ROW_MAPPER);
    }

    /**
     * Find customer by ID
     */
    public Optional<Customer> findById(Long id) {
        log.debug("JDBC: Finding customer by ID: {}", id);
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try {
            Customer customer = jdbcTemplate.queryForObject(sql, CUSTOMER_ROW_MAPPER, id);
            return Optional.ofNullable(customer);
        } catch (Exception e) {
            log.warn("Customer not found with ID: {}", id);
            return Optional.empty();
        }
    }

    /**
     * Find customer by email
     */
    public Optional<Customer> findByEmail(String email) {
        log.debug("JDBC: Finding customer by email: {}", email);
        String sql = "SELECT * FROM customers WHERE email = ?";
        try {
            Customer customer = jdbcTemplate.queryForObject(sql, CUSTOMER_ROW_MAPPER, email);
            return Optional.ofNullable(customer);
        } catch (Exception e) {
            log.warn("Customer not found with email: {}", email);
            return Optional.empty();
        }
    }

    /**
     * Find customers by type
     */
    public List<Customer> findByType(Customer.CustomerType type) {
        log.debug("JDBC: Finding customers by type: {}", type);
        String sql = "SELECT * FROM customers WHERE customer_type = ?";
        return jdbcTemplate.query(sql, CUSTOMER_ROW_MAPPER, type.name());
    }

    /**
     * Find active customers
     */
    public List<Customer> findActiveCustomers() {
        log.debug("JDBC: Finding active customers");
        String sql = "SELECT * FROM customers WHERE is_active = true";
        return jdbcTemplate.query(sql, CUSTOMER_ROW_MAPPER);
    }

    /**
     * Create a new customer
     */
    public Customer save(Customer customer) {
        log.info("JDBC: Creating new customer: {}", customer.getEmail());
        
        String sql = """
            INSERT INTO customers 
            (first_name, last_name, email, phone, date_of_birth, registration_date, is_active, customer_type)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(sql, new String[]{"customer_id"});
            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getLastName());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getPhone());
            
            if (customer.getDateOfBirth() != null) {
                ps.setTimestamp(5, new Timestamp(customer.getDateOfBirth().getTime()));
            } else {
                ps.setTimestamp(5, null);
            }
            
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setBoolean(7, customer.getIsActive() != null ? customer.getIsActive() : true);
            ps.setString(8, customer.getCustomerType() != null ? customer.getCustomerType().name() : "Regular");
            
            return ps;
        }, keyHolder);
        
        customer.setCustomerId(keyHolder.getKey().longValue());
        log.info("Customer created with ID: {}", customer.getCustomerId());
        
        return customer;
    }

    /**
     * Update a customer
     */
    public Customer update(Customer customer) {
        log.info("JDBC: Updating customer: {}", customer.getCustomerId());
        
        String sql = """
            UPDATE customers 
            SET first_name = ?, last_name = ?, email = ?, phone = ?, 
                date_of_birth = ?, is_active = ?, customer_type = ?, last_login = ?
            WHERE customer_id = ?
            """;
        
        int rowsAffected = jdbcTemplate.update(sql,
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getDateOfBirth() != null ? new Timestamp(customer.getDateOfBirth().getTime()) : null,
                customer.getIsActive(),
                customer.getCustomerType() != null ? customer.getCustomerType().name() : "Regular",
                customer.getLastLogin() != null ? Timestamp.valueOf(customer.getLastLogin()) : null,
                customer.getCustomerId()
        );
        
        if (rowsAffected > 0) {
            log.info("Customer updated successfully: {}", customer.getCustomerId());
        } else {
            log.warn("No customer found with ID: {}", customer.getCustomerId());
        }
        
        return customer;
    }

    /**
     * Delete a customer
     */
    public boolean delete(Long id) {
        log.info("JDBC: Deleting customer: {}", id);
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        
        if (rowsAffected > 0) {
            log.info("Customer deleted successfully: {}", id);
            return true;
        } else {
            log.warn("No customer found with ID: {}", id);
            return false;
        }
    }

    // ============================================
    // Batch Operations
    // ============================================

    /**
     * Batch insert customers
     */
    public int[][] batchInsert(List<Customer> customers) {
        log.info("JDBC: Batch inserting {} customers", customers.size());
        
        String sql = """
            INSERT INTO customers 
            (first_name, last_name, email, phone, registration_date, is_active, customer_type)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        
        return jdbcTemplate.batchUpdate(sql, customers, customers.size(), (ps, customer) -> {
            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getLastName());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getPhone());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setBoolean(6, true);
            ps.setString(7, customer.getCustomerType() != null ? customer.getCustomerType().name() : "Regular");
        });
    }

    /**
     * Batch update customer status
     */
    public int[][] batchUpdateStatus(List<Long> customerIds, boolean active) {
        log.info("JDBC: Batch updating status for {} customers", customerIds.size());
        
        String sql = "UPDATE customers SET is_active = ? WHERE customer_id = ?";
        
        return jdbcTemplate.batchUpdate(sql, customerIds, customerIds.size(), (ps, id) -> {
            ps.setBoolean(1, active);
            ps.setLong(2, id);
        });
    }

    // ============================================
    // Named Parameter Queries
    // ============================================

    /**
     * Find customers by type using named parameters
     */
    public List<Customer> findByTypeNamed(String type) {
        log.debug("JDBC Named: Finding customers by type: {}", type);
        String sql = "SELECT * FROM customers WHERE customer_type = :type";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("type", type);
        return namedParameterJdbcTemplate.query(sql, params, CUSTOMER_ROW_MAPPER);
    }

    /**
     * Find customers by multiple criteria
     */
    public List<Customer> findByCriteria(String firstName, String lastName, String email) {
        log.debug("JDBC Named: Finding customers by criteria");
        StringBuilder sql = new StringBuilder("SELECT * FROM customers WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();
        
        if (firstName != null && !firstName.isEmpty()) {
            sql.append(" AND first_name LIKE :firstName");
            params.addValue("firstName", "%" + firstName + "%");
        }
        if (lastName != null && !lastName.isEmpty()) {
            sql.append(" AND last_name LIKE :lastName");
            params.addValue("lastName", "%" + lastName + "%");
        }
        if (email != null && !email.isEmpty()) {
            sql.append(" AND email LIKE :email");
            params.addValue("email", "%" + email + "%");
        }
        
        return namedParameterJdbcTemplate.query(sql.toString(), params, CUSTOMER_ROW_MAPPER);
    }
}
