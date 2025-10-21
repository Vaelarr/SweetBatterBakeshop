# Library Dependencies

This directory contains external JAR dependencies required for the project.

## MySQL JDBC Driver

**Required for:** Restaurant Review System database connectivity

**File:** `mysql-connector-j-8.0.33.jar` (or latest version)

**Download from:** https://dev.mysql.com/downloads/connector/j/

### Quick Download (Maven Central):
```bash
cd lib
wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar
```

### Manual Download:
1. Visit https://dev.mysql.com/downloads/connector/j/
2. Select "Platform Independent"
3. Download the ZIP archive
4. Extract `mysql-connector-j-X.X.XX.jar`
5. Place it in this `lib/` directory

### Alternative Versions:
- mysql-connector-j-8.0.33.jar (Recommended)
- mysql-connector-java-8.0.33.jar (Older naming)
- Any MySQL Connector/J 8.0.x version should work

## Usage

The JAR file must be in the classpath when compiling and running:

### Compile:
```bash
javac -cp "lib/mysql-connector-j-8.0.33.jar" -d bin -sourcepath src src/RestaurantReviewApp.java
```

### Run:
```bash
# Linux/Mac:
java -cp "bin:lib/mysql-connector-j-8.0.33.jar" RestaurantReviewApp

# Windows:
java -cp "bin;lib/mysql-connector-j-8.0.33.jar" RestaurantReviewApp
```

## Note

JAR files are excluded from version control (see `.gitignore`). Each developer must download the MySQL connector separately.
