# Sweet Batter Bakeshop - Transaction System

A modern, full-featured bakery point-of-sale system built with Java Swing using **MVC (Model-View-Controller) architecture** with **Serialization** and **Generics** support. Features a comprehensive **Admin Dashboard** and **true fullscreen experience** for professional retail operations!

## 🌟 Key Highlights

- 🖥️ **Fullscreen Experience** - Immersive, distraction-free interface
- 🔐 **Dual Mode Operation** - Separate Customer and Admin applications
- 💾 **Data Persistence** - Serialization for cart and transaction storage
- 🎨 **Modern UI** - Custom gradient designs, rounded corners, hover effects
- 📊 **Complete Admin Dashboard** - Inventory, transactions, and analytics
- 🛒 **Shopping Cart** - Auto-save with full customization options

## 🏗️ Architecture

This application follows the **MVC design pattern** for clean separation of concerns:

- **Model:** Business logic and data (`model/` package) - All classes are **Serializable**
- **View:** User interface components (`view/` package)  
- **Controller:** Application logic and coordination (`controller/` package)

## 📂 Project Structure

```
src/
├── model/              # Data models (All Serializable)
│   ├── Product.java            # Enhanced with stock management
│   ├── CartItem.java
│   ├── ShoppingCart.java
│   ├── ProductCatalog.java     # Enhanced with admin methods
│   ├── Transaction.java         # NEW: Transaction records
│   ├── TransactionManager.java  # NEW: Transaction handling
│   ├── Admin.java              # NEW: Admin authentication
│   ├── AdminManager.java        # NEW: Admin management
│   ├── SerializationUtil.java  # Generic serialization utility
│   ├── Repository.java          # Generic repository interface
│   └── InMemoryRepository.java  # Generic in-memory implementation
├── view/               # UI components
│   ├── SplashScreenView.java
│   ├── MainView.java           # Enhanced with admin access
│   ├── CatalogueView.java
│   ├── AdminLoginView.java      # NEW: Admin login dialog
│   └── AdminDashboardView.java  # NEW: Admin dashboard
├── controller/         # Business logic
│   ├── ApplicationController.java
│   ├── CatalogueController.java
│   └── AdminController.java     # NEW: Admin business logic
├── App.java            # Customer application entry point
└── AdminApp.java       # NEW: Admin application entry point
data/                   # Auto-created for serialized data
└── shopping_cart.ser   # Saved cart (auto-managed)
```

## ✨ Features

### 👥 Two User Modes

#### Customer Mode
- Browse 25+ bakery products across 5 categories
- Product cards with visual previews
- Category filtering (Cakes, Cupcakes, Pastries, Cookies, Breads)
- Shopping cart with live total updates
- Order customization with toppings and special notes

#### Admin Mode (🆕 NEW!)
- 🔐 Secure login system (default: admin/admin123)
- 📊 Dashboard with revenue and transaction statistics
- 🎂 Product Management (Add, Edit, Delete, Update Stock)
- 💰 Transaction History and Details
- 📈 Multiple Report Types (Sales, Inventory, etc.)
- 📦 Real-time stock tracking

### 🎨 Modern UI Design

- **Fullscreen Experience:** True fullscreen windows with no decorations
- **Custom Controls:** Styled close buttons (✕) in headers
- **Gradient Backgrounds:** Smooth taupe (#A9907E) to brown (#8B7355) transitions
- **Rounded Corners:** Modern button and card designs (8-12px radius)
- **Hover Effects:** Interactive feedback on all buttons
- **Professional Typography:** Segoe UI family throughout
- **Immersive Interface:** No distractions, maximum focus

### 🖥️ Fullscreen Features (NEW!)

All views now support true fullscreen experience:
- **SplashScreenView:** Fullscreen splash on startup
- **MainView:** Fullscreen customer interface with custom close button
- **CatalogueView:** Fullscreen product browsing
- **AdminLoginView:** Fullscreen login with centered form card
- **AdminDashboardView:** Fullscreen admin panel with custom controls

Benefits:
- No window decorations (title bars, borders)
- Maximum screen space utilization
- Professional kiosk-like experience
- Exit confirmations for safety
- Custom-styled close buttons matching brand colors

### 🛒 Complete Shopping Experience
- Browse 25+ bakery products across 5 categories
- Product cards with visual previews
- Category filtering (Cakes, Cupcakes, Pastries, Cookies, Breads)
- Shopping cart with live total updates

### 🎯 Comprehensive Customization
- Quantity selection
- 6 topping options (Sprinkles, Chocolate Chips, Caramel Drizzle, etc.)
- Special request notes
- Full-screen customization dialog

### 💰 Philippine Localization
- Prices in Philippine Pesos (₱)
- Realistic bakery pricing (₱18 - ₱550)
- Filipino thank-you messages

### 🧾 Order Management
- View cart with detailed item descriptions
- Update quantities
- Remove items
- Checkout with receipt generation
- Order summary with all customizations

### 💾 Cart Persistence (NEW!)
- **Auto-Save:** Cart automatically saves after every modification
- **Auto-Load:** Cart restores on app restart
- **Serialization:** Uses Java serialization for data persistence
- **File-Based:** Stored in `data/shopping_cart.ser`
- **No Data Loss:** Cart survives app crashes and restarts

### 🔧 Advanced Features
- **Generics:** Type-safe collections (`List<CartItem>`, `List<Product>`)
- **Repository Pattern:** Generic `Repository<T, ID>` interface
- **SerializationUtil<T>:** Generic utility with bounded type parameters
- **Optional<T>:** Null-safe deserialization
- **Thread-Safe:** AtomicLong for ID generation
- **Predicate Search:** Generic `findByPredicate()` method

## 🚀 How to Run

### Quick Start

**Customer Application (Fullscreen):**
```bash
cd "C:\Users\atupa\Documents\Development\Java\OOP\FINAL PROJECT\SweetBatterBakeshop"
javac -d bin -sourcepath src src/App.java
java -cp bin App
```

**Admin Dashboard (Fullscreen):**
```bash
cd "C:\Users\atupa\Documents\Development\Java\OOP\FINAL PROJECT\SweetBatterBakeshop"
javac -d bin -sourcepath src src/AdminApp.java
java -cp bin AdminApp
```

### Customer Application

#### Using App.java (Recommended)
```bash
cd src
javac App.java
java App
```

#### Using VS Code
1. Open `src/App.java`
2. Click Run or press F5
3. Application launches with fullscreen splash screen

### Admin Dashboard

#### Method 1: Run Separately (Recommended)
```bash
cd src
javac AdminApp.java
java AdminApp
```
This launches directly into the fullscreen admin login screen.

#### Method 2: Access from Main Application
1. Run the main customer application
2. Click "🔐 Admin Access" button on the home menu
3. Enter admin credentials (default: admin/admin123)
4. Admin dashboard opens in fullscreen

### Compile All Files
```bash
# Compile all source files
javac -d bin -sourcepath src src/**/*.java src/*.java

# Run customer application (fullscreen)
java -cp bin App

# Run admin dashboard (fullscreen)
java -cp bin AdminApp
```

### Exit Fullscreen Applications

Both applications feature custom close buttons:
- **Customer App:** Click ✕ button in top-right header (exit confirmation)
- **Admin Dashboard:** Click ✕ button next to logout (exit confirmation)
- **Alternative:** Use Alt+F4 or Task Manager if needed

## 🔐 Admin Dashboard

For complete admin dashboard documentation, see **[ADMIN_GUIDE.md](ADMIN_GUIDE.md)**

**Default Login:**
- Username: `admin`
- Password: `admin123`

**Features:**
- Product management (Add, Edit, Delete, Stock Updates)
- Transaction history and details
- Sales reports and analytics
- Inventory tracking
- Dashboard statistics

## 📦 Requirements

- Java Development Kit (JDK) 8 or higher
- No external dependencies (uses Java Swing)

## 🖼️ Screenshots

The application features:
- **Splash Screen:** Fullscreen loading with logo support
- **Home Menu:** Clean navigation with gradient header and custom close button
- **Product Catalogue:** 3-column grid with modern cards in fullscreen
- **Add to Cart Dialog:** Fullscreen customization interface
- **Shopping Cart:** Fullscreen cart management with live updates
- **Checkout:** Fullscreen receipt display
- **Admin Login:** Fullscreen centered login form with gradient header
- **Admin Dashboard:** Fullscreen tabbed interface with analytics and management tools

All interfaces feature:
- No window decorations for immersive experience
- Custom styled close buttons (✕) where appropriate
- Smooth gradient backgrounds (taupe to brown)
- Professional typography and spacing
- Interactive hover effects

## 🏆 MVC Benefits

### Before Refactoring
- Monolithic code structure
- Mixed UI and business logic
- Difficult to maintain and test

### After MVC Implementation
✅ Clear separation of concerns  
✅ Easy to test components independently  
✅ Improved code organization  
✅ Scalable architecture  
✅ Better collaboration potential  
✅ Industry-standard design pattern

## 📚 Documentation

Complete documentation available:
- **[FULLSCREEN_UPDATE.md](FULLSCREEN_UPDATE.md)** - 🆕 Fullscreen features and implementation details
- **[ADMIN_GUIDE.md](ADMIN_GUIDE.md)** - Complete admin dashboard guide with screenshots
- **[ADMIN_QUICKSTART.md](ADMIN_QUICKSTART.md)** - Quick start guide for admin users
- **[ADMIN_QUICK_REFERENCE.md](ADMIN_QUICK_REFERENCE.md)** - Command reference card
- **[MODERN_UI_UPDATE.md](MODERN_UI_UPDATE.md)** - Modern UI features and updates
- **[MVC_ARCHITECTURE.md](MVC_ARCHITECTURE.md)** - Detailed MVC architecture guide
- **[REFACTORING_SUMMARY.md](REFACTORING_SUMMARY.md)** - Complete refactoring documentation
- **[QUICKSTART.md](QUICKSTART.md)** - Quick start guide for developers
- **[CHECKOUT_IMPROVEMENTS.md](CHECKOUT_IMPROVEMENTS.md)** - Receipt formatting details
- **[SERIALIZATION_AND_GENERICS.md](SERIALIZATION_AND_GENERICS.md)** - Serialization & generics implementation
- **[DATABASE_SETUP.md](DATABASE_SETUP.md)** - 🆕 Database setup guide for review system

## 🌟 New Feature: Restaurant Review System

**Version 4.1** introduces a fully functional review system with MySQL database integration!

### Features
- ✅ **Database Integration** - MySQL via XAMPP with automatic setup
- ✅ **CRUD Operations** - Create, Read, Update, Delete reviews
- ✅ **Data Persistence** - Reviews stored permanently in database
- ✅ **Input Validation** - Comprehensive validation for all fields
- ✅ **Error Handling** - Graceful handling of database errors
- ✅ **Repository Pattern** - Consistent with existing architecture
- ✅ **Generic Types** - Type-safe operations using generics

### Quick Start

#### Prerequisites
1. XAMPP with MySQL installed and running
2. MySQL JDBC Driver (see `lib/README.md`)

#### Run the Review App
```bash
# Compile
javac -cp "lib/mysql-connector-j-8.0.33.jar" -d bin -sourcepath src src/RestaurantReviewApp.java

# Run (Linux/Mac)
java -cp "bin:lib/mysql-connector-j-8.0.33.jar" RestaurantReviewApp

# Run (Windows)
java -cp "bin;lib/mysql-connector-j-8.0.33.jar" RestaurantReviewApp
```

#### Test In-Memory Mode
```bash
javac -d bin -sourcepath src src/ReviewModelTest.java
java -cp bin ReviewModelTest
```

### Database Schema
```sql
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant VARCHAR(255) NOT NULL,
    reviewer VARCHAR(255) NOT NULL,
    rating INT NOT NULL,
    review TEXT NOT NULL
);
```

### Architecture
- **Review** - Entity model with validation
- **DatabaseConnector** - MySQL connection management
- **ReviewDatabaseRepository** - Database CRUD implementation
- **ReviewManager** - Business logic layer
- **RestaurantReviewApp** - Command-line interface

For complete setup instructions, see **[DATABASE_SETUP.md](DATABASE_SETUP.md)**

## 🔮 Future Enhancements

Potential features to add:

**Customer Features:**
- ✅ ~~Database integration (MySQL/PostgreSQL)~~ (COMPLETED - Review System)
- User authentication and profiles
- Payment gateway integration (GCash, PayMaya)
- Order history and tracking
- Product search functionality
- Discount codes and promotions

**Admin Features:**
- ✅ ~~Product management~~ (COMPLETED)
- ✅ ~~Transaction tracking~~ (COMPLETED)
- ✅ ~~Inventory management~~ (COMPLETED)
- ✅ ~~Sales reports~~ (COMPLETED)
- Email notifications for low stock
- Multi-admin user management
- Export reports to PDF/Excel
- Customer analytics dashboard

**Review System Enhancements:**
- Photo uploads with reviews
- Reply to reviews (admin feature)
- Review verification (purchase required)
- Rating breakdowns (taste, service, value)
- Email notifications for new reviews

## Folder Structure

The workspace contains the following folders:

- `src/model/` - Data models and business logic
- `src/view/` - User interface components
- `src/controller/` - Application controllers
- `bin/` - Compiled output files
- `lib/` - External dependencies (MySQL JDBC driver)
- `images/` - Application assets (logo, icons)

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

**External Dependencies:**
- MySQL Connector/J 8.0.33 (for database connectivity)

## 📄 License

Educational use only. Created for OOP Final Project 2025.

---

**Version 4.1 (Database Integration)** - MySQL database support for review system with CRUD operations  
**Version 4.0 (Fullscreen Experience)** - All views now fullscreen with custom controls  
**Version 3.0 (Serialization & Generics)** - Added data persistence and type-safe generics  
**Version 2.0 (MVC)** - Refactored with Model-View-Controller architecture  
**Version 1.0** - Initial implementation

## 🛠️ Technical Stack

- **Language:** Java (JDK 8+)
- **GUI Framework:** Java Swing
- **Architecture:** MVC (Model-View-Controller)
- **Data Persistence:** Java Serialization + MySQL Database
- **Database:** MySQL (via XAMPP)
- **Design Patterns:** Repository Pattern, Generics
- **UI Features:** Custom painting, gradients, rounded corners
- **Window Mode:** Fullscreen (undecorated) with custom controls

---

*Developed using Java Swing with custom UI components, modern design principles, enterprise-grade architecture, and immersive fullscreen experience for professional retail operations.*

