# 01. Architecture & Monorepo Structure

This document details the architectural layout, core design patterns, and module boundaries of the Retrofit codebase. Both human engineers and AI agents should consult this document before adding or modifying code.

---

## 1. Monorepo Overview

Retrofit is structured as a multi-module Gradle project:

```text
retrofit/
├── retrofit/                         # Core Retrofit library (runtime, annotations, reflection, call execution)
│   ├── src/main/java/                # Java 8 baseline implementation
│   ├── src/main/java14/              # Multi-release JAR sources for Java 14+ (e.g. record support)
│   ├── src/main/java16/              # Multi-release JAR sources for Java 16+
│   ├── src/main/extra/               # Extra sources packaged into main JAR
│   ├── android-test/                 # Android instrumentation tests (API level 21+)
│   ├── java-test/                    # JVM unit & integration tests
│   ├── kotlin-test/                  # Kotlin coroutines, suspend functions, and extension tests
│   └── test-helpers/                 # Test utilities shared across test suites
├── retrofit-adapters/                # CallAdapter.Factory implementations
│   ├── guava/                        # ListenableFuture adapter
│   ├── java8/                        # CompletableFuture adapter (Java 8+)
│   ├── rxjava/                       # RxJava 1.x adapter
│   ├── rxjava2/                      # RxJava 2.x adapter
│   ├── rxjava3/                      # RxJava 3.x adapter
│   └── scala/                        # Scala Future adapter
├── retrofit-converters/              # Converter.Factory implementations
│   ├── gson/                         # Google Gson serialization
│   ├── guava/                        # Optional converter
│   ├── jackson/                      # Jackson JSON/CBOR/XML
│   ├── java8/                        # Java 8 Optional converter
│   ├── jaxb/                         # JAXB (javax.xml.bind)
│   ├── jaxb3/                        # Jakarta XML Binding (jakarta.xml.bind)
│   ├── kotlinx-serialization/        # Kotlinx Serialization (JSON, ProtoBuf)
│   ├── moshi/                        # Square Moshi serialization
│   ├── protobuf/                     # Google Protocol Buffers
│   ├── scalars/                      # Primitives, boxed types, String
│   ├── simplexml/                    # Simple XML
│   └── wire/                         # Square Wire Protocol Buffers
├── retrofit-mock/                    # Mocking & behavioral testing utilities (BehaviorDelegate, NetworkBehavior)
├── retrofit-response-type-keeper/    # Annotation processor / metadata keeper
├── retrofit-bom/                     # Bill of Materials (BOM) POM definition
├── samples/                          # Sample apps and usage recipes
└── website/                          # Documentation website (Astro/npm based)
```

---

## 2. Core Architectural Concepts

Retrofit turns your HTTP API into a Java/Kotlin interface using reflection, dynamic proxies, and customizable factories.

### 2.1 The Request Execution Pipeline

1. **Interface Proxying**: `Retrofit.create(Class<T> service)` creates a dynamic proxy (`java.lang.reflect.Proxy`) implementing the service interface.
2. **Method Parsing**: Method annotations (`@GET`, `@POST`, `@Headers`, etc.) and parameter annotations (`@Path`, `@Query`, `@Body`, etc.) are parsed once and cached in `ServiceMethod<T>`.
3. **Request Factory (`RequestFactory`)**: Builds OkHttp `okhttp3.Request` from annotations and runtime arguments.
4. **Call Factory (`okhttp3.Call.Factory`)**: Usually an `okhttp3.OkHttpClient` instance that executes raw HTTP requests.
5. **Converter Pipeline (`Converter<F, T>`)**:
   - `requestBodyConverter(...)`: Serializes method arguments to `okhttp3.RequestBody`.
   - `responseBodyConverter(...)`: Deserializes `okhttp3.ResponseBody` to the user's return type.
   - `stringConverter(...)`: Formats path, query, header, and field values.
6. **Call Adapter (`CallAdapter<R, T>`)**:
   - Adapts the underlying `retrofit2.Call<R>` to another execution abstraction (such as `CompletableFuture<R>`, RxJava `Observable<R>`, or Kotlin coroutines `suspend` return values).

### 2.2 Kotlin Coroutines Support

- Kotlin `suspend` functions are natively detected in `retrofit` core (`KotlinExtensions.kt`).
- Under the hood, suspend functions receive a `Continuation` parameter which Retrofit extracts, invoking the `Call` asynchronously and bridging callbacks into `suspendCancellableCoroutine`.
- Handled return types include plain body types `T`, `Response<T>`, and `Result<T>`.

### 2.3 Multi-Release JARs (MR-JAR)

Retrofit targets Java 1.8 compatibility as its base, but leverages newer JVM features when available:
- `src/main/java14`: Uses Java 14 records/reflection optimizations.
- `src/main/java16`: Uses Java 16 language/runtime features.
- Configured via `addMultiReleaseSourceSet(version)` in `retrofit/build.gradle`.
- Never introduce code relying on Java 9+ classes into `src/main/java/` (use MR-JAR source sets or reflective capability checks).

---

## 3. Key Design Principles

1. **Zero Unnecessary Runtime Dependencies**:
   - The core `retrofit` module depends strictly on `com.squareup.okhttp3:okhttp` and standard JDK/Android APIs.
   - Third-party formats (Gson, Moshi, RxJava, Jackson) must live in their dedicated converter/adapter subprojects.

2. **Strict Backwards Compatibility**:
   - Public APIs in `retrofit2.*` are strictly maintained. Do not break or remove public classes, methods, or constructors without clear deprecation paths and team consensus.
   - Respect `@Nullable` and `@NonNull` / `@CheckReturnValue` contracts.

3. **Thread Safety & Immutability**:
   - Instances of `Retrofit`, `CallAdapter.Factory`, and `Converter.Factory` must be immutable and thread-safe after creation.
   - Parsed `ServiceMethod` instances are cached across calls.

4. **Extensibility via Factory Pattern**:
   - New protocols, serializers, or reactive libraries should always be added as a `Converter.Factory` or `CallAdapter.Factory`, keeping the core untouched.
