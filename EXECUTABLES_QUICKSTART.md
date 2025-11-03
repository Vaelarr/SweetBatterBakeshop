# Windows Executables - Quick Start

## ✅ Build Complete!

Two Windows executables have been successfully created:

### 📦 Executables Location
- `target/BakeryKiosk.exe` (20.24 MB)
- `target/CustomerPortal.exe` (20.24 MB)

## 🚀 How to Run

### Option 1: Direct Launch
Navigate to the `target/` folder and double-click:
- **BakeryKiosk.exe** - Main bakery kiosk application
- **CustomerPortal.exe** - Customer-facing portal

### Option 2: Use Launchers
From the project root, double-click:
- **BakeryKiosk.bat** - Launches the bakery kiosk
- **CustomerPortal.bat** - Launches the customer portal

### Option 3: Command Line
```cmd
cd target
BakeryKiosk.exe
```

## 📋 Requirements

To run the executables, you need:
- **Java 21 or higher** installed on your system
- The executables will automatically find Java if it's installed

## 🔄 Rebuilding

To rebuild the executables after code changes:

### Using the Build Script
```cmd
.\scripts\build_executables.bat
```

### Using Maven Directly
```cmd
mvn clean package -DskipTests
```

## 📁 What You Get

Each executable:
- ✅ Is a standalone Windows .exe file
- ✅ Contains all dependencies bundled inside
- ✅ Can be copied to any location
- ✅ Requires only Java to run
- ✅ Has a professional Windows application appearance

## 📚 More Information

For detailed information, see:
- **[docs/EXECUTABLES_GUIDE.md](docs/EXECUTABLES_GUIDE.md)** - Complete guide
- **[docs/LAUNCHERS_README.md](docs/LAUNCHERS_README.md)** - All launcher options

## 🎯 Quick Tips

1. **No Database?** The apps will automatically use SQLite if MySQL isn't available
2. **Java Not Found?** Install Java 21 from [Adoptium](https://adoptium.net/)
3. **Share the Apps?** Just copy the .exe files - everything is bundled inside!

---

**Built with:** Launch4j Maven Plugin  
**Date:** November 3, 2025
