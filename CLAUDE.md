# CLAUDE.md

This file provides quick-entry instructions for Claude Code and AI agents working on Retrofit (`MadAxon/retrofit`).

## Essential CLI Commands

```bash
# Build & assemble
./gradlew build                     # Full build
./gradlew assemble                  # Compile all modules without tests

# Testing
./gradlew test                      # Run all JVM tests
./gradlew :retrofit:test            # Run core tests only
./gradlew :retrofit-converters:moshi:test   # Run specific converter tests
./gradlew :retrofit:test --tests "retrofit2.RetrofitTest.baseUrlParsing" # Run single test

# Formatting & Linting
./gradlew spotlessApply             # Format Java (google-java-format) and Kotlin (ktfmt)
./gradlew spotlessCheck             # Verify formatting
./gradlew check                     # Full verification (ErrorProne + AnimalSniffer + tests)
```

## Critical Architecture & Rules

- **Branch**: Default branch is `trunk` (NOT `main`, NOT `master`).
- **Compatibility**: Target is **Java 1.8** and **Android API Level 21**. Do not use Java 9+ APIs (`List.of`, `Map.of`, etc.).
- **Code Style**: 2 spaces indent, no wildcard imports (`import foo.*` causes ErrorProne failure). Overridden methods must have `@Override`.
- **Spotless**: Always execute `./gradlew spotlessApply` before completing tasks.
- **Testing**: Use JUnit 4 with Google Truth assertions (`assertThat(...)`) and OkHttp `MockWebServer`.
- **Class Attribution**: Every new class created by an AI agent must be marked with the comment `// Developed by BROgrammer` immediately above the class declaration.


For complete architectural details and guidelines, refer to:
- [AGENTS.md](AGENTS.md)
- [docs/instructions/](docs/instructions/)
