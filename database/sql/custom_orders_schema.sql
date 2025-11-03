-- ========================================
-- CUSTOM ORDERS SYSTEM - DATABASE SCHEMA
-- Customer Accounts & Custom Order Management
-- ========================================

USE kiosk_db;

-- ========================================
-- CUSTOMER ACCOUNT MANAGEMENT
-- ========================================

-- Customer accounts table
CREATE TABLE IF NOT EXISTS customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL UNIQUE COMMENT 'Format: CUST-YYYYMMDD-XXXX',
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    date_of_birth DATE,
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state_province VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100) DEFAULT 'Philippines',
    customer_type ENUM('REGULAR', 'VIP', 'WHOLESALE') DEFAULT 'REGULAR',
    loyalty_points INT DEFAULT 0,
    total_orders INT DEFAULT 0,
    total_spent DECIMAL(10, 2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    is_email_verified BOOLEAN DEFAULT FALSE,
    email_verification_token VARCHAR(255),
    password_reset_token VARCHAR(255),
    password_reset_expires TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer_id (customer_id),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_customer_type (customer_type),
    INDEX idx_active (is_active),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Customer addresses for multiple delivery locations
CREATE TABLE IF NOT EXISTS customer_addresses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL,
    address_label VARCHAR(50) NOT NULL COMMENT 'e.g., Home, Office, etc.',
    recipient_name VARCHAR(200),
    phone VARCHAR(20),
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state_province VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100) DEFAULT 'Philippines',
    is_default BOOLEAN DEFAULT FALSE,
    delivery_instructions TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    INDEX idx_customer_id (customer_id),
    INDEX idx_is_default (is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- CUSTOM ORDER CATALOG & TEMPLATES
-- ========================================

-- Custom order categories (e.g., Custom Cakes, Bulk Orders, etc.)
CREATE TABLE IF NOT EXISTS custom_order_categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_code VARCHAR(50) NOT NULL UNIQUE,
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
    icon_path VARCHAR(500),
    min_order_lead_time_days INT DEFAULT 3 COMMENT 'Minimum days notice required',
    is_active BOOLEAN DEFAULT TRUE,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category_code (category_code),
    INDEX idx_active (is_active),
    INDEX idx_display_order (display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Base products available for customization
CREATE TABLE IF NOT EXISTS custom_order_base_products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL UNIQUE,
    category_code VARCHAR(50) NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    description TEXT,
    base_price DECIMAL(10, 2) NOT NULL,
    price_per_serving DECIMAL(10, 2) COMMENT 'Price per person/serving',
    min_servings INT DEFAULT 1,
    max_servings INT DEFAULT 200,
    preparation_time_hours INT DEFAULT 24,
    image_path VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_code) REFERENCES custom_order_categories(category_code) ON DELETE CASCADE,
    INDEX idx_product_code (product_code),
    INDEX idx_category_code (category_code),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add-on categories (Flavors, Fillings, Decorations, etc.)
CREATE TABLE IF NOT EXISTS addon_categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_code VARCHAR(50) NOT NULL UNIQUE,
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
    selection_type ENUM('SINGLE', 'MULTIPLE', 'OPTIONAL') DEFAULT 'SINGLE',
    max_selections INT DEFAULT 1,
    is_required BOOLEAN DEFAULT FALSE,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category_code (category_code),
    INDEX idx_display_order (display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Individual add-ons (specific flavors, decorations, etc.)
CREATE TABLE IF NOT EXISTS addons (
    id INT AUTO_INCREMENT PRIMARY KEY,
    addon_code VARCHAR(50) NOT NULL UNIQUE,
    category_code VARCHAR(50) NOT NULL,
    addon_name VARCHAR(200) NOT NULL,
    description TEXT,
    price_modifier DECIMAL(10, 2) DEFAULT 0.00 COMMENT 'Additional cost for this addon',
    price_type ENUM('FLAT', 'PERCENTAGE', 'PER_SERVING') DEFAULT 'FLAT',
    image_path VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    is_premium BOOLEAN DEFAULT FALSE,
    stock_limited BOOLEAN DEFAULT FALSE,
    stock_quantity INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_code) REFERENCES addon_categories(category_code) ON DELETE CASCADE,
    INDEX idx_addon_code (addon_code),
    INDEX idx_category_code (category_code),
    INDEX idx_active (is_active),
    INDEX idx_premium (is_premium)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Product-addon compatibility (which addons work with which products)
CREATE TABLE IF NOT EXISTS product_addon_compatibility (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL,
    addon_category_code VARCHAR(50) NOT NULL,
    is_required BOOLEAN DEFAULT FALSE,
    display_order INT DEFAULT 0,
    FOREIGN KEY (product_code) REFERENCES custom_order_base_products(product_code) ON DELETE CASCADE,
    FOREIGN KEY (addon_category_code) REFERENCES addon_categories(category_code) ON DELETE CASCADE,
    INDEX idx_product_code (product_code),
    INDEX idx_addon_category (addon_category_code),
    UNIQUE KEY unique_product_addon (product_code, addon_category_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- CUSTOM ORDERS
-- ========================================

-- Main custom orders table
CREATE TABLE IF NOT EXISTS custom_orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE COMMENT 'Format: CO-YYYYMMDD-XXXX',
    customer_id VARCHAR(50) NOT NULL,
    product_code VARCHAR(50) NOT NULL,
    order_type VARCHAR(50) NOT NULL COMMENT 'CUSTOM_CAKE, BULK_ORDER, etc.',
    
    -- Order specifications
    servings INT NOT NULL,
    message_on_item TEXT COMMENT 'Message to write on cake/item',
    special_instructions TEXT,
    
    -- Pricing
    base_price DECIMAL(10, 2) NOT NULL,
    addons_total DECIMAL(10, 2) DEFAULT 0.00,
    subtotal DECIMAL(10, 2) NOT NULL,
    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
    tax_amount DECIMAL(10, 2) DEFAULT 0.00,
    delivery_fee DECIMAL(10, 2) DEFAULT 0.00,
    total_amount DECIMAL(10, 2) NOT NULL,
    
    -- Deposit and payment
    deposit_required DECIMAL(10, 2) DEFAULT 0.00,
    deposit_paid DECIMAL(10, 2) DEFAULT 0.00,
    deposit_payment_method VARCHAR(50),
    deposit_paid_at TIMESTAMP NULL,
    balance_due DECIMAL(10, 2) DEFAULT 0.00,
    payment_status ENUM('PENDING', 'DEPOSIT_PAID', 'FULLY_PAID', 'REFUNDED') DEFAULT 'PENDING',
    
    -- Delivery/Pickup
    fulfillment_type ENUM('PICKUP', 'DELIVERY') NOT NULL DEFAULT 'PICKUP',
    pickup_datetime DATETIME NOT NULL,
    delivery_address_id INT,
    delivery_datetime DATETIME,
    
    -- Order status
    order_status ENUM('PENDING', 'CONFIRMED', 'IN_PRODUCTION', 'READY', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    admin_notes TEXT,
    cancellation_reason TEXT,
    cancelled_at TIMESTAMP NULL,
    cancelled_by VARCHAR(100),
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Assigned staff
    assigned_baker VARCHAR(100),
    assigned_decorator VARCHAR(100),
    
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (product_code) REFERENCES custom_order_base_products(product_code),
    FOREIGN KEY (delivery_address_id) REFERENCES customer_addresses(id) ON DELETE SET NULL,
    
    INDEX idx_order_number (order_number),
    INDEX idx_customer_id (customer_id),
    INDEX idx_product_code (product_code),
    INDEX idx_order_status (order_status),
    INDEX idx_payment_status (payment_status),
    INDEX idx_fulfillment_type (fulfillment_type),
    INDEX idx_pickup_datetime (pickup_datetime),
    INDEX idx_created_at (created_at),
    INDEX idx_confirmed_at (confirmed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Custom order add-ons (selected add-ons for each order)
CREATE TABLE IF NOT EXISTS custom_order_addons (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL,
    addon_code VARCHAR(50) NOT NULL,
    addon_category VARCHAR(50) NOT NULL,
    quantity INT DEFAULT 1,
    price_modifier DECIMAL(10, 2) NOT NULL,
    total_addon_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_number) REFERENCES custom_orders(order_number) ON DELETE CASCADE,
    FOREIGN KEY (addon_code) REFERENCES addons(addon_code),
    INDEX idx_order_number (order_number),
    INDEX idx_addon_code (addon_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Custom order attachments (reference images, design files, etc.)
CREATE TABLE IF NOT EXISTS custom_order_attachments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(50),
    file_size_kb INT,
    description TEXT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_number) REFERENCES custom_orders(order_number) ON DELETE CASCADE,
    INDEX idx_order_number (order_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Custom order status history
CREATE TABLE IF NOT EXISTS custom_order_status_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL,
    old_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    notes TEXT,
    changed_by VARCHAR(100),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_number) REFERENCES custom_orders(order_number) ON DELETE CASCADE,
    INDEX idx_order_number (order_number),
    INDEX idx_changed_at (changed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- CUSTOMER REVIEWS & FEEDBACK
-- ========================================

-- Order reviews
CREATE TABLE IF NOT EXISTS custom_order_reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL,
    customer_id VARCHAR(50) NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review_title VARCHAR(200),
    review_text TEXT,
    would_recommend BOOLEAN DEFAULT TRUE,
    image_path VARCHAR(500),
    admin_response TEXT,
    responded_at TIMESTAMP NULL,
    is_approved BOOLEAN DEFAULT FALSE,
    is_featured BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_number) REFERENCES custom_orders(order_number) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    INDEX idx_order_number (order_number),
    INDEX idx_customer_id (customer_id),
    INDEX idx_rating (rating),
    INDEX idx_approved (is_approved),
    INDEX idx_featured (is_featured)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- SAMPLE DATA FOR TESTING
-- ========================================

-- Insert custom order categories
INSERT INTO custom_order_categories (category_code, category_name, description, min_order_lead_time_days) VALUES
('CUSTOM_CAKE', 'Custom Cakes', 'Personalized cakes for any occasion', 3),
('BULK_PASTRIES', 'Bulk Pastries', 'Large quantity pastries for events', 2),
('BULK_BREADS', 'Bulk Breads', 'Wholesale bread orders', 1),
('CUSTOM_DESSERT', 'Custom Desserts', 'Specialty dessert platters', 2),
('EVENT_CATERING', 'Event Catering', 'Full bakery catering service', 7)
ON DUPLICATE KEY UPDATE category_name=category_name;

-- Insert base products
INSERT INTO custom_order_base_products (product_code, category_code, product_name, description, base_price, price_per_serving, min_servings, max_servings, preparation_time_hours) VALUES
-- Custom Cakes
('CAKE_ROUND_BASIC', 'CUSTOM_CAKE', 'Round Basic Cake', 'Classic round cake with basic decoration', 800.00, 50.00, 10, 100, 24),
('CAKE_ROUND_PREMIUM', 'CUSTOM_CAKE', 'Round Premium Cake', 'Premium round cake with elaborate decoration', 1500.00, 80.00, 10, 100, 48),
('CAKE_SQUARE_BASIC', 'CUSTOM_CAKE', 'Square Basic Cake', 'Classic square cake with basic decoration', 850.00, 55.00, 10, 100, 24),
('CAKE_TIERED_2', 'CUSTOM_CAKE', '2-Tier Cake', 'Elegant 2-tier cake', 2500.00, 100.00, 20, 150, 48),
('CAKE_TIERED_3', 'CUSTOM_CAKE', '3-Tier Cake', 'Grand 3-tier cake', 4500.00, 120.00, 30, 200, 72),
('CAKE_SHEET', 'CUSTOM_CAKE', 'Sheet Cake', 'Large sheet cake perfect for parties', 1200.00, 40.00, 20, 100, 24),

-- Bulk Orders
('BULK_CUPCAKES', 'BULK_PASTRIES', 'Cupcake Assortment', 'Assorted cupcakes in bulk', 50.00, 50.00, 12, 500, 12),
('BULK_COOKIES', 'BULK_PASTRIES', 'Cookie Assortment', 'Variety of cookies in bulk', 30.00, 30.00, 24, 1000, 8),
('BULK_DONUTS', 'BULK_PASTRIES', 'Donut Box', 'Fresh donuts in bulk quantities', 40.00, 40.00, 12, 300, 6),
('BULK_BREAD_ROLLS', 'BULK_BREADS', 'Dinner Rolls', 'Soft dinner rolls in bulk', 20.00, 20.00, 24, 500, 4),
('BULK_PANDESAL', 'BULK_BREADS', 'Pandesal', 'Filipino bread rolls in bulk', 15.00, 15.00, 50, 1000, 4)
ON DUPLICATE KEY UPDATE product_name=product_name;

-- Insert addon categories
INSERT INTO addon_categories (category_code, category_name, description, selection_type, max_selections, is_required) VALUES
('FLAVOR', 'Cake Flavor', 'Choose your cake flavor', 'SINGLE', 1, TRUE),
('FILLING', 'Cake Filling', 'Select cake filling', 'SINGLE', 1, TRUE),
('FROSTING', 'Frosting Type', 'Choose frosting style', 'SINGLE', 1, TRUE),
('DECORATION', 'Decoration Theme', 'Select decoration theme', 'SINGLE', 1, FALSE),
('TOPPING', 'Toppings', 'Additional toppings', 'MULTIPLE', 5, FALSE),
('COLOR_SCHEME', 'Color Scheme', 'Choose color theme', 'SINGLE', 1, FALSE),
('SIZE_MODIFIER', 'Size Options', 'Additional size options', 'SINGLE', 1, FALSE)
ON DUPLICATE KEY UPDATE category_name=category_name;

-- Insert addons
INSERT INTO addons (addon_code, category_code, addon_name, description, price_modifier, price_type) VALUES
-- Flavors
('FLAVOR_VANILLA', 'FLAVOR', 'Classic Vanilla', 'Traditional vanilla cake', 0.00, 'FLAT'),
('FLAVOR_CHOCOLATE', 'FLAVOR', 'Rich Chocolate', 'Decadent chocolate cake', 50.00, 'FLAT'),
('FLAVOR_RED_VELVET', 'FLAVOR', 'Red Velvet', 'Classic red velvet cake', 100.00, 'FLAT'),
('FLAVOR_UBE', 'FLAVOR', 'Ube (Purple Yam)', 'Filipino ube flavored cake', 120.00, 'FLAT'),
('FLAVOR_MOCHA', 'FLAVOR', 'Mocha', 'Coffee-chocolate blend', 80.00, 'FLAT'),
('FLAVOR_STRAWBERRY', 'FLAVOR', 'Strawberry', 'Fresh strawberry cake', 90.00, 'FLAT'),
('FLAVOR_MANGO', 'FLAVOR', 'Mango', 'Tropical mango cake', 110.00, 'FLAT'),
('FLAVOR_LEMON', 'FLAVOR', 'Lemon', 'Zesty lemon cake', 70.00, 'FLAT'),

-- Fillings
('FILLING_BUTTERCREAM', 'FILLING', 'Buttercream', 'Classic buttercream filling', 0.00, 'FLAT'),
('FILLING_CHOCOLATE_GANACHE', 'FILLING', 'Chocolate Ganache', 'Rich chocolate ganache', 100.00, 'FLAT'),
('FILLING_FRUIT_JAM', 'FILLING', 'Fruit Jam', 'Mixed fruit jam filling', 60.00, 'FLAT'),
('FILLING_CREAM_CHEESE', 'FILLING', 'Cream Cheese', 'Smooth cream cheese filling', 80.00, 'FLAT'),
('FILLING_CUSTARD', 'FILLING', 'Custard', 'Vanilla custard filling', 70.00, 'FLAT'),

-- Frosting
('FROSTING_BUTTERCREAM', 'FROSTING', 'Buttercream', 'Classic buttercream frosting', 0.00, 'FLAT'),
('FROSTING_FONDANT', 'FROSTING', 'Fondant', 'Smooth fondant covering', 200.00, 'FLAT'),
('FROSTING_WHIPPED_CREAM', 'FROSTING', 'Whipped Cream', 'Light whipped cream', 50.00, 'FLAT'),
('FROSTING_CREAM_CHEESE', 'FROSTING', 'Cream Cheese Frosting', 'Tangy cream cheese frosting', 100.00, 'FLAT'),
('FROSTING_GANACHE', 'FROSTING', 'Chocolate Ganache', 'Rich chocolate ganache coating', 150.00, 'FLAT'),

-- Decorations
('DECO_SIMPLE', 'DECORATION', 'Simple Border', 'Basic piped border', 0.00, 'FLAT'),
('DECO_FLORAL', 'DECORATION', 'Floral Design', 'Beautiful edible flowers', 300.00, 'FLAT'),
('DECO_CARTOON', 'DECORATION', 'Cartoon Character', 'Custom cartoon design', 500.00, 'FLAT'),
('DECO_PHOTO', 'DECORATION', 'Edible Photo', 'Printed edible image', 250.00, 'FLAT'),
('DECO_GOLD_LEAF', 'DECORATION', 'Gold Leaf Accent', 'Elegant gold leaf details', 400.00, 'FLAT'),
('DECO_FRESH_FLOWERS', 'DECORATION', 'Fresh Flowers', 'Real fresh flower decoration', 600.00, 'FLAT'),

-- Toppings
('TOPPING_SPRINKLES', 'TOPPING', 'Sprinkles', 'Colorful sprinkles', 50.00, 'FLAT'),
('TOPPING_CHOCOLATE_CHIPS', 'TOPPING', 'Chocolate Chips', 'Mini chocolate chips', 80.00, 'FLAT'),
('TOPPING_FRESH_FRUIT', 'TOPPING', 'Fresh Fruit', 'Seasonal fresh fruits', 150.00, 'FLAT'),
('TOPPING_NUTS', 'TOPPING', 'Mixed Nuts', 'Crushed mixed nuts', 100.00, 'FLAT'),
('TOPPING_MACARONS', 'TOPPING', 'Mini Macarons', 'Decorative mini macarons', 200.00, 'FLAT')
ON DUPLICATE KEY UPDATE addon_name=addon_name;

-- Link products with compatible addons
INSERT INTO product_addon_compatibility (product_code, addon_category_code, is_required, display_order) VALUES
-- Custom cakes get all decoration options
('CAKE_ROUND_BASIC', 'FLAVOR', TRUE, 1),
('CAKE_ROUND_BASIC', 'FILLING', TRUE, 2),
('CAKE_ROUND_BASIC', 'FROSTING', TRUE, 3),
('CAKE_ROUND_BASIC', 'DECORATION', FALSE, 4),
('CAKE_ROUND_BASIC', 'TOPPING', FALSE, 5),

('CAKE_ROUND_PREMIUM', 'FLAVOR', TRUE, 1),
('CAKE_ROUND_PREMIUM', 'FILLING', TRUE, 2),
('CAKE_ROUND_PREMIUM', 'FROSTING', TRUE, 3),
('CAKE_ROUND_PREMIUM', 'DECORATION', TRUE, 4),
('CAKE_ROUND_PREMIUM', 'TOPPING', FALSE, 5),

('CAKE_TIERED_2', 'FLAVOR', TRUE, 1),
('CAKE_TIERED_2', 'FILLING', TRUE, 2),
('CAKE_TIERED_2', 'FROSTING', TRUE, 3),
('CAKE_TIERED_2', 'DECORATION', TRUE, 4),
('CAKE_TIERED_2', 'TOPPING', FALSE, 5)
ON DUPLICATE KEY UPDATE is_required=is_required;

-- Insert sample customer
INSERT INTO customers (customer_id, email, password_hash, first_name, last_name, phone, customer_type) VALUES
('CUST-20250101-0001', 'customer@example.com', 'password123', 'John', 'Doe', '09171234567', 'REGULAR')
ON DUPLICATE KEY UPDATE email=email;

-- ========================================
-- INDEXES FOR PERFORMANCE
-- ========================================

-- Additional composite indexes for common queries
CREATE INDEX idx_orders_customer_status ON custom_orders(customer_id, order_status);
CREATE INDEX idx_orders_pickup_status ON custom_orders(pickup_datetime, order_status);
CREATE INDEX idx_orders_payment_status ON custom_orders(payment_status, order_status);

-- ========================================
-- VIEWS FOR REPORTING
-- ========================================

-- Active custom orders view for admin dashboard
CREATE OR REPLACE VIEW v_active_custom_orders AS
SELECT 
    co.order_number,
    co.customer_id,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    c.phone AS customer_phone,
    c.email AS customer_email,
    bp.product_name,
    co.servings,
    co.total_amount,
    co.deposit_paid,
    co.balance_due,
    co.payment_status,
    co.order_status,
    co.fulfillment_type,
    co.pickup_datetime,
    co.created_at,
    co.confirmed_at,
    DATEDIFF(co.pickup_datetime, NOW()) AS days_until_pickup
FROM custom_orders co
JOIN customers c ON co.customer_id = c.customer_id
JOIN custom_order_base_products bp ON co.product_code = bp.product_code
WHERE co.order_status IN ('PENDING', 'CONFIRMED', 'IN_PRODUCTION', 'READY')
ORDER BY co.pickup_datetime ASC;

-- Customer order history view
CREATE OR REPLACE VIEW v_customer_order_history AS
SELECT 
    c.customer_id,
    c.email,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    c.total_orders,
    c.total_spent,
    c.loyalty_points,
    COUNT(co.id) AS custom_orders_count,
    SUM(co.total_amount) AS custom_orders_total,
    MAX(co.created_at) AS last_order_date
FROM customers c
LEFT JOIN custom_orders co ON c.customer_id = co.customer_id
GROUP BY c.customer_id, c.email, c.first_name, c.last_name, c.total_orders, c.total_spent, c.loyalty_points;

-- Daily custom orders summary
CREATE OR REPLACE VIEW v_daily_custom_orders_summary AS
SELECT 
    DATE(created_at) AS order_date,
    COUNT(*) AS total_orders,
    SUM(total_amount) AS total_revenue,
    SUM(deposit_paid) AS total_deposits,
    SUM(balance_due) AS total_balance_due,
    COUNT(CASE WHEN order_status = 'PENDING' THEN 1 END) AS pending_orders,
    COUNT(CASE WHEN order_status = 'CONFIRMED' THEN 1 END) AS confirmed_orders,
    COUNT(CASE WHEN order_status = 'COMPLETED' THEN 1 END) AS completed_orders,
    COUNT(CASE WHEN order_status = 'CANCELLED' THEN 1 END) AS cancelled_orders
FROM custom_orders
GROUP BY DATE(created_at)
ORDER BY order_date DESC;

-- ========================================
-- TRIGGERS FOR AUTOMATION
-- ========================================

-- Update customer stats when order is completed
DELIMITER //
CREATE TRIGGER after_custom_order_complete
AFTER UPDATE ON custom_orders
FOR EACH ROW
BEGIN
    IF NEW.order_status = 'COMPLETED' AND OLD.order_status != 'COMPLETED' THEN
        UPDATE customers
        SET 
            total_orders = total_orders + 1,
            total_spent = total_spent + NEW.total_amount,
            loyalty_points = loyalty_points + FLOOR(NEW.total_amount / 100)
        WHERE customer_id = NEW.customer_id;
    END IF;
END//
DELIMITER ;

-- Track order status changes
DELIMITER //
CREATE TRIGGER after_order_status_change
AFTER UPDATE ON custom_orders
FOR EACH ROW
BEGIN
    IF NEW.order_status != OLD.order_status THEN
        INSERT INTO custom_order_status_history (order_number, old_status, new_status, notes)
        VALUES (NEW.order_number, OLD.order_status, NEW.order_status, 
                CONCAT('Status changed from ', OLD.order_status, ' to ', NEW.order_status));
    END IF;
END//
DELIMITER ;

DELIMITER ;

-- ========================================
-- COMPLETION MESSAGE
-- ========================================
SELECT 'Custom Orders System Schema Created Successfully!' AS Status;
SELECT 'Tables: customers, customer_addresses, custom_orders, addons, and more' AS Info;
SELECT 'Run test_data.sql to populate with sample data' AS NextStep;
