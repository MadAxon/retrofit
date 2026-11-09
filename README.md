Retrofit
========

A type-safe HTTP client for Android and Java/Kotlin.

Retrofit turns your HTTP API into a Java/Kotlin interface. Instead of manually constructing URLs, parsing raw JSON, managing network sockets, and orchestrating threads, you define your API methods as an annotated interface and Retrofit handles the rest.

For more information please see [the website][1].


Introduction & How It Works
----------------------------

In modern applications, communicating with backend REST APIs involves:
1. Constructing request URLs with paths and query parameters.
2. Serializing request objects into JSON/XML formats.
3. Sending HTTP requests and receiving responses via a networking engine.
4. Deserializing raw responses into strongly-typed domain models.
5. Handling asynchronous execution, background threading, and errors.

Retrofit abstracts this entire pipeline behind intuitive interfaces using four core components:

* **Declarative API Interfaces & Annotations**: Define HTTP methods (`@GET`, `@POST`, `@PUT`, `@DELETE`, etc.) and parameters (`@Path`, `@Query`, `@Body`, `@Header`, `@Multipart`, etc.) directly on interface methods.
* **Type-Safe Dynamic Proxies**: Retrofit dynamically generates implementations of your interfaces at runtime.
* **Pluggable Converters**: Automatically serialize and deserialize data formats between HTTP bodies and your classes. Out of the box, converters are available for **Moshi**, **Gson**, **Jackson**, **kotlinx.serialization**, **Protocol Buffers**, **Wire**, **JAXB**, and raw scalars.
* **Call Adapters**: Choose your preferred asynchronous execution model: standard callbacks (`Call<T>`), Kotlin coroutines (`suspend fun`), `CompletableFuture`, or reactive streams (`RxJava`, `Guava`).
* **Powered by OkHttp**: Under the hood, Retrofit delegates robust connection pooling, HTTP/2, caching, and interceptors to Square's OkHttp.


Common Use Cases
----------------

| Scenario | Example | Description |
| :--- | :--- | :--- |
| **Fetch Data (`@GET`)** | `@GET("users") suspend fun getUsers(): List<User>` | Querying resources, news feeds, catalogs. |
| **Path Replacement (`@Path`)** | `@GET("users/{id}") suspend fun getUser(@Path("id") id: String): User` | Dynamically targeting specific resource IDs (`/users/42`). |
| **Query Parameters (`@Query`)** | `@GET("search") suspend fun search(@Query("q") query: String, @Query("page") page: Int): SearchResult` | Filtering and pagination (`/search?q=retrofit&page=1`). |
| **Send Data (`@POST` & `@Body`)** | `@POST("auth/login") suspend fun login(@Body req: LoginRequest): AuthResponse` | Authentication, creating records, submitting forms as JSON payloads. |
| **Request Headers (`@Header`)** | `@GET("profile") suspend fun getProfile(@Header("Authorization") token: String): Profile` | Passing authorization tokens, custom user-agent, or cache policies. |
| **File Uploads (`@Multipart`)** | `@Multipart @POST("upload") suspend fun uploadFile(@Part file: MultipartBody.Part): UploadResponse` | Uploading images, documents, or mixed payloads. |


Quick Start
-----------

### 1. Define your API interface

```kotlin
data class Repo(val name: String, val description: String?)

interface GitHubService {
  @GET("users/{user}/repos")
  suspend fun listRepos(@Path("user") user: String): List<Repo>
}
```

Or in Java:

```java
public interface GitHubService {
  @GET("users/{user}/repos")
  Call<List<Repo>> listRepos(@Path("user") String user);
}
```

### 2. Create the Retrofit instance & execute calls

```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("https://api.github.com/")
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

val service = retrofit.create(GitHubService::class.java)

// Execute asynchronously via coroutines:
val repos = service.listRepos("square")
```


Download
--------

Download [the latest JAR][2] or grab from Maven central at the coordinates `com.squareup.retrofit2:retrofit:3.0.0`.

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].

Retrofit requires at minimum Java 8+ or Android API 21+.


R8 / ProGuard
-------------

If you are using R8 the shrinking and obfuscation rules are included automatically.

ProGuard users must manually add the options from
[retrofit2.pro][proguard file].
You might also need [rules for OkHttp][okhttp proguard] which is a dependency of this library.


Development & Contributing
==========================

We welcome contributions from both human engineers and AI coding agents:

* **Contributing Guide**: See [CONTRIBUTING.md](CONTRIBUTING.md) for our contribution workflow and AI policy.
* **AI Agent & Developer Instructions**: See [AGENTS.md](AGENTS.md) for fast-path commands, invariants, and monorepo overview.
* **Detailed Technical Guides**: See [docs/instructions/](docs/instructions/) for modular documentation on architecture, build commands, code style, testing, and git conventions.


License
=======

    Copyright 2013 Square, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


 [1]: https://lysine.dev/retrofit/
 [2]: https://search.maven.org/remote_content?g=com.squareup.retrofit2&a=retrofit&v=LATEST
 [snap]: https://s01.oss.sonatype.org/content/repositories/snapshots/
 [proguard file]: https://github.com/lysine-dev/retrofit/blob/master/retrofit/src/main/resources/META-INF/proguard/retrofit2.pro
 [okhttp proguard]: https://lysine.dev/okhttp/r8_proguard/
 [okio proguard]: https://lysine.dev/okio/#r8-proguard
