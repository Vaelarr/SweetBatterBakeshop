@echo off
REM Test SQLite Fallback by temporarily renaming the database properties file

echo ===============================================
echo    SQLite Fallback Demonstration
echo ===============================================
echo.

REM Change to project root directory
cd /d "%~dp0\.."

REM Backup the original database.properties
if exist config\database.properties (
    echo [1/4] Backing up database.properties...
    copy config\database.properties config\database.properties.backup >nul
    echo       Backup created: config\database.properties.backup
)

REM Create a config that will fail to connect to MySQL
echo [2/4] Creating MySQL connection failure scenario...
echo # Temporary config to force MySQL connection failure > config\database.properties
echo db.url=jdbc:mysql://invalid-host:9999/nonexistent_db >> config\database.properties
echo db.username=invalid_user >> config\database.properties
echo db.password=invalid_password >> config\database.properties
echo db.driver=com.mysql.cj.jdbc.Driver >> config\database.properties
echo db.sqlite.url=jdbc:sqlite:bakery_kiosk_demo.db >> config\database.properties
echo db.sqlite.driver=org.sqlite.JDBC >> config\database.properties
echo       MySQL will fail to connect, forcing SQLite fallback
echo.

REM Run the test
echo [3/4] Running database fallback test...
echo ===============================================
call mvn exec:java -Dexec.mainClass=kiosk.database.DatabaseFallbackTest -q
echo ===============================================
echo.

REM Restore the original database.properties
echo [4/4] Restoring original database.properties...
if exist config\database.properties.backup (
    move /Y config\database.properties.backup config\database.properties >nul
    echo       Original configuration restored
) else (
    del config\database.properties >nul 2>&1
    echo       Temporary configuration removed
)

echo.
echo ===============================================
echo    Demonstration Complete
echo ===============================================
echo.
echo Note: A SQLite database file 'bakery_kiosk_demo.db' 
echo       was created to demonstrate the fallback.
echo.

pause
