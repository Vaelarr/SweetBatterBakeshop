-- ========================================
-- SweetBatterBakeshop Kiosk Database Setup
-- SQLite Version - Complete Schema
-- ========================================

-- Enable foreign keys support in SQLite
PRAGMA foreign_keys = ON;

-- ========================================
-- INVENTORY MANAGEMENT TABLES
-- ========================================

-- Main inventory table with enhanced tracking
CREATE TABLE IF NOT EXISTS inventory (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    category TEXT NOT NULL,
    price REAL NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    min_stock_level INTEGER DEFAULT 10,
    date_baked TEXT,
    good_until TEXT,
    expiration_date TEXT,
    barcode TEXT UNIQUE,
    supplier TEXT,
    description TEXT,
    image_path TEXT,
    is_active INTEGER DEFAULT 1,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_inventory_category ON inventory(category);
CREATE INDEX IF NOT EXISTS idx_inventory_barcode ON inventory(barcode);
CREATE INDEX IF NOT EXISTS idx_inventory_date_baked ON inventory(date_baked);
CREATE INDEX IF NOT EXISTS idx_inventory_good_until ON inventory(good_until);
CREATE INDEX IF NOT EXISTS idx_inventory_expiration ON inventory(expiration_date);
CREATE INDEX IF NOT EXISTS idx_inventory_stock ON inventory(stock_quantity);
CREATE INDEX IF NOT EXISTS idx_inventory_active ON inventory(is_active);
CREATE INDEX IF NOT EXISTS idx_inventory_name ON inventory(name);

-- Inventory stock history for tracking changes
CREATE TABLE IF NOT EXISTS inventory_stock_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    item_name TEXT NOT NULL,
    old_quantity INTEGER NOT NULL,
    new_quantity INTEGER NOT NULL,
    change_amount INTEGER NOT NULL,
    change_type TEXT NOT NULL CHECK(change_type IN ('SALE', 'RESTOCK', 'ADJUSTMENT', 'EXPIRED', 'DAMAGED')),
    notes TEXT,
    changed_by TEXT,
    changed_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_name) REFERENCES inventory(name) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_stock_history_item ON inventory_stock_history(item_name);
CREATE INDEX IF NOT EXISTS idx_stock_history_date ON inventory_stock_history(changed_at);
CREATE INDEX IF NOT EXISTS idx_stock_history_type ON inventory_stock_history(change_type);

-- ========================================
-- SALES TRANSACTION TABLES
-- ========================================

-- Main sales transactions table
CREATE TABLE IF NOT EXISTS sales_transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    transaction_id TEXT NOT NULL UNIQUE,
    transaction_date TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal REAL NOT NULL,
    discount_amount REAL DEFAULT 0.00,
    tax_amount REAL DEFAULT 0.00,
    total REAL NOT NULL,
    payment_method TEXT DEFAULT 'CASH' CHECK(payment_method IN ('CASH', 'ECASH', 'CARD', 'OTHER')),
    payment_amount REAL DEFAULT 0.00,
    change_amount REAL DEFAULT 0.00,
    discount_applied INTEGER DEFAULT 0,
    discount_type TEXT,
    customer_type TEXT DEFAULT 'REGULAR' CHECK(customer_type IN ('REGULAR', 'SENIOR', 'PWD', 'EMPLOYEE')),
    served_by TEXT,
    notes TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sales_transaction_id ON sales_transactions(transaction_id);
CREATE INDEX IF NOT EXISTS idx_sales_transaction_date ON sales_transactions(transaction_date);
CREATE INDEX IF NOT EXISTS idx_sales_payment_method ON sales_transactions(payment_method);
CREATE INDEX IF NOT EXISTS idx_sales_customer_type ON sales_transactions(customer_type);
CREATE INDEX IF NOT EXISTS idx_sales_created_at ON sales_transactions(created_at);

-- Sales items detail table
CREATE TABLE IF NOT EXISTS sales_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    transaction_id TEXT NOT NULL,
    item_name TEXT NOT NULL,
    category TEXT,
    price REAL NOT NULL,
    quantity INTEGER NOT NULL,
    subtotal REAL NOT NULL,
    discount_amount REAL DEFAULT 0.00,
    FOREIGN KEY (transaction_id) REFERENCES sales_transactions(transaction_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_sales_items_transaction ON sales_items(transaction_id);
CREATE INDEX IF NOT EXISTS idx_sales_items_name ON sales_items(item_name);
CREATE INDEX IF NOT EXISTS idx_sales_items_category ON sales_items(category);

-- ========================================
-- ADMIN & USER MANAGEMENT TABLES
-- ========================================

-- Admin users table with role-based access
CREATE TABLE IF NOT EXISTS admin_users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    full_name TEXT NOT NULL,
    email TEXT UNIQUE,
    role TEXT DEFAULT 'STAFF' CHECK(role IN ('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'STAFF')),
    phone TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    last_login TEXT,
    is_active INTEGER DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_admin_username ON admin_users(username);
CREATE INDEX IF NOT EXISTS idx_admin_email ON admin_users(email);
CREATE INDEX IF NOT EXISTS idx_admin_role ON admin_users(role);
CREATE INDEX IF NOT EXISTS idx_admin_active ON admin_users(is_active);

-- Admin activity log for audit trail
CREATE TABLE IF NOT EXISTS admin_activity_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    admin_username TEXT NOT NULL,
    action_type TEXT NOT NULL,
    action_description TEXT,
    affected_table TEXT,
    affected_record_id TEXT,
    ip_address TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_username) REFERENCES admin_users(username) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_activity_admin ON admin_activity_log(admin_username);
CREATE INDEX IF NOT EXISTS idx_activity_type ON admin_activity_log(action_type);
CREATE INDEX IF NOT EXISTS idx_activity_created ON admin_activity_log(created_at);

-- ========================================
-- CUSTOMER SUPPORT TABLES
-- ========================================

-- Help requests table
CREATE TABLE IF NOT EXISTS help_requests (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    request_id TEXT NOT NULL UNIQUE,
    location TEXT NOT NULL,
    request_type TEXT NOT NULL,
    description TEXT,
    priority TEXT DEFAULT 'MEDIUM' CHECK(priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),
    status TEXT DEFAULT 'PENDING' CHECK(status IN ('PENDING', 'IN_PROGRESS', 'RESOLVED', 'CANCELLED')),
    assigned_to TEXT,
    resolved_by TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    resolved_at TEXT,
    resolution_notes TEXT,
    FOREIGN KEY (assigned_to) REFERENCES admin_users(username) ON DELETE SET NULL,
    FOREIGN KEY (resolved_by) REFERENCES admin_users(username) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_help_request_id ON help_requests(request_id);
CREATE INDEX IF NOT EXISTS idx_help_status ON help_requests(status);
CREATE INDEX IF NOT EXISTS idx_help_priority ON help_requests(priority);
CREATE INDEX IF NOT EXISTS idx_help_created ON help_requests(created_at);
CREATE INDEX IF NOT EXISTS idx_help_assigned ON help_requests(assigned_to);

-- ========================================
-- ANALYTICS & REPORTING TABLES
-- ========================================

-- Daily sales summary for quick reporting
CREATE TABLE IF NOT EXISTS daily_sales_summary (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    summary_date TEXT NOT NULL UNIQUE,
    total_transactions INTEGER DEFAULT 0,
    total_items_sold INTEGER DEFAULT 0,
    gross_sales REAL DEFAULT 0.00,
    total_discounts REAL DEFAULT 0.00,
    total_tax REAL DEFAULT 0.00,
    net_sales REAL DEFAULT 0.00,
    cash_sales REAL DEFAULT 0.00,
    ecash_sales REAL DEFAULT 0.00,
    card_sales REAL DEFAULT 0.00,
    avg_transaction_value REAL DEFAULT 0.00,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_daily_summary_date ON daily_sales_summary(summary_date);
CREATE INDEX IF NOT EXISTS idx_daily_summary_created ON daily_sales_summary(created_at);

-- Product sales analytics for performance tracking
CREATE TABLE IF NOT EXISTS product_sales_analytics (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    item_name TEXT NOT NULL,
    category TEXT,
    analysis_period TEXT NOT NULL CHECK(analysis_period IN ('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY')),
    period_start_date TEXT NOT NULL,
    period_end_date TEXT NOT NULL,
    units_sold INTEGER DEFAULT 0,
    total_revenue REAL DEFAULT 0.00,
    avg_price REAL DEFAULT 0.00,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_analytics_item ON product_sales_analytics(item_name);
CREATE INDEX IF NOT EXISTS idx_analytics_category ON product_sales_analytics(category);
CREATE INDEX IF NOT EXISTS idx_analytics_period ON product_sales_analytics(analysis_period);
CREATE INDEX IF NOT EXISTS idx_analytics_dates ON product_sales_analytics(period_start_date, period_end_date);

-- ========================================
-- SYSTEM CONFIGURATION TABLE
-- ========================================

-- System settings and configuration
CREATE TABLE IF NOT EXISTS system_settings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    setting_key TEXT NOT NULL UNIQUE,
    setting_value TEXT,
    setting_type TEXT DEFAULT 'STRING',
    description TEXT,
    updated_by TEXT,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_settings_key ON system_settings(setting_key);

-- ========================================
-- CUSTOM ORDERS SYSTEM TABLES
-- ========================================

-- Customer accounts table
CREATE TABLE IF NOT EXISTS customers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_id TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    phone TEXT NOT NULL,
    date_of_birth TEXT,
    address_line1 TEXT,
    address_line2 TEXT,
    city TEXT,
    state_province TEXT,
    postal_code TEXT,
    country TEXT DEFAULT 'Philippines',
    customer_type TEXT DEFAULT 'REGULAR' CHECK(customer_type IN ('REGULAR', 'VIP', 'WHOLESALE')),
    loyalty_points INTEGER DEFAULT 0,
    total_orders INTEGER DEFAULT 0,
    total_spent REAL DEFAULT 0.00,
    is_active INTEGER DEFAULT 1,
    is_email_verified INTEGER DEFAULT 0,
    email_verification_token TEXT,
    password_reset_token TEXT,
    password_reset_expires TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    last_login TEXT,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_customer_id ON customers(customer_id);
CREATE INDEX IF NOT EXISTS idx_customer_email ON customers(email);
CREATE INDEX IF NOT EXISTS idx_customer_phone ON customers(phone);
CREATE INDEX IF NOT EXISTS idx_customer_type ON customers(customer_type);
CREATE INDEX IF NOT EXISTS idx_customer_active ON customers(is_active);
CREATE INDEX IF NOT EXISTS idx_customer_created ON customers(created_at);

-- Customer addresses
CREATE TABLE IF NOT EXISTS customer_addresses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_id TEXT NOT NULL,
    address_label TEXT NOT NULL,
    recipient_name TEXT,
    phone TEXT,
    address_line1 TEXT NOT NULL,
    address_line2 TEXT,
    city TEXT NOT NULL,
    state_province TEXT,
    postal_code TEXT,
    country TEXT DEFAULT 'Philippines',
    is_default INTEGER DEFAULT 0,
    delivery_instructions TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_address_customer ON customer_addresses(customer_id);
CREATE INDEX IF NOT EXISTS idx_address_default ON customer_addresses(is_default);

-- Custom order categories
CREATE TABLE IF NOT EXISTS custom_order_categories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_code TEXT NOT NULL UNIQUE,
    category_name TEXT NOT NULL,
    description TEXT,
    icon_path TEXT,
    min_order_lead_time_days INTEGER DEFAULT 3,
    is_active INTEGER DEFAULT 1,
    display_order INTEGER DEFAULT 0,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_custom_category_code ON custom_order_categories(category_code);
CREATE INDEX IF NOT EXISTS idx_custom_category_active ON custom_order_categories(is_active);
CREATE INDEX IF NOT EXISTS idx_custom_category_order ON custom_order_categories(display_order);

-- Base products for customization
CREATE TABLE IF NOT EXISTS custom_order_base_products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_code TEXT NOT NULL UNIQUE,
    category_code TEXT NOT NULL,
    product_name TEXT NOT NULL,
    description TEXT,
    base_price REAL NOT NULL,
    price_per_serving REAL,
    min_servings INTEGER DEFAULT 1,
    max_servings INTEGER DEFAULT 200,
    preparation_time_hours INTEGER DEFAULT 24,
    image_path TEXT,
    is_active INTEGER DEFAULT 1,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_code) REFERENCES custom_order_categories(category_code) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_base_product_code ON custom_order_base_products(product_code);
CREATE INDEX IF NOT EXISTS idx_base_category ON custom_order_base_products(category_code);
CREATE INDEX IF NOT EXISTS idx_base_active ON custom_order_base_products(is_active);

-- Add-on categories
CREATE TABLE IF NOT EXISTS addon_categories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_code TEXT NOT NULL UNIQUE,
    category_name TEXT NOT NULL,
    description TEXT,
    selection_type TEXT DEFAULT 'SINGLE' CHECK(selection_type IN ('SINGLE', 'MULTIPLE', 'OPTIONAL')),
    max_selections INTEGER DEFAULT 1,
    is_required INTEGER DEFAULT 0,
    display_order INTEGER DEFAULT 0,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_addon_cat_code ON addon_categories(category_code);
CREATE INDEX IF NOT EXISTS idx_addon_cat_order ON addon_categories(display_order);

-- Individual add-ons
CREATE TABLE IF NOT EXISTS addons (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    addon_code TEXT NOT NULL UNIQUE,
    category_code TEXT NOT NULL,
    addon_name TEXT NOT NULL,
    description TEXT,
    price_modifier REAL DEFAULT 0.00,
    price_type TEXT DEFAULT 'FLAT' CHECK(price_type IN ('FLAT', 'PERCENTAGE', 'PER_SERVING')),
    image_path TEXT,
    is_active INTEGER DEFAULT 1,
    is_premium INTEGER DEFAULT 0,
    stock_limited INTEGER DEFAULT 0,
    stock_quantity INTEGER,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_code) REFERENCES addon_categories(category_code) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_addon_code ON addons(addon_code);
CREATE INDEX IF NOT EXISTS idx_addon_category ON addons(category_code);
CREATE INDEX IF NOT EXISTS idx_addon_active ON addons(is_active);
CREATE INDEX IF NOT EXISTS idx_addon_premium ON addons(is_premium);

-- Product-addon compatibility
CREATE TABLE IF NOT EXISTS product_addon_compatibility (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_code TEXT NOT NULL,
    addon_category_code TEXT NOT NULL,
    is_required INTEGER DEFAULT 0,
    display_order INTEGER DEFAULT 0,
    FOREIGN KEY (product_code) REFERENCES custom_order_base_products(product_code) ON DELETE CASCADE,
    FOREIGN KEY (addon_category_code) REFERENCES addon_categories(category_code) ON DELETE CASCADE,
    UNIQUE(product_code, addon_category_code)
);

CREATE INDEX IF NOT EXISTS idx_compat_product ON product_addon_compatibility(product_code);
CREATE INDEX IF NOT EXISTS idx_compat_addon ON product_addon_compatibility(addon_category_code);

-- Main custom orders table
CREATE TABLE IF NOT EXISTS custom_orders (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_number TEXT NOT NULL UNIQUE,
    customer_id TEXT NOT NULL,
    product_code TEXT NOT NULL,
    order_type TEXT NOT NULL,
    servings INTEGER NOT NULL,
    message_on_item TEXT,
    special_instructions TEXT,
    base_price REAL NOT NULL,
    addons_total REAL DEFAULT 0.00,
    subtotal REAL NOT NULL,
    discount_amount REAL DEFAULT 0.00,
    tax_amount REAL DEFAULT 0.00,
    delivery_fee REAL DEFAULT 0.00,
    total_amount REAL NOT NULL,
    deposit_required REAL DEFAULT 0.00,
    deposit_paid REAL DEFAULT 0.00,
    deposit_payment_method TEXT,
    deposit_paid_at TEXT,
    balance_due REAL DEFAULT 0.00,
    payment_status TEXT DEFAULT 'PENDING' CHECK(payment_status IN ('PENDING', 'DEPOSIT_PAID', 'FULLY_PAID', 'REFUNDED')),
    fulfillment_type TEXT NOT NULL DEFAULT 'PICKUP' CHECK(fulfillment_type IN ('PICKUP', 'DELIVERY')),
    pickup_datetime TEXT NOT NULL,
    delivery_address_id INTEGER,
    delivery_datetime TEXT,
    order_status TEXT DEFAULT 'PENDING' CHECK(order_status IN ('PENDING', 'CONFIRMED', 'IN_PRODUCTION', 'READY', 'COMPLETED', 'CANCELLED')),
    admin_notes TEXT,
    cancellation_reason TEXT,
    cancelled_at TEXT,
    cancelled_by TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TEXT,
    completed_at TEXT,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP,
    assigned_baker TEXT,
    assigned_decorator TEXT,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (product_code) REFERENCES custom_order_base_products(product_code),
    FOREIGN KEY (delivery_address_id) REFERENCES customer_addresses(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_order_number ON custom_orders(order_number);
CREATE INDEX IF NOT EXISTS idx_order_customer ON custom_orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_order_product ON custom_orders(product_code);
CREATE INDEX IF NOT EXISTS idx_order_status ON custom_orders(order_status);
CREATE INDEX IF NOT EXISTS idx_order_payment ON custom_orders(payment_status);
CREATE INDEX IF NOT EXISTS idx_order_fulfillment ON custom_orders(fulfillment_type);
CREATE INDEX IF NOT EXISTS idx_order_pickup ON custom_orders(pickup_datetime);
CREATE INDEX IF NOT EXISTS idx_order_created ON custom_orders(created_at);
CREATE INDEX IF NOT EXISTS idx_order_confirmed ON custom_orders(confirmed_at);

-- Custom order add-ons
CREATE TABLE IF NOT EXISTS custom_order_addons (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_number TEXT NOT NULL,
    addon_code TEXT NOT NULL,
    addon_category TEXT NOT NULL,
    quantity INTEGER DEFAULT 1,
    price_modifier REAL NOT NULL,
    total_addon_price REAL NOT NULL,
    FOREIGN KEY (order_number) REFERENCES custom_orders(order_number) ON DELETE CASCADE,
    FOREIGN KEY (addon_code) REFERENCES addons(addon_code)
);

CREATE INDEX IF NOT EXISTS idx_order_addon_order ON custom_order_addons(order_number);
CREATE INDEX IF NOT EXISTS idx_order_addon_code ON custom_order_addons(addon_code);

-- Custom order attachments
CREATE TABLE IF NOT EXISTS custom_order_attachments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_number TEXT NOT NULL,
    file_name TEXT NOT NULL,
    file_path TEXT NOT NULL,
    file_type TEXT,
    file_size_kb INTEGER,
    description TEXT,
    uploaded_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_number) REFERENCES custom_orders(order_number) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_attachment_order ON custom_order_attachments(order_number);

-- Custom order status history
CREATE TABLE IF NOT EXISTS custom_order_status_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_number TEXT NOT NULL,
    old_status TEXT,
    new_status TEXT NOT NULL,
    notes TEXT,
    changed_by TEXT,
    changed_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_number) REFERENCES custom_orders(order_number) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_status_history_order ON custom_order_status_history(order_number);
CREATE INDEX IF NOT EXISTS idx_status_history_changed ON custom_order_status_history(changed_at);

-- Order reviews
CREATE TABLE IF NOT EXISTS custom_order_reviews (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_number TEXT NOT NULL,
    customer_id TEXT NOT NULL,
    rating INTEGER NOT NULL CHECK(rating >= 1 AND rating <= 5),
    review_title TEXT,
    review_text TEXT,
    would_recommend INTEGER DEFAULT 1,
    image_path TEXT,
    admin_response TEXT,
    responded_at TEXT,
    is_approved INTEGER DEFAULT 0,
    is_featured INTEGER DEFAULT 0,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_number) REFERENCES custom_orders(order_number) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_review_order ON custom_order_reviews(order_number);
CREATE INDEX IF NOT EXISTS idx_review_customer ON custom_order_reviews(customer_id);
CREATE INDEX IF NOT EXISTS idx_review_rating ON custom_order_reviews(rating);
CREATE INDEX IF NOT EXISTS idx_review_approved ON custom_order_reviews(is_approved);
CREATE INDEX IF NOT EXISTS idx_review_featured ON custom_order_reviews(is_featured);

-- ========================================
-- SAMPLE DATA INSERTIONS
-- ========================================

-- Insert default admin users
INSERT OR IGNORE INTO admin_users (username, password_hash, full_name, email, role) VALUES
('admin', 'admin123', 'System Administrator', 'admin@sweetbatter.com', 'SUPER_ADMIN'),
('manager', 'manager123', 'Store Manager', 'manager@sweetbatter.com', 'MANAGER');

-- Insert system settings
INSERT OR IGNORE INTO system_settings (setting_key, setting_value, setting_type, description) VALUES
('STORE_NAME', 'SweetBatterBakeshop', 'STRING', 'Store name displayed on receipts'),
('TAX_RATE', '0.12', 'DECIMAL', 'VAT tax rate (12%)'),
('LOW_STOCK_THRESHOLD', '10', 'INTEGER', 'Minimum stock level before warning'),
('SENIOR_PWD_DISCOUNT', '0.20', 'DECIMAL', 'Senior/PWD discount rate (20%)'),
('BUSINESS_HOURS_OPEN', '08:00', 'TIME', 'Store opening time'),
('BUSINESS_HOURS_CLOSE', '20:00', 'TIME', 'Store closing time'),
('RECEIPT_FOOTER', 'Thank you for your purchase!', 'STRING', 'Message at bottom of receipt'),
('ENABLE_NOTIFICATIONS', 'true', 'BOOLEAN', 'Enable system notifications');

-- Sample inventory items
INSERT OR IGNORE INTO inventory (name, category, price, stock_quantity, min_stock_level, date_baked, good_until, expiration_date, supplier, description) VALUES
('French Baguette', 'Breads & Rolls', 85.00, 30, 10, date('now'), date('now', '+1 day'), date('now', '+2 days'), 'Bakery Chef', 'Fresh daily baked French bread'),
('Chocolate Croissant', 'Pastries & Desserts', 75.00, 35, 15, date('now'), date('now', '+1 day'), date('now', '+2 days'), 'French Bakery', 'Buttery croissant with rich chocolate'),
('Ube Pan de Sal', 'Breads & Rolls', 45.00, 50, 20, date('now'), date('now', '+1 day'), date('now', '+2 days'), 'Local Bakery', 'Filipino favorite with ube flavor'),
('Fresh Brewed Coffee', 'Beverages & Extras', 55.00, 100, 25, NULL, NULL, date('now', '+30 days'), 'Coffee Supplier', 'Premium arabica coffee'),
('Chocolate Cake Slice', 'Cakes & Special Occasions', 120.00, 20, 5, date('now', '-1 day'), date('now', '+2 days'), date('now', '+3 days'), 'Cake Masters', 'Rich chocolate layer cake'),
('Ensaymada', 'Pastries & Desserts', 35.00, 40, 15, date('now'), date('now', '+1 day'), date('now', '+2 days'), 'Local Bakery', 'Sweet Filipino pastry with butter and sugar'),
('Cinnamon Roll', 'Pastries & Desserts', 65.00, 25, 10, date('now'), date('now', '+1 day'), date('now', '+2 days'), 'Bakery Chef', 'Soft roll with cinnamon and cream cheese frosting'),
('Orange Juice', 'Beverages & Extras', 45.00, 60, 20, NULL, date('now', '+3 days'), date('now', '+5 days'), 'Juice Co', 'Freshly squeezed orange juice');

-- Insert custom order categories
INSERT OR IGNORE INTO custom_order_categories (category_code, category_name, description, min_order_lead_time_days) VALUES
('CUSTOM_CAKE', 'Custom Cakes', 'Personalized cakes for any occasion', 3),
('BULK_PASTRIES', 'Bulk Pastries', 'Large quantity pastries for events', 2),
('BULK_BREADS', 'Bulk Breads', 'Wholesale bread orders', 1),
('CUSTOM_DESSERT', 'Custom Desserts', 'Specialty dessert platters', 2);

-- Insert base products
INSERT OR IGNORE INTO custom_order_base_products (product_code, category_code, product_name, description, base_price, price_per_serving, min_servings, max_servings, preparation_time_hours) VALUES
('CAKE_ROUND_BASIC', 'CUSTOM_CAKE', 'Round Basic Cake', 'Classic round cake with basic decoration', 800.00, 50.00, 10, 100, 24),
('CAKE_ROUND_PREMIUM', 'CUSTOM_CAKE', 'Round Premium Cake', 'Premium round cake with elaborate decoration', 1500.00, 80.00, 10, 100, 48),
('CAKE_TIERED_2', 'CUSTOM_CAKE', '2-Tier Cake', 'Elegant 2-tier cake', 2500.00, 100.00, 20, 150, 48),
('BULK_CUPCAKES', 'BULK_PASTRIES', 'Cupcake Assortment', 'Assorted cupcakes in bulk', 50.00, 50.00, 12, 500, 12);

-- Insert addon categories
INSERT OR IGNORE INTO addon_categories (category_code, category_name, description, selection_type, max_selections, is_required) VALUES
('FLAVOR', 'Cake Flavor', 'Choose your cake flavor', 'SINGLE', 1, 1),
('FILLING', 'Cake Filling', 'Select cake filling', 'SINGLE', 1, 1),
('FROSTING', 'Frosting Type', 'Choose frosting style', 'SINGLE', 1, 1),
('DECORATION', 'Decoration Theme', 'Select decoration theme', 'SINGLE', 1, 0);

-- Insert addons
INSERT OR IGNORE INTO addons (addon_code, category_code, addon_name, description, price_modifier, price_type) VALUES
('FLAVOR_VANILLA', 'FLAVOR', 'Classic Vanilla', 'Traditional vanilla cake', 0.00, 'FLAT'),
('FLAVOR_CHOCOLATE', 'FLAVOR', 'Rich Chocolate', 'Decadent chocolate cake', 50.00, 'FLAT'),
('FLAVOR_UBE', 'FLAVOR', 'Ube (Purple Yam)', 'Filipino ube flavored cake', 120.00, 'FLAT'),
('FILLING_BUTTERCREAM', 'FILLING', 'Buttercream', 'Classic buttercream filling', 0.00, 'FLAT'),
('FILLING_CHOCOLATE_GANACHE', 'FILLING', 'Chocolate Ganache', 'Rich chocolate ganache', 100.00, 'FLAT'),
('FROSTING_BUTTERCREAM', 'FROSTING', 'Buttercream', 'Classic buttercream frosting', 0.00, 'FLAT'),
('FROSTING_FONDANT', 'FROSTING', 'Fondant', 'Smooth fondant covering', 200.00, 'FLAT');

-- Insert sample customer
INSERT OR IGNORE INTO customers (customer_id, email, password_hash, first_name, last_name, phone, customer_type) VALUES
('CUST-20250101-0001', 'customer@example.com', 'password123', 'John', 'Doe', '09171234567', 'REGULAR');

-- ========================================
-- COMPLETION MESSAGE
-- ========================================
SELECT '✅ SQLite Database setup completed successfully!' AS Status,
       'All tables, indexes, and sample data created.' AS Details,
       'Database file: bakery_kiosk.db' AS Info;
