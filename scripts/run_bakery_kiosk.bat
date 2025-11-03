@echo off
REM Bakery Pastries Kiosk Launcher
REM Run this to start the main bakery kiosk application

echo.
echo ==========================================
echo  Sweet Batter Bakeshop - Main Kiosk
echo ==========================================
echo.

cd /d "%~dp0"

REM Check if Maven is available
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven or use mvnw.cmd instead
    pause
    exit /b 1
)

REM Compile and run the main kiosk
echo Compiling and launching...
mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Compilation failed
    pause
    exit /b 1
)

echo Starting Bakery Kiosk...
java -cp "target/classes;target/lib/*" main.java.kiosk.BakeryPastriesKiosk

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Failed to start the application
    echo Please check the error messages above
    pause
    exit /b 1
)

pause
