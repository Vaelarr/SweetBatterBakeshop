-- Quick Setup Script for Custom Orders System
-- Run this after running setup.sql

USE kiosk_db;

-- Verify tables exist
SELECT 'Checking Custom Orders Tables...' AS Status;

SELECT 
    'customers' AS TableName,
    COUNT(*) AS RecordCount
FROM customers
UNION ALL
SELECT 
    'custom_orders' AS TableName,
    COUNT(*) AS RecordCount
FROM custom_orders
UNION ALL
SELECT 
    'custom_order_base_products' AS TableName,
    COUNT(*) AS RecordCount
FROM custom_order_base_products
UNION ALL
SELECT 
    'addons' AS TableName,
    COUNT(*) AS RecordCount
FROM addons
UNION ALL
SELECT 
    'addon_categories' AS TableName,
    COUNT(*) AS RecordCount
FROM addon_categories;

-- Show available products
SELECT 
    'Available Products' AS Info;
    
SELECT 
    category_name,
    COUNT(*) as product_count
FROM custom_order_base_products bp
JOIN custom_order_categories cat ON bp.category_code = cat.category_code
GROUP BY category_name;

-- Show add-on summary
SELECT 
    'Available Add-ons' AS Info;
    
SELECT 
    category_name,
    COUNT(*) as addon_count
FROM addons a
JOIN addon_categories ac ON a.category_code = ac.category_code
GROUP BY category_name;

SELECT 'Setup Complete! ✓' AS Status;
SELECT 'You can now use the Custom Orders System' AS Message;
