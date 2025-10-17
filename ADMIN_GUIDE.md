# Sweet Batter Bakeshop - Admin Dashboard Guide

## 🎯 Overview

The admin dashboard is a separate management module for Sweet Batter Bakeshop that allows administrators to manage products, view transactions, track inventory, and generate reports.

## 🚀 Features

### 1. **Admin Authentication**
- Secure login system for admin access
- Role-based access (Super Admin, Manager, Staff)
- Default credentials provided for initial setup

### 2. **Product Management**
- ➕ Add new products with details (name, category, price, description, stock)
- ✏️ Edit existing product information
- 🗑️ Delete products from catalog
- 📦 Update stock levels and availability
- Real-time inventory tracking

### 3. **Transaction Management**
- 💰 View all customer transactions
- 📝 Track transaction details (items, totals, payment methods)
- 🔍 View individual transaction details
- Transaction status tracking (Completed, Pending, Cancelled)

### 4. **Dashboard Analytics**
- 💵 Total revenue display
- 📊 Transaction count statistics
- 🎂 Product inventory overview
- Real-time data updates

### 5. **Reports**
- **Sales Summary**: Total revenue, transaction counts, average values
- **Product Inventory**: Complete stock overview with availability status
- **Top Selling Products**: Product performance analysis (coming soon)
- **Transaction History**: Complete transaction logs

## 🔧 Running the Admin Dashboard

### Method 1: Run Separately (Recommended for Admin-Only Access)
```bash
cd "c:\Users\atupa\Documents\Development\Java\OOP\FINAL PROJECT\SweetBatterBakeshop"
java -cp bin AdminApp
```

### Method 2: Access from Main Application
1. Run the main customer application:
   ```bash
   java -cp bin App
   ```
2. Click the "🔐 Admin Access" button on the main menu
3. Enter admin credentials

## 🔐 Default Admin Credentials

**Username:** `admin`  
**Password:** `admin123`

> ⚠️ **Important:** Change the default password after first login for security!

## 📖 How to Use

### Adding a New Product
1. Navigate to the "🎂 Products" tab
2. Click "➕ Add Product"
3. Fill in product details:
   - Product Name
   - Category (Cakes, Cupcakes, Pastries, Cookies, Breads)
   - Price (in Philippine Pesos)
   - Description
   - Initial Stock quantity
4. Click "OK" to save

### Updating Stock
1. Go to "🎂 Products" tab
2. Select a product from the table
3. Click "📦 Update Stock"
4. Enter new stock quantity
5. Toggle availability if needed
6. Click "OK" to save

### Viewing Transactions
1. Navigate to "💰 Transactions" tab
2. Select a transaction from the list
3. Click "👁️ View Details" to see:
   - Transaction ID
   - Date and time
   - All items purchased
   - Customizations (toppings, special notes)
   - Payment method
   - Total amount

### Generating Reports
1. Go to "📈 Reports" tab
2. Select report type from dropdown:
   - Sales Summary
   - Product Inventory
   - Top Selling Products
   - Transaction History
3. Click "📊 Generate Report"
4. View detailed report in the text area

## 🏗️ Architecture

### New Files Created

**Models:**
- `model/Admin.java` - Admin user authentication
- `model/AdminManager.java` - Admin user management
- `model/Transaction.java` - Transaction records
- `model/TransactionManager.java` - Transaction handling

**Views:**
- `view/AdminLoginView.java` - Login dialog
- `view/AdminDashboardView.java` - Main dashboard interface

**Controllers:**
- `controller/AdminController.java` - Admin business logic

**Entry Point:**
- `AdminApp.java` - Separate admin application launcher

### Enhanced Files
- `model/Product.java` - Added stock management features
- `model/ProductCatalog.java` - Added admin methods (add/remove products)
- `view/MainView.java` - Added admin access button

## 📊 Product Stock Management

Each product now includes:
- **Stock quantity**: Number of items available
- **Availability flag**: Whether product is currently available for purchase
- **Auto-availability**: Automatically marks as unavailable when stock reaches 0

### Stock Methods:
```java
product.addStock(quantity)      // Increase stock
product.reduceStock(quantity)   // Decrease stock (returns false if insufficient)
product.setStock(quantity)      // Set exact stock amount
product.isAvailable()           // Check if available for purchase
```

## 🔄 Quick Actions

### From Dashboard:
- **🔄 Refresh Data**: Update all statistics and tables
- **🏠 Back to Main App**: Return to customer application
- **Logout**: Exit admin session

## 💡 Tips

1. **Regular Stock Updates**: Check and update stock levels daily
2. **Transaction Review**: Review transactions regularly for sales insights
3. **Report Generation**: Generate weekly/monthly reports for business analysis
4. **Product Management**: Keep product descriptions clear and prices updated
5. **Backup**: Regularly backup your data files

## 🛡️ Security Features

- Password-protected admin access
- Role-based permissions (extensible for future)
- Separate admin module from customer interface
- Session management

## 🔮 Future Enhancements

- [ ] Top selling products analytics
- [ ] Date range filters for transactions
- [ ] Export reports to PDF/Excel
- [ ] Admin activity logs
- [ ] Multi-user admin management
- [ ] Email notifications for low stock
- [ ] Customer order history tracking
- [ ] Promotional discount management

## 🐛 Troubleshooting

**Can't login?**
- Verify credentials (username: `admin`, password: `admin123`)
- Check if AdminManager is properly initialized

**Products not showing?**
- Click "🔄 Refresh Data" button
- Verify ProductCatalog is loading correctly

**Transactions empty?**
- Transactions are created when customers complete purchases
- Use the main app to make test purchases

## 📞 Support

For issues or questions about the admin dashboard, please refer to the main project documentation or contact the development team.

---

**Version:** 1.0  
**Last Updated:** October 17, 2025  
**Project:** Sweet Batter Bakeshop Transaction System
