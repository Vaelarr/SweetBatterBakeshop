@echo off
REM Quick MySQL Setup for SweetBatterBakeshop
REM This script helps you set up the MySQL database quickly

echo ========================================
echo SweetBatterBakeshop - Quick MySQL Setup
echo ========================================
echo.

REM Get MySQL password from user
set /p MYSQL_PASS="Enter your MySQL root password (press Enter if empty): "

echo.
echo Setting up database...
echo.

REM Create and populate database
mysql -u root --password=%MYSQL_PASS% < database\sql\setup.sql
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to create database schema
    echo Please check your MySQL password and try again
    pause
    exit /b 1
)

echo ✓ Database schema created successfully!
echo.

REM Import test data
mysql -u root --password=%MYSQL_PASS% kiosk_db < database\sql\test_data.sql
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to import test data
    pause
    exit /b 1
)

echo ✓ Test data imported successfully!
echo.

REM Verify setup
echo Verifying database setup...
mysql -u root --password=%MYSQL_PASS% -e "USE kiosk_db; SELECT COUNT(*) as 'Total Items' FROM inventory; SELECT COUNT(*) as 'Total Transactions' FROM sales_transactions;"

echo.
echo ========================================
echo Database setup completed successfully!
echo ========================================
echo.
echo Next steps:
echo 1. Make sure your MySQL password is set in config\database.properties
echo 2. Run the application
echo.
pause
