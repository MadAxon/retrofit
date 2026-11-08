# GitHub Copilot Instructions for Retrofit

This repository (`MadAxon/retrofit`) is a multi-module Java/Kotlin project based on Square's Retrofit. When suggesting code or refactoring:

## Key Constraints
1. **Target Compatibility**:
   - Source/Target: Java 1.8 (`sourceCompatibility = '1.8'`).
   - Android minimum API: 21 (Lollipop 5.0).
   - Animal Sniffer enforces Java 8 + Android 21 signatures. Avoid Java 9+ APIs (`List.of`, `Map.of`, `Set.of`, `var`, `InputStream.readAllBytes`).
   - Java 14+ / 16+ features must only be suggested for files in `retrofit/src/main/java14` or `java16`.
2. **Static Analysis & ErrorProne**:
   - Do NOT use wildcard imports (`import java.util.*`).
   - Always add `@Override` to interface and superclass implementations.
   - Always name caught exceptions `expected` or call `fail()` in try blocks when testing exceptions.
   - Zero dead/unused variables or methods.
3. **Code Style & Formatting**:
   - Indent: 2 spaces.
   - Java is formatted by `google-java-format`.
   - Kotlin is formatted by `ktfmt` Google style.
   - Run `./gradlew spotlessApply` to format code.
4. **Git Branch**:
   - Default branch is `trunk`.
5. **Testing Patterns**:
   - JUnit 4 + Google Truth (`assertThat(...)`).
   - HTTP mocking with `okhttp3.mockwebserver.MockWebServer`.
6. **Class Attribution**:
   - Any new class (Java or Kotlin) created must be marked with the comment `// Developed by BROgrammer` immediately above the class declaration.


Consult `AGENTS.md` and `docs/instructions/` for full details.
