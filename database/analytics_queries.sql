-- ========================================
-- ANALYTICS & REPORTING QUERIES
-- Pre-built queries for admin panel
-- ========================================

USE kiosk_db;

-- ========================================
-- SALES ANALYTICS
-- ========================================

-- Today's sales summary
SELECT 
    COUNT(DISTINCT st.transaction_id) AS total_transactions,
    SUM(st.total) AS total_revenue,
    SUM(st.discount_amount) AS total_discounts,
    AVG(st.total) AS avg_transaction,
    SUM(si.quantity) AS items_sold
FROM sales_transactions st
LEFT JOIN sales_items si ON st.transaction_id = si.transaction_id
WHERE DATE(st.transaction_date) = CURDATE();

-- Daily sales report (specific date)
SELECT 
    st.transaction_id,
    st.transaction_date,
    st.subtotal,
    st.discount_amount,
    st.tax_amount,
    st.total,
    st.payment_method,
    COUNT(si.id) AS item_count
FROM sales_transactions st
LEFT JOIN sales_items si ON st.transaction_id = si.transaction_id
WHERE DATE(st.transaction_date) = '2024-01-01' -- Replace with target date
GROUP BY st.transaction_id
ORDER BY st.transaction_date DESC;

-- Weekly sales report (last 7 days)
SELECT 
    DATE(st.transaction_date) AS sale_date,
    DAYNAME(st.transaction_date) AS day_name,
    COUNT(DISTINCT st.transaction_id) AS transactions,
    SUM(st.total) AS total_sales,
    SUM(st.discount_amount) AS discounts,
    SUM(si.quantity) AS items_sold,
    AVG(st.total) AS avg_transaction
FROM sales_transactions st
LEFT JOIN sales_items si ON st.transaction_id = si.transaction_id
WHERE st.transaction_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
GROUP BY DATE(st.transaction_date)
ORDER BY sale_date DESC;

-- Monthly sales report (current month)
SELECT 
    WEEK(st.transaction_date, 1) AS week_num,
    DATE(MIN(st.transaction_date)) AS week_start,
    DATE(MAX(st.transaction_date)) AS week_end,
    COUNT(DISTINCT st.transaction_id) AS transactions,
    SUM(st.total) AS total_sales,
    SUM(si.quantity) AS items_sold,
    AVG(st.total) AS avg_transaction
FROM sales_transactions st
LEFT JOIN sales_items si ON st.transaction_id = si.transaction_id
WHERE MONTH(st.transaction_date) = MONTH(CURDATE())
  AND YEAR(st.transaction_date) = YEAR(CURDATE())
GROUP BY WEEK(st.transaction_date, 1)
ORDER BY week_num;

-- Top selling products (all time)
SELECT 
    si.item_name,
    si.category,
    SUM(si.quantity) AS total_sold,
    SUM(si.subtotal) AS total_revenue,
    COUNT(DISTINCT si.transaction_id) AS times_purchased,
    AVG(si.price) AS avg_price
FROM sales_items si
GROUP BY si.item_name, si.category
ORDER BY total_sold DESC
LIMIT 20;

-- Top selling products by category
SELECT 
    si.category,
    si.item_name,
    SUM(si.quantity) AS total_sold,
    SUM(si.subtotal) AS revenue
FROM sales_items si
GROUP BY si.category, si.item_name
ORDER BY si.category, total_sold DESC;

-- Sales by payment method
SELECT 
    payment_method,
    COUNT(*) AS transaction_count,
    SUM(total) AS total_amount,
    AVG(total) AS avg_amount,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM sales_transactions), 2) AS percentage
FROM sales_transactions
GROUP BY payment_method
ORDER BY total_amount DESC;

-- Hourly sales distribution
SELECT 
    HOUR(transaction_date) AS hour,
    COUNT(*) AS transaction_count,
    SUM(total) AS total_sales,
    AVG(total) AS avg_transaction
FROM sales_transactions
WHERE DATE(transaction_date) = CURDATE()
GROUP BY HOUR(transaction_date)
ORDER BY hour;

-- ========================================
-- INVENTORY ANALYTICS
-- ========================================

-- Current inventory value
SELECT 
    SUM(price * stock_quantity) AS total_inventory_value,
    COUNT(*) AS total_products,
    SUM(stock_quantity) AS total_units
FROM inventory
WHERE is_active = TRUE;

-- Low stock items
SELECT 
    name,
    category,
    stock_quantity,
    min_stock_level,
    (min_stock_level - stock_quantity) AS stock_deficit,
    price,
    supplier
FROM inventory
WHERE stock_quantity < min_stock_level
  AND is_active = TRUE
ORDER BY stock_deficit DESC;

-- Expired items
SELECT 
    name,
    category,
    stock_quantity,
    expiration_date,
    DATEDIFF(CURDATE(), expiration_date) AS days_expired,
    (price * stock_quantity) AS value_lost
FROM inventory
WHERE expiration_date < CURDATE()
  AND is_active = TRUE
ORDER BY value_lost DESC;

-- Expiring soon (within 7 days)
SELECT 
    name,
    category,
    stock_quantity,
    expiration_date,
    DATEDIFF(expiration_date, CURDATE()) AS days_until_expiration,
    supplier
FROM inventory
WHERE expiration_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
  AND is_active = TRUE
ORDER BY expiration_date;

-- Inventory by category
SELECT 
    category,
    COUNT(*) AS product_count,
    SUM(stock_quantity) AS total_units,
    SUM(price * stock_quantity) AS category_value,
    AVG(price) AS avg_price
FROM inventory
WHERE is_active = TRUE
GROUP BY category
ORDER BY category_value DESC;

-- Stock movement analysis
SELECT 
    item_name,
    change_type,
    COUNT(*) AS change_count,
    SUM(change_amount) AS total_change,
    AVG(change_amount) AS avg_change,
    MIN(changed_at) AS first_change,
    MAX(changed_at) AS last_change
FROM inventory_stock_history
WHERE changed_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY item_name, change_type
ORDER BY item_name, change_type;

-- ========================================
-- CUSTOMER & DISCOUNT ANALYTICS
-- ========================================

-- Discount usage summary
SELECT 
    discount_type,
    COUNT(*) AS usage_count,
    SUM(discount_amount) AS total_discount,
    AVG(discount_amount) AS avg_discount,
    SUM(total) AS revenue_with_discount
FROM sales_transactions
WHERE discount_applied = TRUE
GROUP BY discount_type
ORDER BY usage_count DESC;

-- Senior/PWD transactions
SELECT 
    customer_type,
    COUNT(*) AS transaction_count,
    SUM(total) AS total_sales,
    SUM(discount_amount) AS total_discounts,
    AVG(total) AS avg_transaction
FROM sales_transactions
WHERE customer_type IN ('SENIOR', 'PWD')
GROUP BY customer_type;

-- ========================================
-- TIME-BASED ANALYSIS
-- ========================================

-- Sales trend by day of week
SELECT 
    DAYNAME(transaction_date) AS day_name,
    DAYOFWEEK(transaction_date) AS day_num,
    COUNT(*) AS transaction_count,
    SUM(total) AS total_sales,
    AVG(total) AS avg_transaction
FROM sales_transactions
WHERE transaction_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY DAYOFWEEK(transaction_date), DAYNAME(transaction_date)
ORDER BY day_num;

-- Monthly comparison (year-over-year)
SELECT 
    YEAR(transaction_date) AS year,
    MONTH(transaction_date) AS month,
    MONTHNAME(transaction_date) AS month_name,
    COUNT(*) AS transactions,
    SUM(total) AS total_sales,
    SUM(discount_amount) AS discounts
FROM sales_transactions
GROUP BY YEAR(transaction_date), MONTH(transaction_date)
ORDER BY year DESC, month DESC;

-- ========================================
-- HELP REQUEST ANALYTICS
-- ========================================

-- Help request status summary
SELECT 
    status,
    COUNT(*) AS request_count,
    AVG(TIMESTAMPDIFF(MINUTE, created_at, COALESCE(resolved_at, NOW()))) AS avg_resolution_minutes
FROM help_requests
GROUP BY status
ORDER BY request_count DESC;

-- Help requests by type
SELECT 
    request_type,
    priority,
    COUNT(*) AS request_count,
    SUM(CASE WHEN status = 'RESOLVED' THEN 1 ELSE 0 END) AS resolved_count,
    SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) AS pending_count
FROM help_requests
GROUP BY request_type, priority
ORDER BY request_count DESC;

-- ========================================
-- PERFORMANCE METRICS
-- ========================================

-- Best performing products (last 30 days)
SELECT 
    si.item_name,
    si.category,
    COUNT(DISTINCT DATE(st.transaction_date)) AS days_sold,
    SUM(si.quantity) AS total_sold,
    SUM(si.subtotal) AS revenue,
    AVG(si.quantity) AS avg_qty_per_transaction
FROM sales_items si
JOIN sales_transactions st ON si.transaction_id = st.transaction_id
WHERE st.transaction_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY si.item_name, si.category
HAVING total_sold > 0
ORDER BY revenue DESC
LIMIT 10;

-- Slow-moving inventory
SELECT 
    i.name,
    i.category,
    i.stock_quantity,
    i.price,
    COALESCE(SUM(si.quantity), 0) AS sold_last_30_days,
    DATEDIFF(CURDATE(), i.created_at) AS days_in_inventory,
    i.supplier
FROM inventory i
LEFT JOIN sales_items si ON i.name = si.item_name
LEFT JOIN sales_transactions st ON si.transaction_id = st.transaction_id 
    AND st.transaction_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
WHERE i.is_active = TRUE
GROUP BY i.name
HAVING sold_last_30_days < 5
ORDER BY sold_last_30_days ASC, i.stock_quantity DESC;

-- ========================================
-- ADMIN ACTIVITY ANALYTICS
-- ========================================

-- Admin activity summary
SELECT 
    admin_username,
    action_type,
    COUNT(*) AS action_count,
    MAX(created_at) AS last_action
FROM admin_activity_log
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
GROUP BY admin_username, action_type
ORDER BY admin_username, action_count DESC;

-- System usage by time
SELECT 
    HOUR(created_at) AS hour,
    COUNT(*) AS activity_count
FROM admin_activity_log
WHERE DATE(created_at) = CURDATE()
GROUP BY HOUR(created_at)
ORDER BY hour;
