-- ========================================
-- Flush Test Transactions
-- Clear all sales transactions and related data
-- ========================================

USE kiosk_db;

-- Display current data before flushing
SELECT 'Before Flush - Sales Transactions:' as 'Status';
SELECT COUNT(*) as 'Total Transactions' FROM sales_transactions;
SELECT COUNT(*) as 'Total Sales Items' FROM sales_items;

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Clear sales items (child table first)
TRUNCATE TABLE sales_items;

-- Clear sales transactions
TRUNCATE TABLE sales_transactions;

-- Clear inventory stock history if exists
TRUNCATE TABLE inventory_stock_history;

-- Clear daily sales summary if exists
TRUNCATE TABLE daily_sales_summary;

-- Clear product sales analytics if exists
TRUNCATE TABLE product_sales_analytics;

-- Clear admin activity log if exists
TRUNCATE TABLE admin_activity_log;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Display results after flushing
SELECT 'After Flush - Sales Transactions:' as 'Status';
SELECT COUNT(*) as 'Total Transactions' FROM sales_transactions;
SELECT COUNT(*) as 'Total Sales Items' FROM sales_items;

SELECT 'Transactions flushed successfully!' as 'Result';
