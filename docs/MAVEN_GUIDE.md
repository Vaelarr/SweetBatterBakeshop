# Maven Guide for Convenient Store Kiosk

This project now uses **Apache Maven** for dependency management and build automation.

## 📋 Prerequisites

- **Java JDK 11** or higher
- **Apache Maven 3.6+** (or use the included Maven Wrapper)
- **MySQL 5.7+** (for database features)

## 🚀 Quick Start

### Using Maven Wrapper (Recommended)

The project includes Maven Wrapper, so you don't need to install Maven separately:

```powershell
# Windows PowerShell
.\mvnw.cmd clean install

# Run the application
.\mvnw.cmd exec:java
```

### Using Installed Maven

If you have Maven installed:

```powershell
# Clean and build
mvn clean install

# Run the application
mvn exec:java

# Run tests
mvn test

# Create JAR with dependencies
mvn package
```

## 🔨 Maven Commands

### Build Commands

```powershell
# Clean build artifacts
mvn clean

# Compile source code
mvn compile

# Package as JAR
mvn package

# Install to local repository
mvn install

# Clean and package
mvn clean package
```

### Run Commands

```powershell
# Run the application
mvn exec:java

# Run with specific JVM arguments
mvn exec:java -Dexec.args="arg1 arg2"
```

### Test Commands

```powershell
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=TestClassName

# Skip tests
mvn package -DskipTests
```

### Dependency Commands

```powershell
# Show dependency tree
mvn dependency:tree

# Download dependencies
mvn dependency:resolve

# Copy dependencies to target/lib
mvn dependency:copy-dependencies
```

## 📦 Project Structure

```
Convenient-Store-Kiosk/
├── pom.xml                    # Maven configuration
├── src/                       # Source code
│   └── kiosk/                # Package root
│       ├── BakeryPastriesKiosk.java
│       ├── controller/       # MVC Controllers
│       ├── model/            # Data models
│       ├── view/             # UI components
│       ├── database/         # Database layer
│       ├── util/             # Utilities
│       └── resources/        # Icons and resources
├── config/                    # Configuration files
│   ├── database.properties.template
│   └── database.properties   # (gitignored)
├── database/                  # SQL scripts
│   └── setup.sql
├── target/                    # Build output (gitignored)
│   ├── classes/
│   ├── lib/                  # Dependencies
│   └── *.jar                 # Built artifacts
└── lib/                       # Legacy libs (can be removed)
```

## 📚 Dependencies

The project uses the following dependencies (managed by Maven):

### UI Libraries
- **FlatLaf 3.5.2** - Modern Look and Feel
- **FlatLaf Extras 3.6.2** - Additional UI components
- **SVG Salamander 1.1.4** - SVG icon support

### Database
- **MySQL Connector/J 9.5.0** - MySQL JDBC driver

### Testing
- **JUnit 4.13.2** - Unit testing framework

All dependencies are automatically downloaded from Maven Central.

## 🎯 Building the Application

### Standard JAR (requires dependencies separately)

```powershell
mvn clean package
```

Output: `target/convenient-store-kiosk-1.0.0.jar`

Dependencies: `target/lib/*.jar`

Run with:
```powershell
java -jar target/convenient-store-kiosk-1.0.0.jar
```

### Fat JAR (all dependencies included)

```powershell
mvn clean package
```

Output: `target/convenient-store-kiosk-1.0.0-jar-with-dependencies.jar`

Run with:
```powershell
java -jar target/convenient-store-kiosk-1.0.0-jar-with-dependencies.jar
```

## 🔧 Configuration

### Database Configuration

1. Copy template:
   ```powershell
   Copy-Item config/database.properties.template config/database.properties
   ```

2. Edit `config/database.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/kiosk_db
   db.username=your_username
   db.password=your_password
   ```

### Maven Settings

Edit `pom.xml` to customize:
- Java version (default: 11)
- Dependencies versions
- Main class
- Build plugins

## 🏗️ Development Workflow

### 1. Clone and Setup

```powershell
git clone <repository-url>
cd Convenient-Store-Kiosk
.\mvnw.cmd clean install
```

### 2. Development Cycle

```powershell
# Make code changes
# ...

# Compile and test
.\mvnw.cmd compile

# Run application
.\mvnw.cmd exec:java

# Build final JAR
.\mvnw.cmd package
```

### 3. Adding Dependencies

Edit `pom.xml` and add to `<dependencies>`:

```xml
<dependency>
    <groupId>group-id</groupId>
    <artifactId>artifact-id</artifactId>
    <version>version</version>
</dependency>
```

Then run:
```powershell
mvn dependency:resolve
```

## 🐛 Troubleshooting

### Maven not found
Use Maven Wrapper: `.\mvnw.cmd` instead of `mvn`

### Dependencies not downloading
```powershell
mvn dependency:purge-local-repository
mvn clean install
```

### Compilation errors
```powershell
mvn clean compile -X  # Debug mode
```

### Database connection issues
- Check `config/database.properties`
- Verify MySQL is running
- Run `database/setup.sql`

## 📖 Resources

- [Maven Official Documentation](https://maven.apache.org/guides/)
- [Maven in 5 Minutes](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)
- [POM Reference](https://maven.apache.org/pom.html)

## 🔄 Migration from lib/ folder

The old `lib/` folder with JAR files is now replaced by Maven dependencies. You can safely:

1. Keep `lib/` for reference
2. Remove `lib/*.jar` files (keep README and images)
3. Update any IDE classpath settings to use Maven

Maven automatically manages all dependencies in your local repository (`~/.m2/repository`).

## ✅ Next Steps

1. ✅ Maven setup complete
2. 📝 Review and test all features
3. 🧪 Add unit tests in `src/test/java`
4. 📦 Configure CI/CD pipeline
5. 🚀 Deploy to production
