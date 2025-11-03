# 🚀 Quick Start Guide

Get the SweetBatter Bakeshop system running in minutes!

## Prerequisites

- **Java 11** or higher ([Download](https://adoptium.net/))
- **MySQL 5.7+** or **SQLite** (automatic fallback)
- **Maven** (included via wrapper)

---

## 📦 Installation

### 1. Clone or Download
```powershell
git clone https://github.com/Vaelarr/SweetBatterBakeshop.git
cd SweetBatterBakeshop
```

### 2. Database Setup

#### Option A: MySQL (Recommended)
```powershell
# Run the setup script
.\setup_database.bat

# Install custom orders schema
cd database
.\install_custom_orders.bat
cd ..
```

The script will:
- Create the `bakery_db` database
- Set up all tables and relationships
- Insert sample data (50+ products, test accounts)

**Default Credentials:**
- Database: `bakery_db`
- User: `bakery_user`
- Password: `bakery_pass`

#### Option B: SQLite (Automatic)
If MySQL is not available, the system automatically uses SQLite. No setup required!

### 3. Configure Database (if needed)

Edit `config/database.properties`:
```properties
# MySQL Configuration
db.type=mysql
db.url=jdbc:mysql://localhost:3306/bakery_db
db.username=bakery_user
db.password=bakery_pass

# Or use SQLite
db.type=sqlite
db.url=jdbc:sqlite:data/bakery.db
```

---

## 🎯 Running the Applications

### Bakery Kiosk (Main POS)
```powershell
.\run_bakery_kiosk.bat
```

**Admin Login:**
- Username: `admin`
- Password: `admin123`

**Features:**
- Browse product catalog
- Shopping cart & checkout
- Admin panel (inventory, sales, custom orders)
- Sales reporting

### Customer Portal
```powershell
.\run_customer_portal.bat
```

**Test Account:**
- Email: `john.doe@email.com`
- Password: `password123`

**Features:**
- Create custom orders
- Schedule pickup/delivery
- View order history
- Manage account

---

## 🔧 Building from Source

### Compile & Package
```powershell
.\mvnw clean package
```

### Build Windows Executables
```powershell
.\scripts\build_executables.bat
```

Creates `target/BakeryKiosk.exe` and `target/CustomerPortal.exe`

---

## 🗄️ Database Access

### MySQL Command Line
```bash
mysql -u bakery_user -p
# Password: bakery_pass
use bakery_db;
```

### View Tables
```sql
SHOW TABLES;
SELECT * FROM products LIMIT 10;
SELECT * FROM custom_orders;
```

### Reset Database
```powershell
cd database\sql
mysql -u bakery_user -p bakery_db < setup.sql
```

---

## 📁 Project Structure

```
SweetBatterBakeshop/
├── src/main/java/kiosk/          # Source code
│   ├── BakeryPastriesKiosk.java  # Main kiosk app
│   ├── SimpleCustomerPortal.java # Customer portal
│   ├── controller/               # Business logic
│   ├── model/                    # Data models
│   ├── view/                     # UI components
│   └── database/                 # DAO layer
├── database/                      # Database scripts
│   ├── sql/setup.sql             # Main schema
│   └── sql/custom_orders_schema.sql
├── config/                        # Configuration
│   └── database.properties
├── docs/                          # Documentation
├── scripts/                       # Build/run scripts
└── target/                        # Compiled output
```

---

## 🆘 Troubleshooting

### Database Connection Failed
1. Check MySQL is running: `mysql -u root -p`
2. Verify credentials in `config/database.properties`
3. The system will auto-fallback to SQLite if MySQL unavailable

### Java Version Issues
```powershell
java -version  # Should be 11 or higher
```

### Maven Build Fails
```powershell
.\mvnw clean install -U
```

### Port Already in Use
MySQL default port is 3306. Change in `database.properties` if needed.

---

## 📚 Next Steps

- **Full Documentation:** See [README.md](README.md)
- **Windows Executables:** See [EXECUTABLES_QUICKSTART.md](EXECUTABLES_QUICKSTART.md)
- **Database Details:** See [database/README.md](database/README.md)

---

## 💡 Tips

- Use **SQLite mode** for quick testing without MySQL setup
- Default admin password should be changed for production
- Sample data includes 50+ products across 4 categories
- Custom orders require customer accounts

---

**Need Help?** Check the main [README.md](README.md) or create an issue on GitHub.
