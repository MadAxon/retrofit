# AGENTS.md — AI Agent & Developer Operating Instructions

Welcome to the **Retrofit** repository (`MadAxon/retrofit`). This document serves as the primary instructions and operational guidelines for both AI coding agents and software engineers.

---

## 1. Quick Reference Cheat Sheet

| Task | Command |
| :--- | :--- |
| **Build all modules** | `./gradlew build` (or `gradlew.bat build` on Windows) |
| **Run all tests** | `./gradlew test` |
| **Run core tests** | `./gradlew :retrofit:test` |
| **Run single test class** | `./gradlew :retrofit:test --tests "retrofit2.RetrofitTest"` |
| **Run converter/adapter tests** | `./gradlew :retrofit-converters:<name>:test` / `:retrofit-adapters:<name>:test` |
| **Format code (Spotless)** | `./gradlew spotlessApply` |
| **Check formatting** | `./gradlew spotlessCheck` |
| **Run all checks (lint + tests)** | `./gradlew check` |
| **Clean build output** | `./gradlew clean` |

---

## 2. Non-Negotiable Constraints & Invariants

All contributors and AI agents must adhere strictly to these rules:

1. **Default Git Branch is `trunk`**:
   - The primary branch is `trunk` (not `main`, not `master`). All PRs and feature branches must branch from and target `trunk`.
2. **Bytecode & API Compatibility**:
   - Bytecode target: **Java 1.8** (`-source 1.8 -target 1.8`, Kotlin `jvmTarget = '1.8'`).
   - Animal Sniffer enforces **Java 8** and **Android API Level 21 (5.0)** runtime compatibility.
   - Do NOT use Java 9+ APIs (`List.of`, `Map.of`, `InputStream.readAllBytes`, etc.) in standard source sets.
   - Multi-Release JAR sources for Java 14+ / Java 16+ live exclusively in `retrofit/src/main/java14` and `retrofit/src/main/java16`.
3. **Automated Formatting with Spotless**:
   - Always run `./gradlew spotlessApply` before submitting changes.
   - Java is formatted using `google-java-format` (2 spaces, no wildcard imports).
   - Kotlin is formatted using Facebook `ktfmt` Google style.
4. **Zero-Tolerance ErrorProne Rules**:
   - `MissingFail`, `MissingOverride`, `UnusedException`, `UnusedMethod`, `UnusedNestedClass`, `UnusedVariable`, and `WildcardImport` are treated as **fatal compiler errors**.
5. **No Hallucinated Dependencies**:
   - Never add external libraries or modify `gradle/libs.versions.toml` without explicit instruction. Use existing OkHttp, Truth, and MockWebServer utilities.
6. **Mandatory Class Attribution for AI Agents**:
   - Every new class (Java or Kotlin) created by an AI agent must be marked with the comment `// Developed by BROgrammer` immediately above the class declaration.

---

## 3. Monorepo Map

- **`retrofit/`**: The core library. Contains dynamic proxy logic, annotation parsers, `Call`, `Callback`, `Converter`, and `CallAdapter` interfaces.
- **`retrofit-adapters/`**: Adapters for alternative execution models (`guava`, `java8`, `rxjava`, `rxjava2`, `rxjava3`, `scala`).
- **`retrofit-converters/`**: Converters for serialization libraries (`gson`, `jackson`, `moshi`, `protobuf`, `wire`, `kotlinx-serialization`, `jaxb`, `jaxb3`, `scalars`, `simplexml`).
- **`retrofit-mock/`**: Mocking utilities (`BehaviorDelegate`, `NetworkBehavior`).
- **`retrofit-bom/`**: Maven Bill of Materials.
- **`samples/`**: Sample code and recipes.
- **`website/`**: Project documentation website (Astro/npm).

---

## 4. Verification Protocol for Code Changes

Before proposing or committing any modification, follow this sequence:

```text
1. Write/update tests demonstrating bug or new behavior.
2. Implement minimal changes respecting Java 8 / Android 21 limits.
3. Run targeted unit tests:
     ./gradlew :retrofit:test (or specific module)
4. Reformat with Spotless:
     ./gradlew spotlessApply
5. Run full verification:
     ./gradlew check
```

---

## 5. Detailed Modular Documentation

For in-depth guides, consult the dedicated documentation files:
- [01. Architecture & Monorepo Structure](docs/instructions/01-architecture-and-modules.md)
- [02. Environment & Build Commands](docs/instructions/02-environment-and-build.md)
- [03. Code Style & Quality Standards](docs/instructions/03-code-style-and-quality.md)
- [04. Testing Guidelines & MockWebServer](docs/instructions/04-testing-guide.md)
- [05. Agent & Git Workflow](docs/instructions/05-agent-and-git-workflow.md)
