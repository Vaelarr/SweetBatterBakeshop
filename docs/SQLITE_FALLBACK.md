# SQLite Fallback Database

## Overview

The bakery kiosk system now supports automatic database fallback functionality. If MySQL is unavailable, the system will automatically switch to SQLite as a local file-based database.

## How It Works

1. **Primary Database (MySQL)**: The system first attempts to connect to MySQL using the configuration in `config/database.properties`
2. **Automatic Fallback**: If MySQL connection fails after 3 retry attempts, the system automatically falls back to SQLite
3. **Transparent Operation**: All database operations work identically regardless of which database is in use

## Connection Priority

```
MySQL (Attempt 1) → MySQL (Attempt 2) → MySQL (Attempt 3) → SQLite Fallback
```

## Configuration

### MySQL Settings (config/database.properties)
```properties
db.url=jdbc:mysql://localhost:3306/kiosk_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=root
db.password=your_password
db.driver=com.mysql.cj.jdbc.Driver
```

### SQLite Settings (config/database.properties)
```properties
db.sqlite.url=jdbc:sqlite:bakery_kiosk.db
db.sqlite.driver=org.sqlite.JDBC
```

## SQLite Database File

When using SQLite fallback:
- Database file: `bakery_kiosk.db` (created in the project root directory)
- Location: Same directory as the application JAR
- File-based: No server installation required
- Portable: Can be easily backed up or moved

## Features

### Supported Operations
- ✓ All CRUD operations (Create, Read, Update, Delete)
- ✓ Transactions
- ✓ Foreign key constraints
- ✓ Indexes
- ✓ Complex queries

### Automatic SQL Dialect Translation
The system automatically translates MySQL-specific SQL syntax to SQLite-compatible syntax:

| Feature | MySQL | SQLite |
|---------|-------|--------|
| Auto Increment | `AUTO_INCREMENT` | `AUTOINCREMENT` |
| Timestamp | `TIMESTAMP` | `DATETIME` |
| Boolean | `BOOLEAN` | `INTEGER (0/1)` |
| Update Trigger | `ON UPDATE CURRENT_TIMESTAMP` | Handled programmatically |

## Checking Active Database

The system logs which database is active on startup:

```
✓ MySQL database connection established successfully!
```

or

```
MySQL connection failed. Falling back to SQLite...
✓ SQLite database connection established successfully!
  Database file: bakery_kiosk.db
```

## Programmatic Access

Check the current database type in code:

```java
DatabaseConnection dbConn = DatabaseConnection.getInstance();

if (dbConn.isMysql()) {
    System.out.println("Using MySQL database");
} else if (dbConn.isSqlite()) {
    System.out.println("Using SQLite database");
}

// Or get the database type
DatabaseConfig.DatabaseType type = dbConn.getDatabaseType();
```

## Benefits

### Development
- No MySQL server required for local development
- Quick setup for new developers
- Works offline

### Production
- High availability: continues operation if MySQL is temporarily unavailable
- Data preservation: transactions saved locally until MySQL is restored
- Graceful degradation

### Testing
- Isolated test environments
- Fast test execution
- No shared database conflicts

## Migration Between Databases

### From SQLite to MySQL
If you start with SQLite and later want to migrate to MySQL:

1. Set up MySQL server and create the database
2. Export SQLite data:
   ```bash
   sqlite3 bakery_kiosk.db .dump > backup.sql
   ```
3. Import to MySQL (after syntax adjustments)
4. Restart the application - it will automatically use MySQL

### From MySQL to SQLite
SQLite fallback happens automatically when MySQL is unavailable. To manually force SQLite:

1. Stop MySQL server, or
2. Modify `config/database.properties` with incorrect MySQL credentials

## Limitations

### SQLite Limitations (compared to MySQL)
- Single writer at a time (not suitable for high-concurrency scenarios)
- No user authentication/permissions
- Limited to local file access
- Smaller maximum database size (~281 TB vs unlimited)

### When to Use Each Database

**Use MySQL for:**
- Production deployments
- Multi-user environments
- High transaction volumes
- Remote database access
- Advanced security requirements

**Use SQLite for:**
- Development and testing
- Single-kiosk deployments
- Offline operation
- Demonstration purposes
- Quick prototyping

## Troubleshooting

### SQLite file permissions error
**Problem**: "Unable to open database file"  
**Solution**: Ensure write permissions in the application directory

### Database locked error
**Problem**: "Database is locked"  
**Solution**: Close other connections or applications accessing the SQLite file

### Cannot connect to either database
**Problem**: Both MySQL and SQLite fail  
**Solution**: 
1. Check SQLite JDBC driver is in classpath
2. Verify disk space available
3. Check application has write permissions

## Dependencies

Required Maven dependencies (already added to `pom.xml`):

```xml
<!-- MySQL Connector -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>9.5.0</version>
</dependency>

<!-- SQLite JDBC Driver -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.47.0.0</version>
</dependency>
```

## Backup Recommendations

### SQLite Backup
```bash
# Simple file copy
copy bakery_kiosk.db bakery_kiosk_backup.db

# Or SQL dump
sqlite3 bakery_kiosk.db .dump > backup.sql
```

### MySQL Backup
```bash
mysqldump -u root -p kiosk_db > backup.sql
```

## Additional Resources

- [SQLite Documentation](https://www.sqlite.org/docs.html)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/)
