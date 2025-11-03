# Quick Start Guide - MySQL Database Integration

This guide will help you quickly set up MySQL database for the Kiosk system.

> **✨ NEW: Automatic SQLite Fallback**  
> The system now automatically uses SQLite if MySQL is unavailable. This means you can start developing immediately without MySQL setup! See [SQLite Fallback Guide](SQLITE_FALLBACK.md) for details.

## Prerequisites Checklist

- [ ] MySQL Server 5.7+ installed *(Optional - SQLite fallback available)*
- [ ] MySQL JDBC Driver (mysql-connector-java-8.0.33.jar or later) *(Included in pom.xml)*
- [ ] MySQL server is running *(Optional - SQLite fallback available)*

## Quick Start Options

### Option 1: Use SQLite Fallback (Fastest - No Setup Required)
Simply run the application! It will automatically create a local SQLite database file (`bakery_kiosk.db`) if MySQL is not available.

```powershell
mvn clean compile exec:java
```

### Option 2: Full MySQL Setup (Recommended for Production)
Follow the steps below to set up MySQL for production use.

## Step-by-Step MySQL Setup

### 1. Install MySQL (if not already installed)

**Windows:**
```powershell
winget install Oracle.MySQL
```

**Or download from:** https://dev.mysql.com/downloads/mysql/

**Mac:**
```bash
brew install mysql
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt-get install mysql-server
```

### 2. Start MySQL Service

**Windows:**
```powershell
net start MySQL80
```

**Mac:**
```bash
brew services start mysql
```

**Linux:**
```bash
sudo systemctl start mysql
```

### 3. Run Database Setup

**Option A: Use the setup script (Recommended)**

Windows:
```powershell
.\setup_database.bat
```

Linux/Mac:
```bash
chmod +x setup_database.sh
./setup_database.sh
```

**Option B: Manual setup**

1. Open MySQL command line:
```bash
mysql -u root -p
```

2. Run:
```sql
source database/sql/setup.sql
```

### 4. Download MySQL JDBC Driver

Download from: https://dev.mysql.com/downloads/connector/j/

Or use direct link:
- Platform Independent (ZIP): Extract the `.jar` file

### 5. Add JDBC Driver to Project

**Option A: Copy to lib folder**
```
JavaKiosk/ConvenientStoreKiosk/lib/mysql-connector-java-8.0.33.jar
```

**Option B: Using Maven** (if using Maven)
Add to `pom.xml`:
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

**Option C: Using Gradle** (if using Gradle)
Add to `build.gradle`:
```gradle
dependencies {
    implementation 'mysql:mysql-connector-java:8.0.33'
}
```

### 6. Configure Database Connection

Edit `config/database.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/kiosk_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=root
db.password=YOUR_MYSQL_PASSWORD
db.driver=com.mysql.cj.jdbc.Driver
```

**⚠️ IMPORTANT:** Replace `YOUR_MYSQL_PASSWORD` with your actual MySQL root password!

### 7. Add to IDE Classpath

**Eclipse:**
1. Right-click project → Properties
2. Java Build Path → Libraries
3. Add External JARs → Select mysql-connector-java-8.0.33.jar

**IntelliJ IDEA:**
1. File → Project Structure
2. Libraries → + → Java
3. Select mysql-connector-java-8.0.33.jar

**VS Code:**
1. Add to Referenced Libraries
2. Or update `.classpath` file

### 8. Run the Application

The database tables will be created automatically on first run!

## Verification

### Check if database was created:

```sql
mysql -u root -p
```

```sql
USE kiosk_db;
SHOW TABLES;
```

You should see:
- inventory
- sales_transactions
- sales_items
- admin_users (optional)
- help_requests (optional)

### Check if sample data was loaded:

```sql
SELECT COUNT(*) FROM inventory;
```

Should return approximately 51 items.

## Troubleshooting

### Problem: "Access denied for user 'root'@'localhost'"

**Solution:**
1. Check your password in `config/database.properties`
2. Try resetting MySQL root password:

```bash
mysql -u root
ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';
FLUSH PRIVILEGES;
```

### Problem: "No suitable driver found"

**Solution:**
1. Verify `mysql-connector-java-8.0.33.jar` is in classpath
2. Check the jar file is not corrupted
3. Restart your IDE

### Problem: "Communications link failure"

**Solution:**
1. Ensure MySQL service is running:
   - Windows: `net start MySQL80`
   - Mac: `brew services list`
   - Linux: `sudo systemctl status mysql`

2. Check port 3306 is not blocked by firewall

3. Verify MySQL is listening on localhost:
   ```bash
   netstat -an | findstr 3306
   ```

### Problem: Database already exists

**Solution:**
```sql
DROP DATABASE kiosk_db;
```
Then run setup again.

## Default Database Credentials

The application defaults to:
- **Host:** localhost
- **Port:** 3306
- **Database:** kiosk_db
- **Username:** root
- **Password:** (empty - YOU MUST SET THIS)

## Testing the Connection

Run the database initializer to test:

```bash
javac kiosk/database/DatabaseInitializer.java
java kiosk.database.DatabaseInitializer
```

Expected output:
```
Initializing database...
Database connection established successfully!
Inventory table created/verified successfully.
Sales tables created/verified successfully.
Database initialization completed!
```

## What's Different from File Storage?

### Before (File-based):
- Data stored in `data/inventory.dat` and `data/sales.dat`
- Manual serialization/deserialization
- Limited querying capabilities
- No concurrent access protection

### After (MySQL-based):
- Data stored in MySQL database
- Automatic persistence
- SQL queries for advanced filtering
- Transaction support
- Better data integrity
- Concurrent access handling

## Next Steps

1. ✅ Database is set up
2. ✅ Tables are created
3. ✅ Sample data is loaded
4. 🎯 Run your application
5. 🎯 Test inventory and sales features
6. 🎯 Check database to see records being saved

## Need Help?

See `DATABASE_SETUP.md` for detailed documentation.

## Security Note

**NEVER commit your `config/database.properties` file with real passwords!**

Add to `.gitignore`:
```
config/database.properties
```

Create a template instead:
```
config/database.properties.template
```
