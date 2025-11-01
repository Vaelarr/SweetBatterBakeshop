# SweetBatter Bakeshop - Kiosk System

A professional bakery kiosk application built with Java Swing, implementing **MVC Architecture**, **MySQL Database Integration with DAO Pattern**, **Maven Build System**, and **Modern FlatLaf UI** for robust data management and an elegant user experience.

**Current Version:** 3.0.0 | **Last Updated:** November 1, 2025

---

## 📑 Table of Contents

- [Quick Start](#-quick-start-with-maven)
- [Features](#-features)
- [Database Integration](#️-database-integration)
- [Architecture](#️-architecture)
- [Technical Features](#-technical-features)
- [Documentation](#-documentation)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [Usage Examples](#-usage-examples)
- [Development](#️-development)
- [Contributing](#-contributing)
- [Author](#-author)
- [Changelog & Version History](#-changelog--version-history)
- [Future Updates & Roadmap](#-future-updates--roadmap)
- [Update Guidelines](#-update-guidelines)
- [Project Statistics](#-project-statistics)

---

## 🚀 Quick Start with Maven

```powershell
# Build the project
.\mvnw.cmd clean package

# Run the application
.\mvnw.cmd exec:java

# Or run the fat JAR
java -jar target/convenient-store-kiosk-1.0.0-jar-with-dependencies.jar
```

📖 **Full Maven Guide:** See [docs/MAVEN_GUIDE.md](docs/MAVEN_GUIDE.md) for detailed instructions.

## 🎯 Features

- **Modern Touch-Friendly Kiosk UI** - Full-screen interface with elegant gradient designs
- **Product Categories** - Breads & Rolls, Pastries & Desserts, Cakes & Special Occasions, Beverages & Extras
- **Shopping Cart** - Real-time cart management with live updates
- **Advanced Inventory Management** - Track stock, expiration dates, barcodes, suppliers
- **Sales Analytics** - Comprehensive transaction history, sales reports, daily summaries
- **MySQL Database with DAO Pattern** - Professional database architecture
- **Admin Panel** - Full inventory control, expiration tracking, help request management
- **Modern UI Theme** - Custom FlatLaf theme with bakery-inspired colors and gradients
- **50+ Sample Products** - Pre-populated database with realistic bakery inventory

## 🗄️ Database Integration

The system uses **MySQL database storage** with a professional DAO (Data Access Object) pattern:

- **Persistent Storage** - All data stored in MySQL database tables
- **Transaction Support** - ACID-compliant sales recording
- **Advanced Queries** - SQL-based filtering, reporting, and analytics
- **DAO Pattern** - Clean separation of data access logic
- **Data Integrity** - Relational database with proper constraints
- **Auto-initialization** - Database tables created automatically on first run

### Quick Database Setup

1. **Install MySQL** (5.7 or higher)
2. **Run setup script:**
   ```bash
   # Windows
   .\setup_database.bat
   
   # Linux/Mac
   ./setup_database.sh
   ```
3. **Configure credentials** in `config/database.properties`
4. **Run the application** - Tables are created automatically!

📖 **Detailed Guide:** See [docs/QUICKSTART_DATABASE.md](docs/QUICKSTART_DATABASE.md) for complete setup instructions.

### Database Features

- **3 Main Tables:**
  - `inventory` - Product catalog with categories, pricing, stock, expiration dates, barcodes, suppliers
  - `sales_transactions` - Completed sales with timestamps and totals
  - `sales_items` - Individual items per transaction with quantities
  
- **DAO Pattern:**
  - `InventoryDAO` - CRUD operations for inventory management
  - `SalesDAO` - Sales transaction and analytics management
  
- **Auto-initialization:**
  - Tables created automatically on first run
  - **50+ sample bakery products** populated if database is empty
  - Four categories: Breads & Rolls, Pastries & Desserts, Cakes & Special Occasions, Beverages & Extras

## 🏗️ Architecture

This project follows **MVC (Model-View-Controller)** architecture with a **DAO (Data Access Object)** pattern:

### Application Entry Point
- **`kiosk.BakeryPastriesKiosk`** - Main application launcher with splash screen and initialization

### Models (`kiosk.model.*`)
  - `InventoryItem` - Product with stock, expiration, barcode, supplier
  - `CartItem` - Shopping cart line items
  - `SaleTransaction` - Completed sales with items

### Controllers (`kiosk.controller.*`)
  - `InventoryController` - Singleton controller for inventory operations (MySQL-backed)
  - `CartController` - Cart management with real-time updates
  - `SalesController` - Sales tracking, analytics, and reporting (MySQL-backed)

### Database Layer (`kiosk.database.*`)
  - `DatabaseConnection` - Singleton connection manager
  - `DatabaseConfig` - Configuration from properties file
  - `InventoryDAO` - Complete CRUD for inventory items
  - `SalesDAO` - Transaction recording and queries
  - `DatabaseInitializer` - Database setup utilities

### Views (`kiosk.view.*`)
  - `KioskMainPage` - Main navigation with category cards
  - `SplashScreen` - Professional loading screen
  - `CartPage` - Shopping cart interface
  - `AdminPanel` - Inventory management and reports
  - `AdminLoginPanel` - Admin authentication
  - Product pages: `CakesPage`, `PastriesPage`, `BreadsPage`, `BeveragesPage`
  - `BakeryTheme` & `ModernBakeryTheme` - Custom FlatLaf styling

### Utilities (`kiosk.util.*`)
  - `CartManager` - Static cart operations helper
  - `InventoryManager` - Inventory helper methods
  - `SalesReport` - Report generation utilities
  - `HelpRequestManager` - Customer assistance requests
  - `SVGIconUtil` - Icon loading utilities

### Why MVC + DAO?

- ✅ **Separation of Concerns** - Business logic separated from UI and data access
- ✅ **Testability** - Controllers and DAOs can be unit tested independently
- ✅ **Maintainability** - Changes to database don't affect business logic or UI
- ✅ **Reusability** - DAOs can be reused across different controllers
- ✅ **Professional Pattern** - Industry-standard architecture

### Why MySQL?

- ✅ **Professional Storage** - Industry-standard relational database
- ✅ **Scalability** - Handles large product catalogs and transaction history
- ✅ **Advanced Queries** - Complex filtering, aggregations, and analytics
- ✅ **Data Integrity** - ACID transactions and foreign key constraints
- ✅ **Concurrent Access** - Multiple kiosk terminals can share same database
- ✅ **Production Ready** - Suitable for real-world deployment

## 🔧 Technical Features

### MySQL Database Integration
Professional database persistence with DAO pattern:
```java
// DAO pattern example
InventoryDAO inventoryDAO = new InventoryDAO();
List<InventoryItem> items = inventoryDAO.getAll();
inventoryDAO.update(item);
List<CartItem> items = cartController.getAllItems(); // No casting!
```

### Database Persistence
Professional MySQL database storage:
```java
// Data automatically saved to MySQL on each operation
inventoryController.addItem(newItem);  // Immediately saved to database
salesController.recordTransaction(tx); // Transaction committed to database

// No manual save/load needed - database is always up to date!
```

### Legacy File Serialization (Still Supported)
The original file-based serialization is still available:
```java
// Manual file-based persistence (legacy)
Repository<T> repo = new Repository<>("data/items.dat");
repo.saveToFile();   // Serialize to file
repo.loadFromFile(); // Deserialize from file
```

### Design Patterns
- **Singleton** - Controllers and database connection ensure single instance
- **DAO (Data Access Object)** - Database operations abstracted
- **Repository** - Generic data access layer (legacy file-based)
- **Adapter** - Backward compatibility with legacy code
- **MVC** - Overall architectural pattern

## 📚 Documentation

### Quick Links
- **[QUICKSTART.md](QUICKSTART.md)** - ⭐ Quick setup guide (start here!)
- **[docs/](docs/)** - 📁 All specialized documentation
- **[docs/ADMIN_GUIDE.md](docs/ADMIN_GUIDE.md)** - Admin panel guide
- **[docs/MAVEN_GUIDE.md](docs/MAVEN_GUIDE.md)** - Complete Maven setup and usage guide
- **[docs/MAVEN_QUICKREF.md](docs/MAVEN_QUICKREF.md)** - Maven quick reference
- **[docs/QUICKSTART_DATABASE.md](docs/QUICKSTART_DATABASE.md)** - Quick database setup guide
- **[database/](database/)** - 📁 Database scripts and documentation
- **[database/setup.sql](database/setup.sql)** - MySQL database schema

## 🚀 Getting Started

### Prerequisites
- **Java JDK 11** or higher
- **Apache Maven 3.6+** (or use included Maven Wrapper)
- **MySQL Server 5.7+** (for database features)
- IDE (Eclipse, IntelliJ IDEA, or VS Code) - optional

### Option 1: Quick Start with Maven (Recommended)

1. **Clone the repository**
```powershell
git clone https://github.com/Vaelarr/SweetBatterBakeshop.git
cd SweetBatterBakeshop
```

2. **Set up MySQL database**
```powershell
# Windows
.\setup_database.bat

# Linux/Mac
./setup_database.sh

# Configure credentials
Copy-Item config/database.properties.template config/database.properties
# Edit config/database.properties with your MySQL credentials
```

3. **Build with Maven**
```powershell
# Using Maven Wrapper (recommended - no Maven installation needed)
.\mvnw.cmd clean package

# Or using installed Maven
mvn clean package
```

4. **Run the application**
```powershell
# Run with Maven
.\mvnw.cmd exec:java

# Or run the JAR directly
java -jar target/convenient-store-kiosk-1.0.0-jar-with-dependencies.jar
```

**Note:** Maven automatically downloads all dependencies (FlatLaf, MySQL Connector, SVG Salamander) - no manual JAR setup needed!

### Option 2: Manual Build (Without Maven)

If you prefer not to use Maven:

1. Clone the repository
```powershell
git clone https://github.com/Vaelarr/SweetBatterBakeshop.git
cd SweetBatterBakeshop
```

2. Download dependencies manually
   - FlatLaf 3.6.2
   - FlatLaf Extras 3.6.2
   - MySQL Connector/J 9.5.0
   - SVG Salamander 1.1.4
   
   Place JARs in `lib/` folder

3. Set up MySQL database
```powershell
# Windows
.\setup_database.bat

# Configure credentials
Copy-Item config/database.properties.template config/database.properties
```

4. Compile and run
```powershell
# Add JARs to classpath and compile
javac -cp "lib/*;." -d bin src/main/java/kiosk/**/*.java
java -cp "bin;lib/*" main.java.kiosk.BakeryPastriesKiosk
```

**Note:** Maven is strongly recommended as it handles all dependencies automatically.

## 📦 Project Structure

```
SweetBatterBakeshop/
├── pom.xml                             # Maven configuration
├── mvnw.cmd / mvnw                     # Maven Wrapper scripts
├── .mvn/                               # Maven wrapper config
├── setup_database.bat/sh               # Database setup scripts
├── QUICKSTART.md                       # ⭐ Quick setup guide
├── README.md                           # This file (main documentation)
├── src/
│   └── main/java/kiosk/
│       ├── BakeryPastriesKiosk.java   # Main application entry point
│       ├── model/                      # Data models
│       │   ├── InventoryItem.java
│       │   ├── CartItem.java
│       │   └── SaleTransaction.java
│       ├── controller/                 # Business logic
│       │   ├── CartController.java
│       │   ├── InventoryController.java
│       │   └── SalesController.java
│       ├── view/                       # UI components
│       │   ├── KioskMainPage.java
│       │   ├── SplashScreen.java
│       │   ├── AdminPanel.java
│       │   └── pages/                  # Product category pages
│       ├── database/                   # Database layer
│       │   ├── dao/                    # Data Access Objects
│       │   │   ├── InventoryDAO.java
│       │   │   └── SalesDAO.java
│       │   ├── DatabaseConnection.java
│       │   ├── DatabaseConfig.java
│       │   └── DatabaseInitializer.java
│       └── util/                       # Utilities
├── config/
│   ├── database.properties.template    # DB config template
│   └── database.properties             # DB configuration (gitignored)
├── database/
│   ├── setup.sql                       # MySQL setup script
│   ├── test_data.sql                   # Sample bakery data
│   └── README.md                       # Database documentation
├── docs/                               # Documentation folder
│   ├── README.md                       # Documentation index
│   ├── ADMIN_GUIDE.md                  # Admin panel guide
│   ├── MAVEN_GUIDE.md                  # Maven documentation
│   ├── MAVEN_QUICKREF.md               # Maven quick reference
│   └── QUICKSTART_DATABASE.md          # Quick DB setup guide
├── archive/                            # Legacy code (for reference)
│   └── old-app/                        # Previous serialization-based version
├── target/                             # Maven build output (gitignored)
│   ├── classes/                        # Compiled classes
│   ├── lib/                            # Dependencies
│   └── *.jar                           # Built artifacts
└── lib/                                # Legacy JARs (now managed by Maven)
```

**Note:** Maven manages all dependencies automatically - no need to manually download JARs!

## 💻 Usage Examples

### Add Items to Cart
```java
CartController cart = CartController.getInstance();
cart.addItem("Piattos", 35.1);
cart.addItem("Coca Cola", 30.0);
double total = cart.getTotal();
```

### Manage Inventory
```java
InventoryController inventory = InventoryController.getInstance();
List<InventoryItem> lowStock = inventory.getLowStockItems(10);
List<InventoryItem> expiring = inventory.getExpiringItems(7);
```

### Sales Analytics
```java
SalesController sales = SalesController.getInstance();
double todaySales = sales.getTodayTotalSales();
List<Map.Entry<String, Integer>> topItems = sales.getTopSellingItems(5, startDate, endDate);
```

## 📁 Project Structure

```
Convenient-Store-Kiosk/
├── JavaKiosk/
│   └── ConvenientStoreKiosk/
│       ├── src/kiosk/
│       │   ├── model/              # Data models (Serializable)
│       │   ├── controller/         # Business logic
│       │   ├── view/               # UI components (future)
│       │   ├── util/               # Utilities & generics
│       │   ├── ConvenienceStoreKiosk.java  # Main class
│       │   └── (Legacy adapter classes)
│       └── bin/
├── data/                           # Serialized data files
│   ├── inventory.dat
│   ├── cart.dat
│   └── sales.dat
├── MVC_ARCHITECTURE.md            # Architecture documentation
├── MVC_EXAMPLES.md                # Code examples
└── README.md
```

## 🔄 Migration from Old Architecture

The application maintains backward compatibility. Existing UI code works unchanged:

```java
// Old way (still works)
CartManager.addItem("Product", 99.99);

// New way (recommended)
CartController.getInstance().addItem("Product", 99.99);
```

Both approaches work, but the controller-based approach is recommended for new code.

## 🛠️ Development

### Adding New Features

1. Create model class in `kiosk.model` (implement `Serializable`)
2. Create/update controller in `kiosk.controller`
3. Use `Repository<T>` for data storage
4. Update UI to use controller methods

### Data Persistence

Data is automatically saved on application exit and loaded on startup. Manual save/load:

```java
// Save
InventoryController.getInstance().save();
CartController.getInstance().save();
SalesController.getInstance().save();

// Load
InventoryController.getInstance().load();
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Follow MVC architecture
4. Add tests for controllers
5. Submit a pull request

## 📝 License

This project is for educational purposes.

## 👤 Author

**Vaelarr**
- GitHub: [@Vaelarr](https://github.com/Vaelarr)
- Repository: [SweetBatterBakeshop](https://github.com/Vaelarr/SweetBatterBakeshop)

---

## 📋 CHANGELOG & VERSION HISTORY

### Version 2.1.0 (November 1, 2025) - Maven Integration ✨ **LATEST**

#### � Major Updates
- **Maven Build System Implementation**
  - Added `pom.xml` with complete project configuration
  - Integrated Maven Wrapper (`mvnw.cmd`, `mvnw`) - no Maven installation required
  - Automated dependency management for all external libraries
  - Configured build plugins for JAR packaging and execution

#### 📦 Dependencies Now Auto-Managed
- FlatLaf 3.5.2 (Modern Look and Feel)
- FlatLaf Extras 3.6.2 (Additional UI components)
- MySQL Connector/J 9.5.0 (Database driver)
- SVG Salamander 1.1.4 (SVG icon support)
- JUnit 4.13.2 (Testing framework)

#### 📚 New Documentation
- `MAVEN_GUIDE.md` - Comprehensive Maven usage guide with examples
- `MAVEN_QUICKREF.md` - Quick reference for common Maven commands
- `MAVEN_IMPLEMENTATION.md` - Detailed implementation summary
- Updated `README.md` - Maven quick start and integration docs

#### 🔧 Build Configuration
- Java 11 source/target compatibility
- Standard JAR and Fat JAR (with dependencies) generation
- Automatic dependency copying to `target/lib/`
- Resource filtering and inclusion
- Exec plugin for running application via Maven

#### 🚀 New Commands Available
```powershell
# Build project
.\mvnw.cmd clean package

# Run application
.\mvnw.cmd exec:java

# Run tests
.\mvnw.cmd test

# Show dependency tree
.\mvnw.cmd dependency:tree
```

#### ✅ Benefits
- ✅ No manual JAR downloads required
- ✅ Reproducible builds across environments
- ✅ Easy dependency version management
- ✅ IDE-friendly setup (VS Code, IntelliJ, Eclipse)
- ✅ Professional build workflow
- ✅ Simplified project onboarding

#### 🗂️ Project Structure Updates
- Added `pom.xml` at project root
- Added `.mvn/wrapper/` directory
- Added Maven wrapper scripts
- Updated `.gitignore` for Maven artifacts
- Build outputs now in `target/` directory

---

### Version 2.0.0 (November 2025) - MVC Architecture & Database Integration

#### 🏗️ Architecture Redesign
- **MVC Pattern Implementation**
  - Separated concerns: Model, View, Controller
  - `kiosk.model.*` - Data models (InventoryItem, CartItem, SaleTransaction)
  - `kiosk.controller.*` - Business logic controllers
  - `kiosk.view.*` - UI components
  
#### 🗄️ MySQL Database Integration
- **Database Layer** (`kiosk.database.*`)
  - `DatabaseConnection` - Singleton connection manager
  - `DatabaseConfig` - Configuration management
  - `DatabaseInitializer` - Automatic setup and initialization
  - DAO Pattern implementation (InventoryDAO, SalesDAO)
  
- **Database Features**
  - 3 main tables: inventory, sales_transactions, sales_items
  - ACID-compliant transactions
  - Foreign key constraints and indexes
  - Auto-initialization with sample data
  
#### 🎨 Modern UI
- **FlatLaf Integration**
  - Modern, professional look and feel
  - Bakery-themed color scheme
  - Touch-friendly interface
  - SVG icon support

#### 🔐 Data Persistence
- Dual persistence strategy:
  - MySQL database (primary, recommended)
  - File serialization (legacy, fallback)
- Generic Repository pattern
- Automatic save/load operations

#### 📊 Enhanced Features
- **Inventory Management**
  - Track stock levels and expiration dates
  - Low stock alerts
  - Category-based organization
  
- **Sales Analytics**
  - Transaction history
  - Top-selling items reports
  - Daily/period sales summaries
  
- **Admin Panel**
  - Inventory management
  - Sales reports viewing
  - System configuration

#### 🎯 Design Patterns Implemented
- Singleton (Controllers, Database Connection)
- DAO (Data Access Object)
- Repository (Generic data access)
- Adapter (Legacy compatibility)
- MVC (Overall architecture)

#### 📚 Documentation Added
- `QUICKSTART_DATABASE.md` - Quick database setup
- `DATABASE_SETUP.md` - Comprehensive database docs
- `MVC_ARCHITECTURE.md` - Architecture overview
- `MVC_EXAMPLES.md` - Code examples
- `database/setup.sql` - Database schema

---

### Version 1.0.0 - Initial Release

#### 🎯 Core Features
- Basic kiosk functionality
- Product catalog
- Shopping cart
- Simple UI with Java Swing
- File-based data storage

#### 📦 Product Categories
- Foods & Beverages
- Personal Care
- Household Items
- Tobacco & Alcohol

#### 💾 Data Management
- File serialization for data persistence
- Basic inventory tracking
- Simple sales recording

---

## 🔮 FUTURE UPDATES & ROADMAP

### Planned Features
- [ ] Unit testing suite with JUnit
- [ ] Integration tests for database operations
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Docker containerization
- [ ] REST API for remote management
- [ ] Multi-language support (i18n)
- [ ] Receipt printing functionality
- [ ] Barcode scanner integration
- [ ] Cloud database support (Azure, AWS)
- [ ] Mobile admin app
- [ ] Real-time inventory sync
- [ ] Advanced analytics dashboard

### Under Consideration
- [ ] Payment gateway integration
- [ ] Customer loyalty program
- [ ] Email receipt delivery
- [ ] Inventory forecasting with ML
- [ ] Voice-activated interface
- [ ] Touchscreen optimization
- [ ] Multi-terminal support
- [ ] Backup and restore utilities

---

## 📝 UPDATE GUIDELINES

### When Making Updates, Record:

1. **Version Number** - Follow semantic versioning (MAJOR.MINOR.PATCH)
2. **Date** - Update date in format (Month Day, Year)
3. **Category** - Feature, Bug Fix, Enhancement, Documentation, etc.
4. **Description** - Clear explanation of changes
5. **Files Changed** - List key files added/modified
6. **Commands** - New commands or usage patterns
7. **Breaking Changes** - Note any compatibility issues

### How to Add Updates to This README:

```markdown
### Version X.Y.Z (Date) - Title

#### Category (🎉 Major/🔧 Minor/🐛 Bug Fix)
- **Feature Name**
  - Description of changes
  - Impact and benefits
  - Example usage if applicable

#### Files Changed
- `path/to/file.java` - Description
- `path/to/config.xml` - Description

#### Migration Notes (if applicable)
- Steps to upgrade from previous version
- Breaking changes and how to handle them
```

---

## 🏷️ Version Naming Convention

- **Major (X.0.0)** - Breaking changes, major features, architecture changes
- **Minor (x.Y.0)** - New features, non-breaking changes
- **Patch (x.y.Z)** - Bug fixes, minor improvements

---

## 📊 Project Statistics

- **Current Version:** 3.0.0
- **Java Version:** 11+
- **Build Tool:** Maven 3.9.x
- **Database:** MySQL 5.7+
- **UI Framework:** Swing with FlatLaf 3.6.2
- **Architecture:** MVC Pattern with DAO
- **License:** Educational Use
- **Repository:** [github.com/Vaelarr/SweetBatterBakeshop](https://github.com/Vaelarr/SweetBatterBakeshop)
- **Last Updated:** November 1, 2025

---

**Built with ❤️ using Java Swing, Maven, and MVC Architecture**
