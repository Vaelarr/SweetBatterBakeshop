#!/bin/bash
# Quick setup script for Kiosk Database (Linux/Mac)
# This script helps set up the MySQL database

echo "========================================"
echo "Kiosk Database Setup Script"
echo "========================================"
echo ""

# Check if MySQL is installed
if ! command -v mysql &> /dev/null; then
    echo "ERROR: MySQL is not installed or not in PATH"
    echo "Please install MySQL first"
    echo ""
    exit 1
fi

echo "MySQL found!"
echo ""

# Prompt for MySQL credentials
read -p "Enter MySQL username (default: root): " MYSQL_USER
MYSQL_USER=${MYSQL_USER:-root}

echo ""
echo "Please enter your MySQL password when prompted..."
echo ""

# Run the setup SQL script
mysql -u "$MYSQL_USER" -p < database/setup.sql

if [ $? -eq 0 ]; then
    echo ""
    echo "========================================"
    echo "Database setup completed successfully!"
    echo "========================================"
    echo ""
    echo "Next steps:"
    echo "1. Update config/database.properties with your MySQL password"
    echo "2. Add MySQL Connector/J to your project classpath"
    echo "3. Run the application"
    echo ""
else
    echo ""
    echo "========================================"
    echo "ERROR: Database setup failed!"
    echo "========================================"
    echo ""
    echo "Please check:"
    echo "- MySQL server is running"
    echo "- Credentials are correct"
    echo "- You have permissions to create databases"
    echo ""
fi
