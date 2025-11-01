# 🗄️ SweetBatterBakeshop Database Documentation

## Overview
This directory contains the complete database schema and utilities for the SweetBatterBakeshop kiosk system. The database is designed to support full admin panel integration with advanced analytics, inventory tracking, sales reporting, and customer support features.

## 📁 Files

### Core Schema
- **`setup.sql`** - Main database setup script with all tables, views, procedures, and triggers
- **`analytics_queries.sql`** - Pre-built queries for admin dashboard and reports
- **`maintenance.sql`** - Database maintenance tasks and utilities
- **`test_data.sql`** - Sample data generation for testing

## 🗃️ Database Structure

### Database Name: `kiosk_db`

### Tables

#### 1. **inventory** - Main product catalog
Stores all bakery products with stock tracking and expiration management.

| Column | Type | Description |
|--------|------|-------------|
| id | INT (PK) | Auto-increment primary key |
| name | VARCHAR(255) | Unique product name |
| category | VARCHAR(100) | Product category |
| price | DECIMAL(10,2) | Current selling price |
| stock_quantity | INT | Current stock level |
| min_stock_level | INT | Minimum stock threshold (default: 10) |
| expiration_date | DATE | Product expiration date |
| barcode | VARCHAR(50) | Unique barcode identifier |
| supplier | VARCHAR(255) | Supplier name |
| description | TEXT | Product description |
| image_path | VARCHAR(500) | Path to product image |
| is_active | BOOLEAN | Active status (default: TRUE) |
| created_at | TIMESTAMP | Record creation time |
| updated_at | TIMESTAMP | Last update time |

**Indexes:** category, barcode, expiration_date, stock_quantity, is_active, name

---

#### 2. **inventory_stock_history** - Stock change tracking
Maintains audit trail of all inventory adjustments.

| Column | Type | Description |
|--------|------|-------------|
| id | INT (PK) | Auto-increment primary key |
| item_name | VARCHAR(255) | Product name (FK to inventory) |
| old_quantity | INT | Stock before change |
| new_quantity | INT | Stock after change |
| change_amount | INT | Calculated difference |
| change_type | ENUM | SALE, RESTOCK, ADJUSTMENT, EXPIRED, DAMAGED |
| notes | TEXT | Change notes/reason |
| changed_by | VARCHAR(100) | User who made the change |
| changed_at | TIMESTAMP | When change occurred |

**Indexes:** item_name, changed_at, change_type

---

#### 3. **sales_transactions** - Transaction records
Main sales transaction table with payment and discount tracking.

| Column | Type | Description |
|--------|------|-------------|
| id | INT (PK) | Auto-increment primary key |
| transaction_id | VARCHAR(50) | Unique transaction ID |
| transaction_date | TIMESTAMP | Transaction date/time |
| subtotal | DECIMAL(10,2) | Pre-discount subtotal |
| discount_amount | DECIMAL(10,2) | Total discount applied |
| tax_amount | DECIMAL(10,2) | VAT amount (12%) |
| total | DECIMAL(10,2) | Final total amount |
| payment_method | ENUM | CASH, ECASH, CARD, OTHER |
| payment_amount | DECIMAL(10,2) | Amount tendered |
| change_amount | DECIMAL(10,2) | Change given |
| discount_applied | BOOLEAN | Whether discount was used |
| discount_type | VARCHAR(50) | Type of discount (promo code) |
| customer_type | ENUM | REGULAR, SENIOR, PWD, EMPLOYEE |
| served_by | VARCHAR(100) | Staff member name |
| notes | TEXT | Transaction notes |

**Indexes:** transaction_id, transaction_date, payment_method, customer_type

---

#### 4. **sales_items** - Line items detail
Individual items within each transaction.

| Column | Type | Description |
|--------|------|-------------|
| id | INT (PK) | Auto-increment primary key |
| transaction_id | VARCHAR(50) | FK to sales_transactions |
| item_name | VARCHAR(255) | Product name |
| category | VARCHAR(100) | Product category |
| price | DECIMAL(10,2) | Item price at time of sale |
| quantity | INT | Quantity purchased |
| subtotal | DECIMAL(10,2) | Line item subtotal |
| discount_amount | DECIMAL(10,2) | Line-level discount |

**Indexes:** transaction_id, item_name, category

---

#### 5. **admin_users** - Administrative users
User accounts for admin panel access.

| Column | Type | Description |
|--------|------|-------------|
| id | INT (PK) | Auto-increment primary key |
| username | VARCHAR(50) | Unique username |
| password_hash | VARCHAR(255) | Hashed password |
| full_name | VARCHAR(100) | Full display name |
| email | VARCHAR(100) | Email address |
| role | ENUM | SUPER_ADMIN, ADMIN, MANAGER, STAFF |
| phone | VARCHAR(20) | Contact number |
| created_at | TIMESTAMP | Account creation date |
| last_login | TIMESTAMP | Last login time |
| is_active | BOOLEAN | Account active status |

**Indexes:** username, email, role, is_active

**Default Users:**
- Username: `admin`, Password: `admin123`, Role: SUPER_ADMIN
- Username: `manager`, Password: `manager123`, Role: MANAGER

---

#### 6. **admin_activity_log** - Audit trail
Tracks all admin actions for security and compliance.

| Column | Type | Description |
|--------|------|-------------|
| id | INT (PK) | Auto-increment primary key |
| admin_username | VARCHAR(50) | Admin who performed action |
| action_type | VARCHAR(100) | Type of action |
| action_description | TEXT | Detailed description |
| affected_table | VARCHAR(100) | Database table affected |
| affected_record_id | VARCHAR(100) | Record ID affected |
| ip_address | VARCHAR(45) | IP address of action |
| created_at | TIMESTAMP | Action timestamp |

**Indexes:** admin_username, action_type, created_at

---

#### 7. **help_requests** - Customer support
Tracks customer assistance requests from kiosks.

| Column | Type | Description |
|--------|------|-------------|
| id | INT (PK) | Auto-increment primary key |
| request_id | VARCHAR(50) | Unique request identifier |
| location | VARCHAR(100) | Kiosk location |
| request_type | VARCHAR(50) | Type of request |
| description | TEXT | Request details |
| priority | ENUM | LOW, MEDIUM, HIGH, URGENT |
| status | ENUM | PENDING, IN_PROGRESS, RESOLVED, CANCELLED |
| assigned_to | VARCHAR(50) | Assigned admin (FK) |
| resolved_by | VARCHAR(50) | Admin who resolved (FK) |
| created_at | TIMESTAMP | Request creation time |
| resolved_at | TIMESTAMP | Resolution time |
| resolution_notes | TEXT | Resolution details |

**Indexes:** request_id, status, priority, created_at, assigned_to

---

#### 8. **daily_sales_summary** - Aggregated sales
Pre-computed daily sales for fast reporting.

| Column | Type | Description |
|--------|------|-------------|
| id | INT (PK) | Auto-increment primary key |
| summary_date | DATE | Summary date (unique) |
| total_transactions | INT | Transaction count |
| total_items_sold | INT | Items sold count |
| gross_sales | DECIMAL(10,2) | Total before discounts |
| total_discounts | DECIMAL(10,2) | Sum of discounts |
| total_tax | DECIMAL(10,2) | Total VAT collected |
| net_sales | DECIMAL(10,2) | Final revenue |
| cash_sales | DECIMAL(10,2) | Cash payments |
| ecash_sales | DECIMAL(10,2) | E-cash payments |
| card_sales | DECIMAL(10,2) | Card payments |
| avg_transaction_value | DECIMAL(10,2) | Average transaction |

**Indexes:** summary_date, created_at

---

#### 9. **product_sales_analytics** - Product performance
Tracks product sales over time periods.

| Column | Type | Description |
|--------|------|-------------|
| id | INT (PK) | Auto-increment primary key |
| item_name | VARCHAR(255) | Product name |
| category | VARCHAR(100) | Product category |
| analysis_period | ENUM | DAILY, WEEKLY, MONTHLY, YEARLY |
| period_start_date | DATE | Analysis period start |
| period_end_date | DATE | Analysis period end |
| units_sold | INT | Total units sold |
| total_revenue | DECIMAL(10,2) | Revenue generated |
| avg_price | DECIMAL(10,2) | Average selling price |

**Indexes:** item_name, category, analysis_period, dates

---

#### 10. **system_settings** - Configuration
System-wide settings and parameters.

| Column | Type | Description |
|--------|------|-------------|
| id | INT (PK) | Auto-increment primary key |
| setting_key | VARCHAR(100) | Unique setting key |
| setting_value | TEXT | Setting value |
| setting_type | VARCHAR(50) | Data type (STRING, DECIMAL, etc.) |
| description | TEXT | Setting description |
| updated_by | VARCHAR(50) | Last updated by |
| updated_at | TIMESTAMP | Last update time |

**Default Settings:**
- `STORE_NAME`: SweetBatterBakeshop
- `TAX_RATE`: 0.12 (12%)
- `LOW_STOCK_THRESHOLD`: 10
- `SENIOR_PWD_DISCOUNT`: 0.20 (20%)
- Business hours, receipt footer, notifications

---

## 👁️ Database Views

### v_expired_items
Lists all expired inventory items with calculated value loss.

### v_low_stock_items
Shows items below minimum stock levels with deficit calculation.

### v_expiring_soon
Items expiring within 7 days with countdown.

### v_today_sales
Real-time summary of today's sales statistics.

### v_top_products
Top 20 best-selling products of all time.

---

## ⚙️ Stored Procedures

### update_daily_sales_summary(target_date DATE)
Updates or creates daily sales summary for a specific date.

**Usage:**
```sql
CALL update_daily_sales_summary(CURDATE());
```

### record_stock_change(params)
Manually records stock changes with audit trail.

**Parameters:**
- `p_item_name`: Product name
- `p_old_qty`: Previous quantity
- `p_new_qty`: New quantity
- `p_change_type`: Type of change
- `p_notes`: Change notes
- `p_changed_by`: User making change

### generate_test_sales()
Generates 30 days of realistic test sales data (from test_data.sql).

### cleanup_test_data()
Removes all test data from database.

---

## 🔔 Triggers

### trg_inventory_stock_update
Automatically logs stock changes to inventory_stock_history when inventory.stock_quantity is updated.

---

## 🚀 Quick Start

### 1. Initial Setup
```bash
# Windows
mysql -u root -p < database\setup.sql

# Linux/Mac
mysql -u root -p < database/setup.sql
```

### 2. Load Test Data (Optional)
```sql
SOURCE database/test_data.sql;
-- OR execute specific insert statements
```

### 3. Verify Installation
```sql
USE kiosk_db;
SHOW TABLES;
SELECT * FROM system_settings;
```

### 4. Test Admin Login
```sql
SELECT * FROM admin_users WHERE username = 'admin';
-- Default password: admin123 (CHANGE IN PRODUCTION!)
```

---

## 📊 Common Queries

### Today's Sales Dashboard
```sql
-- Quick stats
SELECT * FROM v_today_sales;

-- Detailed transactions
SELECT * FROM sales_transactions 
WHERE DATE(transaction_date) = CURDATE()
ORDER BY transaction_date DESC;
```

### Inventory Alerts
```sql
-- Low stock
SELECT * FROM v_low_stock_items;

-- Expiring soon
SELECT * FROM v_expiring_soon;

-- Expired
SELECT * FROM v_expired_items;
```

### Sales Reports
```sql
-- Weekly report
SELECT 
    DATE(transaction_date) AS date,
    COUNT(*) AS transactions,
    SUM(total) AS revenue
FROM sales_transactions
WHERE transaction_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
GROUP BY DATE(transaction_date);

-- Top products
SELECT * FROM v_top_products LIMIT 10;
```

### Help Requests
```sql
-- Pending requests
SELECT * FROM help_requests 
WHERE status = 'PENDING'
ORDER BY priority DESC, created_at;
```

---

## 🔧 Maintenance

### Daily Tasks
```sql
-- Update sales summaries
CALL update_daily_sales_summary(CURDATE());
CALL update_daily_sales_summary(DATE_SUB(CURDATE(), INTERVAL 1 DAY));

-- Check database health
SOURCE database/maintenance.sql; -- See diagnostic queries
```

### Weekly Tasks
```sql
-- Analyze tables for optimization
ANALYZE TABLE inventory, sales_transactions, sales_items;

-- Check table sizes
SELECT table_name, 
       ROUND((data_length + index_length) / 1024 / 1024, 2) AS size_mb
FROM information_schema.TABLES
WHERE table_schema = 'kiosk_db'
ORDER BY (data_length + index_length) DESC;
```

### Monthly Tasks
```sql
-- Archive old data (modify dates as needed)
-- See maintenance.sql for archive procedures

-- Update product analytics
-- See maintenance.sql for product analytics update
```

---

## 🔐 Security

### Password Management
**⚠️ IMPORTANT:** Default passwords are for development only!

```sql
-- Change admin password (hash in application)
UPDATE admin_users 
SET password_hash = 'NEW_HASHED_PASSWORD' 
WHERE username = 'admin';
```

### Access Control
- **SUPER_ADMIN**: Full system access
- **ADMIN**: Inventory, sales, reports
- **MANAGER**: Sales reports, help requests
- **STAFF**: Limited read access

### Audit Trail
All admin actions are logged in `admin_activity_log` for security compliance.

---

## 📈 Performance Optimization

### Indexes
All critical columns are indexed for query performance:
- Transaction dates (for date-range queries)
- Product names and categories
- Stock levels and expiration dates
- User authentication fields

### Views for Fast Access
Pre-computed views reduce query complexity:
- `v_today_sales` - Real-time dashboard
- `v_top_products` - Best sellers
- `v_low_stock_items` - Inventory alerts

### Caching Strategy
- Use `daily_sales_summary` for historical reports
- Refresh summaries nightly with stored procedure
- Product analytics updated monthly

---

## 🧪 Testing

### Load Sample Data
```sql
SOURCE database/test_data.sql;
```

### Generate 30 Days of Sales
```sql
CALL generate_test_sales();
```

### Clean Up Test Data
```sql
CALL cleanup_test_data();
```

---

## 🐛 Troubleshooting

### Connection Issues
```bash
# Test connection
mysql -u root -p -h localhost

# Check if database exists
SHOW DATABASES LIKE 'kiosk_db';
```

### Foreign Key Errors
```sql
-- Temporarily disable
SET FOREIGN_KEY_CHECKS = 0;
-- ... perform operations ...
SET FOREIGN_KEY_CHECKS = 1;
```

### Missing Tables
```sql
-- Re-run setup
SOURCE database/setup.sql;
```

### Slow Queries
```sql
-- Check for missing indexes
SHOW INDEX FROM sales_transactions;

-- Analyze slow query
EXPLAIN SELECT * FROM sales_transactions WHERE ...;
```

---

## 📚 Additional Resources

### File References
- **`analytics_queries.sql`** - Ready-to-use analytics queries
- **`maintenance.sql`** - Database maintenance scripts
- **`test_data.sql`** - Sample data generation

### Application Integration
- Java DAO classes: `src/kiosk/database/dao/`
- Database config: `config/database.properties`
- Connection manager: `src/kiosk/database/DatabaseConnection.java`

---

## 🔄 Version History

### v2.0 (Current) - Admin Integration
- ✅ Enhanced schema for admin panel
- ✅ Sales analytics tables and views
- ✅ Help request system
- ✅ Admin activity logging
- ✅ Stored procedures and triggers
- ✅ Comprehensive test data

### v1.0 - Initial Release
- Basic inventory management
- Simple transaction tracking
- Core sales recording

---

## 📞 Support

For database-related issues:
1. Check troubleshooting section above
2. Review error logs: `/var/log/mysql/error.log`
3. Verify connection in `database.properties`
4. Test with sample queries from this documentation

---

**Last Updated:** 2024
**Database Version:** 2.0
**Compatible With:** MySQL 5.7+, MariaDB 10.2+
