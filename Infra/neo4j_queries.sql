// ============================================
// 1. CREATE UNIQUENESS CONSTRAINTS
// ============================================
// Constraints ensure data integrity and improve performance [citation:5]
CREATE CONSTRAINT customer_customer_id IF NOT EXISTS FOR (c:Customer) REQUIRE (c.customer_id) IS UNIQUE;
CREATE CONSTRAINT product_product_id IF NOT EXISTS FOR (p:Product) REQUIRE (p.product_id) IS UNIQUE;
CREATE CONSTRAINT order_order_id IF NOT EXISTS FOR (o:Order) REQUIRE (o.order_id) IS UNIQUE;
CREATE CONSTRAINT address_address_id IF NOT EXISTS FOR (a:Address) REQUIRE (a.address_id) IS UNIQUE;
CREATE CONSTRAINT review_review_id IF NOT EXISTS FOR (r:ProductReview) REQUIRE (r.review_id) IS UNIQUE;
CREATE CONSTRAINT cart_cart_id IF NOT EXISTS FOR (c:ShoppingCart) REQUIRE (c.cart_id) IS UNIQUE;

// ============================================
// 2. CREATE NODES (Entities)
// ============================================

// 2.1 Customers - Each customer is a node with properties
CREATE (c1:Customer {
  customer_id: 1,
  first_name: 'John',
  last_name: 'Smith',
  email: 'john.smith@email.com',
  phone: '555-0101',
  date_of_birth: date('1985-03-15'),
  registration_date: datetime('2026-01-01T10:00:00'),
  customer_type: 'Regular',
  is_active: true
});

CREATE (c2:Customer {
  customer_id: 2,
  first_name: 'Jane',
  last_name: 'Doe',
  email: 'jane.doe@email.com',
  phone: '555-0102',
  date_of_birth: date('1990-07-22'),
  registration_date: datetime('2026-01-02T14:30:00'),
  customer_type: 'Premium',
  is_active: true
});

CREATE (c3:Customer {
  customer_id: 3,
  first_name: 'Robert',
  last_name: 'Johnson',
  email: 'robert.j@email.com',
  phone: '555-0103',
  date_of_birth: date('1978-11-05'),
  registration_date: datetime('2026-01-03T09:15:00'),
  customer_type: 'VIP',
  is_active: true
});

CREATE (c4:Customer {
  customer_id: 4,
  first_name: 'Maria',
  last_name: 'Garcia',
  email: 'maria.g@email.com',
  phone: '555-0104',
  date_of_birth: date('1995-01-30'),
  registration_date: datetime('2026-01-04T11:45:00'),
  customer_type: 'Regular',
  is_active: true
});

CREATE (c5:Customer {
  customer_id: 5,
  first_name: 'David',
  last_name: 'Brown',
  email: 'david.b@email.com',
  phone: '555-0105',
  date_of_birth: date('1982-09-12'),
  registration_date: datetime('2026-01-05T16:20:00'),
  customer_type: 'Premium',
  is_active: true
});

// 2.2 Products - Each product is a node
CREATE (p1:Product {
  product_id: 1,
  product_name: 'Wireless Bluetooth Headphones',
  sku: 'ELEC-001',
  category: 'Electronics',
  sub_category: 'Audio',
  unit_price: 149.99,
  cost_price: 89.99,
  quantity_in_stock: 150,
  reorder_level: 20,
  weight_kg: 0.35,
  is_active: true
});

CREATE (p2:Product {
  product_id: 2,
  product_name: 'Smartphone 5G',
  sku: 'ELEC-002',
  category: 'Electronics',
  sub_category: 'Phones',
  unit_price: 899.99,
  cost_price: 599.99,
  quantity_in_stock: 50,
  reorder_level: 10,
  weight_kg: 0.22,
  is_active: true
});

CREATE (p3:Product {
  product_id: 3,
  product_name: 'Laptop Stand',
  sku: 'OFF-001',
  category: 'Office',
  sub_category: 'Furniture',
  unit_price: 45.99,
  cost_price: 22.50,
  quantity_in_stock: 200,
  reorder_level: 25,
  weight_kg: 0.80,
  is_active: true
});

CREATE (p4:Product {
  product_id: 4,
  product_name: 'USB-C Hub 7-in-1',
  sku: 'ELEC-003',
  category: 'Electronics',
  sub_category: 'Accessories',
  unit_price: 39.99,
  cost_price: 18.99,
  quantity_in_stock: 300,
  reorder_level: 30,
  weight_kg: 0.10,
  is_active: true
});

CREATE (p5:Product {
  product_id: 5,
  product_name: 'Wireless Keyboard',
  sku: 'OFF-002',
  category: 'Office',
  sub_category: 'Keyboards',
  unit_price: 79.99,
  cost_price: 40.00,
  quantity_in_stock: 120,
  reorder_level: 15,
  weight_kg: 0.40,
  is_active: true
});

CREATE (p6:Product {
  product_id: 6,
  product_name: '4K Webcam',
  sku: 'ELEC-004',
  category: 'Electronics',
  sub_category: 'Cameras',
  unit_price: 129.99,
  cost_price: 69.99,
  quantity_in_stock: 80,
  reorder_level: 10,
  weight_kg: 0.15,
  is_active: true
});

CREATE (p7:Product {
  product_id: 7,
  product_name: 'Coffee Mug Warmer',
  sku: 'HOME-001',
  category: 'Home',
  sub_category: 'Kitchen',
  unit_price: 24.99,
  cost_price: 12.50,
  quantity_in_stock: 250,
  reorder_level: 20,
  weight_kg: 0.30,
  is_active: true
});

CREATE (p8:Product {
  product_id: 8,
  product_name: 'Desk LED Lamp',
  sku: 'HOME-002',
  category: 'Home',
  sub_category: 'Lighting',
  unit_price: 34.99,
  cost_price: 18.00,
  quantity_in_stock: 180,
  reorder_level: 15,
  weight_kg: 0.50,
  is_active: true
});

// 2.3 Addresses - Each address is a node
CREATE (a1:Address {
  address_id: 1,
  address_line1: '123 Main St',
  address_line2: 'Apt 4B',
  city: 'New York',
  state: 'NY',
  postal_code: '10001',
  country: 'USA',
  is_default: true,
  address_type: 'Home'
});

CREATE (a2:Address {
  address_id: 2,
  address_line1: '456 Park Ave',
  address_line2: 'Suite 200',
  city: 'New York',
  state: 'NY',
  postal_code: '10022',
  country: 'USA',
  is_default: false,
  address_type: 'Work'
});

CREATE (a3:Address {
  address_id: 3,
  address_line1: '789 Oak Dr',
  address_line2: '',
  city: 'Los Angeles',
  state: 'CA',
  postal_code: '90001',
  country: 'USA',
  is_default: true,
  address_type: 'Home'
});

CREATE (a4:Address {
  address_id: 4,
  address_line1: '321 Palm St',
  address_line2: 'Building 3',
  city: 'Los Angeles',
  state: 'CA',
  postal_code: '90012',
  country: 'USA',
  is_default: false,
  address_type: 'Work'
});

CREATE (a5:Address {
  address_id: 5,
  address_line1: '555 Pine Ln',
  address_line2: '',
  city: 'Chicago',
  state: 'IL',
  postal_code: '60601',
  country: 'USA',
  is_default: true,
  address_type: 'Home'
});

CREATE (a6:Address {
  address_id: 6,
  address_line1: '777 Maple Ave',
  address_line2: 'Apartment 12',
  city: 'Houston',
  state: 'TX',
  postal_code: '77001',
  country: 'USA',
  is_default: true,
  address_type: 'Home'
});

CREATE (a7:Address {
  address_id: 7,
  address_line1: '999 Cedar Rd',
  address_line2: '',
  city: 'Phoenix',
  state: 'AZ',
  postal_code: '85001',
  country: 'USA',
  is_default: true,
  address_type: 'Home'
});

CREATE (a8:Address {
  address_id: 8,
  address_line1: '111 Birch St',
  address_line2: 'Unit 5',
  city: 'Philadelphia',
  state: 'PA',
  postal_code: '19101',
  country: 'USA',
  is_default: true,
  address_type: 'Home'
});

CREATE (a9:Address {
  address_id: 9,
  address_line1: '333 Elm Blvd',
  address_line2: '',
  city: 'San Antonio',
  state: 'TX',
  postal_code: '78201',
  country: 'USA',
  is_default: true,
  address_type: 'Home'
});

CREATE (a10:Address {
  address_id: 10,
  address_line1: '444 Spruce Way',
  address_line2: '',
  city: 'San Diego',
  state: 'CA',
  postal_code: '92101',
  country: 'USA',
  is_default: true,
  address_type: 'Home'
});

// 2.4 Orders - Each order is a node
CREATE (o1:Order {
  order_id: 1,
  order_date: datetime('2026-01-15T10:30:00'),
  order_status: 'Delivered',
  total_amount: 189.98,
  discount_amount: 20.00,
  tax_amount: 13.60,
  shipping_charge: 5.99,
  final_amount: 189.57,
  payment_method: 'Credit Card',
  payment_status: 'Paid',
  notes: ''
});

CREATE (o2:Order {
  order_id: 2,
  order_date: datetime('2026-01-16T14:20:00'),
  order_status: 'Shipped',
  total_amount: 899.99,
  discount_amount: 50.00,
  tax_amount: 68.00,
  shipping_charge: 0.00,
  final_amount: 917.99,
  payment_method: 'PayPal',
  payment_status: 'Paid',
  notes: ''
});

CREATE (o3:Order {
  order_id: 3,
  order_date: datetime('2026-01-17T09:15:00'),
  order_status: 'Processing',
  total_amount: 45.99,
  discount_amount: 0.00,
  tax_amount: 3.22,
  shipping_charge: 4.99,
  final_amount: 54.20,
  payment_method: 'Credit Card',
  payment_status: 'Pending',
  notes: ''
});

CREATE (o4:Order {
  order_id: 4,
  order_date: datetime('2026-01-18T16:45:00'),
  order_status: 'Processing',
  total_amount: 79.99,
  discount_amount: 5.00,
  tax_amount: 5.25,
  shipping_charge: 0.00,
  final_amount: 80.24,
  payment_method: 'Debit Card',
  payment_status: 'Pending',
  notes: ''
});

CREATE (o5:Order {
  order_id: 5,
  order_date: datetime('2026-01-19T11:00:00'),
  order_status: 'Delivered',
  total_amount: 164.98,
  discount_amount: 10.00,
  tax_amount: 10.85,
  shipping_charge: 3.99,
  final_amount: 169.82,
  payment_method: 'Credit Card',
  payment_status: 'Paid',
  notes: ''
});

CREATE (o6:Order {
  order_id: 6,
  order_date: datetime('2026-01-20T08:30:00'),
  order_status: 'Pending',
  total_amount: 24.99,
  discount_amount: 0.00,
  tax_amount: 1.75,
  shipping_charge: 0.00,
  final_amount: 26.74,
  payment_method: 'PayPal',
  payment_status: 'Pending',
  notes: ''
});

CREATE (o7:Order {
  order_id: 7,
  order_date: datetime('2026-01-21T13:10:00'),
  order_status: 'Shipped',
  total_amount: 129.99,
  discount_amount: 15.00,
  tax_amount: 8.05,
  shipping_charge: 0.00,
  final_amount: 123.04,
  payment_method: 'Credit Card',
  payment_status: 'Paid',
  notes: ''
});

CREATE (o8:Order {
  order_id: 8,
  order_date: datetime('2026-01-22T15:00:00'),
  order_status: 'Delivered',
  total_amount: 34.99,
  discount_amount: 0.00,
  tax_amount: 2.45,
  shipping_charge: 0.00,
  final_amount: 37.44,
  payment_method: 'Cash',
  payment_status: 'Paid',
  notes: ''
});

// 2.5 Product Reviews - Each review is a node
CREATE (r1:ProductReview {
  review_id: 1,
  rating: 5,
  review_title: 'Excellent Headphones',
  review_text: 'Great sound quality and battery life',
  is_verified_purchase: true,
  helpful_count: 0,
  created_at: datetime('2026-01-20T10:00:00')
});

CREATE (r2:ProductReview {
  review_id: 2,
  rating: 4,
  review_title: 'Good Phone',
  review_text: 'Fast and responsive, but expensive',
  is_verified_purchase: true,
  helpful_count: 0,
  created_at: datetime('2026-01-21T11:30:00')
});

CREATE (r3:ProductReview {
  review_id: 3,
  rating: 5,
  review_title: 'Perfect for Home Office',
  review_text: 'Sturdy and well-built',
  is_verified_purchase: true,
  helpful_count: 0,
  created_at: datetime('2026-01-22T09:15:00')
});

CREATE (r4:ProductReview {
  review_id: 4,
  rating: 4,
  review_title: 'Very Useful',
  review_text: 'Great for connecting multiple devices',
  is_verified_purchase: true,
  helpful_count: 0,
  created_at: datetime('2026-01-23T14:45:00')
});

CREATE (r5:ProductReview {
  review_id: 5,
  rating: 3,
  review_title: 'Decent Keyboard',
  review_text: 'Works well but not backlit',
  is_verified_purchase: true,
  helpful_count: 0,
  created_at: datetime('2026-01-24T16:20:00')
});

CREATE (r6:ProductReview {
  review_id: 6,
  rating: 5,
  review_title: 'Best purchase this year',
  review_text: 'Highly recommend these headphones',
  is_verified_purchase: true,
  helpful_count: 0,
  created_at: datetime('2026-01-25T08:00:00')
});

CREATE (r7:ProductReview {
  review_id: 7,
  rating: 5,
  review_title: 'Great Video Quality',
  review_text: 'Perfect for streaming and meetings',
  is_verified_purchase: true,
  helpful_count: 0,
  created_at: datetime('2026-01-26T13:00:00')
});

// 2.6 Shopping Cart nodes
CREATE (sc1:ShoppingCart {cart_id: 1});
CREATE (sc2:ShoppingCart {cart_id: 2});
CREATE (sc3:ShoppingCart {cart_id: 3});
CREATE (sc4:ShoppingCart {cart_id: 4});
CREATE (sc5:ShoppingCart {cart_id: 5});

// ============================================
// 3. CREATE RELATIONSHIPS
// ============================================
// This is where the graph model shines - relationships are first-class citizens [citation:5]

// 3.1 Customer to Address relationships
MATCH (c:Customer {customer_id: 1}), (a:Address {address_id: 1})
CREATE (c)-[:HAS_ADDRESS {is_default: true}]->(a);

MATCH (c:Customer {customer_id: 1}), (a:Address {address_id: 2})
CREATE (c)-[:HAS_ADDRESS {is_default: false}]->(a);

MATCH (c:Customer {customer_id: 2}), (a:Address {address_id: 3})
CREATE (c)-[:HAS_ADDRESS {is_default: true}]->(a);

MATCH (c:Customer {customer_id: 2}), (a:Address {address_id: 4})
CREATE (c)-[:HAS_ADDRESS {is_default: false}]->(a);

MATCH (c:Customer {customer_id: 3}), (a:Address {address_id: 5})
CREATE (c)-[:HAS_ADDRESS {is_default: true}]->(a);

MATCH (c:Customer {customer_id: 4}), (a:Address {address_id: 6})
CREATE (c)-[:HAS_ADDRESS {is_default: true}]->(a);

MATCH (c:Customer {customer_id: 5}), (a:Address {address_id: 7})
CREATE (c)-[:HAS_ADDRESS {is_default: true}]->(a);

// 3.2 Customer to Order relationships (PLACED)
MATCH (c:Customer {customer_id: 1}), (o:Order {order_id: 1})
CREATE (c)-[:PLACED_ORDER {order_date: datetime('2026-01-15T10:30:00'), shipping_address_id: 1, billing_address_id: 1}]->(o);

MATCH (c:Customer {customer_id: 2}), (o:Order {order_id: 2})
CREATE (c)-[:PLACED_ORDER {order_date: datetime('2026-01-16T14:20:00'), shipping_address_id: 3, billing_address_id: 3}]->(o);

MATCH (c:Customer {customer_id: 3}), (o:Order {order_id: 3})
CREATE (c)-[:PLACED_ORDER {order_date: datetime('2026-01-17T09:15:00'), shipping_address_id: 5, billing_address_id: 5}]->(o);

MATCH (c:Customer {customer_id: 1}), (o:Order {order_id: 4})
CREATE (c)-[:PLACED_ORDER {order_date: datetime('2026-01-18T16:45:00'), shipping_address_id: 2, billing_address_id: 1}]->(o);

MATCH (c:Customer {customer_id: 4}), (o:Order {order_id: 5})
CREATE (c)-[:PLACED_ORDER {order_date: datetime('2026-01-19T11:00:00'), shipping_address_id: 6, billing_address_id: 6}]->(o);

MATCH (c:Customer {customer_id: 5}), (o:Order {order_id: 6})
CREATE (c)-[:PLACED_ORDER {order_date: datetime('2026-01-20T08:30:00'), shipping_address_id: 7, billing_address_id: 7}]->(o);

MATCH (c:Customer {customer_id: 2}), (o:Order {order_id: 7})
CREATE (c)-[:PLACED_ORDER {order_date: datetime('2026-01-21T13:10:00'), shipping_address_id: 4, billing_address_id: 3}]->(o);

// 3.3 Order to Product relationships (CONTAINS) with relationship properties
MATCH (o:Order {order_id: 1}), (p:Product {product_id: 1})
CREATE (o)-[:CONTAINS {quantity: 1, unit_price_at_order: 149.99, discount_percent: 10.00, tax_percent: 8.00, total_price: 135.00}]->(p);

MATCH (o:Order {order_id: 1}), (p:Product {product_id: 4})
CREATE (o)-[:CONTAINS {quantity: 1, unit_price_at_order: 39.99, discount_percent: 5.00, tax_percent: 8.00, total_price: 37.00}]->(p);

MATCH (o:Order {order_id: 2}), (p:Product {product_id: 2})
CREATE (o)-[:CONTAINS {quantity: 1, unit_price_at_order: 899.99, discount_percent: 5.00, tax_percent: 8.00, total_price: 855.00}]->(p);

MATCH (o:Order {order_id: 3}), (p:Product {product_id: 3})
CREATE (o)-[:CONTAINS {quantity: 1, unit_price_at_order: 45.99, discount_percent: 0.00, tax_percent: 7.00, total_price: 45.99}]->(p);

MATCH (o:Order {order_id: 4}), (p:Product {product_id: 5})
CREATE (o)-[:CONTAINS {quantity: 1, unit_price_at_order: 79.99, discount_percent: 5.00, tax_percent: 7.00, total_price: 76.00}]->(p);

MATCH (o:Order {order_id: 5}), (p:Product {product_id: 1})
CREATE (o)-[:CONTAINS {quantity: 1, unit_price_at_order: 149.99, discount_percent: 0.00, tax_percent: 8.00, total_price: 149.99}]->(p);

MATCH (o:Order {order_id: 5}), (p:Product {product_id: 6})
CREATE (o)-[:CONTAINS {quantity: 1, unit_price_at_order: 129.99, discount_percent: 0.00, tax_percent: 8.00, total_price: 129.99}]->(p);

MATCH (o:Order {order_id: 6}), (p:Product {product_id: 7})
CREATE (o)-[:CONTAINS {quantity: 1, unit_price_at_order: 24.99, discount_percent: 0.00, tax_percent: 7.00, total_price: 24.99}]->(p);

MATCH (o:Order {order_id: 7}), (p:Product {product_id: 6})
CREATE (o)-[:CONTAINS {quantity: 1, unit_price_at_order: 129.99, discount_percent: 10.00, tax_percent: 8.00, total_price: 117.00}]->(p);

MATCH (o:Order {order_id: 8}), (p:Product {product_id: 8})
CREATE (o)-[:CONTAINS {quantity: 1, unit_price_at_order: 34.99, discount_percent: 0.00, tax_percent: 7.00, total_price: 34.99}]->(p);

// 3.4 Customer to Product Review relationships (WROTE_REVIEW)
MATCH (c:Customer {customer_id: 1}), (r:ProductReview {review_id: 1})
CREATE (c)-[:WROTE_REVIEW {review_date: datetime('2026-01-20T10:00:00')}]->(r);

MATCH (c:Customer {customer_id: 2}), (r:ProductReview {review_id: 2})
CREATE (c)-[:WROTE_REVIEW {review_date: datetime('2026-01-21T11:30:00')}]->(r);

MATCH (c:Customer {customer_id: 3}), (r:ProductReview {review_id: 3})
CREATE (c)-[:WROTE_REVIEW {review_date: datetime('2026-01-22T09:15:00')}]->(r);

MATCH (c:Customer {customer_id: 1}), (r:ProductReview {review_id: 4})
CREATE (c)-[:WROTE_REVIEW {review_date: datetime('2026-01-23T14:45:00')}]->(r);

MATCH (c:Customer {customer_id: 4}), (r:ProductReview {review_id: 5})
CREATE (c)-[:WROTE_REVIEW {review_date: datetime('2026-01-24T16:20:00')}]->(r);

MATCH (c:Customer {customer_id: 5}), (r:ProductReview {review_id: 6})
CREATE (c)-[:WROTE_REVIEW {review_date: datetime('2026-01-25T08:00:00')}]->(r);

MATCH (c:Customer {customer_id: 2}), (r:ProductReview {review_id: 7})
CREATE (c)-[:WROTE_REVIEW {review_date: datetime('2026-01-26T13:00:00')}]->(r);

// 3.5 Product Review to Product relationships (REVIEWS)
MATCH (r:ProductReview {review_id: 1}), (p:Product {product_id: 1})
CREATE (r)-[:REVIEWS]->(p);

MATCH (r:ProductReview {review_id: 2}), (p:Product {product_id: 2})
CREATE (r)-[:REVIEWS]->(p);

MATCH (r:ProductReview {review_id: 3}), (p:Product {product_id: 3})
CREATE (r)-[:REVIEWS]->(p);

MATCH (r:ProductReview {review_id: 4}), (p:Product {product_id: 4})
CREATE (r)-[:REVIEWS]->(p);

MATCH (r:ProductReview {review_id: 5}), (p:Product {product_id: 5})
CREATE (r)-[:REVIEWS]->(p);

MATCH (r:ProductReview {review_id: 6}), (p:Product {product_id: 1})
CREATE (r)-[:REVIEWS]->(p);

MATCH (r:ProductReview {review_id: 7}), (p:Product {product_id: 6})
CREATE (r)-[:REVIEWS]->(p);

// 3.6 Customer to Shopping Cart relationships (HAS_CART)
MATCH (c:Customer {customer_id: 1}), (sc:ShoppingCart {cart_id: 1})
CREATE (c)-[:HAS_CART]->(sc);

MATCH (c:Customer {customer_id: 2}), (sc:ShoppingCart {cart_id: 2})
CREATE (c)-[:HAS_CART]->(sc);

MATCH (c:Customer {customer_id: 3}), (sc:ShoppingCart {cart_id: 3})
CREATE (c)-[:HAS_CART]->(sc);

MATCH (c:Customer {customer_id: 4}), (sc:ShoppingCart {cart_id: 4})
CREATE (c)-[:HAS_CART]->(sc);

MATCH (c:Customer {customer_id: 5}), (sc:ShoppingCart {cart_id: 5})
CREATE (c)-[:HAS_CART]->(sc);

// 3.7 Shopping Cart to Product relationships (CONTAINS_ITEM) with quantity
MATCH (sc:ShoppingCart {cart_id: 1}), (p:Product {product_id: 2})
CREATE (sc)-[:CONTAINS_ITEM {quantity: 1}]->(p);

MATCH (sc:ShoppingCart {cart_id: 1}), (p:Product {product_id: 5})
CREATE (sc)-[:CONTAINS_ITEM {quantity: 2}]->(p);

MATCH (sc:ShoppingCart {cart_id: 2}), (p:Product {product_id: 7})
CREATE (sc)-[:CONTAINS_ITEM {quantity: 1}]->(p);

MATCH (sc:ShoppingCart {cart_id: 3}), (p:Product {product_id: 8})
CREATE (sc)-[:CONTAINS_ITEM {quantity: 2}]->(p);

MATCH (sc:ShoppingCart {cart_id: 4}), (p:Product {product_id: 1})
CREATE (sc)-[:CONTAINS_ITEM {quantity: 1}]->(p);

MATCH (sc:ShoppingCart {cart_id: 5}), (p:Product {product_id: 6})
CREATE (sc)-[:CONTAINS_ITEM {quantity: 1}]->(p);



-- ============================================
-- Example queries to try
-- ============================================

// Find all products a customer purchased
MATCH (c:Customer {customer_id: 1})-[:PLACED_ORDER]->(o:Order)-[:CONTAINS]->(p:Product)
RETURN c.first_name, p.product_name, o.order_date;

// Find customers who bought a specific product
MATCH (p:Product {product_id: 1})<-[:CONTAINS]-(o:Order)<-[:PLACED_ORDER]-(c:Customer)
RETURN c.first_name, c.last_name;

// Find product recommendations for a customer (bought by others who bought the same product)
MATCH (c:Customer {customer_id: 1})-[:PLACED_ORDER]->(:Order)-[:CONTAINS]->(p1:Product)<-[:CONTAINS]-(:Order)<-[:PLACED_ORDER]-(other:Customer)
MATCH (other)-[:PLACED_ORDER]->(:Order)-[:CONTAINS]->(recommended:Product)
WHERE recommended <> p1
RETURN DISTINCT recommended.product_name;

// Get customer 360 view - all relationships
MATCH (c:Customer {customer_id: 1})-[r]-(connected)
RETURN c, r, connected;

// Find customers with their addresses and recent orders
MATCH (c:Customer)-[:HAS_ADDRESS]->(a:Address)
OPTIONAL MATCH (c)-[:PLACED_ORDER]->(o:Order)
RETURN c.first_name, c.last_name, a.city, o.order_id, o.order_status
ORDER BY o.order_date DESC;