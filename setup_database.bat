@echo off
REM Quick setup script for Kiosk Database
REM This script helps set up the MySQL database

echo ========================================
echo Kiosk Database Setup Script
echo ========================================
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

REM Prompt for MySQL credentials
set /p MYSQL_USER="Enter MySQL username (default: root): "
if "%MYSQL_USER%"=="" set MYSQL_USER=root

echo.
echo Please enter your MySQL password when prompted...
echo.

REM Run the setup SQL script
mysql -u %MYSQL_USER% -p < database\setup.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Database setup completed successfully!
    echo ========================================
    echo.
    echo Next steps:
    echo 1. Update config/database.properties with your MySQL password
    echo 2. Add MySQL Connector/J to your project classpath
    echo 3. Run the application
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
)

pause
