# Database SQL Scripts

This directory contains the essential SQL scripts for the SweetBatterBakeshop kiosk system.

## Files

### setup.sql
**Primary MySQL database setup script** - Run this to create the complete database schema for MySQL/MariaDB.

### setup_sqlite.sql
**SQLite database setup script** - Complete schema for SQLite3 with identical structure to MySQL version.

#### Includes:
- **Core Tables:**
  - `inventory` - Product inventory management
  - `inventory_stock_history` - Stock change tracking
  - `sales_transactions` - Sales transaction records
  - `sales_items` - Individual items per transaction
  - `admin_users` - Admin account management
  - `admin_activity_log` - Admin action audit trail
  - `help_requests` - Customer support tickets
  - `daily_sales_summary` - Aggregated daily sales data
  - `product_sales_analytics` - Product performance metrics
  - `system_settings` - Application configuration

- **Custom Orders System:**
  - `customers` - Customer account management
  - `customer_addresses` - Delivery addresses
  - `custom_order_categories` - Order types (Custom Cakes, Bulk Orders, etc.)
  - `custom_order_base_products` - Customizable product catalog
  - `addon_categories` - Add-on groupings (Flavors, Fillings, etc.)
  - `addons` - Individual add-on options
  - `product_addon_compatibility` - Product-addon relationships
  - `custom_orders` - Custom order tracking
  - `custom_order_addons` - Selected add-ons per order
  - `custom_order_attachments` - Order reference files
  - `custom_order_status_history` - Order status audit trail
  - `custom_order_reviews` - Customer feedback

- **Database Features:**
  - All indexes for performance optimization
  - Sample data for testing (8 products, 2 admin users, test customer)
  - CHECK constraints for data validation
  - Foreign keys with CASCADE rules

## Usage

### MySQL Setup
```bash
mysql -u root -p < setup.sql
```

### SQLite Setup

**Automatic (Recommended):**
The application automatically initializes SQLite schema on first run when MySQL is unavailable.

**Manual:**
```bash
sqlite3 bakery_kiosk.db < setup_sqlite.sql
```

### Reset Database (if needed)

**MySQL:**
```bash
mysql -u root -p -e "DROP DATABASE IF EXISTS kiosk_db;"
mysql -u root -p < setup.sql
```

**SQLite:**
```bash
# Delete the database file
rm bakery_kiosk.db

# Recreate with schema
sqlite3 bakery_kiosk.db < setup_sqlite.sql
```

## Schema Compatibility

Both `setup.sql` (MySQL) and `setup_sqlite.sql` (SQLite) create **identical schemas** with these adaptations:

### MySQL-specific syntax:
- `INT AUTO_INCREMENT`
- `ENUM` data types
- `BOOLEAN` data type
- `TIMESTAMP` with `ON UPDATE CURRENT_TIMESTAMP`
- `ENGINE=InnoDB`, `CHARSET=utf8mb4`
- `INSERT ... ON DUPLICATE KEY UPDATE`

### SQLite-equivalent syntax:
- `INTEGER PRIMARY KEY AUTOINCREMENT`
- `TEXT` with `CHECK` constraints for enums
- `INTEGER` (0/1) for boolean
- `TEXT` for timestamps (ISO 8601 format)
- No engine/charset specifications
- `INSERT OR IGNORE`

The application uses `SqlDialect.java` to handle these differences transparently.

## Default Credentials

**Admin Users:**
- Username: `admin` / Password: `admin123` (SUPER_ADMIN)
- Username: `manager` / Password: `manager123` (MANAGER)

**Test Customer:**
- Email: `customer@example.com` / Password: `password123`

⚠️ **IMPORTANT:** Change all default passwords in production!

## Database: kiosk_db
- Character Set: utf8mb4
- Collation: utf8mb4_unicode_ci
- Engine: InnoDB

## Notes
- All timestamps use MySQL's CURRENT_TIMESTAMP
- Foreign keys enforce referential integrity
- Indexes optimize common query patterns
- IF NOT EXISTS prevents errors on re-run
