# ✅ Database Setup Checklist

Use this checklist to ensure proper database setup and integration with your admin panel.

---

## 📋 Pre-Installation Checklist

### Requirements
- [ ] MySQL 5.7+ or MariaDB 10.2+ installed
- [ ] MySQL server running
- [ ] Root or admin access to MySQL
- [ ] Database client (MySQL Workbench, command line, etc.)

### Verification Commands
```bash
# Check MySQL version
mysql --version

# Test connection
mysql -u root -p -e "SELECT VERSION();"

# Check if server is running
# Windows:
sc query MySQL

# Linux/Mac:
sudo systemctl status mysql
```

---

## 🚀 Installation Steps

### Option A: Fresh Installation (Recommended for New Projects)

#### Step 1: Create Database
- [ ] Open MySQL command line or client
- [ ] Run setup script:
```bash
# From project root
mysql -u root -p < database/sql/setup.sql

# Or use the setup script
.\setup_database.bat
```

**Expected Output:**
```
Database created: kiosk_db
Tables created: 10
Views created: 5
Procedures created: 2
Triggers created: 1
✅ Database setup completed successfully!
```

#### Step 2: Verify Installation
- [ ] Check tables exist:
```sql
USE kiosk_db;
SHOW TABLES;
```

**Expected Tables (10):**
- admin_activity_log
- admin_users
- daily_sales_summary
- help_requests
- inventory
- inventory_stock_history
- product_sales_analytics
- sales_items
- sales_transactions
- system_settings

#### Step 3: Load Sample Data (Optional)
- [ ] Load test data for development:
```bash
mysql -u root -p kiosk_db < database/test_data.sql
```

**Expected Output:**
```
Inventory Items: 30+
Sample Transactions: 5
Help Requests: 5
Admin Users: 2
✅ Test data generation completed!
```

#### Step 4: Test Database Functions
- [ ] Test views:
```sql
SELECT * FROM v_today_sales;
SELECT * FROM v_low_stock_items;
SELECT * FROM v_top_products LIMIT 5;
```

- [ ] Test stored procedures:
```sql
CALL update_daily_sales_summary(CURDATE());
SELECT * FROM daily_sales_summary ORDER BY summary_date DESC LIMIT 1;
```

---

### Option B: Upgrade Existing Database

#### Step 1: Backup Current Database
- [ ] Create backup:
```bash
mysqldump -u root -p kiosk_db > backup_$(date +%Y%m%d_%H%M%S).sql
```

- [ ] Verify backup file exists and has content:
```bash
ls -lh backup_*.sql
```

#### Step 2: Follow Migration Guide
- [ ] Open `database/MIGRATION_GUIDE.md`
- [ ] Follow "Option 2: In-Place Upgrade" steps
- [ ] Complete all 10 migration steps

#### Step 3: Verify Upgrade
- [ ] Run verification queries:
```sql
-- Check all tables
SHOW TABLES;

-- Verify new columns exist
DESCRIBE inventory;
DESCRIBE sales_transactions;

-- Test new views
SELECT * FROM v_today_sales;

-- Test new procedures
CALL update_daily_sales_summary(CURDATE());
```

---

## 🔧 Configuration

### Step 1: Update Database Connection Properties
- [ ] Open `config/database.properties`
- [ ] Verify/update connection details:
```properties
db.url=jdbc:mysql://localhost:3306/kiosk_db
db.username=root
db.password=your_password
db.driver=com.mysql.cj.jdbc.Driver
```

### Step 2: Change Default Admin Passwords
- [ ] Login to MySQL
- [ ] Update admin password:
```sql
UPDATE admin_users 
SET password_hash = 'NEW_SECURE_PASSWORD_HASH' 
WHERE username = 'admin';

UPDATE admin_users 
SET password_hash = 'NEW_SECURE_PASSWORD_HASH' 
WHERE username = 'manager';
```

**⚠️ NOTE:** In production, use proper password hashing (bcrypt, SHA-256, etc.)

### Step 3: Configure System Settings
- [ ] Review system settings:
```sql
SELECT * FROM system_settings;
```

- [ ] Update as needed:
```sql
-- Example: Change tax rate
UPDATE system_settings 
SET setting_value = '0.15', updated_by = 'admin'
WHERE setting_key = 'TAX_RATE';

-- Example: Update store name
UPDATE system_settings 
SET setting_value = 'My Bakery Shop'
WHERE setting_key = 'STORE_NAME';
```

---

## 🧪 Testing

### Step 1: Test Inventory Operations
- [ ] View all products:
```sql
SELECT * FROM inventory WHERE is_active = TRUE;
```

- [ ] Check low stock alerts:
```sql
SELECT * FROM v_low_stock_items;
```

- [ ] Check expiration alerts:
```sql
SELECT * FROM v_expiring_soon;
```

### Step 2: Test Sales Tracking
- [ ] View recent transactions:
```sql
SELECT * FROM sales_transactions 
ORDER BY transaction_date DESC 
LIMIT 10;
```

- [ ] Check today's sales:
```sql
SELECT * FROM v_today_sales;
```

- [ ] Test top products:
```sql
SELECT * FROM v_top_products LIMIT 10;
```

### Step 3: Test Admin Features
- [ ] Verify admin users:
```sql
SELECT username, role, is_active FROM admin_users;
```

- [ ] Test admin login:
```sql
SELECT * FROM admin_users 
WHERE username = 'admin' AND is_active = TRUE;
```

- [ ] Check activity log:
```sql
SELECT * FROM admin_activity_log 
ORDER BY created_at DESC 
LIMIT 10;
```

### Step 4: Test Help Request System
- [ ] View pending requests:
```sql
SELECT * FROM help_requests 
WHERE status = 'PENDING';
```

- [ ] Create test request:
```sql
INSERT INTO help_requests 
(request_id, location, request_type, description, priority, status)
VALUES ('TEST-001', 'Kiosk-Test', 'TECHNICAL', 'Test request', 'MEDIUM', 'PENDING');
```

---

## 🔍 Validation

### Database Health Check
- [ ] Run diagnostic query:
```sql
SELECT 
    'Active Products' AS metric, COUNT(*) AS value 
FROM inventory WHERE is_active = TRUE
UNION ALL SELECT 'Transactions Today', COUNT(*) 
FROM sales_transactions WHERE DATE(transaction_date) = CURDATE()
UNION ALL SELECT 'Low Stock Items', COUNT(*) 
FROM v_low_stock_items
UNION ALL SELECT 'Pending Help Requests', COUNT(*) 
FROM help_requests WHERE status = 'PENDING'
UNION ALL SELECT 'Active Admin Users', COUNT(*) 
FROM admin_users WHERE is_active = TRUE;
```

### Performance Check
- [ ] Check table sizes:
```sql
SELECT table_name,
       ROUND((data_length + index_length) / 1024 / 1024, 2) AS size_mb,
       table_rows
FROM information_schema.TABLES
WHERE table_schema = 'kiosk_db'
ORDER BY (data_length + index_length) DESC;
```

- [ ] Test query performance:
```sql
-- Should run fast (< 100ms)
EXPLAIN SELECT * FROM v_today_sales;
EXPLAIN SELECT * FROM v_top_products;
```

---

## 📱 Application Integration

### Step 1: Test Database Connection
- [ ] Run Java application
- [ ] Check connection in console:
```
Expected output:
"Database connection established successfully"
```

### Step 2: Test Admin Panel
- [ ] Launch admin panel
- [ ] Verify Dashboard tab loads:
  - [ ] Today's Sales displays
  - [ ] Total Revenue displays
  - [ ] Low Stock Items count
  - [ ] Expired Items count

- [ ] Verify Sales Reports tab:
  - [ ] Daily report loads
  - [ ] Weekly report loads
  - [ ] Monthly report loads
  - [ ] All-time report loads

- [ ] Verify Inventory Management:
  - [ ] Products list loads
  - [ ] Can add new product
  - [ ] Can update stock
  - [ ] Can delete product

### Step 3: Test Cart System
- [ ] Add items to cart
- [ ] Apply promo code
- [ ] Check tax calculation (12%)
- [ ] Complete purchase
- [ ] Verify transaction saved to database:
```sql
SELECT * FROM sales_transactions 
ORDER BY created_at DESC 
LIMIT 1;
```

---

## 🔐 Security Checklist

### Production Readiness
- [ ] Changed default admin passwords
- [ ] Using proper password hashing
- [ ] Reviewed user roles and permissions
- [ ] Enabled admin activity logging
- [ ] Set up database backups
- [ ] Configured firewall rules
- [ ] Limited database access by IP
- [ ] Using SSL for database connections (if applicable)

### Access Control
- [ ] Only authorized users have MySQL access
- [ ] Application uses dedicated database user (not root)
- [ ] Database user has minimum required permissions
- [ ] Password stored securely in application

---

## 📊 Maintenance Setup

### Daily Tasks
- [ ] Set up automated daily summary:
```sql
-- Add to cron/scheduler
CALL update_daily_sales_summary(CURDATE());
```

### Weekly Tasks
- [ ] Schedule table optimization:
```sql
ANALYZE TABLE inventory, sales_transactions, sales_items;
```

### Monthly Tasks
- [ ] Set up automated backups:
```bash
# Example cron job (Linux)
0 2 * * * mysqldump -u root -p kiosk_db > /backups/kiosk_db_$(date +\%Y\%m\%d).sql
```

- [ ] Review and archive old data
- [ ] Update product analytics

---

## 📚 Documentation Review

- [ ] Read `database/README.md` - Complete schema documentation
- [ ] Bookmark `database/QUICK_REFERENCE.md` - Common commands
- [ ] Review `database/analytics_queries.sql` - Pre-built queries
- [ ] Save `database/MIGRATION_GUIDE.md` - For future upgrades
- [ ] Check `database/SCHEMA_DIAGRAM.md` - Visual reference

---

## ✅ Final Verification

### Checklist Summary
- [ ] Database installed successfully
- [ ] All 10 tables created
- [ ] All 5 views working
- [ ] Sample data loaded (if applicable)
- [ ] Database connection configured
- [ ] Admin passwords changed
- [ ] Application connects to database
- [ ] Admin panel displays correctly
- [ ] Sales transactions working
- [ ] All reports generating
- [ ] Backups configured

### Success Criteria
✅ All tables and views exist  
✅ Sample queries run successfully  
✅ Application connects without errors  
✅ Admin panel loads all tabs  
✅ Can create and view transactions  
✅ Reports generate correct data  
✅ Security measures in place  

---

## 🎊 You're Ready!

If all checkboxes are marked, your database is:

✅ **Installed** - All components in place  
✅ **Configured** - Settings optimized  
✅ **Tested** - All features working  
✅ **Secured** - Passwords changed, access controlled  
✅ **Integrated** - Application connected  
✅ **Documented** - Guides available  

**Your SweetBatterBakeshop database is ready for production! 🚀**

---

## 🆘 Troubleshooting

### Issue: Can't connect to database
- [ ] Check MySQL is running
- [ ] Verify credentials in `database.properties`
- [ ] Test connection: `mysql -u root -p`

### Issue: Tables not found
- [ ] Verify database selected: `USE kiosk_db;`
- [ ] Re-run setup: `mysql -u root -p < database/sql/setup.sql`

### Issue: Queries running slow
- [ ] Run: `ANALYZE TABLE inventory, sales_transactions;`
- [ ] Check indexes: `SHOW INDEX FROM sales_transactions;`

### Issue: Views not working
- [ ] Re-create views from `setup.sql`
- [ ] Check view definitions: `SHOW CREATE VIEW v_today_sales;`

### Need More Help?
📖 See `database/README.md` - Troubleshooting section  
📧 Check application error logs  
💬 Review MySQL error log  

---

**Checklist Version:** 1.0  
**Last Updated:** 2024  
**Database Version:** 2.0
