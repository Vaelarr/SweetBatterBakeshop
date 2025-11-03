@echo off
REM ================================================================
REM SweetBatterBakeshop Database Setup Script
REM ================================================================

echo ================================================================
echo SweetBatterBakeshop Database Setup
echo ================================================================
echo.

REM Check if MySQL is installed
where mysql >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: MySQL is not installed or not in PATH
    echo Please install MySQL from: https://dev.mysql.com/downloads/mysql/
    echo.
    pause
    exit /b 1
)

echo MySQL found!
echo.
echo Choose installation option:
echo.
echo 1. Fresh Install (Main Schema + Test Data)
echo 2. Fresh Install + Custom Orders System
echo 3. Add Custom Orders to Existing Database
echo 4. Exit
echo.

set /p OPTION="Enter option (1-4): "

if "%OPTION%"=="4" exit /b 0

if "%OPTION%"=="" (
    echo Invalid option!
    pause
    exit /b 1
)

REM Prompt for MySQL credentials
set /p MYSQL_USER="Enter MySQL username (default: root): "
if "%MYSQL_USER%"=="" set MYSQL_USER=root

echo.
echo You will be prompted for your MySQL password...
echo.

REM Execute based on option
if "%OPTION%"=="1" (
    echo.
    echo Installing main database schema...
    echo.
    mysql -u %MYSQL_USER% -p < database\sql\setup.sql
    
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo Loading test data...
        mysql -u %MYSQL_USER% -p kiosk_db < database\sql\test_data.sql
    )
)

if "%OPTION%"=="2" (
    echo.
    echo Installing main database schema...
    echo.
    mysql -u %MYSQL_USER% -p < database\sql\setup.sql
    
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo Loading test data...
        mysql -u %MYSQL_USER% -p kiosk_db < database\sql\test_data.sql
        
        echo.
        echo Installing custom orders system...
        mysql -u %MYSQL_USER% -p kiosk_db < database\sql\custom_orders_schema.sql
    )
)

if "%OPTION%"=="3" (
    echo.
    echo Installing custom orders system to existing database...
    echo.
    mysql -u %MYSQL_USER% -p kiosk_db < database\sql\custom_orders_schema.sql
)

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo SUCCESS! Database setup completed!
    echo ========================================
    echo.
    echo Next steps:
    echo 1. Update config/database.properties with your MySQL credentials
    echo 2. Verify installation: mysql -u %MYSQL_USER% -p kiosk_db -e "SHOW TABLES;"
    echo 3. Run the application
    echo.
    echo Documentation:
    echo - database/README.md - Main documentation
    echo - database/docs/SETUP_CHECKLIST.md - Detailed setup guide
    echo.
) else (
    echo.
    echo ========================================
    echo ERROR: Database setup failed!
    echo ========================================
    echo.
    echo Please check:
    echo - MySQL server is running
    echo - Credentials are correct
    echo - You have permissions to create databases
    echo.
    echo For help, see database/README.md
    echo.
)

pause
