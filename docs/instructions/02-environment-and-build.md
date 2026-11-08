# 02. Environment & Build Commands

This document specifies the required environment, toolchains, and common Gradle commands to build, test, and package Retrofit.

---

## 1. Prerequisites & Toolchains

- **JDK for Gradle Execution**: JDK 17 or higher (LTS recommended: JDK 17 or JDK 21). In CI, Java 27 is used with the Foojay resolver.
- **Java Compilation Target**: Java 1.8 (`-source 1.8 -target 1.8`, Kotlin `jvmTarget = '1.8'`).
- **Android Target**: Android API level 21 (Lollipop 5.0).
- **Gradle**: Uses the Gradle Wrapper (`./gradlew` on Linux/macOS, `gradlew.bat` on Windows). The wrapper version is Gradle 9.8.2.
- **Node.js** (Optional, only for documentation site): Node.js 18+ and `npm` inside `website/`.

---

## 2. Fast Command Reference

All commands should be run from the repository root:

### 2.1 Building & Compiling

```bash
# Full build of all JVM modules and documentation
./gradlew build

# Compile all Java & Kotlin sources without running tests
./gradlew assemble

# Compile a specific module (e.g. core retrofit)
./gradlew :retrofit:assemble

# Clean build artifacts
./gradlew clean
```

### 2.2 Testing

```bash
# Run all JVM unit tests across all modules
./gradlew test

# Run tests in the core module
./gradlew :retrofit:test

# Run tests in a specific sub-project (e.g., Moshi converter, RxJava3 adapter)
./gradlew :retrofit-converters:moshi:test
./gradlew :retrofit-adapters:rxjava3:test

# Run a single test class
./gradlew :retrofit:test --tests "retrofit2.RetrofitTest"

# Run a specific test method
./gradlew :retrofit:test --tests "retrofit2.RetrofitTest.baseUrlParsing"

# Run Android emulator tests (requires connected device or running emulator)
./gradlew connectedCheck
```

### 2.3 Code Formatting & Linting

```bash
# Automatically format all Java and Kotlin code with Spotless (google-java-format + ktfmt)
./gradlew spotlessApply

# Verify code formatting without modifying files (fails on misformatted code)
./gradlew spotlessCheck

# Run all checks (ErrorProne, AnimalSniffer, Spotless, Tests)
./gradlew check

# Run checks for a single module
./gradlew :retrofit:check
```

### 2.4 Documentation Website

```bash
# Copy Dokka/Javadoc outputs into website
./gradlew copyWebsiteDocs

# Run or build website
cd website
npm ci
npm run build -- --mode snapshot
```

---

## 3. Environment Troubleshooting

### 3.1 Network Timeouts & Proxies
When Gradle Wrapper downloads Gradle distribution or Maven dependencies, timeout may occur in restricted network environments.
- Gradle Wrapper timeout can be raised in `gradle/wrapper/gradle-wrapper.properties` via `networkTimeout`.
- Run with `--offline` if dependencies are already downloaded to local cache:
  ```bash
  ./gradlew test --offline
  ```

### 3.2 Memory Settings
For large multi-module builds, the recommended memory arguments are:
```properties
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m
```
Or passed directly to the command line:
```bash
./gradlew build -Dorg.gradle.jvmargs="-Xmx4g"
```
