# Restaurant Review System - Database Setup Guide

## Overview

The Restaurant Review System is an extension to the Sweet Batter Bakeshop application that allows customers to submit and manage reviews. Reviews are stored persistently in a MySQL database via XAMPP.

## Database Structure

### Reviews Table
```sql
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant VARCHAR(255) NOT NULL,
    reviewer VARCHAR(255) NOT NULL,
    rating INT NOT NULL,
    review TEXT NOT NULL
);
```

## Prerequisites

1. **XAMPP with MySQL**
   - Download and install XAMPP from https://www.apachefriends.org/
   - Start MySQL server from XAMPP control panel

2. **MySQL JDBC Driver**
   - Download `mysql-connector-java-8.0.33.jar` (or latest version)
   - From: https://dev.mysql.com/downloads/connector/j/
   - Place the JAR file in a `lib` directory in the project root

## Setup Instructions

### 1. Start XAMPP MySQL Server

1. Open XAMPP Control Panel
2. Click "Start" next to MySQL
3. Verify MySQL is running (port 3306)

### 2. Database Initialization

The application will automatically:
- Create the `sweetbatterbakeshop` database if it doesn't exist
- Create the `reviews` table with the proper schema
- Configure the connection with default XAMPP settings

Default configuration:
- Host: `localhost`
- Port: `3306`
- Database: `sweetbatterbakeshop`
- Username: `root`
- Password: `` (empty)

### 3. Compile and Run

#### Option 1: Using Command Line (Recommended)

```bash
# Create lib directory and download MySQL connector
mkdir lib
# Download mysql-connector-java-8.0.33.jar to lib/ directory

# Compile with JDBC driver in classpath
cd /path/to/SweetBatterBakeshop
javac -cp "lib/mysql-connector-java-8.0.33.jar" -d bin -sourcepath src src/RestaurantReviewApp.java src/model/*.java

# Run the application
java -cp "bin:lib/mysql-connector-java-8.0.33.jar" RestaurantReviewApp

# On Windows, use semicolon instead of colon:
java -cp "bin;lib/mysql-connector-java-8.0.33.jar" RestaurantReviewApp
```

#### Option 2: Using VS Code

1. Place `mysql-connector-java-8.0.33.jar` in the `lib` directory
2. VS Code should automatically detect it
3. Open `src/RestaurantReviewApp.java`
4. Click "Run" or press F5

## Application Features

### Main Menu Options

1. **Add New Review** - Create a new review with validation
2. **View All Reviews** - Display all reviews in the database
3. **View Review by ID** - Search for a specific review
4. **Search Reviews by Restaurant** - Find reviews for a specific restaurant
5. **Search Reviews by Rating** - Find reviews with a specific rating
6. **Update Review** - Modify an existing review
7. **Delete Review** - Remove a review from the database
8. **View Statistics** - See total reviews and average rating
9. **Exit** - Close the application

### Input Validation

The system validates:
- Restaurant name: Cannot be empty
- Reviewer name: Cannot be empty
- Rating: Must be between 1-10
- Review text: Cannot be empty, max 1000 characters

## Architecture

The review system follows the existing MVC pattern:

### Model Classes

- **`Review`** - Entity class implementing `IdentifiableEntity`
- **`DatabaseConnector`** - Manages MySQL connections and initialization
- **`ReviewDatabaseRepository`** - Implements `Repository<Review, Long>` with MySQL
- **`ReviewManager`** - Business logic layer with validation

### Key Features

- **Repository Pattern** - Consistent with existing codebase
- **Generic Types** - Type-safe operations using generics
- **Error Handling** - Comprehensive exception handling for database operations
- **Connection Management** - Proper resource cleanup with try-with-resources
- **PreparedStatements** - SQL injection prevention
- **Auto-increment IDs** - Database-managed primary keys

## Troubleshooting

### MySQL Connection Failed

**Problem:** "Database connection test failed"

**Solutions:**
1. Ensure XAMPP MySQL is running
2. Check port 3306 is not blocked by firewall
3. Verify credentials (default: root with no password)

### JDBC Driver Not Found

**Problem:** "MySQL JDBC Driver not found"

**Solutions:**
1. Download mysql-connector-java JAR file
2. Place it in the `lib` directory
3. Include it in the classpath when compiling and running

### Database Already Exists

The application handles existing databases gracefully:
- Uses existing database if already created
- Creates tables only if they don't exist
- No data loss on re-initialization

### Cannot Update/Delete Review

**Problem:** Review not found or operation fails

**Solutions:**
1. Verify the review ID exists (use "View All Reviews")
2. Check database connection is active
3. Ensure no database locks or constraints

## Database Management

### Using phpMyAdmin (XAMPP)

1. Start Apache and MySQL in XAMPP
2. Navigate to http://localhost/phpmyadmin
3. Select `sweetbatterbakeshop` database
4. View/manage the `reviews` table

### Manual Database Creation (Optional)

If automatic creation fails, manually create the database:

```sql
CREATE DATABASE sweetbatterbakeshop;
USE sweetbatterbakeshop;

CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant VARCHAR(255) NOT NULL,
    reviewer VARCHAR(255) NOT NULL,
    rating INT NOT NULL,
    review TEXT NOT NULL
);
```

## Example Usage

```
=== Add New Review ===
Restaurant name: Sweet Batter Bakeshop
Your name: John Doe
Rating (1-10): 9
Your review: Amazing pastries! The croissants are heavenly.

✓ Review added successfully! (ID: 1)
```

## Integration with Main Application

The review system is standalone but can be integrated with the main bakeshop application:

1. Add a "Reviews" button to the main menu
2. Import review management classes
3. Display reviews in a Swing UI panel
4. Link reviews to products/transactions

## Future Enhancements

Potential improvements:
- Photo uploads with reviews
- Reply to reviews (admin feature)
- Review verification (purchase required)
- Rating breakdowns (taste, service, value)
- Email notifications for new reviews
- Export reviews to PDF/Excel
- Review moderation system

## Security Considerations

Current implementation:
- PreparedStatements prevent SQL injection
- Input validation on all fields
- Exception handling prevents crashes

Recommended for production:
- Password hashing for admin access
- Rate limiting to prevent spam
- Profanity filter for review text
- User authentication system
- HTTPS for web deployment

## License

Educational use only. Part of Sweet Batter Bakeshop OOP Final Project 2025.

---

**Version 1.0** - Database-backed review system with CRUD operations
