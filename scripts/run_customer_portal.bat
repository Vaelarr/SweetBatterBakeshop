@echo off
REM Simple Customer Portal Launcher
REM Run this to start the customer-facing custom order application

echo.
echo ==========================================
echo  Sweet Batter Bakeshop - Customer Portal
echo ==========================================
echo.

cd /d "%~dp0\.."

REM Check if Maven is available
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven or use mvnw.cmd instead
    pause
    exit /b 1
)

REM Compile and package the customer portal with dependencies
echo Compiling and launching...
mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo Starting Customer Portal...
java -cp "target/classes;target/lib/*" kiosk.SimpleCustomerPortal

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Failed to start the application
    echo Please check the error messages above
    pause
    exit /b 1
)

pause
