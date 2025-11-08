-- ========================================
-- SweetBatterBakeshop Kiosk Database Setup
-- Comprehensive Schema for Admin Panel Integration
-- ========================================

-- Create database
CREATE DATABASE IF NOT EXISTS kiosk_db;
USE kiosk_db;

-- ========================================
-- INVENTORY MANAGEMENT TABLES
-- ========================================

-- Main inventory table with enhanced tracking
CREATE TABLE IF NOT EXISTS inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    category VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    min_stock_level INT DEFAULT 10,
    date_baked DATE COMMENT 'Date when the item was baked',
    good_until DATE COMMENT 'Date until the item is good/fresh (best before)',
    expiration_date DATE COMMENT 'Hard expiration date (do not sell after)',
    barcode VARCHAR(50) UNIQUE,
    supplier VARCHAR(255),
    description TEXT,
    image_path VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_barcode (barcode),
    INDEX idx_date_baked (date_baked),
    INDEX idx_good_until (good_until),
    INDEX idx_expiration (expiration_date),
    INDEX idx_stock (stock_quantity),
    INDEX idx_active (is_active),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Inventory stock history for tracking changes
CREATE TABLE IF NOT EXISTS inventory_stock_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(255) NOT NULL,
    old_quantity INT NOT NULL,
    new_quantity INT NOT NULL,
    change_amount INT NOT NULL,
    change_type ENUM('SALE', 'RESTOCK', 'ADJUSTMENT', 'EXPIRED', 'DAMAGED') NOT NULL,
    notes TEXT,
    changed_by VARCHAR(100),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_name) REFERENCES inventory(name) ON DELETE CASCADE,
    INDEX idx_item_name (item_name),
    INDEX idx_change_date (changed_at),
    INDEX idx_change_type (change_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- SALES TRANSACTION TABLES
-- ========================================

-- Main sales transactions table
CREATE TABLE IF NOT EXISTS sales_transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(50) NOT NULL UNIQUE,
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(10, 2) NOT NULL,
    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
    tax_amount DECIMAL(10, 2) DEFAULT 0.00,
    total DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('CASH', 'ECASH', 'CARD', 'OTHER') DEFAULT 'CASH',
    payment_amount DECIMAL(10, 2) DEFAULT 0.00,
    change_amount DECIMAL(10, 2) DEFAULT 0.00,
    discount_applied BOOLEAN DEFAULT FALSE,
    discount_type VARCHAR(50),
    customer_type ENUM('REGULAR', 'SENIOR', 'PWD', 'EMPLOYEE') DEFAULT 'REGULAR',
    served_by VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_payment_method (payment_method),
    INDEX idx_customer_type (customer_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sales items detail table
CREATE TABLE IF NOT EXISTS sales_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(50) NOT NULL,
    item_name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
    FOREIGN KEY (transaction_id) REFERENCES sales_transactions(transaction_id) ON DELETE CASCADE,
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_item_name (item_name),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- ADMIN & USER MANAGEMENT TABLES
-- ========================================

-- Admin users table with role-based access
CREATE TABLE IF NOT EXISTS admin_users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    role ENUM('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'STAFF') DEFAULT 'STAFF',
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Admin activity log for audit trail
CREATE TABLE IF NOT EXISTS admin_activity_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    admin_username VARCHAR(50) NOT NULL,
    action_type VARCHAR(100) NOT NULL,
    action_description TEXT,
    affected_table VARCHAR(100),
    affected_record_id VARCHAR(100),
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_username) REFERENCES admin_users(username) ON DELETE CASCADE,
    INDEX idx_admin (admin_username),
    INDEX idx_action_type (action_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- CUSTOMER SUPPORT TABLES
-- ========================================

-- Help requests table
CREATE TABLE IF NOT EXISTS help_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    request_id VARCHAR(50) NOT NULL UNIQUE,
    location VARCHAR(100) NOT NULL,
    request_type VARCHAR(50) NOT NULL,
    description TEXT,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') DEFAULT 'MEDIUM',
    status ENUM('PENDING', 'IN_PROGRESS', 'RESOLVED', 'CANCELLED') DEFAULT 'PENDING',
    assigned_to VARCHAR(50),
    resolved_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    resolution_notes TEXT,
    FOREIGN KEY (assigned_to) REFERENCES admin_users(username) ON DELETE SET NULL,
    FOREIGN KEY (resolved_by) REFERENCES admin_users(username) ON DELETE SET NULL,
    INDEX idx_request_id (request_id),
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_created_at (created_at),
    INDEX idx_assigned_to (assigned_to)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- ANALYTICS & REPORTING TABLES
-- ========================================

-- Daily sales summary for quick reporting
CREATE TABLE IF NOT EXISTS daily_sales_summary (
    id INT AUTO_INCREMENT PRIMARY KEY,
    summary_date DATE NOT NULL UNIQUE,
    total_transactions INT DEFAULT 0,
    total_items_sold INT DEFAULT 0,
    gross_sales DECIMAL(10, 2) DEFAULT 0.00,
    total_discounts DECIMAL(10, 2) DEFAULT 0.00,
    total_tax DECIMAL(10, 2) DEFAULT 0.00,
    net_sales DECIMAL(10, 2) DEFAULT 0.00,
    cash_sales DECIMAL(10, 2) DEFAULT 0.00,
    ecash_sales DECIMAL(10, 2) DEFAULT 0.00,
    card_sales DECIMAL(10, 2) DEFAULT 0.00,
    avg_transaction_value DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_summary_date (summary_date),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Product sales analytics for performance tracking
CREATE TABLE IF NOT EXISTS product_sales_analytics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    analysis_period ENUM('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY') NOT NULL,
    period_start_date DATE NOT NULL,
    period_end_date DATE NOT NULL,
    units_sold INT DEFAULT 0,
    total_revenue DECIMAL(10, 2) DEFAULT 0.00,
    avg_price DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_item_name (item_name),
    INDEX idx_category (category),
    INDEX idx_period (analysis_period),
    INDEX idx_dates (period_start_date, period_end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- SYSTEM CONFIGURATION TABLE
-- ========================================

-- System settings and configuration
CREATE TABLE IF NOT EXISTS system_settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT,
    setting_type VARCHAR(50) DEFAULT 'STRING',
    description TEXT,
    updated_by VARCHAR(50),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_setting_key (setting_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- SAMPLE DATA INSERTIONS
-- ========================================

-- Insert default admin user (password: admin123 - CHANGE IN PRODUCTION!)
INSERT INTO admin_users (username, password_hash, full_name, email, role) VALUES
('admin', 'admin123', 'System Administrator', 'admin@sweetbatter.com', 'SUPER_ADMIN'),
('manager', 'manager123', 'Store Manager', 'manager@sweetbatter.com', 'MANAGER')
ON DUPLICATE KEY UPDATE username=username;

-- Insert system settings
INSERT INTO system_settings (setting_key, setting_value, setting_type, description) VALUES
('STORE_NAME', 'SweetBatterBakeshop', 'STRING', 'Store name displayed on receipts'),
('TAX_RATE', '0.12', 'DECIMAL', 'VAT tax rate (12%)'),
('LOW_STOCK_THRESHOLD', '10', 'INTEGER', 'Minimum stock level before warning'),
('SENIOR_PWD_DISCOUNT', '0.20', 'DECIMAL', 'Senior/PWD discount rate (20%)'),
('BUSINESS_HOURS_OPEN', '08:00', 'TIME', 'Store opening time'),
('BUSINESS_HOURS_CLOSE', '20:00', 'TIME', 'Store closing time'),
('RECEIPT_FOOTER', 'Thank you for your purchase!', 'STRING', 'Message at bottom of receipt'),
('ENABLE_NOTIFICATIONS', 'true', 'BOOLEAN', 'Enable system notifications')
ON DUPLICATE KEY UPDATE setting_key=setting_key;

-- Comprehensive inventory items for production
INSERT INTO inventory (name, category, price, stock_quantity, min_stock_level, date_baked, good_until, expiration_date, supplier, description) VALUES
-- Breads & Rolls (Enhanced Selection)
('French Baguette', 'Breads & Rolls', 85.00, 30, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Bakery Chef', 'Fresh daily baked French bread'),
('Whole Wheat Bread', 'Breads & Rolls', 95.00, 25, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Bakery Chef', 'Healthy whole grain bread'),
('White Sandwich Bread', 'Breads & Rolls', 75.00, 40, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Bakery Chef', 'Soft white bread perfect for sandwiches'),
('Multigrain Bread', 'Breads & Rolls', 105.00, 20, 8, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Artisan Bakery', 'Nutritious bread with multiple grains'),
('Sourdough Bread', 'Breads & Rolls', 120.00, 15, 8, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'Artisan Bakery', 'Traditional tangy sourdough'),
('Ube Pan de Sal', 'Breads & Rolls', 45.00, 60, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Filipino favorite with ube flavor'),
('Classic Pan de Sal', 'Breads & Rolls', 35.00, 80, 30, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Traditional Filipino bread roll'),
('Cheese Pan de Sal', 'Breads & Rolls', 50.00, 50, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Pan de sal filled with cheese'),
('Dinner Rolls', 'Breads & Rolls', 55.00, 45, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Bakery Chef', 'Soft and fluffy dinner rolls'),
('Garlic Bread', 'Breads & Rolls', 95.00, 20, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Bakery Chef', 'Aromatic garlic butter bread'),
('Ciabatta', 'Breads & Rolls', 110.00, 12, 6, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Artisan Bakery', 'Italian rustic bread'),
('Focaccia', 'Breads & Rolls', 125.00, 15, 8, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Artisan Bakery', 'Italian flatbread with herbs'),

-- Pastries & Desserts (Extensive Collection)
('Chocolate Croissant', 'Pastries & Desserts', 75.00, 35, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'French Bakery', 'Buttery croissant with rich chocolate'),
('Plain Croissant', 'Pastries & Desserts', 60.00, 40, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'French Bakery', 'Classic buttery French croissant'),
('Almond Croissant', 'Pastries & Desserts', 85.00, 25, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'French Bakery', 'Croissant filled with almond cream'),
('Ensaymada', 'Pastries & Desserts', 35.00, 50, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Sweet Filipino pastry with butter and sugar'),
('Ube Ensaymada', 'Pastries & Desserts', 45.00, 40, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Purple yam flavored ensaymada'),
('Cheese Ensaymada', 'Pastries & Desserts', 50.00, 35, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Ensaymada topped with cheese'),
('Cinnamon Roll', 'Pastries & Desserts', 65.00, 30, 12, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Bakery Chef', 'Soft roll with cinnamon and cream cheese frosting'),
('Danish Pastry', 'Pastries & Desserts', 70.00, 25, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'French Bakery', 'Flaky pastry with fruit filling'),
('Apple Turnover', 'Pastries & Desserts', 80.00, 20, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'French Bakery', 'Puff pastry filled with spiced apples'),
('Eclair', 'Pastries & Desserts', 90.00, 18, 8, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'French Bakery', 'Chocolate eclair with custard filling'),
('Cream Puff', 'Pastries & Desserts', 75.00, 20, 8, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'French Bakery', 'Light pastry filled with cream'),
('Muffin - Blueberry', 'Pastries & Desserts', 55.00, 30, 12, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Bakery Chef', 'Moist blueberry muffin'),
('Muffin - Chocolate Chip', 'Pastries & Desserts', 55.00, 30, 12, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Bakery Chef', 'Chocolate chip muffin'),
('Muffin - Banana Nut', 'Pastries & Desserts', 55.00, 25, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Bakery Chef', 'Banana muffin with walnuts'),
('Brownie', 'Pastries & Desserts', 65.00, 35, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'Cake Masters', 'Rich fudgy chocolate brownie'),
('Blondie', 'Pastries & Desserts', 60.00, 25, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'Cake Masters', 'Vanilla brownie with butterscotch'),
('Scone - Plain', 'Pastries & Desserts', 50.00, 25, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Bakery Chef', 'Traditional British scone'),
('Scone - Cranberry', 'Pastries & Desserts', 60.00, 20, 8, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Bakery Chef', 'Scone with dried cranberries'),
('Macaron - Assorted', 'Pastries & Desserts', 40.00, 60, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'French Bakery', 'Delicate French macaron'),
('Cupcake - Vanilla', 'Pastries & Desserts', 55.00, 30, 12, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Cake Masters', 'Classic vanilla cupcake with frosting'),
('Cupcake - Chocolate', 'Pastries & Desserts', 55.00, 30, 12, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Cake Masters', 'Rich chocolate cupcake'),
('Cupcake - Red Velvet', 'Pastries & Desserts', 65.00, 25, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Cake Masters', 'Red velvet cupcake with cream cheese frosting'),
('Donut - Glazed', 'Pastries & Desserts', 35.00, 50, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Classic glazed donut'),
('Donut - Chocolate', 'Pastries & Desserts', 40.00, 45, 18, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Chocolate frosted donut'),
('Donut - Strawberry', 'Pastries & Desserts', 40.00, 40, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Strawberry frosted donut'),
('Donut - Bavarian', 'Pastries & Desserts', 50.00, 30, 12, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Custard filled donut'),

-- Cakes & Special Occasions (Premium Selection)
('Chocolate Cake Slice', 'Cakes & Special Occasions', 120.00, 24, 8, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Cake Masters', 'Rich chocolate layer cake'),
('Vanilla Cake Slice', 'Cakes & Special Occasions', 110.00, 24, 8, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Cake Masters', 'Classic vanilla cake with buttercream'),
('Red Velvet Cake Slice', 'Cakes & Special Occasions', 130.00, 20, 6, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Cake Masters', 'Red velvet with cream cheese frosting'),
('Carrot Cake Slice', 'Cakes & Special Occasions', 125.00, 18, 6, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Cake Masters', 'Moist carrot cake with walnuts'),
('Tiramisu Slice', 'Cakes & Special Occasions', 145.00, 15, 5, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Cake Masters', 'Italian coffee-flavored dessert'),
('Cheesecake Slice - Plain', 'Cakes & Special Occasions', 135.00, 20, 6, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'Cake Masters', 'Creamy New York style cheesecake'),
('Cheesecake Slice - Blueberry', 'Cakes & Special Occasions', 150.00, 18, 6, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'Cake Masters', 'Cheesecake with blueberry topping'),
('Cheesecake Slice - Strawberry', 'Cakes & Special Occasions', 150.00, 18, 6, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'Cake Masters', 'Cheesecake with strawberry topping'),
('Black Forest Cake Slice', 'Cakes & Special Occasions', 140.00, 16, 6, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Cake Masters', 'Chocolate cake with cherries and cream'),
('Ube Cake Slice', 'Cakes & Special Occasions', 130.00, 20, 6, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Cake Masters', 'Filipino purple yam cake'),
('Mocha Cake Slice', 'Cakes & Special Occasions', 135.00, 16, 6, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Cake Masters', 'Coffee and chocolate layered cake'),
('Whole Chocolate Cake (8")', 'Cakes & Special Occasions', 850.00, 5, 2, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'Cake Masters', '8-inch whole chocolate cake'),
('Whole Vanilla Cake (8")', 'Cakes & Special Occasions', 800.00, 5, 2, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'Cake Masters', '8-inch whole vanilla cake'),
('Whole Ube Cake (8")', 'Cakes & Special Occasions', 900.00, 4, 2, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'Cake Masters', '8-inch whole ube cake'),
('Birthday Cake - Custom', 'Cakes & Special Occasions', 1200.00, 3, 1, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'Cake Masters', 'Custom decorated birthday cake'),

-- Beverages & Extras (Complete Range)
('Fresh Brewed Coffee', 'Beverages & Extras', 55.00, 120, 30, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'Coffee Supplier', 'Premium arabica coffee'),
('Espresso', 'Beverages & Extras', 65.00, 100, 25, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'Coffee Supplier', 'Strong Italian coffee'),
('Cappuccino', 'Beverages & Extras', 75.00, 100, 25, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'Coffee Supplier', 'Espresso with steamed milk foam'),
('Latte', 'Beverages & Extras', 75.00, 100, 25, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'Coffee Supplier', 'Espresso with steamed milk'),
('Americano', 'Beverages & Extras', 60.00, 100, 25, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'Coffee Supplier', 'Espresso with hot water'),
('Iced Coffee', 'Beverages & Extras', 65.00, 80, 20, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'Coffee Supplier', 'Cold brewed coffee over ice'),
('Iced Latte', 'Beverages & Extras', 80.00, 80, 20, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'Coffee Supplier', 'Iced espresso with cold milk'),
('Hot Chocolate', 'Beverages & Extras', 70.00, 90, 25, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'Coffee Supplier', 'Rich hot chocolate drink'),
('Tea - English Breakfast', 'Beverages & Extras', 50.00, 100, 25, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 180 DAY), 'Tea Company', 'Classic black tea'),
('Tea - Green', 'Beverages & Extras', 55.00, 90, 25, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 180 DAY), 'Tea Company', 'Healthy green tea'),
('Tea - Chamomile', 'Beverages & Extras', 55.00, 80, 20, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 180 DAY), 'Tea Company', 'Relaxing herbal tea'),
('Orange Juice', 'Beverages & Extras', 65.00, 70, 20, NULL, DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'Juice Co', 'Freshly squeezed orange juice'),
('Apple Juice', 'Beverages & Extras', 60.00, 70, 20, NULL, DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'Juice Co', 'Fresh apple juice'),
('Mango Juice', 'Beverages & Extras', 70.00, 60, 18, NULL, DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'Juice Co', 'Tropical mango juice'),
('Bottled Water', 'Beverages & Extras', 25.00, 150, 40, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 365 DAY), 'Water Co', 'Purified drinking water'),
('Sparkling Water', 'Beverages & Extras', 35.00, 100, 30, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 365 DAY), 'Water Co', 'Carbonated water'),
('Milk - Fresh', 'Beverages & Extras', 45.00, 80, 25, NULL, DATE_ADD(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'Dairy Farm', 'Fresh whole milk'),
('Soda - Coke', 'Beverages & Extras', 40.00, 120, 30, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 180 DAY), 'Beverage Co', 'Coca-Cola'),
('Soda - Sprite', 'Beverages & Extras', 40.00, 100, 30, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 180 DAY), 'Beverage Co', 'Sprite lemon-lime'),

-- Sandwiches & Savory Items (New Category)
('Ham & Cheese Sandwich', 'Sandwiches & Savory', 95.00, 25, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Classic ham and cheese on fresh bread'),
('Tuna Sandwich', 'Sandwiches & Savory', 85.00, 20, 8, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Tuna salad sandwich'),
('Egg Sandwich', 'Sandwiches & Savory', 75.00, 25, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Fresh egg salad sandwich'),
('Club Sandwich', 'Sandwiches & Savory', 120.00, 15, 8, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Triple-decker club sandwich'),
('Chicken Sandwich', 'Sandwiches & Savory', 110.00, 18, 8, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Grilled chicken sandwich'),
('Sausage Roll', 'Sandwiches & Savory', 55.00, 35, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Savory pastry with sausage'),
('Empanada - Beef', 'Sandwiches & Savory', 45.00, 40, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Filipino meat-filled pastry'),
('Empanada - Chicken', 'Sandwiches & Savory', 45.00, 35, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Chicken-filled empanada'),
('Cheese Bread', 'Sandwiches & Savory', 40.00, 45, 18, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Soft bread with cheese filling'),
('Pizza Bread', 'Sandwiches & Savory', 50.00, 30, 12, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Bread topped with pizza ingredients'),
('Quiche - Lorraine', 'Sandwiches & Savory', 95.00, 12, 6, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Bakery Chef', 'French bacon and cheese quiche'),
('Quiche - Vegetable', 'Sandwiches & Savory', 90.00, 10, 5, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Bakery Chef', 'Vegetarian quiche')
ON DUPLICATE KEY UPDATE name=name;

-- ========================================
-- VIEWS FOR REPORTING
-- ========================================

-- View for expired items
CREATE OR REPLACE VIEW v_expired_items AS
SELECT 
    name,
    category,
    stock_quantity,
    date_baked,
    good_until,
    expiration_date,
    DATEDIFF(CURDATE(), expiration_date) AS days_expired,
    price * stock_quantity AS value_lost
FROM inventory
WHERE expiration_date < CURDATE()
  AND is_active = TRUE
ORDER BY expiration_date;

-- View for low stock items
CREATE OR REPLACE VIEW v_low_stock_items AS
SELECT 
    name,
    category,
    stock_quantity,
    min_stock_level,
    (min_stock_level - stock_quantity) AS stock_deficit,
    date_baked,
    good_until,
    supplier
FROM inventory
WHERE stock_quantity < min_stock_level
  AND is_active = TRUE
ORDER BY stock_deficit DESC;

-- View for expiring soon items (within 7 days)
CREATE OR REPLACE VIEW v_expiring_soon AS
SELECT 
    name,
    category,
    stock_quantity,
    date_baked,
    good_until,
    expiration_date,
    DATEDIFF(good_until, CURDATE()) AS days_until_not_fresh,
    DATEDIFF(expiration_date, CURDATE()) AS days_until_expiration
FROM inventory
WHERE (good_until BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
    OR expiration_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY))
  AND is_active = TRUE
ORDER BY good_until, expiration_date;

-- View for items past their "good until" date but not expired
CREATE OR REPLACE VIEW v_past_fresh_date AS
SELECT 
    name,
    category,
    stock_quantity,
    date_baked,
    good_until,
    expiration_date,
    DATEDIFF(CURDATE(), good_until) AS days_past_fresh,
    DATEDIFF(expiration_date, CURDATE()) AS days_until_expiration,
    price * stock_quantity AS potential_loss
FROM inventory
WHERE good_until < CURDATE()
  AND expiration_date >= CURDATE()
  AND is_active = TRUE
ORDER BY good_until;

-- View for today's sales summary
CREATE OR REPLACE VIEW v_today_sales AS
SELECT 
    COUNT(*) AS total_transactions,
    SUM(total) AS total_sales,
    SUM(discount_amount) AS total_discounts,
    AVG(total) AS avg_transaction,
    SUM(CASE WHEN payment_method = 'CASH' THEN total ELSE 0 END) AS cash_sales,
    SUM(CASE WHEN payment_method = 'ECASH' THEN total ELSE 0 END) AS ecash_sales,
    SUM(CASE WHEN customer_type IN ('SENIOR', 'PWD') THEN 1 ELSE 0 END) AS senior_pwd_count
FROM sales_transactions
WHERE DATE(transaction_date) = CURDATE();

-- View for top selling products (all time)
CREATE OR REPLACE VIEW v_top_products AS
SELECT 
    si.item_name,
    si.category,
    SUM(si.quantity) AS total_sold,
    SUM(si.subtotal) AS total_revenue,
    AVG(si.price) AS avg_price,
    COUNT(DISTINCT si.transaction_id) AS transaction_count
FROM sales_items si
GROUP BY si.item_name, si.category
ORDER BY total_sold DESC
LIMIT 20;

-- ========================================
-- STORED PROCEDURES
-- ========================================

-- Procedure to update daily sales summary
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS update_daily_sales_summary(IN target_date DATE)
BEGIN
    INSERT INTO daily_sales_summary (
        summary_date,
        total_transactions,
        total_items_sold,
        gross_sales,
        total_discounts,
        total_tax,
        net_sales,
        cash_sales,
        ecash_sales,
        card_sales,
        avg_transaction_value
    )
    SELECT 
        DATE(st.transaction_date) AS summary_date,
        COUNT(DISTINCT st.transaction_id) AS total_transactions,
        SUM(si.quantity) AS total_items_sold,
        SUM(st.subtotal) AS gross_sales,
        SUM(st.discount_amount) AS total_discounts,
        SUM(st.tax_amount) AS total_tax,
        SUM(st.total) AS net_sales,
        SUM(CASE WHEN st.payment_method = 'CASH' THEN st.total ELSE 0 END) AS cash_sales,
        SUM(CASE WHEN st.payment_method = 'ECASH' THEN st.total ELSE 0 END) AS ecash_sales,
        SUM(CASE WHEN st.payment_method = 'CARD' THEN st.total ELSE 0 END) AS card_sales,
        AVG(st.total) AS avg_transaction_value
    FROM sales_transactions st
    LEFT JOIN sales_items si ON st.transaction_id = si.transaction_id
    WHERE DATE(st.transaction_date) = target_date
    GROUP BY DATE(st.transaction_date)
    ON DUPLICATE KEY UPDATE
        total_transactions = VALUES(total_transactions),
        total_items_sold = VALUES(total_items_sold),
        gross_sales = VALUES(gross_sales),
        total_discounts = VALUES(total_discounts),
        total_tax = VALUES(total_tax),
        net_sales = VALUES(net_sales),
        cash_sales = VALUES(cash_sales),
        ecash_sales = VALUES(ecash_sales),
        card_sales = VALUES(card_sales),
        avg_transaction_value = VALUES(avg_transaction_value);
END //
DELIMITER ;

-- Procedure to record stock change
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS record_stock_change(
    IN p_item_name VARCHAR(255),
    IN p_old_qty INT,
    IN p_new_qty INT,
    IN p_change_type VARCHAR(20),
    IN p_notes TEXT,
    IN p_changed_by VARCHAR(100)
)
BEGIN
    INSERT INTO inventory_stock_history (
        item_name,
        old_quantity,
        new_quantity,
        change_amount,
        change_type,
        notes,
        changed_by
    ) VALUES (
        p_item_name,
        p_old_qty,
        p_new_qty,
        p_new_qty - p_old_qty,
        p_change_type,
        p_notes,
        p_changed_by
    );
END //
DELIMITER ;

-- ========================================
-- TRIGGERS
-- ========================================

-- Trigger to log inventory stock changes
DELIMITER //
CREATE TRIGGER IF NOT EXISTS trg_inventory_stock_update
AFTER UPDATE ON inventory
FOR EACH ROW
BEGIN
    IF OLD.stock_quantity != NEW.stock_quantity THEN
        INSERT INTO inventory_stock_history (
            item_name,
            old_quantity,
            new_quantity,
            change_amount,
            change_type,
            notes
        ) VALUES (
            NEW.name,
            OLD.stock_quantity,
            NEW.stock_quantity,
            NEW.stock_quantity - OLD.stock_quantity,
            'ADJUSTMENT',
            CONCAT('Stock updated from ', OLD.stock_quantity, ' to ', NEW.stock_quantity)
        );
    END IF;
END //
DELIMITER ;

-- ========================================
-- VERIFICATION & COMPLETION
-- ========================================

-- Show all tables
SHOW TABLES;

-- Show table counts
SELECT 
    'inventory' AS table_name, COUNT(*) AS record_count FROM inventory
UNION ALL
SELECT 'sales_transactions', COUNT(*) FROM sales_transactions
UNION ALL
SELECT 'sales_items', COUNT(*) FROM sales_items
UNION ALL
SELECT 'admin_users', COUNT(*) FROM admin_users
UNION ALL
SELECT 'help_requests', COUNT(*) FROM help_requests
UNION ALL
SELECT 'system_settings', COUNT(*) FROM system_settings;

SELECT '✅ Database setup completed successfully!' AS Status,
       'All tables, views, procedures, and triggers created.' AS Details;
