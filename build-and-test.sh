#!/bin/bash

# Compile and Test Script for Sweet Batter Bakeshop Review System
# This script compiles all code and runs tests

echo "================================================="
echo "   Sweet Batter Bakeshop - Build & Test"
echo "================================================="
echo ""

# Set working directory
cd "$(dirname "$0")"

# Check if MySQL connector exists
if [ ! -f "lib/mysql-connector-j-8.0.33.jar" ]; then
    echo "⚠️  MySQL connector not found in lib/ directory"
    echo "   Downloading mysql-connector-j-8.0.33.jar..."
    mkdir -p lib
    cd lib
    wget -q https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar
    if [ $? -eq 0 ]; then
        echo "   ✓ Downloaded successfully"
    else
        echo "   ✗ Download failed. Please download manually."
        exit 1
    fi
    cd ..
else
    echo "✓ MySQL connector found"
fi

# Clean and create bin directory
echo ""
echo "Cleaning build directory..."
rm -rf bin
mkdir -p bin

# Compile all source files
echo ""
echo "Compiling all source files..."
javac -cp "lib/mysql-connector-j-8.0.33.jar" -d bin -sourcepath src src/**/*.java src/*.java

if [ $? -eq 0 ]; then
    echo "✓ Compilation successful"
else
    echo "✗ Compilation failed"
    exit 1
fi

# Run in-memory test
echo ""
echo "================================================="
echo "   Running In-Memory Review Model Test"
echo "================================================="
echo ""
java -cp bin ReviewModelTest

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ In-memory tests passed"
else
    echo ""
    echo "✗ In-memory tests failed"
    exit 1
fi

# Instructions for database test
echo ""
echo "================================================="
echo "   Database Application Instructions"
echo "================================================="
echo ""
echo "To run the database-backed review application:"
echo ""
echo "1. Start XAMPP MySQL server"
echo "2. Run the following command:"
echo ""
echo "   Linux/Mac:"
echo "   java -cp \"bin:lib/mysql-connector-j-8.0.33.jar\" RestaurantReviewApp"
echo ""
echo "   Windows:"
echo "   java -cp \"bin;lib/mysql-connector-j-8.0.33.jar\" RestaurantReviewApp"
echo ""
echo "The application will automatically:"
echo "  - Create the 'sweetbatterbakeshop' database"
echo "  - Create the 'reviews' table"
echo "  - Provide a menu for CRUD operations"
echo ""
echo "For more details, see DATABASE_SETUP.md"
echo ""
echo "================================================="
echo "   Build Complete!"
echo "================================================="
