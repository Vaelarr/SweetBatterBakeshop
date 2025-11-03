# CRUD Operations Documentation
## Sweet Batter Bakeshop - Database Access Layer

**Last Updated:** November 3, 2025  
**Status:** ✅ All CRUD operations validated and working

---

## Overview

This document details all CRUD (Create, Read, Update, Delete) operations implemented across all DAO (Data Access Object) classes in the Sweet Batter Bakeshop application.

## Summary of CRUD Operations

| DAO Class | Create | Read | Update | Delete | Status |
|-----------|--------|------|--------|--------|--------|
| CustomerDAO | ✅ | ✅ | ✅ | ✅ | Complete |
| InventoryDAO | ✅ | ✅ | ✅ | ✅ | Complete |
| CustomProductDAO | ✅ | ✅ | ✅ | ✅ | Complete |
| SalesDAO | ✅ | ✅ | ✅ | ✅ | Complete |
| CustomOrderDAO | ✅ | ✅ | ✅ | ✅ | Complete |

---

## 1. CustomerDAO

### CREATE Operations
- **`register(Customer customer)`** - Register a new customer account
  - Auto-generates unique customer ID (format: CUST-YYYYMMDD-####)
  - Hashes and stores password
  - Sets initial customer type (REGULAR/VIP/WHOLESALE)
  - Returns: boolean (success/failure)

### READ Operations
- **`login(String email, String password)`** - Authenticate customer
  - Updates last login timestamp
  - Returns: Customer object or null
  
- **`findByCustomerId(String customerId)`** - Find customer by ID
  - Returns: Customer object or null
  
- **`findByEmail(String email)`** - Find customer by email
  - Returns: Customer object or null
  
- **`getAllCustomers()`** - Get all customers (admin)
  - Ordered by created_at DESC
  - Returns: List<Customer>
  
- **`searchCustomers(String searchTerm)`** - Search by name or email
  - Uses LIKE pattern matching
  - Returns: List<Customer>
  
- **`emailExists(String email)`** - Check if email is already registered
  - Returns: boolean

### UPDATE Operations
- **`update(Customer customer)`** - Update customer profile
  - Updates: email, name, phone, address, DOB
  - Does NOT update password (use updatePassword)
  - Returns: boolean
  
- **`updatePassword(String customerId, String newPasswordHash)`** - Update password
  - Requires hashed password
  - Returns: boolean
  
- **`deactivate(String customerId)`** - Soft delete account
  - Sets is_active = FALSE
  - Returns: boolean
  
- **`activate(String customerId)`** - Reactivate account
  - Sets is_active = TRUE
  - Returns: boolean

### DELETE Operations
- **`delete(String customerId)`** - Hard delete customer (PERMANENT)
  - ⚠️ Use with extreme caution
  - Recommendation: Use `deactivate()` instead
  - Returns: boolean

---

## 2. InventoryDAO

### CREATE Operations
- **`createTable()`** - Initialize inventory table
  - Creates table if not exists
  - Sets up indexes and constraints
  
- **`insert(InventoryItem item)`** - Add new inventory item
  - Validates unique name constraint
  - Returns: boolean

### READ Operations
- **`getByName(String name)`** - Get item by name (primary key)
  - Returns: InventoryItem or null
  
- **`getAll()`** - Get all inventory items
  - Ordered by category, then name
  - Returns: List<InventoryItem>
  
- **`getByCategory(String category)`** - Get items by category
  - Returns: List<InventoryItem>
  
- **`getLowStock(int threshold)`** - Get items below threshold
  - Ordered by stock quantity (lowest first)
  - Returns: List<InventoryItem>
  
- **`getExpiringSoon(int daysWarning)`** - Get items expiring within X days
  - Returns: List<InventoryItem>
  
- **`getExpired()`** - Get expired items
  - Returns: List<InventoryItem>

### UPDATE Operations
- **`update(InventoryItem item)`** - Update all item fields
  - Primary key: name (cannot be changed)
  - Returns: boolean
  
- **`updateStock(String name, int quantity)`** - Update stock only
  - Faster than full update
  - Returns: boolean

### DELETE Operations
- **`delete(String name)`** - Delete single item
  - Returns: boolean
  
- **`deleteAll()`** - Clear entire inventory
  - ⚠️ Use with extreme caution
  - Returns: boolean

---

## 3. CustomProductDAO

### CREATE Operations
- **`insertProduct(CustomProduct product)`** - Add new custom product
  - Requires unique product_code
  - Returns: boolean

### READ Operations
- **`getAllProducts()`** - Get all active products
  - Joins with category table
  - Ordered by display_order
  - Returns: List<CustomProduct>
  
- **`getProductsByCategory(String categoryCode)`** - Get products by category
  - Returns: List<CustomProduct>
  
- **`findByProductCode(String productCode)`** - Get specific product
  - Returns: CustomProduct or null
  
- **`getAllCategories()`** - Get product categories
  - Returns: List<String[]> [code, name, description, lead_time]
  
- **`getCompatibleAddonCategories(String productCode)`** - Get addon categories for product
  - Returns: List<AddonCategory>
  
- **`getAddonsByCategory(String categoryCode)`** - Get addons in category
  - Returns: List<OrderAddOn>
  
- **`findAddonByCode(String addonCode)`** - Get specific addon
  - Returns: OrderAddOn or null

### UPDATE Operations
- **`updateProduct(CustomProduct product)`** - Update product details
  - Cannot change product_code (primary key)
  - Returns: boolean
  
- **`deactivateProduct(String productCode)`** - Soft delete product
  - Sets is_active = FALSE
  - Recommended over hard delete
  - Returns: boolean

### DELETE Operations
- **`deleteProduct(String productCode)`** - Hard delete product (PERMANENT)
  - ⚠️ Will fail if product has existing orders
  - Recommendation: Use `deactivateProduct()` instead
  - Returns: boolean

---

## 4. SalesDAO

### CREATE Operations
- **`createTables()`** - Initialize sales tables
  - Creates sales_transactions and sales_items
  - Sets up foreign key relationships
  
- **`insert(SaleTransaction transaction)`** - Record new sale
  - Uses transaction to ensure atomicity
  - Inserts main transaction + all items
  - Returns: boolean

### READ Operations
- **`getAll()`** - Get all transactions
  - Ordered by date DESC
  - Returns: List<SaleTransaction>
  
- **`getById(String transactionId)`** - Get specific transaction
  - Includes all items
  - Returns: SaleTransaction or null
  
- **`getByDateRange(LocalDateTime start, LocalDateTime end)`** - Get transactions in range
  - Returns: List<SaleTransaction>
  
- **`getTotalSales(LocalDateTime start, LocalDateTime end)`** - Get revenue total
  - Returns: double
  
- **`getTransactionCount(LocalDateTime start, LocalDateTime end)`** - Get transaction count
  - Returns: int

### UPDATE Operations
- **`update(SaleTransaction transaction)`** - Update transaction
  - Deletes old items and inserts new ones
  - Uses transaction for atomicity
  - Returns: boolean

### DELETE Operations
- **`delete(String transactionId)`** - Delete single transaction
  - Deletes transaction and all items
  - Uses transaction for atomicity
  - Returns: boolean
  
- **`deleteAll()`** - Clear all sales data
  - ⚠️ Use with extreme caution
  - Returns: boolean

---

## 5. CustomOrderDAO

### CREATE Operations
- **`insert(CustomOrder order)`** - Create new custom order
  - Auto-generates order number (format: CO-YYYYMMDD-####)
  - Inserts order + all addons
  - Uses transaction for atomicity
  - Returns: boolean

### READ Operations
- **`findByOrderNumber(String orderNumber)`** - Get specific order
  - Includes addons
  - Returns: CustomOrder or null
  
- **`findByCustomerId(String customerId)`** - Get customer's orders
  - Ordered by created_at DESC
  - Returns: List<CustomOrder>
  
- **`getAllOrders()`** - Get all orders (admin)
  - Joins customer and product tables
  - Returns: List<CustomOrder>
  
- **`findByStatus(OrderStatus status)`** - Get orders by status
  - Returns: List<CustomOrder>
  
- **`getUpcomingOrders()`** - Get orders for next 7 days
  - Filters by pickup date
  - Returns: List<CustomOrder>
  
- **`getStatistics()`** - Get order statistics
  - Returns: OrderStatistics (totals, revenue, etc.)

### UPDATE Operations
- **`update(CustomOrder order)`** - Full order update
  - Updates all order fields
  - Does NOT update addons (separate operation)
  - Returns: boolean
  
- **`updateStatus(String orderNumber, OrderStatus newStatus)`** - Update order status
  - Quick status change
  - Returns: boolean
  
- **`updatePaymentStatus(String orderNumber, PaymentStatus newStatus, double depositPaid)`**
  - Updates payment and calculates balance
  - Returns: boolean
  
- **`updateAdminNotes(String orderNumber, String notes)`** - Add admin notes
  - Returns: boolean
  
- **`assignBaker(String orderNumber, String bakerName)`** - Assign baker
  - Returns: boolean
  
- **`cancelOrder(String orderNumber, String reason, String cancelledBy)`** - Cancel order
  - Soft delete (sets status to CANCELLED)
  - Records cancellation details
  - Returns: boolean

### DELETE Operations
- **`delete(String orderNumber)`** - Hard delete order (PERMANENT)
  - Deletes order and all addons
  - Uses transaction for atomicity
  - ⚠️ Use with extreme caution
  - Recommendation: Use `cancelOrder()` instead
  - Returns: boolean

---

## Testing

### Running CRUD Validation Tests

A comprehensive test suite has been created to validate all CRUD operations:

```bash
# Compile the test
javac -cp "target/classes;lib/*" src/main/java/kiosk/database/CRUDValidationTest.java

# Run the test
java -cp "target/classes;lib/*;src/main/java" kiosk.database.CRUDValidationTest
```

### Test Coverage

The `CRUDValidationTest.java` class tests:
- ✅ All CREATE operations
- ✅ All READ operations (single, multiple, filtered)
- ✅ All UPDATE operations (full and partial)
- ✅ All DELETE operations (hard and soft)
- ✅ Verification of deletions
- ✅ Transaction atomicity
- ✅ Error handling

---

## Best Practices

### 1. Soft Delete vs Hard Delete
- **Recommendation**: Always use soft delete (deactivate/cancel) in production
- **Hard delete**: Only for testing or data cleanup with proper backup
- **Benefits**: Data recovery, audit trails, referential integrity

### 2. Transaction Management
- All DAOs use transactions for multi-table operations
- Automatic rollback on errors
- Connection auto-commit properly managed

### 3. Error Handling
- All methods return boolean for success/failure
- Errors logged to System.err
- SQLException details printed for debugging

### 4. Data Validation
- Unique constraints enforced at database level
- Foreign key constraints prevent orphaned records
- Null checks performed before database operations

### 5. Performance Optimization
- Use specific READ methods over getAll() when possible
- Batch operations for multiple inserts/updates
- Indexes on frequently queried columns

---

## Database Schema Dependencies

### Foreign Key Relationships

```
customers
  └── custom_orders (customer_id)

custom_order_base_products
  └── custom_orders (product_code)

custom_orders
  └── custom_order_addons (order_number)

sales_transactions
  └── sales_items (transaction_id)
```

### Deletion Cascade Behavior

- **Customers**: Manual cascade required (or use soft delete)
- **Custom Orders**: Cascade to addons implemented in DAO
- **Sales Transactions**: Cascade to items implemented in DAO
- **Inventory**: No cascades (standalone table)
- **Custom Products**: Protected by foreign keys (use soft delete)

---

## Known Issues & Limitations

1. **CustomOrderDAO**: Full CRUD testing requires existing customer and product records
2. **Date Functions**: Some SQL date functions may differ between MySQL and SQLite
3. **Boolean Fields**: SQLite uses INTEGER (0/1) instead of BOOLEAN
4. **Cascading Deletes**: Implemented in Java code, not database constraints

---

## Future Enhancements

- [ ] Add bulk insert operations for all DAOs
- [ ] Implement pagination for large result sets
- [ ] Add connection pooling for better performance
- [ ] Create stored procedures for complex operations
- [ ] Add audit logging for all CRUD operations
- [ ] Implement caching for frequently accessed data

---

## Related Documentation

- [Database Schema](database/docs/SCHEMA_DIAGRAM.md)
- [Setup Guide](database/docs/SETUP_CHECKLIST.md)
- [Migration Guide](database/docs/MIGRATION_GUIDE.md)
- [Quick Start](docs/QUICKSTART_DATABASE.md)

---

**For questions or issues, please refer to the main documentation or create an issue in the project repository.**
