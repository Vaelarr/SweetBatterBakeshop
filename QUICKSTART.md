# 🚀 Quick Start Guide

Get the SweetBatter Bakeshop kiosk system up and running in minutes!

## 📋 Prerequisites

Before you begin, ensure you have:

- **Java Development Kit (JDK) 11 or higher** - [Download from Adoptium](https://adoptium.net/)
- **MySQL 8.0+** or **MariaDB 10.2+** - [Download MySQL](https://dev.mysql.com/downloads/)
- **Maven** (included via Maven wrapper `mvnw`)
- **Git** (optional, for cloning) - [Download Git](https://git-scm.com/)

### Verify Prerequisites

```powershell
# Check Java version (should be 11 or higher)
java -version

# Check MySQL is installed and running
mysql --version

# Maven wrapper is included, no separate installation needed
```

---

## 📦 Step-by-Step Setup

### Step 1: Download the Project

**Option A: Clone with Git**
```powershell
git clone https://github.com/Vaelarr/SweetBatterBakeshop.git
cd SweetBatterBakeshop
```

**Option B: Download ZIP**
1. Download the ZIP file from GitHub
2. Extract to your desired location
3. Open PowerShell/Command Prompt in the extracted folder

---

### Step 2: Set Up the Database

> **Note:** The system supports **automatic SQLite fallback**. If MySQL is not available or not configured, the application will automatically create and use a SQLite database with the same schema. You can skip to Step 4 if you want to use SQLite.

#### 2.1 Start MySQL Service (Optional - for MySQL users)

**Windows:**
```powershell
# Start MySQL service
net start MySQL
# Or through Services app (services.msc)
```

**Check MySQL is Running:**
```powershell
mysql -u root -p
# Enter your MySQL root password when prompted
# If successful, you'll see the MySQL prompt: mysql>
# Type 'exit' to quit
```

#### 2.2 Create the Database (MySQL Only)

**Method 1: Using MySQL Command Line (Recommended)**
```powershell
# Navigate to the SQL directory
cd database\sql

# Run the setup script
mysql -u root -p < setup.sql

# Go back to project root
cd ..\..
```

**Method 2: From MySQL Prompt**
```powershell
# Login to MySQL
mysql -u root -p

# Then inside MySQL (use absolute path to your project):
source e:/Java/OOP/SweetBatterBakeshop/SweetBatterBakeshop/database/sql/setup.sql
# Or navigate to the sql directory first and use relative path:
# source database/sql/setup.sql
exit;
```

✅ **What this creates:**
- Database: `kiosk_db`
- 20+ tables for inventory, sales, admin, and custom orders
- Sample products and test accounts
- Views, triggers, and stored procedures

#### 2.3 SQLite Automatic Fallback

If you skip the MySQL setup or if MySQL connection fails, the application will:

1. **Automatically detect** MySQL is unavailable
2. **Create a SQLite database** file: `bakery_kiosk.db`
3. **Load the complete schema** from `database/sql/setup_sqlite.sql`
4. **Insert sample data** (8 products, admin accounts, test customer)

**Advantages of SQLite mode:**
- ✅ No separate database server needed
- ✅ Portable - entire database in one file
- ✅ Same schema and features as MySQL
- ✅ Perfect for testing and development

**When you see this message, SQLite is being used:**
```
MySQL connection failed. Falling back to SQLite...
✓ SQLite database connection established successfully!
  Database file: bakery_kiosk.db
```

---

### Step 3: Configure Database Connection (Optional for MySQL)

> **Note:** This step is only needed if you want to use MySQL. SQLite requires no configuration.

#### 3.1 Create Configuration File

The configuration file should be at: `config/database.properties`

**If the file doesn't exist:**
```powershell
# Copy from template
copy config\database.properties.template config\database.properties
```

#### 3.2 Edit Database Properties

Open `config/database.properties` and update with your MySQL credentials:

```properties
# MySQL Database Configuration
db.url=jdbc:mysql://localhost:3306/kiosk_db?useSSL=false&serverTimezone=UTC
db.username=root
db.password=YOUR_MYSQL_ROOT_PASSWORD

# Connection Pool Settings
db.pool.initialSize=5
db.pool.maxActive=10
db.pool.maxIdle=5
db.pool.minIdle=2
```

**Important:** Replace `YOUR_MYSQL_ROOT_PASSWORD` with your actual MySQL root password!

---

### Step 4: Build the Application

```powershell
# Clean and compile the project
mvnw clean compile

# Or if you prefer a full build with tests
mvnw clean package
```

✅ **Successful build output should show:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
```

---

### Step 5: Run the Application

#### 5.1 Run the Main Kiosk Application

```powershell
mvnw clean compile exec:java
```

Or specify the main class explicitly:
```powershell
mvnw exec:java -Dexec.mainClass="kiosk.BakeryPastriesKiosk"
```

#### 5.2 Login to Admin Panel

**Default Admin Credentials:**
- **Username:** `admin`
- **Password:** `admin123`

**Default Manager Credentials:**
- **Username:** `manager`
- **Password:** `manager123`

⚠️ **IMPORTANT:** Change these passwords in production!

---

### Step 6: Run Customer Portal (Optional)

For custom orders functionality:

```powershell
mvnw exec:java -Dexec.mainClass="kiosk.SimpleCustomerPortal"
```

**Test Customer Account:**
- **Email:** `customer@example.com`
- **Password:** `password123`

---

## 🎯 Verify Installation

### Check Database Tables

```powershell
mysql -u root -p kiosk_db
```

Then in MySQL:
```sql
-- Show all tables
SHOW TABLES;

-- Check inventory has products
SELECT name, category, price, stock_quantity FROM inventory LIMIT 10;

-- Check admin users
SELECT username, full_name, role FROM admin_users;

-- Exit MySQL
exit;
```

### Expected Tables (25+ tables)
- `inventory` - Product catalog
- `sales_transactions` - Sales records
- `admin_users` - Admin accounts
- `customers` - Customer accounts
- `custom_orders` - Custom order management
- And many more...

---

## � Project Structure Overview

```
SweetBatterBakeshop/
├── src/main/java/kiosk/          # Application source code
│   ├── BakeryPastriesKiosk.java  # Main kiosk application
│   ├── SimpleCustomerPortal.java # Customer portal app
│   ├── controller/               # Business logic controllers
│   ├── database/                 # Database access layer (DAOs)
│   ├── model/                    # Data models
│   ├── util/                     # Utility classes
│   └── view/                     # UI components
├── src/main/resources/           # Application resources
│   ├── icons/                    # UI icons
│   ├── breads&rolls/            # Product images
│   ├── cakes/                   # Product images
│   └── pastries&desserts/       # Product images
├── config/                       # Configuration files
│   └── database.properties      # Database connection config
├── database/                     # Database scripts & docs
│   ├── sql/
│   │   ├── setup.sql           # Complete database schema
│   │   └── README.md           # SQL documentation
│   └── docs/                    # Database documentation
├── target/                       # Compiled classes (generated)
├── pom.xml                      # Maven project configuration
└── README.md                    # Main documentation
```
```powershell
.\mvnw clean package
```

### Build Windows Executables
```powershell
.\scripts\build_executables.bat
```

Creates `target/BakeryKiosk.exe` and `target/CustomerPortal.exe`

---

## 🆘 Troubleshooting

### Problem: Database Connection Failed

**Error Message:** `Cannot connect to database` or `Access denied`

**Solutions:**
1. **Check MySQL is running:**
   ```powershell
   net start MySQL
   # Or check in Services (Win+R, type: services.msc)
   ```

2. **Verify credentials in `config/database.properties`:**
   - Make sure username and password match your MySQL installation
   - Default MySQL username is usually `root`

3. **Test MySQL connection manually:**
   ```powershell
   mysql -u root -p
   # If this fails, your MySQL credentials are incorrect
   ```

4. **Check the database exists:**
   ```sql
   mysql -u root -p
   SHOW DATABASES;
   # Look for 'kiosk_db' in the list
   ```

### Problem: Java Version Error

**Error Message:** `UnsupportedClassVersionError` or `java: invalid target release`

**Solution:**
```powershell
# Check your Java version
java -version

# Should show version 11 or higher
# Example: openjdk version "11.0.12"
```

If version is below 11, download and install Java 11+ from [Adoptium](https://adoptium.net/)

### Problem: Maven Build Fails

**Error Message:** `BUILD FAILURE` or dependency errors

**Solutions:**
```powershell
# Clean and rebuild
mvnw clean install -U

# If Maven wrapper has issues, try:
mvnw clean compile --fail-at-end

# Delete the target folder and rebuild
Remove-Item -Recurse -Force target
mvnw clean compile
```

### Problem: Port 3306 Already in Use

**Solution:**
Either change MySQL port in `config/database.properties`:
```properties
db.url=jdbc:mysql://localhost:3307/kiosk_db?useSSL=false&serverTimezone=UTC
```

Or stop the conflicting service:
```powershell
# Find what's using port 3306
netstat -ano | findstr :3306
# Kill the process if needed (replace PID with actual process ID)
taskkill /PID <PID> /F
```

### Problem: Missing Configuration File

**Error Message:** `database.properties not found`

**Solution:**
```powershell
# Copy from template
copy config\database.properties.template config\database.properties

# Or create manually with this content:
# db.url=jdbc:mysql://localhost:3306/kiosk_db?useSSL=false&serverTimezone=UTC
# db.username=root
# db.password=YOUR_PASSWORD
```

### Problem: Application Won't Start

**Check these common issues:**
1. Database is running and accessible
2. Configuration file exists and has correct credentials
3. Java version is 11 or higher
4. Project was compiled successfully (`mvnw clean compile`)
5. Check for error messages in the console

---

## 🔐 Security Notes

### Change Default Passwords!

After installation, change these default passwords:

**In MySQL:**
```sql
mysql -u root -p kiosk_db

-- Change admin password
UPDATE admin_users SET password_hash = 'NEW_SECURE_PASSWORD' WHERE username = 'admin';

-- Change manager password
UPDATE admin_users SET password_hash = 'NEW_SECURE_PASSWORD' WHERE username = 'manager';

-- Change test customer password
UPDATE customers SET password_hash = 'NEW_SECURE_PASSWORD' WHERE email = 'customer@example.com';
```

**Note:** In production, passwords should be properly hashed using bcrypt or similar!

---

## 📚 Additional Resources

### Documentation
- **Main README:** [README.md](README.md) - Complete project documentation
- **Database Guide:** [database/README.md](database/README.md) - Database schema and management
- **SQL Scripts:** [database/sql/README.md](database/sql/README.md) - SQL documentation
- **Migration Guide:** [database/docs/MIGRATION_GUIDE.md](database/docs/MIGRATION_GUIDE.md)

### Key Features

**Kiosk Application:**
- 📦 Inventory management with expiration tracking
- 💰 Point of sale (POS) with multiple payment methods
- 📊 Sales analytics and reporting
- 👥 Admin user management
- 🎂 Custom orders system

**Customer Portal:**
- 🛍️ Browse product catalog
- 🎨 Create custom cake orders
- 📅 Schedule pickup/delivery
- 📜 Order history tracking
- 👤 Account management

---

## 💡 Quick Tips

- **Sample Data Included:** The setup script adds 8 sample products across 4 categories
- **Test Accounts Ready:** Use the default credentials to explore the system immediately
- **Custom Orders:** Requires customer account - use the test account to try this feature
- **Admin Panel:** Access via login button in the kiosk application
- **Database Backup:** Regularly backup your `kiosk_db` database in production!

### Useful Commands

```powershell
# Rebuild everything
mvnw clean compile

# Run kiosk
mvnw exec:java

# Run customer portal
mvnw exec:java -Dexec.mainClass="kiosk.SimpleCustomerPortal"

# Check database
mysql -u root -p kiosk_db

# View logs (if any errors occur)
# Check console output for error messages
```

---

## ✅ Installation Checklist

- [ ] Java 11+ installed and verified
- [ ] MySQL 8.0+ installed and running
- [ ] Project downloaded/cloned
- [ ] Database created using `setup.sql`
- [ ] `config/database.properties` configured
- [ ] Project compiled successfully (`mvnw clean compile`)
- [ ] Application runs without errors
- [ ] Can login to admin panel
- [ ] Database contains sample products

---

## 🎓 Next Steps

After successful installation:

1. **Explore the Kiosk:** Browse products, add to cart, complete a sale
2. **Try Admin Panel:** Manage inventory, view sales reports
3. **Create Custom Order:** Use customer portal to place a custom cake order
4. **Add Products:** Use admin panel to add your own products
5. **Customize:** Modify the theme, add new features, etc.

---

## 🆘 Still Need Help?

If you encounter issues not covered here:

1. **Check the main [README.md](README.md)** for detailed documentation
2. **Review error messages** in the console output
3. **Verify all prerequisites** are correctly installed
4. **Check database connectivity** manually using MySQL command line
5. **Create an issue** on GitHub with error details

---

**Happy Baking! 🍰**
