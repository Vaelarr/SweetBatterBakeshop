# 🎯 SweetBatter Bakeshop - System Demo Presentation (5 People)

## 📋 Simple Demo-Only Presentation Division

**Focus:** Show what the system DOES, not how it's built  
**Total Time:** 20-25 minutes (4-5 minutes per person)

---

## 👤 **Person 1: Introduction & System Overview** (4-5 minutes)

### Your Role
Introduce the project and give a high-level tour of what the system can do.

### What to Say & Show

#### 1. Welcome (1 minute)
```
"Welcome! Today we'll demonstrate SweetBatter Bakeshop - a complete 
bakery management system. This system has TWO applications:

1. Main Bakery Kiosk - for in-store sales
2. Customer Portal - for online custom orders

Let me show you what each one does."
```

#### 2. System Overview (3 minutes)

**Show a visual diagram or explain:**

```
┌─────────────────────────────────────────────────┐
│          SWEETBATTER BAKESHOP SYSTEM            │
├─────────────────────────────────────────────────┤
│                                                 │
│  APPLICATION 1: Bakery Kiosk (POS)              │
│  • Browse products                              │
│  • Shopping cart                                │
│  • Checkout                                     │
│  • Admin panel (inventory & sales)              │
│                                                 │
│  APPLICATION 2: Customer Portal                 │
│  • Customer accounts                            │
│  • Create custom orders                         │
│  • Order tracking                               │
│                                                 │
│  SHARED DATABASE: All data synchronized         │
└─────────────────────────────────────────────────┘
```

**Explain the business scenario:**
```
"Imagine you own a bakery. 

Your customers can:
- Walk in and buy items from the kiosk
- OR go online and order custom cakes

Your staff can:
- Manage inventory
- Track sales
- View custom orders

Everything is connected through one database."
```

#### 3. Transition
```
"Let's see this in action. [Person 2] will show you the customer 
experience at the kiosk."
```

---

## 👤 **Person 2: Bakery Kiosk - Customer Experience** (4-5 minutes)

### Your Role
Demonstrate the point-of-sale system from a customer/cashier perspective.

### What to Do

#### Step 1: Launch the Kiosk (30 seconds)
```powershell
# Run this command
mvnw clean compile exec:java
```

**While loading:**
```
"This is the main bakery kiosk. Watch the splash screen as the 
system loads..."
```

#### Step 2: Browse Products (2 minutes)

**Navigate through all categories and show:**

**Breads & Rolls:**
```
"Here we have fresh breads - baguettes, croissants, dinner rolls. 
Each item shows the price and a photo. Notice the clean, modern 
interface."
```

**Pastries & Desserts:**
```
"Our pastries section - cookies, brownies, danishes, éclairs. 
Over 20 different items here."
```

**Cakes & Special Occasions:**
```
"Custom cakes for birthdays, weddings, celebrations. These are 
our premium items."
```

**Beverages & Extras:**
```
"Coffee, tea, and complementary items to go with the pastries."
```

#### Step 3: Shopping Experience (1.5 minutes)

**Add items to cart:**
```
"Let me show you a typical purchase. A customer wants:
- 2 croissants
- 1 chocolate cake
- 1 coffee"
```

**Click products from different categories**

```
"Watch the cart icon update in real-time. Now let's view the cart..."
```

**Open Cart Page:**
```
"Here's the shopping cart. You can see:
- All selected items
- Individual prices
- Quantities (which we can adjust)
- Running total

Very simple and intuitive - perfect for quick transactions."
```

#### Step 4: User Experience (1 minute)

**Highlight:**
```
"Notice the design:
- Large, touch-friendly buttons
- Clear navigation
- Warm colors that match a bakery atmosphere
- Fast and responsive

A customer or cashier can operate this with no training."
```

#### Transition
```
"That's the customer side. But someone needs to manage all these 
products and track sales. [Person 3], show us the admin panel."
```

---

## 👤 **Person 3: Bakery Kiosk - Admin Panel** (4-5 minutes)

### Your Role
Show the business management features - inventory and sales.

### What to Do

#### Step 1: Admin Login (30 seconds)

**From kiosk main screen:**
```
"To access management features, staff log in to the admin panel."
```

**Click "Admin" button**
```
Username: admin
Password: admin123
```

```
"This is secure - only authorized staff can access business data."
```

#### Step 2: Inventory Management Demo (2.5 minutes)

**Open Inventory tab:**

**Show existing products:**
```
"Here's our complete product catalog. Every item in the store is here:
- Product names
- Categories
- Prices
- Current stock levels
- Expiration dates
- Supplier information"
```

**Add a New Product:**
```
"Let's add a new product to the system. Say we just started making 
chocolate chip cookies..."

[Click "Add Product"]

Name: Chocolate Chip Cookies
Category: Pastries & Desserts
Price: $8.99
Stock: 50 units
Expiration: [next week]
Supplier: Sweet Supplies Inc

[Save]

"Done! This product is now available in the kiosk immediately."
```

**Update Stock:**
```
"We just received a delivery. Let's update the stock for croissants..."

[Select croissant, click Edit]
[Change stock from 20 to 50]
[Save]

"Stock updated. The kiosk now shows 50 available."
```

**Show real-world scenario:**
```
"This is how bakery managers would:
- Add new seasonal items
- Update prices for sales
- Track inventory levels
- Know when to reorder supplies"
```

#### Step 3: Sales Tracking (1.5 minutes)

**Open Sales tab:**
```
"Every transaction is recorded here. Let me show you today's sales..."
```

**Show transaction list:**
```
"Each sale shows:
- When it happened
- What was purchased
- Total amount
- Payment method

This helps answer questions like:
- How much money did we make today?
- What's our best-selling item?
- What time is our rush hour?"
```

**Show reports:**
```
"We also have daily summaries:
- Total revenue: $X,XXX.XX
- Number of transactions: XX
- Items sold: XXX
- Top sellers: [list]

This data helps owners make business decisions."
```

#### Step 4: Custom Orders View (30 seconds)
```
"We can also see custom orders from customers who ordered online. 
These appear here so staff know what to prepare.

[Show custom orders tab]

We can update the status as we work on them."
```

#### Transition
```
"That's the in-store system. Now let's see how customers order 
custom cakes from home. [Person 4]?"
```

---

## 👤 **Person 4: Customer Portal - Custom Orders** (4-5 minutes)

### Your Role
Show the online ordering system for custom cakes and special orders.

### What to Do

#### Step 1: Launch Customer Portal (30 seconds)

```powershell
# Run this command
mvnw exec:java -Dexec.mainClass="kiosk.SimpleCustomerPortal"
```

```
"This is our Customer Portal - an online system for custom orders. 
It has a professional loading screen..."
```

#### Step 2: Customer Account (1 minute)

**Show Login:**
```
"Customers need an account to place orders. Let me log in as a 
returning customer..."

Email: john.doe@email.com
Password: password123

[Login]
```

**Optionally show registration:**
```
"New customers can register right here. They just need:
- Name
- Email
- Phone number
- Password

Everything is secure - passwords are encrypted."
```

#### Step 3: Create Custom Order (2.5 minutes)

**This is the main demo - go slowly!**

**Browse Products:**
```
"Let's say I want to order a custom birthday cake..."

[Browse custom products]

"We have 11 different base products customers can customize:
- Custom cakes
- Bulk cookies
- Seasonal specialties
- Wedding cakes

I'll select 'Custom Birthday Cake' - $45.00 base price."
```

**Select Add-Ons:**
```
"Now the fun part - customization! Customers can add:

FLAVORS: [Select 'Chocolate']
'Chocolate cake - classic choice'

FILLINGS: [Select 'Bavarian Cream']
'Rich, creamy filling'

DECORATIONS: [Select 'Fresh Flowers']
'Beautiful presentation'

CUSTOM MESSAGE: [Type 'Happy Birthday Sarah!']
'Personalized message on the cake'

Watch the price update as I add each option...
Base: $45 → now $57 with add-ons"
```

**Choose Fulfillment:**
```
"Now, pickup or delivery?

[Select Delivery]
[Enter address: '123 Main St']
[Choose date: Next Saturday]
[Choose time: 2:00 PM]

'Delivery adds $5, bringing the total to $62.00'
```

**Submit Order:**
```
[Review order]
[Click 'Place Order']

"Order confirmed! I get an order number: CO-20251104-0001

The customer receives confirmation, and this order now appears 
in the bakery's admin panel for staff to prepare."
```

#### Step 4: Order Tracking (30 seconds)

**Show Order History:**
```
"Customers can track their orders here.

[Open Order History]

All my orders are listed with status:
- Pending (just ordered)
- Confirmed (bakery accepted)
- Ready (cake is done)
- Completed (picked up)

Real-time updates keep customers informed."
```

#### Transition
```
"So we've seen both applications working. [Person 5] will show you 
how they all connect together and what data looks like behind the scenes."
```

---

## 👤 **Person 5: System Integration & Data Flow** (4-5 minutes)

### Your Role
Show how the two applications share data and demonstrate the integrated system.

### What to Do

#### Step 1: Explain Integration (1 minute)

**Show diagram or explain:**
```
"Both applications share one database. This means:

When a customer places an order in the portal ➔ 
Staff see it in the kiosk admin panel

When staff update inventory ➔ 
Changes appear in the kiosk immediately

Everything stays synchronized automatically."
```

#### Step 2: Live Data Demonstration (3 minutes)

**Open database viewer (MySQL Workbench) or command line:**

```powershell
mysql -u root -p kiosk_db
```

**Show Products Data:**
```sql
SELECT name, price, stock_quantity 
FROM inventory 
ORDER BY category;
```

```
"This is the same product data we browsed in the kiosk - stored 
in the database."
```

**Show Sales Data:**
```sql
SELECT transaction_id, total_amount, date
FROM sales_transactions 
ORDER BY date DESC 
LIMIT 5;
```

```
"Every transaction we completed in the kiosk is recorded here with 
timestamps, amounts, and details."
```

**Show Custom Orders:**
```sql
SELECT co.order_number, c.name, cp.name as product, co.total_price
FROM custom_orders co
JOIN customers c ON co.customer_id = c.customer_id
JOIN custom_products cp ON co.product_id = cp.product_id
ORDER BY co.created_at DESC;
```

```
"Here's the custom order we just placed - CO-20251104-0001 for 
John Doe, $62.00. It's in the database and visible to both applications."
```

**Show Add-ons:**
```sql
SELECT name, category_name, price 
FROM addons;
```

```
"All the customization options - flavors, fillings, decorations - 
stored systematically."
```

#### Step 3: Data Persistence Demo (1 minute)

**Cross-reference between applications:**

```
"Let me demonstrate synchronization..."

[Switch to kiosk]
"I'll add an item to the cart in the kiosk..."
[Add item]

[Switch to database]
"And we can see it in the database immediately..."
[Query cart or transaction]

[Switch to admin panel]
"The admin panel shows updated inventory..."

"Everything is connected in real-time. No manual updates needed."
```

#### Step 4: Conclusion (30 seconds)

**Summary:**
```
"So that's SweetBatter Bakeshop:

✅ A complete point-of-sale kiosk
✅ An online custom order portal  
✅ Full inventory management
✅ Sales tracking and reporting
✅ Customer accounts and order history
✅ All integrated through a shared database

A real-world solution for bakery businesses.

Questions?"
```

---

## 📊 Quick Visual Summary

```
┌─────────────┐         ┌──────────┐         ┌─────────────┐
│   Person 1  │         │ Person 2 │         │  Person 3   │
│ Introduction│────────▶│  Kiosk   │────────▶│Admin Panel  │
│  & Overview │         │  Demo    │         │   Demo      │
└─────────────┘         └──────────┘         └─────────────┘
                                                     │
                                                     ▼
┌─────────────┐         ┌──────────┐         ┌─────────────┐
│   Person 5  │◀────────│ Person 4 │◀────────│  Continue   │
│Data & Outro │         │ Customer │         │             │
│             │         │  Portal  │         │             │
└─────────────┘         └──────────┘         └─────────────┘
```

---

## ✅ Pre-Demo Checklist

### Everyone:
- [ ] Applications launch successfully
- [ ] Database has sample data
- [ ] Know your 4-5 minute segment
- [ ] Practice transitions

### Person 1:
- [ ] Overview points clear
- [ ] Visual diagram ready

### Person 2:
- [ ] Kiosk launches
- [ ] Know which products to browse
- [ ] Cart demo smooth

### Person 3:
- [ ] Admin login works
- [ ] Sample product ready to add
- [ ] Sales data exists

### Person 4:
- [ ] Portal launches
- [ ] Customer login works
- [ ] Know which add-ons to select

### Person 5:
- [ ] Database access ready
- [ ] SQL queries tested
- [ ] Can switch between apps

---

## 🎯 Demo Tips

### For All Presenters:

**DO:**
- ✅ Show outputs and results
- ✅ Explain what users see
- ✅ Demonstrate features
- ✅ Talk about real-world use cases
- ✅ Keep it simple and clear

**DON'T:**
- ❌ Show code files
- ❌ Explain implementation
- ❌ Discuss programming concepts
- ❌ Open configuration files
- ❌ Get technical unless asked

### Presentation Style:
```
"Watch what happens when I click this..."
"Notice how the system automatically..."
"This helps bakery owners..."
"Customers can easily..."
```

Focus on **USER EXPERIENCE** and **BUSINESS VALUE**.

---

## 🎬 Alternative: Story-Based Demo

Instead of dividing by application, tell a story:

### "A Day at SweetBatter Bakeshop"

**Person 1:** Opening - Owner checks system  
**Person 2:** Morning - First customer purchases  
**Person 3:** Midday - Manager updates inventory  
**Person 4:** Afternoon - Customer orders custom cake online  
**Person 5:** Evening - Review sales data, close up  

This creates a narrative that's engaging and easy to follow!

---

## ⏱️ Timing Guide

| Person | Focus | Time | Main Activity |
|--------|-------|------|---------------|
| 1 | Introduction | 4 min | Explain what system does |
| 2 | Kiosk shopping | 5 min | Browse & cart demo |
| 3 | Admin features | 5 min | Inventory & sales |
| 4 | Custom orders | 5 min | Portal & order creation |
| 5 | Integration | 4 min | Data & wrap-up |
| **Total** | | **23 min** | **+ 5-7 min Q&A** |

---

## 💡 What Makes This Demo Great

1. **User-Focused:** Shows what people DO with the system
2. **Visual:** Live demonstrations, not slides or code
3. **Practical:** Real business scenarios
4. **Connected:** Shows how everything works together
5. **Simple:** Non-technical audience can understand

---

## 🎤 Sample Transitions

**1 → 2:**
"Now let's see this in action. [Person 2], show us the kiosk."

**2 → 3:**
"Great shopping experience. [Person 3], show us the management side."

**3 → 4:**
"That's in-store. [Person 4], show us online ordering."

**4 → 5:**
"Amazing! [Person 5], tie it all together for us."

**5 → End:**
"Thank you! Any questions?"

---

**Focus:** SHOW, don't tell. Let the system speak for itself! 🎉

*Demo Guide for SweetBatter Bakeshop v4.0.0*
