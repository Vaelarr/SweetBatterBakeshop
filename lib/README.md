# Library Folder (Legacy)

**Note:** This project now uses **Maven** for dependency management. Dependencies are automatically downloaded to the `target/lib/` directory when you build the project.

## About This Folder

This folder contains legacy JAR files from before Maven integration. These JARs are **no longer required** for building or running the application when using Maven.

### Legacy Dependencies (For Reference Only)

- `mysql-connector-j-9.5.0.jar` - MySQL JDBC Driver
- `flatlaf-3.5.2.jar` - FlatLaf Look and Feel (older version)
- `flatlaf-3.6.2.jar` - FlatLaf Look and Feel (current)
- `flatlaf-extras-3.6.2.jar` - FlatLaf Extras
- `svgSalamander-1.1.4.jar` - SVG Rendering

## Maven Usage (Recommended)

All dependencies are defined in `pom.xml` and automatically managed by Maven:

```powershell
# Build project (downloads all dependencies automatically)
.\mvnw.cmd clean package

# Dependencies will be in: target/lib/
```

## Manual Usage (Without Maven)

If you prefer not to use Maven, you can still use these JAR files manually:

```powershell
# Compile
javac -cp "lib/*;." -d bin src/main/java/kiosk/**/*.java

# Run
java -cp "bin;lib/*" main.java.kiosk.BakeryPastriesKiosk
```

**However, Maven is strongly recommended** as it ensures all dependencies are at the correct version and properly managed.

---

**See [../docs/MAVEN_GUIDE.md](../docs/MAVEN_GUIDE.md) for complete Maven documentation.**

2. **Place JAR in this folder**
   ```
   lib/mysql-connector-java-8.0.33.jar
   ```

3. **Refresh your IDE**
   - Eclipse: Right-click project → Refresh (F5)
   - IntelliJ: File → Reload All from Disk
   - VS Code: Should auto-detect

4. **Verify**
   - Check that `.classpath` includes the JAR
   - Run a test program to confirm

## Quick Check

After adding the JARs, this folder should contain:
```
lib/
├── README.md (this file)
├── mysql-connector-j-9.5.0.jar      ✓ Database connectivity
├── flatlaf-3.5.2.jar                ✓ Modern UI theme
└── svgSalamander-1.1.4.jar          ✓ SVG icon support
```

## Troubleshooting

### ClassNotFoundException for MySQL Driver
If you see "ClassNotFoundException: com.mysql.cj.jdbc.Driver":
1. Verify `mysql-connector-j-9.5.0.jar` is in this `lib` folder
2. Check that the JAR filename matches what's in `.classpath`
3. Refresh/rebuild your project
4. Clean and rebuild if necessary

### FlatLaf not loading
If you see "ClassNotFoundException: com.formdev.flatlaf.FlatLaf":
1. Verify `flatlaf-3.5.2.jar` is in this `lib` folder
2. Update `.classpath` to include the JAR
3. Rebuild the project

### SVG icons not displaying
If icons don't load:
1. Verify `svgSalamander-1.1.4.jar` is in this `lib` folder
2. Check SVG files are in `src/kiosk/resources/icons/`
3. Ensure resource paths start with `/kiosk/resources/`

## Library Sizes

| Library | Size | Purpose |
|---------|------|---------|
| mysql-connector-j-9.5.0.jar | ~2.6 MB | Database |
| flatlaf-3.5.2.jar | ~900 KB | UI Theme |
| svgSalamander-1.1.4.jar | ~320 KB | SVG Icons |
| **Total** | **~3.8 MB** | |

## More Information

- **MySQL Connector**: See `DATABASE_SETUP.md`
- **FlatLaf & SVG**: See `FLATLAF_INTEGRATION.md` in project root
- **Bakery Theme**: See `BAKERY_THEME_SUMMARY.md` in project root

## Version Compatibility

- **MySQL 8.x:** Use mysql-connector-java-8.0.33.jar
- **MySQL 5.7:** Use mysql-connector-java-5.1.49.jar

The driver version should generally match your MySQL server version.

## Alternative: Maven/Gradle

If you prefer dependency management tools, you can convert this project to Maven/Gradle instead of manually managing JARs. See the main README for instructions.
