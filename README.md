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

## ðŸŽ¯ System Overview

This repository contains **two separate applications** that share a common database:

### 1ï¸âƒ£ **Bakery Kiosk** (Main POS System)
- Point-of-sale terminal for in-store purchases
- Product catalog browsing and shopping cart
- Admin panel with inventory management
- Sales reporting and analytics
- Custom orders management (admin view)

### 2ï¸âƒ£ **Customer Portal** (Custom Orders)
- Customer account registration and login
- Custom order creation with add-ons
- Pickup/delivery scheduling
- Order history tracking
- Beautiful splash screen

---

## ðŸš€ Quick Start

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

ðŸ“– **Detailed Setup:** See [QUICKSTART.md](QUICKSTART.md) for step-by-step instructions.


---

## ï¿½ Applications

### 1. Bakery Kiosk (Main POS)

**Launch:** `.\run_bakery_kiosk.bat`

**Features:**
- ðŸ›ï¸ **Product Catalog** - Browse 50+ bakery items across 4 categories
- ðŸ›’ **Shopping Cart** - Real-time cart management
- ðŸ’³ **Checkout System** - Complete transaction processing
- ðŸ‘¨â€ðŸ’¼ **Admin Panel** - Full inventory and sales management
- ðŸ“Š **Sales Reports** - Daily summaries and analytics
- ðŸŽ‚ **Custom Orders Tab** - View and manage custom orders from customers

**Admin Access:**
- Click "Admin" button on main kiosk
- Login: `admin` / `admin123`

**Main Class:** `main.java.kiosk.BakeryPastriesKiosk`

---

### 2. Customer Portal (Custom Orders)

**Launch:** `.\run_customer_portal.bat`

**Features:**
- âœ¨ **Animated Splash Screen** - Professional loading experience
- ðŸ‘¤ **Account Management** - Customer registration and login
- ðŸŽ‚ **Custom Order Builder** - Create custom cakes and bulk orders
- ðŸŽ¨ **Add-On Selection** - Flavors, fillings, decorations, messages
- ðŸ“… **Pickup/Delivery** - Schedule fulfillment with date/time
- ðŸ“‹ **Order History** - Track all your custom orders
- ðŸ‘¤ **Profile Management** - Update contact information

**Test Account:**
- Email: `john.doe@email.com`
- Password: `password123`

**Main Class:** `main.java.kiosk.SimpleCustomerPortal`

---

## ðŸŽ¯ Features

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

## ðŸ—„ï¸ Database Integration

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

ðŸ“– **Complete Guide:** [docs/QUICKSTART_DATABASE.md](docs/QUICKSTART_DATABASE.md)

## ðŸ—ï¸ Architecture

## ðŸ—ï¸ Architecture

This project follows **MVC (Model-View-Controller)** with **DAO (Data Access Object)** pattern:

### Package Structure
```
kiosk/
â”œâ”€â”€ BakeryPastriesKiosk.java       # Main kiosk launcher
â”œâ”€â”€ SimpleCustomerPortal.java      # Customer portal launcher
â”œâ”€â”€ controller/                     # Business logic
â”‚   â”œâ”€â”€ CartController.java
â”‚   â”œâ”€â”€ CustomerController.java
â”‚   â”œâ”€â”€ CustomOrderController.java
â”‚   â”œâ”€â”€ InventoryController.java
â”‚   â””â”€â”€ SalesController.java
â”œâ”€â”€ database/                       # Data access layer
â”‚   â”œâ”€â”€ dao/                        # Data access objects
â”‚   â”‚   â”œâ”€â”€ CustomerDAO.java
â”‚   â”‚   â”œâ”€â”€ CustomOrderDAO.java
â”‚   â”‚   â”œâ”€â”€ CustomProductDAO.java
â”‚   â”‚   â”œâ”€â”€ InventoryDAO.java
â”‚   â”‚   â””â”€â”€ SalesDAO.java
â”‚   â”œâ”€â”€ DatabaseConfig.java
â”‚   â””â”€â”€ DatabaseConnection.java
â”œâ”€â”€ model/                          # Domain models
â”‚   â”œâ”€â”€ Customer.java
â”‚   â”œâ”€â”€ CustomOrder.java
â”‚   â”œâ”€â”€ CustomProduct.java
â”‚   â”œâ”€â”€ InventoryItem.java
â”‚   â”œâ”€â”€ CartItem.java
â”‚   â””â”€â”€ SaleTransaction.java
â”œâ”€â”€ view/                           # User interfaces
â”‚   â”œâ”€â”€ admin/                      # Admin panels
â”‚   â”‚   â”œâ”€â”€ AdminPanel.java
â”‚   â”‚   â”œâ”€â”€ CustomOrdersAdminPanel.java
â”‚   â”‚   â””â”€â”€ InventoryManagementPanel.java
â”‚   â”œâ”€â”€ customer/                   # Customer portal UI
â”‚   â”‚   â”œâ”€â”€ CustomerAuthPanel.java
â”‚   â”‚   â”œâ”€â”€ CustomerSplashScreen.java
â”‚   â”‚   â””â”€â”€ SimpleOrderPanel.java
â”‚   â”œâ”€â”€ KioskMainPage.java
â”‚   â”œâ”€â”€ CartPage.java
â”‚   â”œâ”€â”€ SplashScreen.java
â”‚   â””â”€â”€ BakeryTheme.java
â””â”€â”€ util/                           # Utilities
    â”œâ”€â”€ CartManager.java
    â”œâ”€â”€ InventoryManager.java
    â””â”€â”€ SVGIconUtil.java
```

### Design Patterns Used
- âœ… **MVC Architecture** - Separation of concerns
- âœ… **DAO Pattern** - Data access abstraction
- âœ… **Singleton Pattern** - Database connections, controllers
- âœ… **Observer Pattern** - Cart updates, UI refresh
- âœ… **Factory Pattern** - Component creation

## ðŸ“š Documentation

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

## ðŸš€ Getting Started

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

## ðŸ’» Development

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
â”œâ”€â”€ src/main/java/kiosk/          # Source code
â”œâ”€â”€ database/                      # SQL schemas and scripts
â”œâ”€â”€ config/                        # Configuration files
â”œâ”€â”€ docs/                          # Documentation
â”œâ”€â”€ lib/images/                    # Image assets
â”œâ”€â”€ run_bakery_kiosk.bat          # Kiosk launcher
â”œâ”€â”€ run_customer_portal.bat       # Portal launcher
â”œâ”€â”€ setup_database.bat            # DB setup script
â””â”€â”€ pom.xml                       # Maven configuration
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
- âœ… Integrated bakery kiosk and customer portal
- âœ… Complete custom orders system
- âœ… Dual-application architecture
- âœ… MySQL database with DAO pattern
- âœ… Modern FlatLaf UI theme
```

### Database Persistence
Professional MySQL database storage:
```java
// Data automatically saved to MySQL on each operation
