# Windows Executable Build Guide

This guide explains how to build and use the Windows `.exe` executables for the Sweet Batter Bakeshop applications.

## Building the Executables

### Quick Build
Run the build script:
```cmd
.\scripts\build_executables.bat
```

### Manual Build
If you prefer to build manually:
```cmd
mvn clean package
```

## Output

After building, you'll find two executables in the `target/` folder:
- **BakeryKiosk.exe** - Main bakery kiosk application
- **CustomerPortal.exe** - Customer-facing custom order portal

## Requirements

### To Build:
- Java Development Kit (JDK) 21 or higher
- Maven (or use the included Maven wrapper: `mvnw.cmd`)

### To Run:
- Java Runtime Environment (JRE) 21 or higher
- The executables will use the system-installed Java

## Running the Executables

Simply double-click the `.exe` files or run them from the command line:
```cmd
cd target
BakeryKiosk.exe
```

or

```cmd
cd target
CustomerPortal.exe
```

## Distribution

### Basic Distribution
Copy the `.exe` file to any location and run it. The executable contains all necessary dependencies bundled inside.

### Full Distribution Package
For a complete distribution, include:
1. The `.exe` file
2. The `config/` folder (for database configuration)
3. Any custom icons or resources

## Troubleshooting

### "Java not found" Error
If you see an error about Java not being found:
1. Install Java 21 or higher from [Adoptium](https://adoptium.net/)
2. Make sure `JAVA_HOME` is set in your environment variables
3. Verify Java is in your PATH: `java -version`

### Build Fails
If the build fails:
1. Ensure Maven is installed: `mvn -version`
2. Check that you're using Java 21: `java -version`
3. Try cleaning the project: `mvn clean`
4. Use the Maven wrapper if Maven is not installed: `mvnw.cmd clean package`

### Icon Files Missing
The build script references icon files in `src/main/resources/icons/`:
- `bakery-icon.ico`
- `customer-icon.ico`

If these don't exist, the executables will still build but won't have custom icons. The configuration will be skipped automatically.

## Technical Details

### Launch4j Plugin
The executables are created using the [Launch4j Maven Plugin](http://launch4j.sourceforge.net/), which wraps the JAR file in a native Windows executable.

### Memory Settings
- Initial Heap Size: 256 MB
- Maximum Heap Size: 1024 MB

You can modify these in `pom.xml` under the Launch4j plugin configuration.

### Main Classes
- **BakeryKiosk.exe**: `kiosk.BakeryPastriesKiosk`
- **CustomerPortal.exe**: `kiosk.SimpleCustomerPortal`

## Advanced Configuration

To modify the executable settings, edit `pom.xml` and look for the `launch4j-maven-plugin` section. You can change:
- Memory allocation
- Version information
- Error messages
- Icon files
- JRE requirements

## Notes

- The executables are platform-specific (Windows only)
- For Linux/Mac, use the JAR files or shell scripts instead
- The fat JAR (`sweet-batter-bake-shop-1.0.0-jar-with-dependencies.jar`) is also created during the build and can be run on any platform with Java 21+

## Quick Reference

| Command | Purpose |
|---------|---------|
| `.\scripts\build_executables.bat` | Build both executables |
| `mvn clean package` | Build project and create executables |
| `mvn clean package -DskipTests` | Build without running tests |
| `target\BakeryKiosk.exe` | Run the bakery kiosk |
| `target\CustomerPortal.exe` | Run the customer portal |
