Kotlin Flow Adapter
===================

A `CallAdapter.Factory` for Kotlin Coroutines `Flow<T>` types.

Available types:

 * `Flow<T>` where `T` is the response body type.
 * `Flow<Response<T>>` where `T` is the response body type.

Usage
-----

Add `FlowCallAdapterFactory` as a `CallAdapter` factory when building your `Retrofit` instance:

```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("https://example.com/")
    .addCallAdapterFactory(FlowCallAdapterFactory.create())
    .build()
```

Your service methods can now return `Flow<T>` or `Flow<Response<T>>`:

```kotlin
interface MyService {
  @GET("/user")
  fun getUser(): Flow<User>

  @GET("/user")
  fun getUserResponse(): Flow<Response<User>>
}
```

By default, the returned `Flow` is cold and executes asynchronously when collected. Each collection creates and executes a fresh HTTP call (`call.clone()`). When the collecting coroutine is cancelled, the underlying HTTP call is automatically cancelled.
