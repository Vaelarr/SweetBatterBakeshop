# Archived: Old Application (Serialization-Based)

This folder contains the original application implementation that used **file-based serialization** for data persistence.

## Archived Files:
- `App.java` - Old main entry point
- `AdminApp.java` - Old admin entry point
- `controller/` - Old MVC controllers (ApplicationController, AdminController, CatalogueController)
- `model/` - Old models (Product, ProductCatalog, ShoppingCart, etc.)
- `view/` - Old views (MainView, CatalogueView, AdminDashboardView, etc.)
- `util/` - Old utilities (FlatLafUtil, IconUtil, SerializationUtil)

## Why Archived?
The project was migrated to use the **MySQL database-backed kiosk system** located in `src/kiosk/`.

The kiosk implementation provides:
- ✅ MySQL database persistence (instead of serialization)
- ✅ DAO pattern for data access
- ✅ Advanced inventory management
- ✅ Sales tracking and reporting
- ✅ More robust architecture

## Archived Date:
November 1, 2025

## Note:
These files are kept for reference and potential future use. The active application is now in `src/kiosk/`.
