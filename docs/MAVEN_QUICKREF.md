# Maven Quick Reference

## Common Commands

### Build & Run
```powershell
# Clean and build
.\mvnw.cmd clean package

# Run application
.\mvnw.cmd exec:java

# Run tests
.\mvnw.cmd test

# Clean only
.\mvnw.cmd clean
```

### Development
```powershell
# Compile only
.\mvnw.cmd compile

# Skip tests during build
.\mvnw.cmd package -DskipTests

# Show dependency tree
.\mvnw.cmd dependency:tree

# Update dependencies
.\mvnw.cmd dependency:resolve
```

### Run JAR Files
```powershell
# Run standard JAR (requires dependencies in lib/)
java -jar target/convenient-store-kiosk-1.0.0.jar

# Run fat JAR (all dependencies included)
java -jar target/convenient-store-kiosk-1.0.0-jar-with-dependencies.jar
```

## Build Output

After running `.\mvnw.cmd package`, you'll find:

- `target/convenient-store-kiosk-1.0.0.jar` - Standard JAR
- `target/convenient-store-kiosk-1.0.0-jar-with-dependencies.jar` - Fat JAR
- `target/lib/` - All dependencies
- `target/classes/` - Compiled classes

## Project Info

- **Group ID:** com.kiosk
- **Artifact ID:** convenient-store-kiosk
- **Version:** 1.0.0
- **Java Version:** 11
- **Main Class:** kiosk.BakeryPastriesKiosk

## Dependencies

- FlatLaf 3.5.2
- FlatLaf Extras 3.6.2
- MySQL Connector/J 9.5.0
- SVG Salamander 1.1.4
- JUnit 4.13.2 (test scope)

All managed automatically by Maven!
