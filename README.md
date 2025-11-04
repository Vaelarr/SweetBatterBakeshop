# SweetBatter Bakeshop - Complete Kiosk & Custom Orders System

A professional bakery management system built with Java Swing, featuring **two integrated applications**: a point-of-sale kiosk and a customer-facing custom orders portal. Built with **MVC Architecture**, **MySQL Database with DAO Pattern**, **Maven Build System**, and **Modern FlatLaf UI**.

**Current Version:** 4.0.0 | **Last Updated:** November 3, 2025

> **✨ NEW: Automatic SQLite Fallback!** No MySQL? No problem! The system now automatically uses SQLite when MySQL is unavailable. Perfect for development and testing.

📚 **Quick Links:** [QUICKSTART.md](QUICKSTART.md) | [EXECUTABLES_QUICKSTART.md](EXECUTABLES_QUICKSTART.md) | [Database Setup](database/README.md)

---

## 📑 Table of Contents

- [System Overview](#-system-overview)
- [Quick Start](#-quick-start)
- [Applications](#-applications)
- [Features](#-features)
- [Database Integration](#ï¸-database-integration)
- [Architecture](#ï¸-architecture)
- [Documentation](#-documentation)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [Development](#ï¸-development)
- [Author](#-author)

---

## System Overview

This repository contains **two separate applications** that share a common database:

### 1. **Bakery Kiosk** (Main POS System)
- Point-of-sale terminal for in-store purchases
- Product catalog browsing and shopping cart
- Admin panel with inventory management
- Sales reporting and analytics
- Custom orders management (admin view)

### 2. **Customer Portal** (Custom Orders)
- Customer account registration and login
- Custom order creation with add-ons
- Pickup/delivery scheduling
- Order history tracking
- Beautiful splash screen

---

## Quick Start

### Prerequisites
- **Java 11** or higher
- **MySQL 5.7** or higher
- **Maven** (included via wrapper)

### 1. Database Setup (One-Time)
```powershell
# Setup main database
.\setup_database.bat

# Install custom orders schema
cd database
.\install_custom_orders.bat
cd ..
```

### 2. Run Applications

**Main Bakery Kiosk:**
```powershell
.\run_bakery_kiosk.bat
```
- Admin Login: `admin` / `admin123`

**Customer Portal:**
```powershell
.\run_customer_portal.bat
```
- Test Account: `john.doe@email.com` / `password123`

– **Detailed Setup:** See [QUICKSTART.md](QUICKSTART.md) for step-by-step instructions.


---

## Applications

### 1. Bakery Kiosk (Main POS)

**Launch:** `.\run_bakery_kiosk.bat`

**Features:**
- **Product Catalog** - Browse 50+ bakery items across 4 categories
- **Shopping Cart** - Real-time cart management
- **Checkout System** - Complete transaction processing
- **Admin Panel** - Full inventory and sales management
- **Sales Reports** - Daily summaries and analytics
- **Custom Orders Tab** - View and manage custom orders from customers

**Admin Access:**
- Click "Admin" button on main kiosk
- Login: `admin` / `admin123`

**Main Class:** `main.java.kiosk.BakeryPastriesKiosk`

---

### 2. Customer Portal (Custom Orders)

**Launch:** `.\run_customer_portal.bat`

**Features:**
- **Animated Splash Screen** - Professional loading experience
- **Account Management** - Customer registration and login
- **Custom Order Builder** - Create custom cakes and bulk orders
- **Add-On Selection** - Flavors, fillings, decorations, messages
- **Pickup/Delivery** - Schedule fulfillment with date/time
- **Order History** - Track all your custom orders
- **Profile Management** - Update contact information

**Test Account:**
- Email: `john.doe@email.com`
- Password: `password123`

**Main Class:** `main.java.kiosk.SimpleCustomerPortal`

---

## Features

### Bakery Kiosk Features
- **Modern Touch-Friendly UI** - Full-screen interface with gradient designs
- **Product Categories** - Breads & Rolls, Pastries & Desserts, Cakes & Special Occasions, Beverages & Extras
- **Shopping Cart** - Real-time updates with live totals
- **Advanced Inventory** - Stock tracking, expiration dates, barcodes, suppliers
- **Sales Analytics** - Transaction history, reports, daily summaries
- **Admin Panel** - Complete inventory control and custom orders management
- **Modern FlatLaf Theme** - Custom bakery-inspired colors and styling
- **50+ Sample Products** - Pre-populated realistic inventory

### Customer Portal Features
- **Beautiful Splash Screen** - Animated loading with progress indicators
- **Customer Accounts** - Secure registration with password hashing (SHA-256)
- **Custom Product Catalog** - 11 customizable products across 4 categories
- **Add-On System** - 20+ add-ons including flavors, fillings, decorations
- **Flexible Fulfillment** - Pickup or delivery with address management
- **Order Tracking** - Real-time status updates (Pending â†’ Confirmed â†’ Ready)
- **Responsive Design** - Clean, modern UI with BakeryTheme styling

## Database Integration

The system uses **MySQL database** with professional DAO pattern for two integrated schemas:

### Main Kiosk Database
**3 Core Tables:**
- `inventory` - Product catalog (stock, pricing, expiration, barcodes, suppliers)
- `sales_transactions` - Completed sales with timestamps
- `sales_items` - Individual transaction line items

### Custom Orders Database
**9 Tables + 3 Views + 2 Triggers:**
- `customers` - Customer accounts with loyalty points
- `customer_addresses` - Delivery addresses
- `custom_products` - Base products for customization
- `addon_categories` - Addon organization
- `addons` - Flavors, fillings, decorations
- `custom_orders` - Order master records
- `order_addons` - Selected add-ons per order
- `order_payments` - Payment tracking
- `order_reviews` - Customer feedback

**Views:**
- `order_details_view` - Complete order information
- `customer_order_summary` - Customer statistics
- `daily_custom_order_stats` - Daily analytics

**Triggers:**
- Auto-generate order numbers (CO-YYYYMMDD-####)
- Update order totals automatically

### Database Setup

```powershell
# Initial setup (one-time)
.\setup_database.bat

# Install custom orders schema
cd database
.\install_custom_orders.bat
```

**Configuration:** Edit `config/database.properties`
```properties
db.url=jdbc:mysql://localhost:3306/kiosk_db
db.username=root
db.password=YOUR_PASSWORD
```

– **Complete Guide:** [docs/QUICKSTART_DATABASE.md](docs/QUICKSTART_DATABASE.md)

## Architecture

This project follows **MVC (Model-View-Controller)** with **DAO (Data Access Object)** pattern:

### Package Structure
```
kiosk/
      BakeryPastriesKiosk.java       # Main kiosk launcher
      SimpleCustomerPortal.java      # Customer portal launcher
controller/                     # Business logic
      CartController.java
      CustomerController.java
      CustomOrderController.java
      InventoryController.java
      SalesController.java
database/                       # Data access layer
   dao/                        # Data access objects
      CustomerDAO.java
      CustomOrderDAO.java
      CustomProductDAO.java
      InventoryDAO.java
      SalesDAO.java
      DatabaseConfig.java
      DatabaseConnection.java
model/                          # Domain models
      Customer.java
      CustomOrder.java
      CustomProduct.java
      InventoryItem.java
      CartItem.java
      SaleTransaction.java
view/                           # User interfaces
   admin/                      # Admin panels
      AdminPanel.java
      CustomOrdersAdminPanel.java
      InventoryManagementPanel.java
   customer/                   # Customer portal UI
      CustomerAuthPanel.java
      CustomerSplashScreen.java
      SimpleOrderPanel.java
      KioskMainPage.java
      CartPage.java
      SplashScreen.java
      BakeryTheme.java
   util/                           # Utilities
      CartManager.java
      InventoryManager.java
      SVGIconUtil.java
```

### Design Patterns Used
- **MVC Architecture** - Separation of concerns
- **DAO Pattern** - Data access abstraction
- **Singleton Pattern** - Database connections, controllers
- **Observer Pattern** - Cart updates, UI refresh
- **Factory Pattern** - Component creation

## Documentation

### Quick Start Guides
- **[QUICKSTART.md](QUICKSTART.md)** - Get started in 5 minutes
- **[docs/QUICKSTART_DATABASE.md](docs/QUICKSTART_DATABASE.md)** - Database setup
- **[LAUNCHERS_README.md](LAUNCHERS_README.md)** - Application launchers guide

### System Documentation
- **[CUSTOM_ORDERS_SYSTEM_README.md](CUSTOM_ORDERS_SYSTEM_README.md)** - Custom orders overview
- **[INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)** - Integration instructions
- **[database/README.md](database/README.md)** - Database schema details

### Developer Guides
- **[docs/MAVEN_GUIDE.md](docs/MAVEN_GUIDE.md)** - Maven build system
- **[docs/ADMIN_GUIDE.md](docs/ADMIN_GUIDE.md)** - Admin panel guide

## Getting Started

### Prerequisites
1. **Java Development Kit (JDK) 11+**
   - Download: https://adoptium.net/
   - Verify: `java -version`

2. **MySQL Server 5.7+**
   - Download: https://dev.mysql.com/downloads/mysql/
   - Verify: `mysql --version`

3. **Maven** (included via wrapper)

### Installation Steps

**1. Clone the repository**
```powershell
git clone https://github.com/Vaelarr/SweetBatterBakeshop.git
cd SweetBatterBakeshop
```

**2. Setup database**
```powershell
# Run setup script
.\setup_database.bat

# Install custom orders
cd database
.\install_custom_orders.bat
cd ..
```

**3. Configure database connection**
```powershell
# Copy template
Copy-Item config/database.properties.template config/database.properties

# Edit config/database.properties with your MySQL credentials
```

**4. Build the project**
```powershell
.\mvnw.cmd clean package
```

**5. Run applications**
```powershell
# Main kiosk
.\run_bakery_kiosk.bat

# Customer portal
.\run_customer_portal.bat
```

## Development

### Build Commands
```powershell
# Clean build
.\mvnw.cmd clean compile

# Package JAR
.\mvnw.cmd package

# Run main kiosk
.\mvnw.cmd exec:java

# Run customer portal
.\mvnw.cmd exec:java -Dexec.mainClass="main.java.kiosk.SimpleCustomerPortal"
```

### Project Structure
```
SweetBatterBakeshop/
src/main/java/kiosk/          # Source code
database/                      # SQL schemas and scripts
config/                        # Configuration files
docs/                          # Documentation
lib/images/                    # Image assets
run_bakery_kiosk.bat          # Kiosk launcher
run_customer_portal.bat       # Portal launcher
setup_database.bat            # DB setup script
pom.xml                       # Maven configuration
```

### Technologies Used
- **Java 11** - Core language
- **Swing** - GUI framework
- **FlatLaf** - Modern look and feel
- **MySQL** - Database
- **Maven** - Build tool
- **JDBC** - Database connectivity

## ðŸ‘¨â€ï¿½ Author

**Vaelarr**
- GitHub: [@Vaelarr](https://github.com/Vaelarr)
- Project: [SweetBatterBakeshop](https://github.com/Vaelarr/SweetBatterBakeshop)

---

## ðŸ“ License

This project is available for educational purposes.

---

**Version 4.0.0** - November 1, 2025
- Integrated bakery kiosk and customer portal
- Complete custom orders system
- Dual-application architecture
- MySQL database with DAO pattern
- Modern FlatLaf UI theme
```

### Database Persistence
Professional MySQL database storage:
```java
// Data automatically saved to MySQL on each operation
