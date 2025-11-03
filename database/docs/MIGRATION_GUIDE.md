# 🔄 Database Migration Guide

## Overview
This guide walks you through upgrading from the basic database schema to the fully integrated admin panel schema with enhanced analytics, audit trails, and reporting capabilities.

---

## ⚠️ Before You Start

### Backup Your Data
**CRITICAL:** Always backup your database before migration!

```bash
# Full database backup
mysqldump -u root -p kiosk_db > backup_kiosk_db_$(date +%Y%m%d).sql

# Backup specific tables only
mysqldump -u root -p kiosk_db inventory sales_transactions sales_items > backup_critical_tables.sql
```

### Verify Current Schema
```sql
USE kiosk_db;
SHOW TABLES;
DESCRIBE inventory;
DESCRIBE sales_transactions;
```

---

## 📋 Migration Options

### Option 1: Fresh Installation (Recommended for Development)
**Best for:** New installations or testing environments

```bash
# 1. Drop existing database (⚠️ ALL DATA WILL BE LOST!)
mysql -u root -p -e "DROP DATABASE IF EXISTS kiosk_db;"

# 2. Run full setup
mysql -u root -p < database/sql/setup.sql

# 3. Load test data (optional)
mysql -u root -p kiosk_db < database/sql/test_data.sql
```

---

### Option 2: In-Place Upgrade (Preserves Existing Data)
**Best for:** Production systems with existing data

#### Step 1: Backup (Required!)
```bash
mysqldump -u root -p kiosk_db > backup_before_migration.sql
```

#### Step 2: Add New Columns to Existing Tables
```sql
USE kiosk_db;

-- Upgrade inventory table
ALTER TABLE inventory 
ADD COLUMN IF NOT EXISTS min_stock_level INT DEFAULT 10 AFTER stock_quantity,
ADD COLUMN IF NOT EXISTS description TEXT AFTER supplier,
ADD COLUMN IF NOT EXISTS image_path VARCHAR(500) AFTER description,
ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE AFTER image_path,
ADD INDEX IF NOT EXISTS idx_active (is_active),
ADD INDEX IF NOT EXISTS idx_name (name);

-- Upgrade sales_transactions table
ALTER TABLE sales_transactions
ADD COLUMN IF NOT EXISTS tax_amount DECIMAL(10,2) DEFAULT 0.00 AFTER discount_amount,
ADD COLUMN IF NOT EXISTS payment_method ENUM('CASH', 'ECASH', 'CARD', 'OTHER') DEFAULT 'CASH' AFTER total,
ADD COLUMN IF NOT EXISTS payment_amount DECIMAL(10,2) DEFAULT 0.00 AFTER payment_method,
ADD COLUMN IF NOT EXISTS change_amount DECIMAL(10,2) DEFAULT 0.00 AFTER payment_amount,
ADD COLUMN IF NOT EXISTS discount_type VARCHAR(50) AFTER discount_applied,
ADD COLUMN IF NOT EXISTS customer_type ENUM('REGULAR', 'SENIOR', 'PWD', 'EMPLOYEE') DEFAULT 'REGULAR' AFTER discount_type,
ADD COLUMN IF NOT EXISTS served_by VARCHAR(100) AFTER customer_type,
ADD COLUMN IF NOT EXISTS notes TEXT AFTER served_by,
ADD INDEX IF NOT EXISTS idx_payment_method (payment_method),
ADD INDEX IF NOT EXISTS idx_customer_type (customer_type),
ADD INDEX IF NOT EXISTS idx_created_at (created_at);

-- Upgrade sales_items table
ALTER TABLE sales_items
ADD COLUMN IF NOT EXISTS category VARCHAR(100) AFTER item_name,
ADD COLUMN IF NOT EXISTS discount_amount DECIMAL(10,2) DEFAULT 0.00 AFTER subtotal,
ADD INDEX IF NOT EXISTS idx_category (category);

-- Upgrade admin_users table
ALTER TABLE admin_users
ADD COLUMN IF NOT EXISTS role ENUM('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'STAFF') DEFAULT 'STAFF' AFTER email,
ADD COLUMN IF NOT EXISTS phone VARCHAR(20) AFTER role,
ADD INDEX IF NOT EXISTS idx_role (role);

-- Upgrade help_requests table
ALTER TABLE help_requests
ADD COLUMN IF NOT EXISTS request_id VARCHAR(50) AFTER id,
ADD COLUMN IF NOT EXISTS location VARCHAR(100) NOT NULL DEFAULT 'Unknown' AFTER request_id,
ADD COLUMN IF NOT EXISTS priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') DEFAULT 'MEDIUM' AFTER description,
ADD COLUMN IF NOT EXISTS assigned_to VARCHAR(50) AFTER status,
ADD COLUMN IF NOT EXISTS resolved_by VARCHAR(50) AFTER assigned_to,
ADD COLUMN IF NOT EXISTS resolved_at TIMESTAMP NULL AFTER resolved_by,
ADD COLUMN IF NOT EXISTS resolution_notes TEXT AFTER resolved_at,
ADD INDEX IF NOT EXISTS idx_request_id (request_id),
ADD INDEX IF NOT EXISTS idx_priority (priority),
ADD INDEX IF NOT EXISTS idx_assigned_to (assigned_to);

-- Generate request_id for existing help requests
UPDATE help_requests 
SET request_id = CONCAT('HELP-', LPAD(id, 5, '0'))
WHERE request_id IS NULL;

-- Make request_id unique
ALTER TABLE help_requests
ADD UNIQUE INDEX IF NOT EXISTS idx_request_id_unique (request_id);

-- Add foreign keys for help_requests if they don't exist
ALTER TABLE help_requests
ADD CONSTRAINT IF NOT EXISTS fk_help_assigned 
    FOREIGN KEY (assigned_to) REFERENCES admin_users(username) ON DELETE SET NULL,
ADD CONSTRAINT IF NOT EXISTS fk_help_resolved 
    FOREIGN KEY (resolved_by) REFERENCES admin_users(username) ON DELETE SET NULL;
```

#### Step 3: Create New Tables
```sql
-- Create inventory_stock_history table
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

-- Create admin_activity_log table
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

-- Create daily_sales_summary table
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

-- Create product_sales_analytics table
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

-- Create system_settings table
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
```

#### Step 4: Insert Default System Settings
```sql
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
```

#### Step 5: Create Views
```sql
-- View for expired items
CREATE OR REPLACE VIEW v_expired_items AS
SELECT 
    name, category, stock_quantity, expiration_date,
    DATEDIFF(CURDATE(), expiration_date) AS days_expired,
    price * stock_quantity AS value_lost
FROM inventory
WHERE expiration_date < CURDATE() AND is_active = TRUE
ORDER BY expiration_date;

-- View for low stock items
CREATE OR REPLACE VIEW v_low_stock_items AS
SELECT 
    name, category, stock_quantity, min_stock_level,
    (min_stock_level - stock_quantity) AS stock_deficit, supplier
FROM inventory
WHERE stock_quantity < min_stock_level AND is_active = TRUE
ORDER BY stock_deficit DESC;

-- View for expiring soon
CREATE OR REPLACE VIEW v_expiring_soon AS
SELECT 
    name, category, stock_quantity, expiration_date,
    DATEDIFF(expiration_date, CURDATE()) AS days_until_expiration
FROM inventory
WHERE expiration_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
  AND is_active = TRUE
ORDER BY expiration_date;

-- View for today's sales
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

-- View for top products
CREATE OR REPLACE VIEW v_top_products AS
SELECT 
    si.item_name, si.category,
    SUM(si.quantity) AS total_sold,
    SUM(si.subtotal) AS total_revenue,
    AVG(si.price) AS avg_price,
    COUNT(DISTINCT si.transaction_id) AS transaction_count
FROM sales_items si
GROUP BY si.item_name, si.category
ORDER BY total_sold DESC
LIMIT 20;
```

#### Step 6: Create Stored Procedures
```sql
-- Procedure to update daily sales summary
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS update_daily_sales_summary(IN target_date DATE)
BEGIN
    INSERT INTO daily_sales_summary (
        summary_date, total_transactions, total_items_sold,
        gross_sales, total_discounts, total_tax, net_sales,
        cash_sales, ecash_sales, card_sales, avg_transaction_value
    )
    SELECT 
        DATE(st.transaction_date), COUNT(DISTINCT st.transaction_id),
        SUM(si.quantity), SUM(st.subtotal), SUM(st.discount_amount),
        SUM(st.tax_amount), SUM(st.total),
        SUM(CASE WHEN st.payment_method = 'CASH' THEN st.total ELSE 0 END),
        SUM(CASE WHEN st.payment_method = 'ECASH' THEN st.total ELSE 0 END),
        SUM(CASE WHEN st.payment_method = 'CARD' THEN st.total ELSE 0 END),
        AVG(st.total)
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

-- Procedure to record stock changes
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
        item_name, old_quantity, new_quantity, change_amount,
        change_type, notes, changed_by
    ) VALUES (
        p_item_name, p_old_qty, p_new_qty, p_new_qty - p_old_qty,
        p_change_type, p_notes, p_changed_by
    );
END //
DELIMITER ;
```

#### Step 7: Create Triggers
```sql
DELIMITER //
CREATE TRIGGER IF NOT EXISTS trg_inventory_stock_update
AFTER UPDATE ON inventory
FOR EACH ROW
BEGIN
    IF OLD.stock_quantity != NEW.stock_quantity THEN
        INSERT INTO inventory_stock_history (
            item_name, old_quantity, new_quantity, change_amount,
            change_type, notes
        ) VALUES (
            NEW.name, OLD.stock_quantity, NEW.stock_quantity,
            NEW.stock_quantity - OLD.stock_quantity, 'ADJUSTMENT',
            CONCAT('Stock updated from ', OLD.stock_quantity, ' to ', NEW.stock_quantity)
        );
    END IF;
END //
DELIMITER ;
```

#### Step 8: Update Existing Data
```sql
-- Update sales_items with category information
UPDATE sales_items si
JOIN inventory i ON si.item_name = i.name
SET si.category = i.category
WHERE si.category IS NULL;

-- Set default payment method for old transactions
UPDATE sales_transactions 
SET payment_method = 'CASH' 
WHERE payment_method IS NULL;

-- Calculate tax for existing transactions (12% of subtotal after discount)
UPDATE sales_transactions
SET tax_amount = (subtotal - discount_amount) * 0.12
WHERE tax_amount IS NULL OR tax_amount = 0;

-- Set minimum stock levels by category
UPDATE inventory 
SET min_stock_level = CASE 
    WHEN category = 'Breads & Rolls' THEN 20
    WHEN category = 'Pastries & Desserts' THEN 15
    WHEN category = 'Cakes & Special Occasions' THEN 10
    WHEN category = 'Beverages & Extras' THEN 25
    ELSE 10
END
WHERE min_stock_level IS NULL OR min_stock_level = 0;
```

#### Step 9: Build Historical Summaries
```sql
-- Build daily summaries for all existing data
INSERT INTO daily_sales_summary (
    summary_date, total_transactions, total_items_sold,
    gross_sales, total_discounts, total_tax, net_sales,
    cash_sales, ecash_sales, card_sales, avg_transaction_value
)
SELECT 
    DATE(st.transaction_date),
    COUNT(DISTINCT st.transaction_id),
    SUM(si.quantity),
    SUM(st.subtotal),
    SUM(st.discount_amount),
    SUM(st.tax_amount),
    SUM(st.total),
    SUM(CASE WHEN st.payment_method = 'CASH' THEN st.total ELSE 0 END),
    SUM(CASE WHEN st.payment_method = 'ECASH' THEN st.total ELSE 0 END),
    SUM(CASE WHEN st.payment_method = 'CARD' THEN st.total ELSE 0 END),
    AVG(st.total)
FROM sales_transactions st
LEFT JOIN sales_items si ON st.transaction_id = si.transaction_id
GROUP BY DATE(st.transaction_date)
ON DUPLICATE KEY UPDATE summary_date=summary_date;
```

#### Step 10: Verify Migration
```sql
-- Check all tables exist
SHOW TABLES;

-- Verify record counts
SELECT 'inventory' AS tbl, COUNT(*) AS cnt FROM inventory
UNION ALL SELECT 'sales_transactions', COUNT(*) FROM sales_transactions
UNION ALL SELECT 'sales_items', COUNT(*) FROM sales_items
UNION ALL SELECT 'admin_users', COUNT(*) FROM admin_users
UNION ALL SELECT 'help_requests', COUNT(*) FROM help_requests
UNION ALL SELECT 'system_settings', COUNT(*) FROM system_settings;

-- Test views
SELECT * FROM v_today_sales;
SELECT * FROM v_top_products LIMIT 5;

-- Test procedures
CALL update_daily_sales_summary(CURDATE());
```

---

## ✅ Post-Migration Checklist

- [ ] All tables created successfully
- [ ] New columns added to existing tables
- [ ] Views created and returning data
- [ ] Stored procedures working
- [ ] Triggers active
- [ ] System settings populated
- [ ] Existing data preserved
- [ ] Test queries run successfully
- [ ] Application connects to database
- [ ] Admin panel displays correctly

---

## 🔙 Rollback Procedure

If migration fails:

```bash
# 1. Drop migrated database
mysql -u root -p -e "DROP DATABASE kiosk_db;"

# 2. Restore from backup
mysql -u root -p -e "CREATE DATABASE kiosk_db;"
mysql -u root -p kiosk_db < backup_before_migration.sql

# 3. Verify restoration
mysql -u root -p kiosk_db -e "SHOW TABLES; SELECT COUNT(*) FROM sales_transactions;"
```

---

## 📊 Performance Considerations

### After Migration
```sql
-- Optimize tables
OPTIMIZE TABLE inventory, sales_transactions, sales_items;

-- Update table statistics
ANALYZE TABLE inventory, sales_transactions, sales_items,
              admin_users, help_requests, daily_sales_summary;

-- Check table sizes
SELECT table_name,
       ROUND((data_length + index_length) / 1024 / 1024, 2) AS size_mb
FROM information_schema.TABLES
WHERE table_schema = 'kiosk_db'
ORDER BY (data_length + index_length) DESC;
```

---

## 🆘 Troubleshooting

### Issue: Foreign Key Constraint Fails
```sql
SET FOREIGN_KEY_CHECKS = 0;
-- Run your ALTER statements
SET FOREIGN_KEY_CHECKS = 1;
```

### Issue: Duplicate Entry Errors
```sql
-- Check for duplicates before adding UNIQUE constraint
SELECT name, COUNT(*) 
FROM inventory 
GROUP BY name 
HAVING COUNT(*) > 1;
```

### Issue: Migration Timeout
```sql
-- Increase timeout
SET SESSION max_execution_time = 300000; -- 5 minutes
SET SESSION wait_timeout = 300;
```

---

## 📝 Notes

- Always test migration on a copy of production data first
- Schedule migration during low-traffic periods
- Keep backups for at least 30 days
- Document any custom modifications
- Update application.properties with any config changes

---

**Migration Version:** 1.0 → 2.0  
**Estimated Time:** 15-30 minutes (depending on data size)  
**Downtime Required:** 5-10 minutes (for production)
