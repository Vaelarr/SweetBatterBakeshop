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

-- Sample inventory items for testing - Filipino Bakery Products
INSERT INTO inventory (name, category, price, stock_quantity, min_stock_level, date_baked, good_until, expiration_date, supplier, description) VALUES
-- Breads & Rolls (Filipino & Traditional)
('Pan de Sal', 'Breads & Rolls', 3.00, 200, 50, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Classic Filipino bread roll, best with butter'),
('Ube Pan de Sal', 'Breads & Rolls', 5.00, 150, 40, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Purple yam flavored Filipino bread roll'),
('Pan de Coco', 'Breads & Rolls', 12.00, 80, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Local Bakery', 'Sweet coconut-filled bread roll'),
('Monay', 'Breads & Rolls', 10.00, 60, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Local Bakery', 'Dense, slightly sweet Filipino bread with butter and sugar topping'),
('Spanish Bread', 'Breads & Rolls', 8.00, 90, 25, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Local Bakery', 'Soft bread roll filled with sweet breadcrumbs'),
('Pandesal de Leche', 'Breads & Rolls', 6.00, 100, 30, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Milk-enriched pan de sal, extra soft'),
('Putok', 'Breads & Rolls', 4.00, 120, 35, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Cracked top bread roll with margarine and sugar'),
('Tasty Bread', 'Breads & Rolls', 15.00, 70, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Local Bakery', 'Loaf bread filled with sweet margarine and sugar'),
('Pinagong', 'Breads & Rolls', 18.00, 50, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Local Bakery', 'Turtle-shaped bread with sweet filling'),
('Kababayan', 'Breads & Rolls', 10.00, 60, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Local Bakery', 'Round flat bread with sweet coconut filling'),

-- Pastries & Desserts (Filipino Specialties)
('Ensaymada', 'Pastries & Desserts', 35.00, 80, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Local Bakery', 'Sweet brioche pastry with butter, sugar, and cheese'),
('Ube Ensaymada', 'Pastries & Desserts', 45.00, 60, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Local Bakery', 'Purple yam flavored ensaymada with cheese'),
('Special Ensaymada', 'Pastries & Desserts', 65.00, 40, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Local Bakery', 'Extra large ensaymada with premium toppings'),
('Hopia Monggo', 'Pastries & Desserts', 12.00, 100, 30, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'Chinese Bakery', 'Traditional hopia with mung bean filling'),
('Hopia Ube', 'Pastries & Desserts', 15.00, 90, 25, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'Chinese Bakery', 'Hopia filled with sweet purple yam'),
('Hopia Baboy', 'Pastries & Desserts', 18.00, 70, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'Chinese Bakery', 'Hopia with savory-sweet pork filling'),
('Hopia Pandan', 'Pastries & Desserts', 15.00, 80, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'Chinese Bakery', 'Pandan-flavored hopia pastry'),
('Pianono', 'Pastries & Desserts', 25.00, 45, 12, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'Local Bakery', 'Rolled sponge cake with custard filling'),
('Ube Pianono', 'Pastries & Desserts', 30.00, 40, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'Local Bakery', 'Ube-flavored rolled cake with custard'),
('Brazo de Mercedes', 'Pastries & Desserts', 45.00, 30, 8, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Local Bakery', 'Meringue roll with custard filling'),
('Sans Rival', 'Pastries & Desserts', 55.00, 25, 8, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'Cake Masters', 'Layered cashew meringue with buttercream'),
('Silvanas', 'Pastries & Desserts', 20.00, 60, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'Local Bakery', 'Cashew meringue wafer with buttercream'),
('Polvoron Original', 'Pastries & Desserts', 8.00, 120, 30, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'Local Bakery', 'Classic milk candy shortbread'),
('Polvoron Pinipig', 'Pastries & Desserts', 10.00, 100, 25, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'Local Bakery', 'Polvoron with crispy rice topping'),
('Polvoron Ube', 'Pastries & Desserts', 10.00, 100, 25, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'Local Bakery', 'Purple yam flavored polvoron'),
('Buko Pie Slice', 'Pastries & Desserts', 45.00, 35, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Pie Masters', 'Traditional young coconut pie'),
('Egg Pie', 'Pastries & Desserts', 40.00, 40, 12, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Pie Masters', 'Custard egg tart pie'),
('Bibingka', 'Pastries & Desserts', 30.00, 50, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Local Bakery', 'Rice cake with salted egg and cheese'),
('Puto', 'Pastries & Desserts', 5.00, 150, 40, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Local Bakery', 'Steamed rice cake, soft and fluffy'),
('Puto Cheese', 'Pastries & Desserts', 8.00, 120, 35, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Local Bakery', 'Steamed rice cake topped with cheese'),
('Sapin-Sapin', 'Pastries & Desserts', 35.00, 40, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Local Bakery', 'Layered sticky rice cake with coconut'),
('Kutsinta', 'Pastries & Desserts', 5.00, 100, 25, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Local Bakery', 'Brown rice cake with grated coconut'),
('Cassava Cake', 'Pastries & Desserts', 40.00, 30, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'Local Bakery', 'Grated cassava cake with custard topping'),
('Maja Blanca', 'Pastries & Desserts', 35.00, 35, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'Local Bakery', 'Coconut pudding with sweet corn'),

-- Cakes & Special Occasions
('Ube Cake Slice', 'Cakes & Special Occasions', 85.00, 40, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'Cake Masters', 'Purple yam chiffon cake slice'),
('Mango Cake Slice', 'Cakes & Special Occasions', 95.00, 35, 8, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Cake Masters', 'Fresh mango cream cake slice'),
('Chocolate Cake Slice', 'Cakes & Special Occasions', 80.00, 45, 12, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'Cake Masters', 'Rich chocolate layer cake'),
('Mocha Cake Slice', 'Cakes & Special Occasions', 90.00, 30, 8, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'Cake Masters', 'Coffee-flavored cake with buttercream'),
('Red Velvet Cake Slice', 'Cakes & Special Occasions', 100.00, 25, 8, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'Cake Masters', 'Red velvet with cream cheese frosting'),
('Mamon', 'Cakes & Special Occasions', 15.00, 80, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'Local Bakery', 'Light and fluffy butter sponge cake'),
('Ube Mamon', 'Cakes & Special Occasions', 18.00, 70, 18, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'Local Bakery', 'Purple yam flavored sponge cake'),
('Cheese Mamon', 'Cakes & Special Occasions', 20.00, 65, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'Local Bakery', 'Cheese-topped butter sponge cake'),
('Crema de Fruta', 'Cakes & Special Occasions', 120.00, 20, 5, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Cake Masters', 'Layered cake with fruits and custard cream'),
('Whole Ube Cake (8")', 'Cakes & Special Occasions', 650.00, 10, 3, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 4 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'Cake Masters', 'Whole purple yam chiffon cake'),
('Whole Mango Cake (8")', 'Cakes & Special Occasions', 750.00, 8, 2, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'Cake Masters', 'Whole fresh mango cream cake'),
('Leche Flan Cake', 'Cakes & Special Occasions', 110.00, 18, 5, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Cake Masters', 'Sponge cake with creamy leche flan layer'),

-- Beverages & Extras
('Fresh Brewed Coffee', 'Beverages & Extras', 55.00, 200, 50, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'Coffee Supplier', 'Premium Batangas barako coffee'),
('Kapeng Barako', 'Beverages & Extras', 65.00, 150, 40, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'Coffee Supplier', 'Strong Philippine liberica coffee'),
('Salabat (Ginger Tea)', 'Beverages & Extras', 45.00, 100, 25, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 60 DAY), 'Tea Supplier', 'Traditional Filipino ginger tea'),
('Tsokolate (Filipino Hot Chocolate)', 'Beverages & Extras', 75.00, 80, 20, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'Cacao Supplier', 'Traditional thick Filipino hot chocolate'),
('Calamansi Juice', 'Beverages & Extras', 40.00, 120, 30, NULL, DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'Juice Co', 'Fresh Philippine lemon juice'),
('Buko Juice', 'Beverages & Extras', 50.00, 100, 25, NULL, DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Juice Co', 'Fresh young coconut water'),
('Sago''t Gulaman', 'Beverages & Extras', 35.00, 90, 25, NULL, DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Beverage Co', 'Traditional Filipino drink with tapioca and jelly'),
('Iced Coffee', 'Beverages & Extras', 65.00, 150, 40, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'Coffee Supplier', 'Cold brewed iced coffee'),
('Ube Latte', 'Beverages & Extras', 85.00, 100, 25, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'Coffee Supplier', 'Purple yam flavored latte'),
('Mango Shake', 'Beverages & Extras', 75.00, 80, 20, NULL, DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'Juice Co', 'Fresh mango smoothie'),
('Bottled Water', 'Beverages & Extras', 20.00, 300, 75, NULL, NULL, DATE_ADD(CURDATE(), INTERVAL 180 DAY), 'Water Supplier', 'Purified bottled water'),
('Butter (100g)', 'Beverages & Extras', 45.00, 50, 15, NULL, DATE_ADD(CURDATE(), INTERVAL 14 DAY), DATE_ADD(CURDATE(), INTERVAL 21 DAY), 'Dairy Co', 'Premium salted butter'),
('Cheese Spread', 'Beverages & Extras', 55.00, 60, 20, NULL, DATE_ADD(CURDATE(), INTERVAL 30 DAY), DATE_ADD(CURDATE(), INTERVAL 45 DAY), 'Dairy Co', 'Creamy cheese spread for bread')
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
-- CUSTOM ORDERS SYSTEM - ADDITIONAL TABLES
-- Customer Accounts & Custom Order Management
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

-- Main custom orders table
CREATE TABLE IF NOT EXISTS custom_orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE COMMENT 'Format: CO-YYYYMMDD-XXXX',
    customer_id VARCHAR(50) NOT NULL,
    product_code VARCHAR(50) NOT NULL,
    order_type VARCHAR(50) NOT NULL COMMENT 'CUSTOM_CAKE, BULK_ORDER, etc.',
    servings INT NOT NULL,
    message_on_item TEXT COMMENT 'Message to write on cake/item',
    special_instructions TEXT,
    base_price DECIMAL(10, 2) NOT NULL,
    addons_total DECIMAL(10, 2) DEFAULT 0.00,
    subtotal DECIMAL(10, 2) NOT NULL,
    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
    tax_amount DECIMAL(10, 2) DEFAULT 0.00,
    delivery_fee DECIMAL(10, 2) DEFAULT 0.00,
    total_amount DECIMAL(10, 2) NOT NULL,
    deposit_required DECIMAL(10, 2) DEFAULT 0.00,
    deposit_paid DECIMAL(10, 2) DEFAULT 0.00,
    deposit_payment_method VARCHAR(50),
    deposit_paid_at TIMESTAMP NULL,
    balance_due DECIMAL(10, 2) DEFAULT 0.00,
    payment_status ENUM('PENDING', 'DEPOSIT_PAID', 'FULLY_PAID', 'REFUNDED') DEFAULT 'PENDING',
    fulfillment_type ENUM('PICKUP', 'DELIVERY') NOT NULL DEFAULT 'PICKUP',
    pickup_datetime DATETIME NOT NULL,
    delivery_address_id INT,
    delivery_datetime DATETIME,
    order_status ENUM('PENDING', 'CONFIRMED', 'IN_PRODUCTION', 'READY', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    admin_notes TEXT,
    cancellation_reason TEXT,
    cancelled_at TIMESTAMP NULL,
    cancelled_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
-- CUSTOM ORDERS SAMPLE DATA
-- ========================================

-- Insert custom order categories
INSERT INTO custom_order_categories (category_code, category_name, description, min_order_lead_time_days) VALUES
('CUSTOM_CAKE', 'Custom Cakes', 'Personalized cakes for any occasion', 3),
('BULK_PASTRIES', 'Bulk Pastries', 'Large quantity pastries for events', 2),
('BULK_BREADS', 'Bulk Breads', 'Wholesale bread orders', 1),
('CUSTOM_DESSERT', 'Custom Desserts', 'Specialty dessert platters', 2)
ON DUPLICATE KEY UPDATE category_name=category_name;

-- Insert base products
INSERT INTO custom_order_base_products (product_code, category_code, product_name, description, base_price, price_per_serving, min_servings, max_servings, preparation_time_hours) VALUES
('CAKE_ROUND_BASIC', 'CUSTOM_CAKE', 'Round Basic Cake', 'Classic round cake with basic decoration', 800.00, 50.00, 10, 100, 24),
('CAKE_ROUND_PREMIUM', 'CUSTOM_CAKE', 'Round Premium Cake', 'Premium round cake with elaborate decoration', 1500.00, 80.00, 10, 100, 48),
('CAKE_TIERED_2', 'CUSTOM_CAKE', '2-Tier Cake', 'Elegant 2-tier cake', 2500.00, 100.00, 20, 150, 48),
('BULK_CUPCAKES', 'BULK_PASTRIES', 'Cupcake Assortment', 'Assorted cupcakes in bulk', 50.00, 50.00, 12, 500, 12)
ON DUPLICATE KEY UPDATE product_name=product_name;

-- Insert addon categories
INSERT INTO addon_categories (category_code, category_name, description, selection_type, max_selections, is_required) VALUES
('FLAVOR', 'Cake Flavor', 'Choose your cake flavor', 'SINGLE', 1, TRUE),
('FILLING', 'Cake Filling', 'Select cake filling', 'SINGLE', 1, TRUE),
('FROSTING', 'Frosting Type', 'Choose frosting style', 'SINGLE', 1, TRUE),
('DECORATION', 'Decoration Theme', 'Select decoration theme', 'SINGLE', 1, FALSE)
ON DUPLICATE KEY UPDATE category_name=category_name;

-- Insert addons
INSERT INTO addons (addon_code, category_code, addon_name, description, price_modifier, price_type) VALUES
('FLAVOR_VANILLA', 'FLAVOR', 'Classic Vanilla', 'Traditional vanilla cake', 0.00, 'FLAT'),
('FLAVOR_CHOCOLATE', 'FLAVOR', 'Rich Chocolate', 'Decadent chocolate cake', 50.00, 'FLAT'),
('FLAVOR_UBE', 'FLAVOR', 'Ube (Purple Yam)', 'Filipino ube flavored cake', 120.00, 'FLAT'),
('FILLING_BUTTERCREAM', 'FILLING', 'Buttercream', 'Classic buttercream filling', 0.00, 'FLAT'),
('FILLING_CHOCOLATE_GANACHE', 'FILLING', 'Chocolate Ganache', 'Rich chocolate ganache', 100.00, 'FLAT'),
('FROSTING_BUTTERCREAM', 'FROSTING', 'Buttercream', 'Classic buttercream frosting', 0.00, 'FLAT'),
('FROSTING_FONDANT', 'FROSTING', 'Fondant', 'Smooth fondant covering', 200.00, 'FLAT')
ON DUPLICATE KEY UPDATE addon_name=addon_name;

-- Insert sample customer
INSERT INTO customers (customer_id, email, password_hash, first_name, last_name, phone, customer_type) VALUES
('CUST-20250101-0001', 'customer@example.com', 'password123', 'John', 'Doe', '09171234567', 'REGULAR')
ON DUPLICATE KEY UPDATE email=email;

-- ========================================
-- CUSTOM ORDERS VIEWS
-- ========================================

-- Active custom orders view for admin dashboard
CREATE OR REPLACE VIEW v_active_custom_orders AS
SELECT 
    co.order_number,
    co.customer_id,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    c.phone AS customer_phone,
    bp.product_name,
    co.servings,
    co.total_amount,
    co.payment_status,
    co.order_status,
    co.pickup_datetime,
    DATEDIFF(co.pickup_datetime, NOW()) AS days_until_pickup
FROM custom_orders co
JOIN customers c ON co.customer_id = c.customer_id
JOIN custom_order_base_products bp ON co.product_code = bp.product_code
WHERE co.order_status IN ('PENDING', 'CONFIRMED', 'IN_PRODUCTION', 'READY')
ORDER BY co.pickup_datetime ASC;

-- ========================================
-- CUSTOM ORDERS TRIGGERS
-- ========================================

-- Update customer stats when order is completed
DELIMITER //
CREATE TRIGGER IF NOT EXISTS after_custom_order_complete
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
CREATE TRIGGER IF NOT EXISTS after_order_status_change
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
SELECT 'system_settings', COUNT(*) FROM system_settings
UNION ALL
SELECT 'customers', COUNT(*) FROM customers
UNION ALL
SELECT 'custom_orders', COUNT(*) FROM custom_orders;

SELECT '✅ Database setup completed successfully!' AS Status,
       'All tables, views, procedures, and triggers created.' AS Details,
       'Includes core inventory, sales, admin, and custom orders systems.' AS Info;

