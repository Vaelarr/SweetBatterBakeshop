@echo off
REM ================================================================
REM Sweet Batter Bakeshop - Database Schema Installer
REM ================================================================

echo ================================================================
echo Sweet Batter Bakeshop - Database Setup
echo ================================================================
echo.
echo This will:
echo  1. Drop existing kiosk_db database (if exists)
echo  2. Create fresh kiosk_db database
echo  3. Apply v2.0 schema
echo  4. Load test data
echo.
echo WARNING: This will DELETE all existing data!
echo.
pause

echo.
echo [1/4] Dropping existing database...
mysql -u root -p -e "DROP DATABASE IF EXISTS kiosk_db; CREATE DATABASE kiosk_db;"
if %errorlevel% neq 0 (
    echo ERROR: Failed to create database. Check your MySQL credentials!
    pause
    exit /b 1
)

echo [2/4] Applying schema...
mysql -u root -p kiosk_db < ..\sql\setup.sql
if %errorlevel% neq 0 (
    echo ERROR: Failed to apply schema!
    pause
    exit /b 1
)

echo [3/4] Loading test data...
mysql -u root -p kiosk_db < ..\sql\test_data.sql
if %errorlevel% neq 0 (
    echo ERROR: Failed to load test data!
    pause
    exit /b 1
)

echo [4/4] Verifying installation...
mysql -u root -p kiosk_db -e "SHOW TABLES; SELECT COUNT(*) as 'View Count' FROM information_schema.views WHERE table_schema='kiosk_db';"

echo.
echo ================================================================
echo SUCCESS! Database v2.0 installed successfully!
echo ================================================================
echo.
echo Database Details:
echo  - 10 Core tables created
echo  - 5 Views created
echo  - 4 Stored procedures
echo  - 1 Trigger
echo  - Test data loaded
echo.
echo Next Steps:
echo  - Run install_custom_orders.bat to add custom orders system
echo  - Update config/database.properties with your credentials
echo.
pause
