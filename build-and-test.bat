@echo off
REM Compile and Test Script for Sweet Batter Bakeshop Review System
REM This script compiles all code and runs tests

echo =================================================
echo    Sweet Batter Bakeshop - Build ^& Test
echo =================================================
echo.

REM Check if MySQL connector exists
if not exist "lib\mysql-connector-j-8.0.33.jar" (
    echo WARNING: MySQL connector not found in lib\ directory
    echo Please download mysql-connector-j-8.0.33.jar manually from:
    echo https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/
    echo.
    echo Place it in the lib\ directory and run this script again.
    pause
    exit /b 1
) else (
    echo ✓ MySQL connector found
)

REM Clean and create bin directory
echo.
echo Cleaning build directory...
if exist bin rmdir /s /q bin
mkdir bin

REM Compile all source files
echo.
echo Compiling all source files...
javac -cp "lib\mysql-connector-j-8.0.33.jar" -d bin -sourcepath src src\model\*.java src\controller\*.java src\view\*.java src\*.java

if %ERRORLEVEL% EQU 0 (
    echo ✓ Compilation successful
) else (
    echo ✗ Compilation failed
    pause
    exit /b 1
)

REM Run in-memory test
echo.
echo =================================================
echo    Running In-Memory Review Model Test
echo =================================================
echo.
java -cp bin ReviewModelTest

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ In-memory tests passed
) else (
    echo.
    echo ✗ In-memory tests failed
    pause
    exit /b 1
)

REM Instructions for database test
echo.
echo =================================================
echo    Database Application Instructions
echo =================================================
echo.
echo To run the database-backed review application:
echo.
echo 1. Start XAMPP MySQL server
echo 2. Run the following command:
echo.
echo    java -cp "bin;lib\mysql-connector-j-8.0.33.jar" RestaurantReviewApp
echo.
echo The application will automatically:
echo   - Create the 'sweetbatterbakeshop' database
echo   - Create the 'reviews' table
echo   - Provide a menu for CRUD operations
echo.
echo For more details, see DATABASE_SETUP.md
echo.
echo =================================================
echo    Build Complete!
echo =================================================
echo.
pause
