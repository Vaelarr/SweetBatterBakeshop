@echo off
echo ========================================
echo SweetBatter Custom Orders System Setup
echo ========================================
echo.

REM Set MySQL credentials (modify if needed)
set MYSQL_USER=root
set MYSQL_DB=kiosk_db

echo This script will install the Custom Orders System into your database.
echo.
echo Database: %MYSQL_DB%
echo User: %MYSQL_USER%
echo.
echo NOTE: You will be prompted for your MySQL password
echo.

pause

echo.
echo Installing Custom Orders Schema...
echo.

mysql -u %MYSQL_USER% -p %MYSQL_DB% < ..\sql\custom_orders_schema.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo SUCCESS! Schema installed successfully!
    echo ========================================
    echo.
    echo Verifying installation...
    echo.
    
    mysql -u %MYSQL_USER% -p %MYSQL_DB% < ..\sql\verify_custom_orders.sql
    
    echo.
    echo ========================================
    echo Installation Complete!
    echo ========================================
    echo.
    echo Added 11 new tables:
    echo  - customers
    echo  - customer_addresses
    echo  - custom_orders
    echo  - custom_order_base_products
    echo  - custom_order_addons
    echo  - addons
    echo  - addon_categories
    echo  - order_status_history
    echo  - customer_notifications
    echo  - order_reviews
    echo  - payment_transactions
    echo.
    echo Next steps:
    echo 1. Check the output above for table counts
    echo 2. Update config/database.properties
    echo 3. Test with SimpleCustomerPortal.java
    echo.
    echo See CUSTOM_ORDERS_SYSTEM_README.md for details
    echo.
) else (
    echo.
    echo ========================================
    echo ERROR: Installation failed!
    echo ========================================
    echo.
    echo Please check:
    echo 1. MySQL is running
    echo 2. Database kiosk_db exists (run apply_schema.bat first)
    echo 3. MySQL credentials are correct
    echo 4. You are in database\scripts directory
    echo.
)

pause
