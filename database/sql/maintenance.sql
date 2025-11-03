-- ========================================
-- DATABASE MAINTENANCE & UTILITIES
-- Routine maintenance tasks and utilities
-- ========================================

USE kiosk_db;

-- ========================================
-- DATA CLEANUP & MAINTENANCE
-- ========================================

-- Clean up old test/demo data (use with caution!)
-- Uncomment to execute
/*
DELETE FROM sales_items WHERE transaction_id IN (
    SELECT transaction_id FROM sales_transactions 
    WHERE transaction_date < DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
);

DELETE FROM sales_transactions 
WHERE transaction_date < DATE_SUB(CURDATE(), INTERVAL 1 YEAR);
*/

-- Archive old help requests
-- Move resolved requests older than 90 days to archive
/*
CREATE TABLE IF NOT EXISTS help_requests_archive LIKE help_requests;

INSERT INTO help_requests_archive 
SELECT * FROM help_requests 
WHERE status = 'RESOLVED' 
  AND resolved_at < DATE_SUB(CURDATE(), INTERVAL 90 DAY);

DELETE FROM help_requests 
WHERE status = 'RESOLVED' 
  AND resolved_at < DATE_SUB(CURDATE(), INTERVAL 90 DAY);
*/

-- Remove inactive products (soft deleted items)
/*
DELETE FROM inventory 
WHERE is_active = FALSE 
  AND updated_at < DATE_SUB(CURDATE(), INTERVAL 180 DAY);
*/

-- Clean up old admin activity logs (keep last 6 months)
/*
DELETE FROM admin_activity_log 
WHERE created_at < DATE_SUB(CURDATE(), INTERVAL 180 DAY);
*/

-- ========================================
-- DATA INTEGRITY CHECKS
-- ========================================

-- Find orphaned sales items (no matching transaction)
SELECT si.* 
FROM sales_items si
LEFT JOIN sales_transactions st ON si.transaction_id = st.transaction_id
WHERE st.transaction_id IS NULL;

-- Find sales transactions with no items
SELECT st.* 
FROM sales_transactions st
LEFT JOIN sales_items si ON st.transaction_id = si.transaction_id
WHERE si.id IS NULL;

-- Check for negative stock quantities
SELECT * FROM inventory 
WHERE stock_quantity < 0;

-- Check for price inconsistencies
SELECT 
    si.item_name,
    si.price AS sales_price,
    i.price AS inventory_price,
    si.price - i.price AS price_diff
FROM sales_items si
JOIN inventory i ON si.item_name = i.name
WHERE ABS(si.price - i.price) > 0.01
GROUP BY si.item_name;

-- Check for duplicate help requests
SELECT 
    location,
    request_type,
    COUNT(*) AS duplicate_count
FROM help_requests
WHERE status = 'PENDING'
  AND created_at >= DATE_SUB(NOW(), INTERVAL 1 HOUR)
GROUP BY location, request_type
HAVING duplicate_count > 1;

-- ========================================
-- BULK DATA OPERATIONS
-- ========================================

-- Bulk update: Mark all expired items as inactive
UPDATE inventory 
SET is_active = FALSE 
WHERE expiration_date < CURDATE();

-- Bulk update: Reset stock for discontinued items
UPDATE inventory 
SET stock_quantity = 0 
WHERE is_active = FALSE;

-- Bulk update: Apply minimum stock levels by category
UPDATE inventory 
SET min_stock_level = CASE 
    WHEN category = 'Breads & Rolls' THEN 20
    WHEN category = 'Pastries & Desserts' THEN 15
    WHEN category = 'Cakes & Special Occasions' THEN 10
    WHEN category = 'Beverages & Extras' THEN 25
    ELSE 10
END
WHERE min_stock_level IS NULL OR min_stock_level = 0;

-- Auto-resolve old pending help requests
UPDATE help_requests 
SET status = 'CANCELLED',
    resolution_notes = 'Auto-cancelled: No action taken within 24 hours'
WHERE status = 'PENDING' 
  AND created_at < DATE_SUB(NOW(), INTERVAL 24 HOUR);

-- ========================================
-- DATA EXPORT QUERIES
-- ========================================

-- Export today's sales for reporting
SELECT 
    st.transaction_id,
    st.transaction_date,
    st.payment_method,
    st.customer_type,
    st.subtotal,
    st.discount_amount,
    st.tax_amount,
    st.total,
    GROUP_CONCAT(CONCAT(si.item_name, ' (', si.quantity, ')') SEPARATOR '; ') AS items
FROM sales_transactions st
LEFT JOIN sales_items si ON st.transaction_id = si.transaction_id
WHERE DATE(st.transaction_date) = CURDATE()
GROUP BY st.transaction_id
ORDER BY st.transaction_date DESC;

-- Export inventory for audit
SELECT 
    name,
    category,
    price,
    stock_quantity,
    min_stock_level,
    expiration_date,
    supplier,
    is_active,
    (price * stock_quantity) AS inventory_value
FROM inventory
ORDER BY category, name;

-- Export complete sales report for month
SELECT 
    DATE(st.transaction_date) AS date,
    st.transaction_id,
    si.item_name,
    si.category,
    si.quantity,
    si.price,
    si.subtotal,
    st.discount_amount,
    st.total,
    st.payment_method
FROM sales_transactions st
JOIN sales_items si ON st.transaction_id = si.transaction_id
WHERE MONTH(st.transaction_date) = MONTH(CURDATE())
  AND YEAR(st.transaction_date) = YEAR(CURDATE())
ORDER BY st.transaction_date, st.transaction_id;

-- ========================================
-- BACKUP & RESTORE HELPERS
-- ========================================

-- Create backup of critical tables (sample structure)
/*
CREATE TABLE inventory_backup_20240101 AS SELECT * FROM inventory;
CREATE TABLE sales_transactions_backup_20240101 AS SELECT * FROM sales_transactions;
CREATE TABLE sales_items_backup_20240101 AS SELECT * FROM sales_items;
*/

-- Verify backup table counts
/*
SELECT 
    (SELECT COUNT(*) FROM inventory) AS inventory_current,
    (SELECT COUNT(*) FROM inventory_backup_20240101) AS inventory_backup,
    (SELECT COUNT(*) FROM sales_transactions) AS transactions_current,
    (SELECT COUNT(*) FROM sales_transactions_backup_20240101) AS transactions_backup;
*/

-- ========================================
-- PERFORMANCE OPTIMIZATION
-- ========================================

-- Analyze table statistics
ANALYZE TABLE inventory;
ANALYZE TABLE sales_transactions;
ANALYZE TABLE sales_items;
ANALYZE TABLE admin_users;
ANALYZE TABLE help_requests;

-- Check table sizes
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS size_mb,
    table_rows
FROM information_schema.TABLES
WHERE table_schema = 'kiosk_db'
ORDER BY (data_length + index_length) DESC;

-- Find missing indexes
SELECT 
    DISTINCT CONCAT('Consider adding index: ',
                    'CREATE INDEX idx_', 
                    table_name, '_', 
                    column_name, 
                    ' ON ', 
                    table_name, 
                    '(', column_name, ');') AS suggestion
FROM information_schema.COLUMNS
WHERE table_schema = 'kiosk_db'
  AND table_name IN ('sales_transactions', 'sales_items', 'inventory')
  AND column_name IN ('category', 'status', 'payment_method', 'customer_type')
  AND NOT EXISTS (
      SELECT 1 FROM information_schema.STATISTICS
      WHERE table_schema = 'kiosk_db'
        AND table_name = COLUMNS.table_name
        AND column_name = COLUMNS.column_name
  );

-- ========================================
-- DAILY MAINTENANCE TASKS
-- ========================================

-- Run this daily to update summaries
CALL update_daily_sales_summary(CURDATE());
CALL update_daily_sales_summary(DATE_SUB(CURDATE(), INTERVAL 1 DAY));

-- Update product analytics (monthly)
-- This should be run at the start of each month
/*
INSERT INTO product_sales_analytics (
    item_name,
    category,
    analysis_period,
    period_start_date,
    period_end_date,
    units_sold,
    total_revenue,
    avg_price
)
SELECT 
    si.item_name,
    si.category,
    'MONTHLY',
    DATE_FORMAT(st.transaction_date, '%Y-%m-01') AS period_start,
    LAST_DAY(st.transaction_date) AS period_end,
    SUM(si.quantity) AS units_sold,
    SUM(si.subtotal) AS revenue,
    AVG(si.price) AS avg_price
FROM sales_items si
JOIN sales_transactions st ON si.transaction_id = st.transaction_id
WHERE MONTH(st.transaction_date) = MONTH(DATE_SUB(CURDATE(), INTERVAL 1 MONTH))
  AND YEAR(st.transaction_date) = YEAR(DATE_SUB(CURDATE(), INTERVAL 1 MONTH))
GROUP BY si.item_name, si.category, DATE_FORMAT(st.transaction_date, '%Y-%m-01')
ON DUPLICATE KEY UPDATE
    units_sold = VALUES(units_sold),
    total_revenue = VALUES(total_revenue),
    avg_price = VALUES(avg_price);
*/

-- ========================================
-- DIAGNOSTIC QUERIES
-- ========================================

-- Check database health
SELECT 
    'Total Products' AS metric, COUNT(*) AS value FROM inventory
UNION ALL
SELECT 'Active Products', COUNT(*) FROM inventory WHERE is_active = TRUE
UNION ALL
SELECT 'Total Transactions', COUNT(*) FROM sales_transactions
UNION ALL
SELECT 'Transactions Today', COUNT(*) FROM sales_transactions WHERE DATE(transaction_date) = CURDATE()
UNION ALL
SELECT 'Low Stock Items', COUNT(*) FROM inventory WHERE stock_quantity < min_stock_level
UNION ALL
SELECT 'Expired Items', COUNT(*) FROM inventory WHERE expiration_date < CURDATE()
UNION ALL
SELECT 'Pending Help Requests', COUNT(*) FROM help_requests WHERE status = 'PENDING'
UNION ALL
SELECT 'Active Admin Users', COUNT(*) FROM admin_users WHERE is_active = TRUE;

-- Check recent database activity
SELECT 
    'Last Sale' AS activity,
    MAX(transaction_date) AS last_occurrence
FROM sales_transactions
UNION ALL
SELECT 
    'Last Inventory Update',
    MAX(updated_at)
FROM inventory
UNION ALL
SELECT 
    'Last Help Request',
    MAX(created_at)
FROM help_requests
UNION ALL
SELECT 
    'Last Admin Login',
    MAX(last_login)
FROM admin_users;

-- System settings overview
SELECT 
    setting_key,
    setting_value,
    setting_type,
    description,
    updated_at
FROM system_settings
ORDER BY setting_key;

-- ========================================
-- END OF MAINTENANCE UTILITIES
-- ========================================
