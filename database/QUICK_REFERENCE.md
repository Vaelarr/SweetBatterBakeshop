# 🎴 Database Quick Reference Card

## 🚀 Essential Commands

### Setup & Installation
```bash
# Fresh install
mysql -u root -p < database/setup.sql

# Load test data
mysql -u root -p kiosk_db < database/test_data.sql

# Backup database
mysqldump -u root -p kiosk_db > backup.sql
```

---

## 📊 Dashboard Queries

### Today's Sales Stats
```sql
SELECT * FROM v_today_sales;
```

### Total Revenue (All Time)
```sql
SELECT SUM(total) AS total_revenue FROM sales_transactions;
```

### Low Stock Items Count
```sql
SELECT COUNT(*) FROM v_low_stock_items;
```

### Expired Items Count
```sql
SELECT COUNT(*) FROM v_expired_items;
```

---

## 📈 Sales Reports

### Daily Report
```sql
SELECT st.*, COUNT(si.id) AS items
FROM sales_transactions st
LEFT JOIN sales_items si ON st.transaction_id = si.transaction_id
WHERE DATE(st.transaction_date) = '2024-01-01'
GROUP BY st.transaction_id
ORDER BY st.transaction_date DESC;
```

### Weekly Report (Last 7 Days)
```sql
SELECT 
    DATE(transaction_date) AS date,
    COUNT(*) AS transactions,
    SUM(total) AS revenue
FROM sales_transactions
WHERE transaction_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
GROUP BY DATE(transaction_date)
ORDER BY date DESC;
```

### Monthly Report (Current Month)
```sql
SELECT 
    WEEK(transaction_date) AS week,
    COUNT(*) AS transactions,
    SUM(total) AS revenue
FROM sales_transactions
WHERE MONTH(transaction_date) = MONTH(CURDATE())
  AND YEAR(transaction_date) = YEAR(CURDATE())
GROUP BY WEEK(transaction_date);
```

### Top Products (All Time)
```sql
SELECT * FROM v_top_products LIMIT 20;
```

---

## 📦 Inventory Management

### All Active Products
```sql
SELECT * FROM inventory WHERE is_active = TRUE ORDER BY category, name;
```

### Low Stock Items
```sql
SELECT * FROM v_low_stock_items;
```

### Expired Items
```sql
SELECT * FROM v_expired_items;
```

### Expiring Soon (7 days)
```sql
SELECT * FROM v_expiring_soon;
```

### Add New Product
```sql
INSERT INTO inventory (name, category, price, stock_quantity, min_stock_level, expiration_date, supplier)
VALUES ('New Product', 'Category', 99.99, 50, 10, '2024-12-31', 'Supplier Name');
```

### Update Stock
```sql
UPDATE inventory 
SET stock_quantity = stock_quantity + 20 
WHERE name = 'Product Name';
```

---

## 🎫 Help Requests

### Pending Requests
```sql
SELECT * FROM help_requests 
WHERE status = 'PENDING' 
ORDER BY priority DESC, created_at;
```

### Assign Request
```sql
UPDATE help_requests 
SET status = 'IN_PROGRESS', assigned_to = 'admin'
WHERE request_id = 'HELP-001';
```

### Resolve Request
```sql
UPDATE help_requests 
SET status = 'RESOLVED', 
    resolved_by = 'admin',
    resolved_at = NOW(),
    resolution_notes = 'Issue fixed'
WHERE request_id = 'HELP-001';
```

---

## 🔐 Admin Users

### Login Check
```sql
SELECT * FROM admin_users 
WHERE username = 'admin' 
  AND password_hash = 'password' 
  AND is_active = TRUE;
```

### Update Last Login
```sql
UPDATE admin_users 
SET last_login = NOW() 
WHERE username = 'admin';
```

### Log Admin Action
```sql
INSERT INTO admin_activity_log 
(admin_username, action_type, action_description, affected_table)
VALUES ('admin', 'INVENTORY_UPDATE', 'Updated stock for Baguette', 'inventory');
```

---

## 🛠️ Maintenance

### Update Daily Summary
```sql
CALL update_daily_sales_summary(CURDATE());
```

### Record Stock Change
```sql
CALL record_stock_change('Baguette', 30, 50, 'RESTOCK', 'Weekly delivery', 'admin');
```

### Optimize Tables
```sql
ANALYZE TABLE inventory, sales_transactions, sales_items;
OPTIMIZE TABLE inventory, sales_transactions, sales_items;
```

### Check Database Health
```sql
SELECT 'Active Products' AS metric, COUNT(*) AS value FROM inventory WHERE is_active = TRUE
UNION ALL SELECT 'Transactions Today', COUNT(*) FROM sales_transactions WHERE DATE(transaction_date) = CURDATE()
UNION ALL SELECT 'Low Stock Items', COUNT(*) FROM v_low_stock_items
UNION ALL SELECT 'Expired Items', COUNT(*) FROM v_expired_items
UNION ALL SELECT 'Pending Help Requests', COUNT(*) FROM help_requests WHERE status = 'PENDING';
```

---

## ⚙️ System Settings

### Get Setting
```sql
SELECT setting_value FROM system_settings WHERE setting_key = 'TAX_RATE';
```

### Update Setting
```sql
UPDATE system_settings 
SET setting_value = '0.15', updated_by = 'admin' 
WHERE setting_key = 'TAX_RATE';
```

### All Settings
```sql
SELECT * FROM system_settings ORDER BY setting_key;
```

---

## 🧪 Testing & Debugging

### Generate Test Sales
```sql
CALL generate_test_sales();
```

### Clean Test Data
```sql
CALL cleanup_test_data();
```

### Transaction Details
```sql
SELECT st.*, si.item_name, si.quantity, si.subtotal
FROM sales_transactions st
JOIN sales_items si ON st.transaction_id = si.transaction_id
WHERE st.transaction_id = 'TXN-001'
ORDER BY si.item_name;
```

### Check Foreign Keys
```sql
SELECT * FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'kiosk_db'
  AND REFERENCED_TABLE_NAME IS NOT NULL;
```

---

## 📋 Common Admin Panel Operations

### Dashboard Load (All Stats)
```sql
-- Today's sales
SELECT SUM(total) FROM sales_transactions WHERE DATE(transaction_date) = CURDATE();

-- Total revenue
SELECT SUM(total) FROM sales_transactions;

-- Low stock count
SELECT COUNT(*) FROM v_low_stock_items;

-- Expired count
SELECT COUNT(*) FROM v_expired_items;

-- Pending help requests
SELECT COUNT(*) FROM help_requests WHERE status = 'PENDING';
```

### Sales Report Generation
```sql
-- Daily
SELECT * FROM sales_transactions WHERE DATE(transaction_date) = ?;

-- Weekly
SELECT DATE(transaction_date), COUNT(*), SUM(total)
FROM sales_transactions
WHERE transaction_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
GROUP BY DATE(transaction_date);

-- Monthly
SELECT WEEK(transaction_date), COUNT(*), SUM(total)
FROM sales_transactions
WHERE MONTH(transaction_date) = MONTH(CURDATE())
GROUP BY WEEK(transaction_date);

-- Top products
SELECT * FROM v_top_products LIMIT 20;
```

---

## 🔍 Useful Diagnostic Queries

### Table Sizes
```sql
SELECT table_name,
       ROUND((data_length + index_length) / 1024 / 1024, 2) AS size_mb,
       table_rows
FROM information_schema.TABLES
WHERE table_schema = 'kiosk_db'
ORDER BY (data_length + index_length) DESC;
```

### Recent Activity
```sql
SELECT * FROM admin_activity_log 
ORDER BY created_at DESC 
LIMIT 20;
```

### Stock History
```sql
SELECT * FROM inventory_stock_history 
WHERE item_name = 'Baguette'
ORDER BY changed_at DESC
LIMIT 10;
```

---

## 🎯 Performance Tips

### Add Indexes (if needed)
```sql
CREATE INDEX idx_custom ON table_name(column_name);
```

### Analyze Slow Query
```sql
EXPLAIN SELECT * FROM sales_transactions WHERE ...;
```

### Clear Query Cache
```sql
RESET QUERY CACHE;
```

---

## 📞 Emergency Commands

### Disable Foreign Keys (Temporarily)
```sql
SET FOREIGN_KEY_CHECKS = 0;
-- ... operations ...
SET FOREIGN_KEY_CHECKS = 1;
```

### Rollback Last Change
```sql
START TRANSACTION;
-- ... check if good ...
ROLLBACK;  -- or COMMIT;
```

### Full Database Restore
```bash
mysql -u root -p -e "DROP DATABASE kiosk_db; CREATE DATABASE kiosk_db;"
mysql -u root -p kiosk_db < backup.sql
```

---

## 🗂️ File Locations

| File | Purpose |
|------|---------|
| `database/setup.sql` | Main schema |
| `database/analytics_queries.sql` | All report queries |
| `database/maintenance.sql` | Utilities |
| `database/test_data.sql` | Sample data |
| `database/README.md` | Full docs |
| `database/MIGRATION_GUIDE.md` | Upgrade guide |

---

## 🏷️ Table Quick Reference

| Table | Purpose | Key Columns |
|-------|---------|-------------|
| `inventory` | Products | name, price, stock_quantity |
| `sales_transactions` | Sales | transaction_id, total, transaction_date |
| `sales_items` | Line items | transaction_id, item_name, quantity |
| `admin_users` | Admins | username, password_hash, role |
| `help_requests` | Support | request_id, status, priority |
| `daily_sales_summary` | Stats | summary_date, net_sales |
| `system_settings` | Config | setting_key, setting_value |

---

## 🔗 View Quick Reference

| View | Purpose |
|------|---------|
| `v_today_sales` | Today's stats |
| `v_top_products` | Best sellers |
| `v_low_stock_items` | Stock alerts |
| `v_expired_items` | Expired products |
| `v_expiring_soon` | Expiring in 7 days |

---

**Quick Reference Version:** 1.0  
**Database Version:** 2.0  
**Last Updated:** 2024
