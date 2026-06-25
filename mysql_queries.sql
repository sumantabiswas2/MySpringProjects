-- ============================================
-- 1. CREATE DATABASE
-- ============================================
CREATE DATABASE IF NOT EXISTS ecommerce_db;
USE ecommerce_db;

-- ============================================
-- 2. CORE TABLES
-- ============================================

-- 2.1 Customers Table
CREATE TABLE customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE,
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_login DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    customer_type ENUM('Regular', 'Premium', 'VIP') DEFAULT 'Regular',
    INDEX idx_email (email),
    INDEX idx_registration_date (registration_date)
);

-- 2.2 Addresses Table (for customer addresses)
CREATE TABLE addresses (
    address_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    address_line1 VARCHAR(200) NOT NULL,
    address_line2 VARCHAR(200),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(50) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(50) DEFAULT 'USA',
    is_default BOOLEAN DEFAULT FALSE,
    address_type ENUM('Home', 'Work', 'Other') DEFAULT 'Home',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    INDEX idx_customer (customer_id)
);

-- 2.3 Products Table
CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(200) NOT NULL,
    product_description TEXT,
    sku VARCHAR(50) UNIQUE NOT NULL,
    category VARCHAR(100) NOT NULL,
    sub_category VARCHAR(100),
    unit_price DECIMAL(10, 2) NOT NULL,
    cost_price DECIMAL(10, 2),
    quantity_in_stock INT DEFAULT 0,
    reorder_level INT DEFAULT 10,
    weight_kg DECIMAL(8, 2),
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_sku (sku),
    INDEX idx_price (unit_price)
);

-- 2.4 Orders Table
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    order_status ENUM('Pending', 'Processing', 'Shipped', 'Delivered', 'Cancelled', 'Refunded') DEFAULT 'Pending',
    total_amount DECIMAL(12, 2) NOT NULL,
    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
    tax_amount DECIMAL(10, 2) DEFAULT 0.00,
    shipping_charge DECIMAL(8, 2) DEFAULT 0.00,
    final_amount DECIMAL(12, 2) NOT NULL,
    shipping_address_id INT NOT NULL,
    billing_address_id INT NOT NULL,
    payment_method VARCHAR(50),
    payment_status ENUM('Pending', 'Paid', 'Failed', 'Refunded') DEFAULT 'Pending',
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (shipping_address_id) REFERENCES addresses(address_id),
    FOREIGN KEY (billing_address_id) REFERENCES addresses(address_id),
    INDEX idx_customer (customer_id),
    INDEX idx_order_date (order_date),
    INDEX idx_status (order_status)
);

-- 2.5 Order Items Table
CREATE TABLE order_items (
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price_at_order DECIMAL(10, 2) NOT NULL,
    discount_percent DECIMAL(5, 2) DEFAULT 0.00,
    tax_percent DECIMAL(5, 2) DEFAULT 0.00,
    total_price DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    INDEX idx_order (order_id),
    INDEX idx_product (product_id)
);

-- 2.6 Payments Table
CREATE TABLE payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    amount DECIMAL(12, 2) NOT NULL,
    payment_method ENUM('Credit Card', 'Debit Card', 'PayPal', 'Bank Transfer', 'Cash') NOT NULL,
    transaction_id VARCHAR(100) UNIQUE,
    payment_status ENUM('Pending', 'Completed', 'Failed', 'Refunded') DEFAULT 'Pending',
    card_last_four CHAR(4),
    expiry_date DATE,
    billing_address_id INT,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (billing_address_id) REFERENCES addresses(address_id),
    INDEX idx_order (order_id),
    INDEX idx_transaction (transaction_id)
);

-- 2.7 Shipments Table
CREATE TABLE shipments (
    shipment_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    carrier VARCHAR(50) NOT NULL,
    tracking_number VARCHAR(100),
    shipped_date DATETIME,
    estimated_delivery_date DATE,
    actual_delivery_date DATE,
    shipment_status ENUM('Preparing', 'Shipped', 'In Transit', 'Delivered', 'Failed') DEFAULT 'Preparing',
    shipping_cost DECIMAL(8, 2),
    notes TEXT,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    INDEX idx_order (order_id),
    INDEX idx_tracking (tracking_number)
);

-- 2.8 Product Reviews Table
CREATE TABLE product_reviews (
    review_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    customer_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    review_title VARCHAR(200),
    review_text TEXT,
    is_verified_purchase BOOLEAN DEFAULT FALSE,
    helpful_count INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    INDEX idx_product (product_id),
    INDEX idx_customer (customer_id),
    INDEX idx_rating (rating),
    UNIQUE KEY unique_review (product_id, customer_id) -- One review per customer per product
);

-- 2.9 Shopping Cart Table
CREATE TABLE shopping_cart (
    cart_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    session_id VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    INDEX idx_customer (customer_id)
);

-- 2.10 Cart Items Table
CREATE TABLE cart_items (
    cart_item_id INT PRIMARY KEY AUTO_INCREMENT,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    added_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_id) REFERENCES shopping_cart(cart_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    INDEX idx_cart (cart_id),
    INDEX idx_product (product_id)
);

-- ============================================
-- 3. INSERT SAMPLE DATA
-- ============================================

-- 3.1 Insert Customers
INSERT INTO customers (first_name, last_name, email, phone, date_of_birth, customer_type) VALUES
('John', 'Smith', 'john.smith@email.com', '555-0101', '1985-03-15', 'Regular'),
('Jane', 'Doe', 'jane.doe@email.com', '555-0102', '1990-07-22', 'Premium'),
('Robert', 'Johnson', 'robert.j@email.com', '555-0103', '1978-11-05', 'VIP'),
('Maria', 'Garcia', 'maria.g@email.com', '555-0104', '1995-01-30', 'Regular'),
('David', 'Brown', 'david.b@email.com', '555-0105', '1982-09-12', 'Premium'),
('Sarah', 'Davis', 'sarah.d@email.com', '555-0106', '1988-06-18', 'Regular'),
('Michael', 'Wilson', 'michael.w@email.com', '555-0107', '1975-04-25', 'VIP'),
('Emily', 'Martinez', 'emily.m@email.com', '555-0108', '1992-12-08', 'Regular');

-- 3.2 Insert Addresses
INSERT INTO addresses (customer_id, address_line1, address_line2, city, state, postal_code, is_default, address_type) VALUES
(1, '123 Main St', 'Apt 4B', 'New York', 'NY', '10001', TRUE, 'Home'),
(1, '456 Park Ave', 'Suite 200', 'New York', 'NY', '10022', FALSE, 'Work'),
(2, '789 Oak Dr', '', 'Los Angeles', 'CA', '90001', TRUE, 'Home'),
(2, '321 Palm St', 'Building 3', 'Los Angeles', 'CA', '90012', FALSE, 'Work'),
(3, '555 Pine Ln', '', 'Chicago', 'IL', '60601', TRUE, 'Home'),
(4, '777 Maple Ave', 'Apartment 12', 'Houston', 'TX', '77001', TRUE, 'Home'),
(5, '999 Cedar Rd', '', 'Phoenix', 'AZ', '85001', TRUE, 'Home'),
(6, '111 Birch St', 'Unit 5', 'Philadelphia', 'PA', '19101', TRUE, 'Home'),
(7, '333 Elm Blvd', '', 'San Antonio', 'TX', '78201', TRUE, 'Home'),
(8, '444 Spruce Way', '', 'San Diego', 'CA', '92101', TRUE, 'Home');

-- 3.3 Insert Products
INSERT INTO products (product_name, product_description, sku, category, sub_category, unit_price, cost_price, quantity_in_stock, reorder_level, weight_kg) VALUES
('Wireless Bluetooth Headphones', 'Premium noise-canceling headphones with 30hr battery', 'ELEC-001', 'Electronics', 'Audio', 149.99, 89.99, 150, 20, 0.35),
('Smartphone 5G', 'Latest 5G smartphone with 6.7" display', 'ELEC-002', 'Electronics', 'Phones', 899.99, 599.99, 50, 10, 0.22),
('Laptop Stand', 'Ergonomic aluminum laptop stand', 'OFF-001', 'Office', 'Furniture', 45.99, 22.50, 200, 25, 0.80),
('USB-C Hub 7-in-1', 'Multiport adapter with HDMI, USB, SD card', 'ELEC-003', 'Electronics', 'Accessories', 39.99, 18.99, 300, 30, 0.10),
('Wireless Keyboard', 'Slim Bluetooth keyboard with backlight', 'OFF-002', 'Office', 'Keyboards', 79.99, 40.00, 120, 15, 0.40),
('4K Webcam', 'Ultra HD webcam with built-in microphone', 'ELEC-004', 'Electronics', 'Cameras', 129.99, 69.99, 80, 10, 0.15),
('Coffee Mug Warmer', 'Temperature control mug warmer', 'HOME-001', 'Home', 'Kitchen', 24.99, 12.50, 250, 20, 0.30),
('Desk LED Lamp', 'Adjustable brightness desk lamp', 'HOME-002', 'Home', 'Lighting', 34.99, 18.00, 180, 15, 0.50);

-- 3.4 Insert Orders
INSERT INTO orders (customer_id, order_date, order_status, total_amount, discount_amount, tax_amount, shipping_charge, final_amount, shipping_address_id, billing_address_id, payment_method, payment_status) VALUES
(1, '2026-01-15 10:30:00', 'Delivered', 189.98, 20.00, 13.60, 5.99, 189.57, 1, 1, 'Credit Card', 'Paid'),
(2, '2026-01-16 14:20:00', 'Shipped', 899.99, 50.00, 68.00, 0.00, 917.99, 3, 3, 'PayPal', 'Paid'),
(3, '2026-01-17 09:15:00', 'Processing', 45.99, 0.00, 3.22, 4.99, 54.20, 5, 5, 'Credit Card', 'Pending'),
(1, '2026-01-18 16:45:00', 'Processing', 79.99, 5.00, 5.25, 0.00, 80.24, 2, 1, 'Debit Card', 'Pending'),
(4, '2026-01-19 11:00:00', 'Delivered', 164.98, 10.00, 10.85, 3.99, 169.82, 6, 6, 'Credit Card', 'Paid'),
(5, '2026-01-20 08:30:00', 'Pending', 24.99, 0.00, 1.75, 0.00, 26.74, 7, 7, 'PayPal', 'Pending'),
(2, '2026-01-21 13:10:00', 'Shipped', 129.99, 15.00, 8.05, 0.00, 123.04, 4, 3, 'Credit Card', 'Paid'),
(6, '2026-01-22 15:00:00', 'Delivered', 34.99, 0.00, 2.45, 0.00, 37.44, 8, 8, 'Cash', 'Paid');

-- 3.5 Insert Order Items
INSERT INTO order_items (order_id, product_id, quantity, unit_price_at_order, discount_percent, tax_percent, total_price) VALUES
(1, 1, 1, 149.99, 10.00, 8.00, 135.00),
(1, 4, 1, 39.99, 5.00, 8.00, 37.00),
(2, 2, 1, 899.99, 5.00, 8.00, 855.00),
(3, 3, 1, 45.99, 0.00, 7.00, 45.99),
(4, 5, 1, 79.99, 5.00, 7.00, 76.00),
(5, 1, 1, 149.99, 0.00, 8.00, 149.99),
(5, 6, 1, 129.99, 0.00, 8.00, 129.99),
(6, 7, 1, 24.99, 0.00, 7.00, 24.99),
(7, 6, 1, 129.99, 10.00, 8.00, 117.00),
(8, 8, 1, 34.99, 0.00, 7.00, 34.99);

-- 3.6 Insert Payments
INSERT INTO payments (order_id, amount, payment_method, transaction_id, payment_status, card_last_four) VALUES
(1, 189.57, 'Credit Card', 'TXN-001-2026', 'Completed', '1234'),
(2, 917.99, 'PayPal', 'TXN-002-2026', 'Completed', NULL),
(3, 54.20, 'Credit Card', 'TXN-003-2026', 'Pending', '5678'),
(4, 80.24, 'Debit Card', 'TXN-004-2026', 'Pending', '9012'),
(5, 169.82, 'Credit Card', 'TXN-005-2026', 'Completed', '3456'),
(7, 123.04, 'Credit Card', 'TXN-007-2026', 'Completed', '7890'),
(8, 37.44, 'Cash', 'TXN-008-2026', 'Completed', NULL);

-- 3.7 Insert Shipments
INSERT INTO shipments (order_id, carrier, tracking_number, shipped_date, estimated_delivery_date, actual_delivery_date, shipment_status) VALUES
(1, 'FedEx', 'FEDEX-001-2026', '2026-01-16 08:00:00', '2026-01-20', '2026-01-19', 'Delivered'),
(2, 'UPS', 'UPS-002-2026', '2026-01-17 10:00:00', '2026-01-22', NULL, 'Shipped'),
(5, 'DHL', 'DHL-005-2026', '2026-01-20 09:00:00', '2026-01-24', '2026-01-23', 'Delivered'),
(7, 'FedEx', 'FEDEX-007-2026', '2026-01-22 14:00:00', '2026-01-27', NULL, 'Shipped'),
(8, 'USPS', 'USPS-008-2026', '2026-01-23 07:00:00', '2026-01-26', '2026-01-25', 'Delivered');

-- 3.8 Insert Reviews
INSERT INTO product_reviews (product_id, customer_id, rating, review_title, review_text, is_verified_purchase) VALUES
(1, 1, 5, 'Excellent Headphones', 'Great sound quality and battery life', TRUE),
(2, 2, 4, 'Good Phone', 'Fast and responsive, but expensive', TRUE),
(3, 3, 5, 'Perfect for Home Office', 'Sturdy and well-built', TRUE),
(4, 1, 4, 'Very Useful', 'Great for connecting multiple devices', TRUE),
(5, 4, 3, 'Decent Keyboard', 'Works well but not backlit', TRUE),
(1, 5, 5, 'Best purchase this year', 'Highly recommend these headphones', TRUE),
(6, 2, 5, 'Great Video Quality', 'Perfect for streaming and meetings', TRUE);

-- 3.9 Insert Shopping Cart
INSERT INTO shopping_cart (customer_id) VALUES
(1), (2), (3), (4), (5);

-- 3.10 Insert Cart Items
INSERT INTO cart_items (cart_id, product_id, quantity) VALUES
(1, 2, 1),
(1, 5, 2),
(2, 7, 1),
(3, 8, 2),
(4, 1, 1),
(5, 6, 1);


-- ============================================
-- Exercise 1: Basic Queries (CRUD)
-- ============================================

-- 1.1 Get all active customers
SELECT * FROM customers WHERE is_active = TRUE;

-- 1.2 Get customer details with their addresses
SELECT c.customer_id, c.first_name, c.last_name, c.email, 
       a.address_line1, a.city, a.state, a.postal_code
FROM customers c
JOIN addresses a ON c.customer_id = a.customer_id
WHERE a.is_default = TRUE;

-- 1.3 Get all products in stock sorted by price (high to low)
SELECT product_name, unit_price, quantity_in_stock 
FROM products 
WHERE quantity_in_stock > 0 
ORDER BY unit_price DESC;

-- 1.4 Update a product price
UPDATE products 
SET unit_price = 55.99 
WHERE sku = 'OFF-001';



-- ============================================
-- Exercise 2: Joins and Relationships
-- ============================================
-- 2.1 Get order details with customer names
SELECT o.order_id, o.order_date, o.order_status, o.final_amount,
       CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
       c.email
FROM orders o
JOIN customers c ON o.customer_id = c.customer_id;

-- 2.2 Get order items with product names
SELECT oi.order_id, p.product_name, oi.quantity, 
       oi.unit_price_at_order, oi.total_price
FROM order_items oi
JOIN products p ON oi.product_id = p.product_id
WHERE oi.order_id = 1;

-- 2.3 Get customer order summary with number of orders and total spent
SELECT c.customer_id, c.first_name, c.last_name,
       COUNT(o.order_id) AS total_orders,
       COALESCE(SUM(o.final_amount), 0) AS total_spent
FROM customers c
LEFT JOIN orders o ON c.customer_id = o.customer_id
WHERE o.order_status IN ('Delivered', 'Shipped')
GROUP BY c.customer_id;

-- 2.4 Get top 5 best-selling products
SELECT p.product_id, p.product_name, 
       SUM(oi.quantity) AS total_quantity_sold,
       COUNT(DISTINCT oi.order_id) AS number_of_orders
FROM products p
JOIN order_items oi ON p.product_id = oi.product_id
JOIN orders o ON oi.order_id = o.order_id
WHERE o.order_status NOT IN ('Cancelled')
GROUP BY p.product_id
ORDER BY total_quantity_sold DESC
LIMIT 5;


-- ============================================
-- Exercise 3: Subqueries and Advanced Filters
-- ============================================

-- 3.1 Get customers who spent more than average
SELECT c.customer_id, c.first_name, c.last_name, 
       SUM(o.final_amount) AS total_spent
FROM customers c
JOIN orders o ON c.customer_id = o.customer_id
WHERE o.order_status = 'Delivered'
GROUP BY c.customer_id
HAVING total_spent > (
    SELECT AVG(total_spent) 
    FROM (
        SELECT SUM(final_amount) AS total_spent
        FROM orders
        WHERE order_status = 'Delivered'
        GROUP BY customer_id
    ) AS avg_spent
);

-- 3.2 Get products never ordered
SELECT * FROM products
WHERE product_id NOT IN (
    SELECT DISTINCT product_id 
    FROM order_items
);

-- 3.3 Get customers who placed orders in the last 7 days
SELECT DISTINCT c.*
FROM customers c
JOIN orders o ON c.customer_id = o.customer_id
WHERE o.order_date >= DATE_SUB(NOW(), INTERVAL 7 DAY);




-- ============================================
-- Exercise 4: Aggregation and Group By
-- ============================================

-- 4.1 Monthly revenue report
SELECT DATE_FORMAT(order_date, '%Y-%m') AS month,
       COUNT(order_id) AS total_orders,
       SUM(final_amount) AS total_revenue,
       AVG(final_amount) AS avg_order_value
FROM orders
WHERE order_status IN ('Delivered', 'Shipped')
GROUP BY DATE_FORMAT(order_date, '%Y-%m')
ORDER BY month DESC;

-- 4.2 Category-wise sales summary
SELECT p.category,
       COUNT(DISTINCT oi.order_id) AS number_of_orders,
       SUM(oi.quantity) AS total_quantity_sold,
       SUM(oi.total_price) AS total_revenue
FROM products p
JOIN order_items oi ON p.product_id = oi.product_id
JOIN orders o ON oi.order_id = o.order_id
WHERE o.order_status NOT IN ('Cancelled')
GROUP BY p.category
ORDER BY total_revenue DESC;

-- 4.3 Customer segmentation by spending
SELECT 
    CASE 
        WHEN SUM(o.final_amount) > 500 THEN 'High Spender'
        WHEN SUM(o.final_amount) > 200 THEN 'Medium Spender'
        WHEN SUM(o.final_amount) > 0 THEN 'Low Spender'
        ELSE 'No Purchases'
    END AS customer_segment,
    COUNT(c.customer_id) AS customer_count,
    AVG(COALESCE(SUM(o.final_amount), 0)) AS avg_spend
FROM customers c
LEFT JOIN orders o ON c.customer_id = o.customer_id AND o.order_status = 'Delivered'
GROUP BY customer_segment;



-- ============================================
-- Exercise 5: Window Functions
-- ============================================

-- 5.1 Rank customers by spending
SELECT c.customer_id, c.first_name, c.last_name,
       COALESCE(SUM(o.final_amount), 0) AS total_spent,
       RANK() OVER (ORDER BY COALESCE(SUM(o.final_amount), 0) DESC) AS spending_rank,
       NTILE(4) OVER (ORDER BY COALESCE(SUM(o.final_amount), 0) DESC) AS spending_quartile
FROM customers c
LEFT JOIN orders o ON c.customer_id = o.customer_id AND o.order_status = 'Delivered'
GROUP BY c.customer_id;

-- 5.2 Running total of customer purchases (for a specific customer)
SELECT order_id, order_date, final_amount,
       SUM(final_amount) OVER (ORDER BY order_date) AS running_total
FROM orders
WHERE customer_id = 1 AND order_status = 'Delivered';




-- ============================================
-- Exercise 6: Views and Procedures
-- ============================================

-- 6.1 Create a view for customer 360
CREATE VIEW customer_360 AS
SELECT c.*,
       COUNT(DISTINCT o.order_id) AS total_orders,
       COALESCE(SUM(o.final_amount), 0) AS total_spent,
       COALESCE(AVG(o.final_amount), 0) AS avg_order_value,
       MAX(o.order_date) AS last_order_date,
       COUNT(DISTINCT pr.review_id) AS total_reviews,
       COALESCE(AVG(pr.rating), 0) AS avg_rating
FROM customers c
LEFT JOIN orders o ON c.customer_id = o.customer_id AND o.order_status IN ('Delivered', 'Shipped')
LEFT JOIN product_reviews pr ON c.customer_id = pr.customer_id
GROUP BY c.customer_id;

-- Use the view
SELECT * FROM customer_360 WHERE total_spent > 200;

-- 6.2 Create a stored procedure for monthly sales report
DELIMITER $$
CREATE PROCEDURE monthly_sales_report(IN report_month DATE)
BEGIN
    SELECT DATE_FORMAT(order_date, '%Y-%m') AS month,
           COUNT(order_id) AS total_orders,
           SUM(final_amount) AS total_revenue,
           COUNT(DISTINCT customer_id) AS unique_customers
    FROM orders
    WHERE DATE_FORMAT(order_date, '%Y-%m') = DATE_FORMAT(report_month, '%Y-%m')
    AND order_status IN ('Delivered', 'Shipped');
END$$
DELIMITER ;

-- Call the procedure
CALL monthly_sales_report('2026-01-01');

-- 6.3 Create a function to calculate discount eligibility
DELIMITER $$
CREATE FUNCTION is_eligible_for_discount(cust_id INT)
RETURNS BOOLEAN
DETERMINISTIC
BEGIN
    DECLARE total_spent DECIMAL(12,2);
    DECLARE order_count INT;
    
    SELECT COALESCE(SUM(final_amount), 0), COUNT(order_id)
    INTO total_spent, order_count
    FROM orders
    WHERE customer_id = cust_id AND order_status = 'Delivered';
    
    RETURN total_spent > 1000 OR order_count > 10;
END$$
DELIMITER ;

-- Test the function
SELECT customer_id, first_name, last_name, 
       is_eligible_for_discount(customer_id) AS eligible_for_discount
FROM customers;



-- ============================================
-- Exercise 7: Triggers and Events
-- ============================================

-- 7.1 Create audit log table
CREATE TABLE audit_log (
    audit_id INT PRIMARY KEY AUTO_INCREMENT,
    table_name VARCHAR(50),
    action_type VARCHAR(20),
    record_id INT,
    old_value JSON,
    new_value JSON,
    changed_by VARCHAR(100),
    changed_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 7.2 Create trigger for order status changes
DELIMITER $$
CREATE TRIGGER order_status_audit
AFTER UPDATE ON orders
FOR EACH ROW
BEGIN
    IF OLD.order_status != NEW.order_status THEN
        INSERT INTO audit_log (table_name, action_type, record_id, old_value, new_value, changed_by)
        VALUES (
            'orders',
            'UPDATE_STATUS',
            NEW.order_id,
            JSON_OBJECT('status', OLD.order_status),
            JSON_OBJECT('status', NEW.order_status),
            USER()
        );
    END IF;
END$$
DELIMITER ;

-- 7.3 Create trigger to update product stock
DELIMITER $$
CREATE TRIGGER update_stock_after_order
AFTER INSERT ON order_items
FOR EACH ROW
BEGIN
    UPDATE products
    SET quantity_in_stock = quantity_in_stock - NEW.quantity
    WHERE product_id = NEW.product_id;
END$$
DELIMITER ;
