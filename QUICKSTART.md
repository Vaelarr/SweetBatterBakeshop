# SweetBatter Bakeshop Kiosk - Quick Setup Guide

Get your bakery kiosk up and running in 5 minutes!

---

## Prerequisites

1. **Java Development Kit (JDK) 11 or higher**
   - Download from: https://adoptium.net/
   - Verify installation: `java -version`

2. **MySQL Server 5.7 or higher**
   - Download from: https://dev.mysql.com/downloads/mysql/
   - Or use XAMPP/WAMP/MAMP for easy setup
   - Verify installation: `mysql --version`

3. **Maven** (included via wrapper - no installation needed)

---

## Quick Setup (4 Steps)

### Step 1: Database Setup ⚡

**Option A: Automatic Setup (Recommended)**

**Windows:**
```powershell
.\setup_database.bat
```

**Linux/Mac:**
```bash
./setup_database.sh
```

**Option B: Manual Setup**

1. Start MySQL server
2. Create database:
   ```sql
   CREATE DATABASE kiosk_db;
   ```
3. Copy and edit configuration:
   ```powershell
   # Windows
   Copy-Item config/database.properties.template config/database.properties
   
   # Linux/Mac
   cp config/database.properties.template config/database.properties
   ```
4. Update `config/database.properties` with your MySQL credentials:
   ```properties
   db.url=jdbc:mysql://localhost:3306/kiosk_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   db.username=root
   db.password=YOUR_PASSWORD_HERE
   db.driver=com.mysql.cj.jdbc.Driver
   ```

---

### Step 2: Build the Application 🔨

```powershell
# Clean and build (downloads dependencies automatically)
.\mvnw.cmd clean package
```

This will:
- ✅ Download all required dependencies (FlatLaf, MySQL Connector, etc.)
- ✅ Compile all Java source files
- ✅ Create executable JAR files in `target/` directory
- ✅ Run all tests (if any)

**Expected output:** `BUILD SUCCESS`

---

### Step 3: Run the Application 🚀

**Option 1: Maven Exec (Development)**
```powershell
.\mvnw.cmd exec:java
```

**Option 2: Executable JAR (Production)**
```powershell
java -jar target/convenient-store-kiosk-1.0.0-jar-with-dependencies.jar
```

**Option 3: IDE (IntelliJ IDEA / Eclipse / VS Code)**
1. Open project in your IDE
2. Navigate to `src/main/java/kiosk/BakeryPastriesKiosk.java`
3. Click **Run** ▶️

---

### Step 4: First Launch Experience ✨

The application will automatically:
1. ✅ Show professional splash screen with SweetBatter branding
2. ✅ Connect to MySQL database
3. ✅ Create database tables (`inventory`, `sales_transactions`, `sales_items`)
4. ✅ Populate **50+ sample bakery products** if database is empty
5. ✅ Launch fullscreen kiosk interface with 4 product categories

**That's it! You're ready to go!** 🎉

---

## Using the Kiosk 🥐

### Customer Mode (Main Interface)

1. **Home Screen:** Browse 4 product categories
   - 🥖 **Breads & Rolls** - Artisan breads, dinner rolls, baguettes
   - 🥐 **Pastries & Desserts** - Croissants, danishes, cookies, tarts
   - 🎂 **Cakes & Special Occasions** - Custom cakes, cupcakes, celebration items
   - ☕ **Beverages & Extras** - Coffee, tea, spreads, bakery accessories

2. **Browse Products:**
   - Click on any category card
   - View product details (name, price, stock, expiration date)
   - Click "Add to Cart" button

3. **Shopping Cart:**
   - Access via **"Cart"** button (top-right navigation)
   - Adjust item quantities (+/- buttons)
   - Remove items with "Remove" button
   - View real-time total
   - Click **"Checkout"** to complete purchase

4. **Help Request:**
   - Click **"Help"** button to request staff assistance

### Admin Mode (Management Interface)

1. **Access Admin Panel:**
   - Click **"Admin"** button in top navigation bar
   
2. **Login Credentials:**
   - **Username:** `admin`
   - **Password:** `password123`
   
3. **Admin Features:**
   - **Inventory Management:**
     - Add new products
     - Update existing items (price, stock, expiration dates)
     - Delete discontinued products
     - Track low stock items
     - Monitor expiring products
   
   - **Sales Reports:**
     - View transaction history
     - Daily sales summaries
     - Top-selling products analysis
     - Revenue tracking
   
   - **Help Requests:**
     - View customer assistance requests
     - Manage and resolve help tickets

---

## Troubleshooting 🔧

### Database Connection Failed ❌
**Symptoms:** Error on startup about database connection

**Solutions:**
- ✅ Ensure MySQL server is running
  ```powershell
  # Windows - Check status
  sc query MySQL80
  
  # Linux - Check status
  sudo systemctl status mysql
  ```
- ✅ Verify credentials in `config/database.properties`
- ✅ Check that database `kiosk_db` exists
  ```sql
  SHOW DATABASES;
  ```
- ✅ Verify firewall allows MySQL port 3306
- ✅ Test connection manually:
  ```powershell
  mysql -u root -p kiosk_db
  ```

### Application Won't Start ❌
**Symptoms:** Application crashes or won't launch

**Solutions:**
- ✅ Verify Java 11+ is installed:
  ```powershell
  java -version
  ```
- ✅ Rebuild project:
  ```powershell
  .\mvnw.cmd clean package
  ```
- ✅ Check console output for specific error messages
- ✅ Ensure no other instance is running

### No Products Showing 📦
**Symptoms:** Empty product categories

**Solutions:**
- ✅ Check database connection (see above)
- ✅ Verify `inventory` table exists and has data:
  ```sql
  USE kiosk_db;
  SELECT COUNT(*) FROM inventory;
  ```
- ✅ Re-run database setup:
  ```powershell
  .\setup_database.bat
  ```
- ✅ Check application logs for errors

### UI Issues / Display Problems 🎨
**Symptoms:** Broken UI, missing buttons, layout issues

**Solutions:**
- ✅ Ensure FlatLaf dependencies are downloaded:
  ```powershell
  .\mvnw.cmd dependency:tree
  ```
- ✅ Force update dependencies:
  ```powershell
  .\mvnw.cmd clean package -U
  ```
- ✅ Check Java version (requires 11+)
- ✅ Try running on different display/resolution

### Build Failures 🔨
**Symptoms:** Maven build fails

**Solutions:**
- ✅ Clean Maven cache:
  ```powershell
  .\mvnw.cmd clean
  ```
- ✅ Delete `target/` folder and rebuild
- ✅ Check internet connection (Maven needs to download dependencies)
- ✅ Verify `pom.xml` is not corrupted

---

## Default Sample Data 📋

On first run with an empty database, the system automatically creates:

### Product Categories (4)
- 🥖 **Breads & Rolls** - ~15 items
- 🥐 **Pastries & Desserts** - ~20 items
- 🎂 **Cakes & Special Occasions** - ~10 items
- ☕ **Beverages & Extras** - ~10 items

### Sample Products Include:
- Artisan sourdough bread, baguettes, dinner rolls
- Croissants, danishes, eclairs, macarons
- Custom celebration cakes, cupcakes
- Coffee, tea, jams, spreads
- **Realistic pricing** in Philippine Pesos (₱)
- **Stock quantities** ranging from 5-100 items
- **Expiration dates** set 3-30 days in the future
- **Barcodes** for inventory tracking
- **Supplier information**

---

## Next Steps 🎯

Once your kiosk is running:

1. ✅ **Explore the Interface**
   - Browse all 4 product categories
   - Add items to cart and test checkout
   - Try the admin panel features

2. ✅ **Customize Your Catalog**
   - Access Admin Panel
   - Update product prices for your region
   - Add your bakery's specialty items
   - Remove sample products you don't need

3. ✅ **Configure Settings**
   - Review database configuration
   - Update admin credentials (change default password!)
   - Adjust stock thresholds

4. ✅ **Test Workflows**
   - Complete a test purchase
   - Generate sales reports
   - Check inventory updates
   - Test help request system

5. ✅ **Learn More**
   - Read full documentation: [README.md](README.md)
   - Admin guide: [docs/ADMIN_GUIDE.md](docs/ADMIN_GUIDE.md)
   - Database guide: [docs/QUICKSTART_DATABASE.md](docs/QUICKSTART_DATABASE.md)
   - Maven guide: [docs/MAVEN_GUIDE.md](docs/MAVEN_GUIDE.md)

---

## Getting Help 💬

Need assistance?

1. **Documentation:**
   - 📖 [Main README](README.md) - Complete project documentation
   - 📖 [Admin Guide](docs/ADMIN_GUIDE.md) - Admin panel features
   - 📖 [Database Guide](docs/QUICKSTART_DATABASE.md) - Database setup details
   - 📖 [Maven Guide](docs/MAVEN_GUIDE.md) - Build system help

2. **Common Issues:**
   - Check [Troubleshooting](#troubleshooting-) section above
   - Review console error messages
   - Check database connection logs

3. **Community:**
   - GitHub Issues: [Report a bug or request a feature](https://github.com/Vaelarr/SweetBatterBakeshop/issues)
   - GitHub Discussions: Ask questions and share ideas

---

## System Requirements Summary ⚙️

| Component | Requirement | Notes |
|-----------|-------------|-------|
| **Java** | JDK 11+ | Required for compilation and runtime |
| **MySQL** | 5.7+ | Can use MariaDB 10.2+ as alternative |
| **Maven** | 3.6+ | Included via Maven Wrapper |
| **RAM** | 512MB+ | 1GB+ recommended |
| **Disk Space** | 200MB+ | For application + database |
| **OS** | Windows/Linux/Mac | Cross-platform compatible |
| **Display** | 1024x768+ | 1920x1080 recommended for fullscreen |

---

**Version:** 3.0.0  
**Last Updated:** November 1, 2025  
**Repository:** [github.com/Vaelarr/SweetBatterBakeshop](https://github.com/Vaelarr/SweetBatterBakeshop)

---

**Ready to start baking? 🥖 Happy selling! 🎂**
