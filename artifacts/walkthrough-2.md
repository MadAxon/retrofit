# Walkthrough: Comprehensive Coroutine Tests, Rebase, and Pull Request

## Summary of Changes
Expanded [KotlinExtensionsTest.kt](../retrofit/kotlin-test/src/test/java/retrofit2/KotlinExtensionsTest.kt) to **39 comprehensive unit tests**, covering:

### 1. HTTP 200 with Null-Body Scenarios
Implemented `NullReturningConverterFactory` (`// Developed by BROgrammer`) that converts HTTP 200 responses to `null` to test converter behavior:
- `awaitThrowsOnHttp200WithNullBody`: `Call<String>.await()` throws `KotlinNullPointerException` when response is HTTP 200 but body deserialized to null.
- `awaitNullableReturnsNullOnHttp200WithNullBody`: `Call<String?>.await()` returns `null` successfully without exception on HTTP 200.
- `awaitResponseReturns200WithNullBody`: `Call<String>.awaitResponse()` returns `Response` with `isSuccessful == true`, `code() == 200`, and `body() == null`.

### 2. Edge Cases with Already Canceled Call
Verifies immediate rejection when `await()` or `awaitResponse()` is called on an already canceled `Call` (`call.cancel()`):
- `awaitThrowsOnAlreadyCanceledCall`: Throws `IOException("Canceled")`.
- `awaitNullableThrowsOnAlreadyCanceledCall`: Throws `IOException("Canceled")`.
- `awaitUnitThrowsOnAlreadyCanceledCall`: Throws `IOException("Canceled")`.
- `awaitResponseThrowsOnAlreadyCanceledCall`: Throws `IOException("Canceled")`.

### 3. Repeated Calls on Already Executed Call
Verifies that Retrofit prevents multiple executions of the same `Call` instance while allowing cloned calls:
- `awaitThrowsOnAlreadyExecutedCall`: Awaiting an already-awaited `Call` throws `IllegalStateException("Already executed.")`.
- `awaitThrowsWhenCallAlreadyExecutedSynchronously`: Awaiting a call previously executed via `call.execute()` throws `IllegalStateException("Already executed.")`.
- `awaitResponseThrowsOnAlreadyExecutedCall`: Calling `awaitResponse()` on an already-executed call throws `IllegalStateException("Already executed.")`.
- `awaitClonedCallSucceedsAfterOriginalExecuted`: Calling `call.clone().await()` succeeds after original call has executed.

---

## Git & Pull Request

1. **Rebase on `trunk`**:
   - Created clean branch `test/kotlin-extensions-coroutines` directly off `origin/trunk`.
   - Verified clean merge base with `trunk`.
2. **Dedicated Pull Request Created**:
   - **PR**: [#3 Add comprehensive coroutine tests to KotlinExtensionsTest](https://github.com/MadAxon/retrofit/pull/3)
   - **Head branch**: `test/kotlin-extensions-coroutines`
   - **Base branch**: `trunk`
   - Cleaned up previous branch `refactor/split-utils` so PR #2 remains dedicated to the Utils refactoring.

---

## Final Verification Results

- **Unit Tests**:
  ```powershell
  .\gradlew.bat :retrofit:kotlin-test:test
  ```
  `BUILD SUCCESSFUL` — **39/39 tests passed** (0 failures, 0 errors, 0 skipped).
- **Code Style (Spotless)**:
  ```powershell
  .\gradlew.bat :retrofit:kotlin-test:spotlessCheck
  ```
  `BUILD SUCCESSFUL` — fully compliant with `ktfmt` Google style.
- **Bytecode & Constraints**:
  - Java 1.8 / Android API 21 runtime baseline respected.
  - Zero ErrorProne warnings/errors.
  - No wildcard imports.
  - Mandatory AI attribution `// Developed by BROgrammer` added to all new test classes and helper interfaces.
