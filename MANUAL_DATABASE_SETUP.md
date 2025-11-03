# Manual MySQL Database Setup

## Quick Setup Instructions

Since you're seeing no items in the inventory management, follow these steps to set up MySQL:

### Step 1: Set MySQL Root Password in Configuration

Edit `config/database.properties` and set your MySQL root password:

```properties
db.password=YOUR_MYSQL_ROOT_PASSWORD
```

Replace `YOUR_MYSQL_ROOT_PASSWORD` with your actual MySQL root password.

### Step 2: Create Database and Import Schema

Open MySQL Command Line or MySQL Workbench and run:

```sql
-- Create database
CREATE DATABASE IF NOT EXISTS kiosk_db;
USE kiosk_db;

-- Import the main schema
SOURCE D:/Development/Java/SweetBatterBakeshop/database/sql/setup.sql;

-- Import test data (includes inventory items)
SOURCE D:/Development/Java/SweetBatterBakeshop/database/sql/test_data.sql;
```

### Step 3: Verify Database Setup

Check if tables were created:

```sql
USE kiosk_db;
SHOW TABLES;
SELECT COUNT(*) FROM inventory;
```

You should see multiple tables and inventory items.

### Step 4: Restart the Application

1. Stop the currently running application
2. Compile: `.\mvnw.cmd clean compile`
3. Run the application again

The application should now connect to MySQL and show all inventory items.

## Troubleshooting

### If you don't know your MySQL root password:

1. Stop MySQL service:
   ```powershell
   net stop MySQL80
   ```

2. Start MySQL in safe mode and reset password (see MySQL documentation)

3. Or create a new MySQL user:
   ```sql
   CREATE USER 'kiosk_user'@'localhost' IDENTIFIED BY 'kiosk123';
   GRANT ALL PRIVILEGES ON kiosk_db.* TO 'kiosk_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

4. Then update `config/database.properties`:
   ```properties
   db.username=kiosk_user
   db.password=kiosk123
   ```

### Check Application Console

When you run the application, check the console output for:
- "✓ MySQL database connection established successfully!" - Good!
- "MySQL connection failed. Falling back to SQLite..." - Database not connected

## Current Configuration

The `config/database.properties` has been updated to use MySQL (not SQLite).

If MySQL connection fails, the app will automatically fall back to SQLite, but the SQLite database might be empty, which is why you see no items.
