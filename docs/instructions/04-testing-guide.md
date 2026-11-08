# 04. Testing Guidelines

This document outlines testing practices, frameworks, and patterns used across the Retrofit codebase.

---

## 1. Test Stack

- **Runner & Framework**: JUnit 4 (`junit.framework.TestCase` / `org.junit.Test`, `@Rule`, `@Before`).
- **Assertions**: Google Truth (`import static com.google.truth.Truth.assertThat;`). Avoid standard JUnit assertions (`assertEquals`) in favor of Truth's readable, fluent API.
- **HTTP Mocking**: OkHttp's `MockWebServer` (`okhttp3.mockwebserver.MockWebServer`, `MockResponse`).
- **Compiler / Annotation Testing**: Google `compile-testing` (`com.google.testing.compile`) and `test-parameter-injector`.

---

## 2. Standard Unit Test Pattern with MockWebServer

Most Retrofit tests verify request building, headers, URL resolution, and response deserialization using `MockWebServer`.

```java
public final class ExampleServiceTest {
  @Rule public final MockWebServer server = new MockWebServer();

  interface ExampleService {
    @GET("/users/{user}/repos")
    Call<List<Repo>> listRepos(@Path("user") String user);
  }

  private ExampleService service;

  @Before public void setUp() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    service = retrofit.create(ExampleService.class);
  }

  @Test public void testListRepos() throws Exception {
    server.enqueue(new MockResponse().setBody("[{\"name\":\"retrofit\"}]"));

    Response<List<Repo>> response = service.listRepos("square").execute();
    assertThat(response.isSuccessful()).isTrue();
    assertThat(response.body()).hasSize(1);
    assertThat(response.body().get(0).name).isEqualTo("retrofit");

    RecordedRequest request = server.takeRequest();
    assertThat(request.getPath()).isEqualTo("/users/square/repos");
    assertThat(request.getMethod()).isEqualTo("GET");
  }
}
```

---

## 3. Testing Exception Handling & ErrorProne Compliance

When asserting that a call throws an expected exception, be aware of ErrorProne's `MissingFail` rule:

```java
// Preferred approach using assertThrows (Truth):
IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
  retrofit.create(InvalidInterface.class);
});
assertThat(e).hasMessageThat().contains("HTTP method annotation required");

// If using try-catch:
try {
  retrofit.create(InvalidInterface.class);
  fail("Expected IllegalArgumentException");
} catch (IllegalArgumentException expected) {
  assertThat(expected).hasMessageThat().contains("HTTP method annotation required");
}
```

> **Note**: In `try/catch` blocks, always call `fail()` at the end of the `try` block, and name the caught exception `expected` to comply with ErrorProne's `UnusedException` rule.

---

## 4. Converter & Adapter Tests

- When adding or modifying a converter in `retrofit-converters/<format>`, add test cases verifying:
  1. Serialization of request bodies (including empty, nested, and special characters).
  2. Deserialization of response bodies.
  3. Handling of HTTP error responses (4xx, 5xx).
  4. Proper closing of response streams (`ResponseBody.close()`).
- When modifying adapters in `retrofit-adapters/<type>`, verify cancellation propagation, synchronous and asynchronous execution, and thread delivery where applicable.

---

## 5. Bug Reproducer Policy

Whenever investigating or fixing an issue:
1. First, write an automated test that reproduces the bug and demonstrates the failure.
2. Ensure the failure matches the observed bug behavior.
3. Apply the minimal necessary fix in production code.
4. Verify that the new test and all existing test suites pass:
   ```bash
   ./gradlew test
   ```
