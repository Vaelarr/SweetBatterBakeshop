# Sweet Batter Bakeshop - Transaction System

A modern, full-featured bakery point-of-sale system built with Java Swing using **MVC (Model-View-Controller) architecture**.

## 🏗️ Architecture

This application follows the **MVC design pattern** for clean separation of concerns:

- **Model:** Business logic and data (`model/` package)
- **View:** User interface components (`view/` package)  
- **Controller:** Application logic and coordination (`controller/` package)

For detailed architecture documentation, see [MVC_ARCHITECTURE.md](MVC_ARCHITECTURE.md)

## 📂 Project Structure

```
src/
├── model/              # Data models
│   ├── Product.java
│   ├── CartItem.java
│   ├── ShoppingCart.java
│   └── ProductCatalog.java
├── view/               # UI components
│   ├── SplashScreenView.java
│   ├── MainView.java
│   └── CatalogueView.java
├── controller/         # Business logic
│   ├── ApplicationController.java
│   └── CatalogueController.java
└── App.java            # Entry point
```

## ✨ Features

### 🎨 Modern UI Design
- Full-screen application and dialogs
- Custom gradient backgrounds (#A9907E taupe theme)
- Smooth animations and hover effects
- Professional typography (Segoe UI family)

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

## 🚀 How to Run

### Using App.java (Recommended)
```bash
cd src
javac App.java
java App
```

### Using VS Code
1. Open `src/App.java`
2. Click Run or press F5
3. Application launches with splash screen

### Compile All Files
```bash
javac src/model/*.java src/view/*.java src/controller/*.java src/App.java -d bin
java -cp bin App
```

## 📦 Requirements

- Java Development Kit (JDK) 8 or higher
- No external dependencies (uses Java Swing)

## 🖼️ Screenshots

The application features:
- **Splash Screen:** Full-screen loading with logo support
- **Home Menu:** Clean navigation with gradient header
- **Product Catalogue:** 3-column grid with modern cards
- **Add to Cart Dialog:** Full-screen customization interface
- **Shopping Cart:** Full-screen cart management
- **Checkout:** Full-screen receipt display

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
- **Code Comments:** Inline documentation throughout source files

## 🔮 Future Enhancements

Potential features to add:
- Database integration (MySQL/PostgreSQL)
- User authentication and profiles
- Payment gateway integration (GCash, PayMaya)
- Inventory management
- Order history and tracking
- Product search functionality
- Discount codes and promotions

## Folder Structure

The workspace contains the following folders:

- `src/model/` - Data models and business logic
- `src/view/` - User interface components
- `src/controller/` - Application controllers
- `bin/` - Compiled output files
- `lib/` - External dependencies (currently none)
- `images/` - Application assets (logo, icons)

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

## 📄 License

Educational use only. Created for OOP Final Project 2025.

---

**Version 2.0 (MVC)** - Refactored with Model-View-Controller architecture  
**Version 1.0** - Initial implementation

*Developed using Java Swing with custom UI components and modern design principles*

