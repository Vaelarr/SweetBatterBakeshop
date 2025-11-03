# 🗄️ SweetBatterBakeshop Database

Complete database setup and management for the SweetBatterBakeshop kiosk system.

## 📁 Directory Structure

```
database/
├── README.md                          # This file - main documentation
├── sql/                               # SQL scripts
│   ├── setup.sql                      # Main database schema
│   ├── custom_orders_schema.sql       # Custom orders extension
│   ├── test_data.sql                  # Sample test data
│   ├── analytics_queries.sql          # Pre-built analytics queries
│   ├── maintenance.sql                # Database maintenance utilities
│   └── verify_custom_orders.sql       # Verification queries
├── scripts/                           # Setup and installation scripts
│   ├── apply_schema.bat              # Windows schema installer
│   └── install_custom_orders.bat     # Custom orders installer
└── docs/                             # Additional documentation
    ├── MIGRATION_GUIDE.md            # Upgrading existing databases
    ├── SCHEMA_DIAGRAM.md             # Visual database structure
    └── SETUP_CHECKLIST.md            # Installation checklist
```

---

## 🚀 Quick Start

### Prerequisites
- MySQL 5.7+ or MariaDB 10.2+
- MySQL server running
- Root or admin access to MySQL

### Basic Installation

**Option 1: Using Windows Batch Script** (Easiest)
```bash
# From project root
.\setup_database.bat
```

**Option 2: Manual MySQL Command**
```bash
# Navigate to database directory
cd database

# Install main schema
mysql -u root -p < sql/setup.sql

# (Optional) Install custom orders extension
mysql -u root -p kiosk_db < sql/custom_orders_schema.sql

# (Optional) Load test data
mysql -u root -p kiosk_db < sql/test_data.sql
```

**Option 3: From Project Scripts Directory**
```bash
# From database/scripts directory
cd database\scripts

# Apply main schema (prompts for password)
.\apply_schema.bat

# Install custom orders
.\install_custom_orders.bat
```

---

## 🗃️ Database Overview

### Database Name: `kiosk_db`

### Core Tables (10)

1. **`inventory`** - Product catalog with stock tracking
2. **`inventory_stock_history`** - Audit trail of stock changes
3. **`sales_transactions`** - Transaction records
4. **`sales_items`** - Line items per transaction
5. **`daily_sales_summary`** - Aggregated daily metrics
6. **`admin_users`** - Admin panel user accounts
7. **`activity_logs`** - System activity audit trail
8. **`help_requests`** - Customer assistance queue
9. **`promotions`** - Discount and promotion campaigns
10. **`supplier_orders`** - Purchase order tracking

### Custom Orders Extension Tables (11)

11. **`customers`** - Customer accounts
12. **`customer_addresses`** - Delivery addresses
13. **`custom_orders`** - Custom order requests
14. **`custom_order_base_products`** - Base product selections
15. **`custom_order_addons`** - Add-on selections
16. **`addons`** - Available add-ons catalog
17. **`addon_categories`** - Add-on categorization
18. **`order_status_history`** - Order status tracking
19. **`customer_notifications`** - Customer alerts
20. **`order_reviews`** - Customer feedback
21. **`payment_transactions`** - Payment processing

---

## 📊 Common Operations

### View Today's Sales
```sql
SELECT * FROM v_today_sales;
```

### Check Low Stock Items
```sql
SELECT * FROM v_low_stock_items;
```

### View Expired Items
```sql
SELECT * FROM v_expired_items;
```

### Top Selling Products
```sql
SELECT * FROM v_top_products LIMIT 20;
```

### Daily Revenue Summary
```sql
SELECT 
    summary_date,
    total_transactions,
    total_revenue
FROM daily_sales_summary
ORDER BY summary_date DESC
LIMIT 30;
```

---

## 🔧 Maintenance

### Backup Database
```bash
# Full backup
mysqldump -u root -p kiosk_db > backup_kiosk_db.sql

# Backup with timestamp
mysqldump -u root -p kiosk_db > backup_$(date +%Y%m%d_%H%M%S).sql
```

### Restore Database
```bash
mysql -u root -p kiosk_db < backup_kiosk_db.sql
```

### Run Maintenance Tasks
```bash
mysql -u root -p kiosk_db < sql/maintenance.sql
```

### Verify Custom Orders Installation
```bash
mysql -u root -p kiosk_db < sql/verify_custom_orders.sql
```

---

## 📈 Analytics & Reporting

Pre-built analytics queries are available in `sql/analytics_queries.sql`:

- **Sales Performance** - Daily, weekly, monthly revenue trends
- **Product Analysis** - Top sellers, slow movers, category performance
- **Inventory Metrics** - Stock levels, turnover rates, expiration tracking
- **Customer Insights** - Purchase patterns, loyalty metrics
- **Operational Reports** - Transaction volumes, payment methods

```bash
# Run analytics queries
mysql -u root -p kiosk_db < sql/analytics_queries.sql
```

---

## 🔐 Security Notes

1. **Never commit database passwords** to version control
2. **Use environment variables** for credentials in production
3. **Enable SSL** for remote database connections
4. **Restrict user permissions** - grant only necessary privileges
5. **Regular backups** - automate daily backups with retention policy
6. **Audit logs** - review activity_logs regularly for suspicious activity

### Create Application User (Recommended)
```sql
-- Create dedicated application user
CREATE USER 'kiosk_app'@'localhost' IDENTIFIED BY 'your_secure_password';

-- Grant necessary permissions
GRANT SELECT, INSERT, UPDATE ON kiosk_db.* TO 'kiosk_app'@'localhost';
GRANT DELETE ON kiosk_db.inventory_stock_history TO 'kiosk_app'@'localhost';
GRANT DELETE ON kiosk_db.activity_logs TO 'kiosk_app'@'localhost';

FLUSH PRIVILEGES;
```

---

## 🐛 Troubleshooting

### Connection Issues
```bash
# Test MySQL connection
mysql -u root -p -e "SELECT VERSION();"

# Check if MySQL service is running (Windows)
sc query MySQL

# Check if MySQL service is running (Linux)
sudo systemctl status mysql
```

### Import Errors
```sql
-- Check current database
SELECT DATABASE();

-- Use correct database
USE kiosk_db;

-- Check existing tables
SHOW TABLES;
```

### Permission Denied
```sql
-- Check user privileges
SHOW GRANTS FOR 'your_username'@'localhost';

-- Grant all privileges (as root)
GRANT ALL PRIVILEGES ON kiosk_db.* TO 'your_username'@'localhost';
FLUSH PRIVILEGES;
```

---

## 📚 Additional Documentation

- **[MIGRATION_GUIDE.md](docs/MIGRATION_GUIDE.md)** - Upgrading from older schema versions
- **[SCHEMA_DIAGRAM.md](docs/SCHEMA_DIAGRAM.md)** - Visual ER diagrams and relationships
- **[SETUP_CHECKLIST.md](docs/SETUP_CHECKLIST.md)** - Detailed installation checklist

---

## 💡 Tips

- **Test Data**: Use `test_data.sql` for development and testing
- **Custom Orders**: Install `custom_orders_schema.sql` only if using customer portal
- **Performance**: Indexes are pre-configured for optimal query performance
- **Monitoring**: Check `activity_logs` and `inventory_stock_history` for system health

---

## 🆘 Support

For issues or questions:
1. Check the [SETUP_CHECKLIST.md](docs/SETUP_CHECKLIST.md) for common setup problems
2. Review [MIGRATION_GUIDE.md](docs/MIGRATION_GUIDE.md) for upgrade issues
3. Verify installation with `sql/verify_custom_orders.sql`

---

## 📝 Version History

- **v2.0** - Added custom orders system with 11 new tables
- **v1.5** - Enhanced analytics views and stored procedures
- **v1.0** - Initial release with core bakery kiosk tables

---

**Database:** kiosk_db  
**Total Tables:** 21 (10 core + 11 custom orders)  
**Views:** 5  
**Stored Procedures:** 4  
**Triggers:** 1
