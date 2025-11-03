# Application Launchers

This directory contains two separate launchers for the Sweet Batter Bakeshop system:

## 🏪 Main Bakery Kiosk
**File:** `run_bakery_kiosk.bat`

**Purpose:** Launches the main bakery point-of-sale kiosk application

**Features:**
- Product catalog browsing (Breads, Pastries, Cakes, Beverages)
- Shopping cart management
- Customer checkout
- Admin login access
- Help request system

**To Run:**
```powershell
.\run_bakery_kiosk.bat
```

**Main Class:** `main.java.kiosk.BakeryPastriesKiosk`

---

## 🎂 Customer Custom Orders Portal
**File:** `run_customer_portal.bat`

**Purpose:** Launches the customer-facing custom order application with splash screen

**Features:**
- Beautiful animated splash screen
- Customer registration and login
- Custom order creation
- Product selection (11 custom products)
- Delivery or pickup options
- Order history tracking
- Date/time scheduling

**To Run:**
```powershell
.\run_customer_portal.bat
```

**Main Class:** `main.java.kiosk.SimpleCustomerPortal`

**Test Credentials:**
- Email: `john.doe@email.com`
- Password: `password123`

---

## 📋 Requirements

Both applications require:
- **Java 11** or higher
- **Maven 3.9.11** or higher
- **MySQL Database** running on `localhost:3306`
- Database: `kiosk_db` with credentials `admin/admin`

## 🗄️ Database Setup

If you haven't set up the database yet:

1. **Main Database:**
   ```powershell
   .\setup_database.bat
   ```

2. **Custom Orders Schema:**
   ```powershell
   cd database
   .\install_custom_orders.bat
   ```

## 🚀 Quick Start

### First Time Setup:
1. Set up database (see above)
2. Choose which application to run
3. Double-click the corresponding `.bat` file

### Running Applications:

**For the Main Kiosk:**
```powershell
.\run_bakery_kiosk.bat
```
- Use the kiosk to browse products and make purchases
- Click "Admin" to access inventory management (login: admin/admin123)

**For Customer Portal:**
```powershell
.\run_customer_portal.bat
```
- Watch the animated splash screen load
- Sign up as a new customer or login
- Create custom orders with special requirements

## 🔧 Troubleshooting

### Application Won't Start
- Verify MySQL is running
- Check database credentials in `config/database.properties`
- Ensure Maven is in your PATH: `mvn --version`

### Compilation Errors
- Make sure you're in the project root directory
- Try: `mvn clean install`

### Database Connection Failed
- Verify MySQL service is running
- Check `kiosk_db` database exists
- Confirm credentials: admin/admin

## 📁 Project Structure

```
SweetBatterBakeshop/
├── run_bakery_kiosk.bat          ← Main kiosk launcher
├── run_customer_portal.bat       ← Customer portal launcher
├── src/main/java/kiosk/
│   ├── BakeryPastriesKiosk.java         (Main kiosk entry)
│   ├── SimpleCustomerPortal.java        (Customer portal entry)
│   ├── view/customer/
│   │   ├── CustomerSplashScreen.java    (Splash screen)
│   │   └── SimpleOrderPanel.java        (Order interface)
│   └── controller/
│       └── CustomOrderController.java   (Order logic)
└── database/
    └── custom_orders_schema.sql         (Custom orders DB)
```

## 📞 Support

For issues or questions:
1. Check the console output for error messages
2. Review the database setup in `database/README.md`
3. Verify all dependencies are installed

---

**Note:** Both applications can run simultaneously if needed, but they share the same database.
