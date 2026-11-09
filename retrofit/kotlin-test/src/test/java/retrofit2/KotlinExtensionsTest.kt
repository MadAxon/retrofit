/*
 * Copyright (C) 2018 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrofit2

import com.google.common.truth.Truth.assertThat
import java.io.IOException
import java.lang.reflect.Type
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AFTER_REQUEST
import okhttp3.mockwebserver.SocketPolicy.NO_RESPONSE
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import retrofit2.helpers.ToStringConverterFactory
import retrofit2.http.GET
import retrofit2.http.HEAD

class KotlinExtensionsTest {
  @get:Rule val server = MockWebServer()

  interface Empty

  // Developed by BROgrammer
  interface Service {
    @GET("/") fun body(): Call<String>

    @GET("/") fun bodyNullable(): Call<String?>

    @GET("/") fun unit(): Call<Unit>

    @HEAD("/") fun headUnit(): Call<Unit>

    @GET("/") fun response(): Call<String>
  }

  // Developed by BROgrammer
  private class CallWithoutInvocation : Call<String> {
    override fun clone(): Call<String> = this

    override fun execute(): Response<String> = Response.success(null)

    override fun enqueue(callback: Callback<String>) {
      callback.onResponse(this, Response.success(null))
    }

    override fun isExecuted(): Boolean = true

    override fun cancel() {}

    override fun isCanceled(): Boolean = false

    override fun request(): Request = Request.Builder().url("http://localhost/").build()

    override fun timeout(): okio.Timeout = okio.Timeout.NONE
  }

  // Developed by BROgrammer
  private class FailingCall(private val failure: Throwable) : Call<String> {
    override fun clone(): Call<String> = this

    override fun execute(): Response<String> = throw failure

    override fun enqueue(callback: Callback<String>) {
      callback.onFailure(this, failure)
    }

    override fun isExecuted(): Boolean = true

    override fun cancel() {}

    override fun isCanceled(): Boolean = false

    override fun request(): Request = Request.Builder().url("http://localhost/").build()

    override fun timeout(): okio.Timeout = okio.Timeout.NONE
  }

  // Developed by BROgrammer
  private class NullReturningConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
      type: Type,
      annotations: Array<Annotation>,
      retrofit: Retrofit,
    ): Converter<ResponseBody, *> {
      return Converter<ResponseBody, Any?> { null }
    }
  }

  @Test
  fun reifiedCreate() {
    val retrofit = Retrofit.Builder().baseUrl(server.url("/")).build()

    assertThat(retrofit.create<Empty>()).isNotNull()
  }

  @Test
  fun awaitReturnsBody() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setBody("Hi"))

    val body = runBlocking { service.body().await() }
    assertThat(body).isEqualTo("Hi")

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/")
  }

  @Test
  fun awaitThrowsOnNullBody() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setResponseCode(204))

    try {
      runBlocking { service.body().await() }
      fail()
    } catch (expected: KotlinNullPointerException) {
      val message = expected.message ?: (expected.cause as KotlinNullPointerException).message
      assertThat(message)
        .isEqualTo(
          "Response from retrofit2.KotlinExtensionsTest\$Service.body was null but response body type was declared as non-null"
        )
    }
  }

  @Test
  fun awaitThrowsOnHttp200WithNullBody() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(NullReturningConverterFactory())
        .build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setResponseCode(200).setBody("hello"))

    try {
      runBlocking { service.body().await() }
      fail()
    } catch (expected: KotlinNullPointerException) {
      val message = expected.message ?: (expected.cause as KotlinNullPointerException).message
      assertThat(message)
        .isEqualTo(
          "Response from retrofit2.KotlinExtensionsTest\$Service.body was null but response body type was declared as non-null"
        )
    }

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/")
  }

  @Test
  fun awaitThrowsHttpException() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setResponseCode(404).setBody("Not Found"))

    try {
      runBlocking { service.body().await() }
      fail()
    } catch (expected: HttpException) {
      assertThat(expected.code()).isEqualTo(404)
      assertThat(expected.response()?.errorBody()?.string()).isEqualTo("Not Found")
    }
  }

  @Test
  fun awaitThrowsHttpException500() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setResponseCode(500).setBody("Server Error"))

    try {
      runBlocking { service.body().await() }
      fail()
    } catch (expected: HttpException) {
      assertThat(expected.code()).isEqualTo(500)
      assertThat(expected.response()?.errorBody()?.string()).isEqualTo("Server Error")
    }
  }

  @Test
  fun awaitThrowsIoException() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST))

    try {
      runBlocking { service.body().await() }
      fail()
    } catch (expected: IOException) {
      assertThat(expected).hasMessageThat().contains("unexpected end of stream")
    }
  }

  @Test
  fun awaitPropagatesExactFailure() {
    val failure = IOException("Custom network error")
    val call = FailingCall(failure)

    try {
      runBlocking { call.await() }
      fail()
    } catch (expected: IOException) {
      val actual = expected.cause ?: expected
      assertThat(actual).isSameInstanceAs(failure)
      assertThat(expected).hasMessageThat().isEqualTo("Custom network error")
    }
  }

  @Test
  fun awaitCancellationWorks() {
    lateinit var rawCall: okhttp3.Call

    val okHttpClient = OkHttpClient()
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .callFactory {
          val newCall = okHttpClient.newCall(it)
          rawCall = newCall
          newCall
        }
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()
    val call = service.body()

    server.enqueue(MockResponse().setSocketPolicy(NO_RESPONSE))

    val deferred = CoroutineScope(Dispatchers.Default).async { call.await() }

    server.takeRequest()
    deferred.cancel()

    assertThat(call.isCanceled).isTrue()
    assertThat(rawCall.isCanceled()).isTrue()
  }

  @Test
  fun awaitThrowsOnAlreadyCanceledCall() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()
    val call = service.body()
    call.cancel()
    assertThat(call.isCanceled).isTrue()

    try {
      runBlocking { call.await() }
      fail()
    } catch (expected: IOException) {
      assertThat(expected).hasMessageThat().contains("Canceled")
    }
  }

  @Test
  fun awaitThrowsOnAlreadyExecutedCall() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()
    val call = service.body()

    server.enqueue(MockResponse().setBody("First"))

    val first = runBlocking { call.await() }
    assertThat(first).isEqualTo("First")
    assertThat(call.isExecuted).isTrue()

    try {
      runBlocking { call.await() }
      fail()
    } catch (expected: IllegalStateException) {
      assertThat(expected).hasMessageThat().isEqualTo("Already executed.")
    }
  }

  @Test
  fun awaitThrowsWhenCallAlreadyExecutedSynchronously() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()
    val call = service.body()

    server.enqueue(MockResponse().setBody("Sync"))

    val syncResponse = call.execute()
    assertThat(syncResponse.isSuccessful).isTrue()
    assertThat(call.isExecuted).isTrue()

    try {
      runBlocking { call.await() }
      fail()
    } catch (expected: IllegalStateException) {
      assertThat(expected).hasMessageThat().isEqualTo("Already executed.")
    }
  }

  @Test
  fun awaitClonedCallSucceedsAfterOriginalExecuted() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()
    val call = service.body()

    server.enqueue(MockResponse().setBody("First"))
    server.enqueue(MockResponse().setBody("Second"))

    val first = runBlocking { call.await() }
    assertThat(first).isEqualTo("First")

    val cloned: Call<String> = call.clone()
    val second = runBlocking { cloned.await() }
    assertThat(second).isEqualTo("Second")
  }

  @Test
  fun awaitThrowsNullPointerExceptionWhenInvocationMissing() {
    val call = CallWithoutInvocation()

    try {
      runBlocking { call.await() }
      fail()
    } catch (expected: NullPointerException) {
      assertThat(expected).isNotInstanceOf(KotlinNullPointerException::class.java)
      assertThat(expected.message).isNull()
    }
  }

  @Test
  fun awaitNullableReturnsBody() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setBody("Hi"))

    val body = runBlocking { service.bodyNullable().await() }
    assertThat(body).isEqualTo("Hi")

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/")
  }

  @Test
  fun awaitNullableReturnsNull() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setResponseCode(204))

    val body = runBlocking { service.bodyNullable().await() }
    assertThat(body).isNull()
  }

  @Test
  fun awaitNullableReturnsNullOnHttp200WithNullBody() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(NullReturningConverterFactory())
        .build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setResponseCode(200).setBody("hello"))

    val body = runBlocking { service.bodyNullable().await() }
    assertThat(body).isNull()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/")
  }

  @Test
  fun awaitNullableThrowsHttpException() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setResponseCode(404).setBody("Not Found"))

    try {
      runBlocking { service.bodyNullable().await() }
      fail()
    } catch (expected: HttpException) {
      assertThat(expected.code()).isEqualTo(404)
      assertThat(expected.response()?.errorBody()?.string()).isEqualTo("Not Found")
    }
  }

  @Test
  fun awaitNullableThrowsHttpException500() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setResponseCode(500).setBody("Server Error"))

    try {
      runBlocking { service.bodyNullable().await() }
      fail()
    } catch (expected: HttpException) {
      assertThat(expected.code()).isEqualTo(500)
      assertThat(expected.response()?.errorBody()?.string()).isEqualTo("Server Error")
    }
  }

  @Test
  fun awaitNullableThrowsIoException() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST))

    try {
      runBlocking { service.bodyNullable().await() }
      fail()
    } catch (expected: IOException) {
      assertThat(expected).hasMessageThat().contains("unexpected end of stream")
    }
  }

  @Test
  fun awaitNullableThrowsOnAlreadyCanceledCall() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()
    val call = service.bodyNullable()
    call.cancel()
    assertThat(call.isCanceled).isTrue()

    try {
      runBlocking { call.await() }
      fail()
    } catch (expected: IOException) {
      assertThat(expected).hasMessageThat().contains("Canceled")
    }
  }

  @Test
  fun awaitNullableCancellationWorks() {
    lateinit var rawCall: okhttp3.Call

    val okHttpClient = OkHttpClient()
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .callFactory {
          val newCall = okHttpClient.newCall(it)
          rawCall = newCall
          newCall
        }
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()
    val call = service.bodyNullable()

    server.enqueue(MockResponse().setSocketPolicy(NO_RESPONSE))

    val deferred = CoroutineScope(Dispatchers.Default).async { call.await() }

    server.takeRequest()
    deferred.cancel()

    assertThat(call.isCanceled).isTrue()
    assertThat(rawCall.isCanceled()).isTrue()
  }

  @Test
  fun awaitUnitSuccess() {
    val retrofit = Retrofit.Builder().baseUrl(server.url("/")).build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setBody("Unit"))

    val result = runBlocking { service.unit().await() }
    assertThat(result).isEqualTo(Unit)

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/")
    assertThat(request.method).isEqualTo("GET")
  }

  @Test
  fun awaitUnit204Success() {
    val retrofit = Retrofit.Builder().baseUrl(server.url("/")).build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setResponseCode(204))

    val result = runBlocking { service.unit().await() }
    assertThat(result).isEqualTo(Unit)

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/")
    assertThat(request.method).isEqualTo("GET")
  }

  @Test
  fun awaitUnitHeadSuccess() {
    val retrofit = Retrofit.Builder().baseUrl(server.url("/")).build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse())

    val result = runBlocking { service.headUnit().await() }
    assertThat(result).isEqualTo(Unit)

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/")
    assertThat(request.method).isEqualTo("HEAD")
  }

  @Test
  fun awaitUnitThrowsHttpException() {
    val retrofit = Retrofit.Builder().baseUrl(server.url("/")).build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setResponseCode(404).setBody("Not Found"))

    try {
      runBlocking { service.unit().await() }
      fail()
    } catch (expected: HttpException) {
      assertThat(expected.code()).isEqualTo(404)
      assertThat(expected.response()?.errorBody()?.string()).isEqualTo("Not Found")
    }
  }

  @Test
  fun awaitUnitThrowsHttpException500() {
    val retrofit = Retrofit.Builder().baseUrl(server.url("/")).build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setResponseCode(500).setBody("Server Error"))

    try {
      runBlocking { service.unit().await() }
      fail()
    } catch (expected: HttpException) {
      assertThat(expected.code()).isEqualTo(500)
      assertThat(expected.response()?.errorBody()?.string()).isEqualTo("Server Error")
    }
  }

  @Test
  fun awaitUnitThrowsIoException() {
    val retrofit = Retrofit.Builder().baseUrl(server.url("/")).build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST))

    try {
      runBlocking { service.unit().await() }
      fail()
    } catch (expected: IOException) {
      assertThat(expected).hasMessageThat().contains("unexpected end of stream")
    }
  }

  @Test
  fun awaitUnitThrowsOnAlreadyCanceledCall() {
    val retrofit = Retrofit.Builder().baseUrl(server.url("/")).build()
    val service = retrofit.create<Service>()
    val call = service.unit()
    call.cancel()
    assertThat(call.isCanceled).isTrue()

    try {
      runBlocking { call.await() }
      fail()
    } catch (expected: IOException) {
      assertThat(expected).hasMessageThat().contains("Canceled")
    }
  }

  @Test
  fun awaitUnitCancellationWorks() {
    lateinit var rawCall: okhttp3.Call

    val okHttpClient = OkHttpClient()
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .callFactory {
          val newCall = okHttpClient.newCall(it)
          rawCall = newCall
          newCall
        }
        .build()
    val service = retrofit.create<Service>()
    val call = service.unit()

    server.enqueue(MockResponse().setSocketPolicy(NO_RESPONSE))

    val deferred = CoroutineScope(Dispatchers.Default).async { call.await() }

    server.takeRequest()
    deferred.cancel()

    assertThat(call.isCanceled).isTrue()
    assertThat(rawCall.isCanceled()).isTrue()
  }

  @Test
  fun awaitResponseReturns200() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setBody("Hi"))

    val response = runBlocking { service.response().awaitResponse() }
    assertThat(response.isSuccessful).isTrue()
    assertThat(response.code()).isEqualTo(200)
    assertThat(response.body()).isEqualTo("Hi")
  }

  @Test
  fun awaitResponseReturns200WithNullBody() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(NullReturningConverterFactory())
        .build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setResponseCode(200).setBody("hello"))

    val response = runBlocking { service.response().awaitResponse() }
    assertThat(response.isSuccessful).isTrue()
    assertThat(response.code()).isEqualTo(200)
    assertThat(response.body()).isNull()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/")
  }

  @Test
  fun awaitResponseReturns204() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setResponseCode(204))

    val response = runBlocking { service.response().awaitResponse() }
    assertThat(response.isSuccessful).isTrue()
    assertThat(response.code()).isEqualTo(204)
    assertThat(response.body()).isNull()
  }

  @Test
  fun awaitResponseReturns404WithoutThrowing() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setResponseCode(404).setBody("Not Found"))

    val response = runBlocking { service.response().awaitResponse() }
    assertThat(response.isSuccessful).isFalse()
    assertThat(response.code()).isEqualTo(404)
    assertThat(response.errorBody()?.string()).isEqualTo("Not Found")
  }

  @Test
  fun awaitResponseReturns500WithoutThrowing() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setResponseCode(500).setBody("Server Error"))

    val response = runBlocking { service.response().awaitResponse() }
    assertThat(response.isSuccessful).isFalse()
    assertThat(response.code()).isEqualTo(500)
    assertThat(response.errorBody()?.string()).isEqualTo("Server Error")
  }

  @Test
  fun awaitResponseThrowsIoException() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()

    server.enqueue(MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST))

    try {
      runBlocking { service.response().awaitResponse() }
      fail()
    } catch (expected: IOException) {
      assertThat(expected).hasMessageThat().contains("unexpected end of stream")
    }
  }

  @Test
  fun awaitResponseThrowsOnAlreadyCanceledCall() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()
    val call = service.response()
    call.cancel()
    assertThat(call.isCanceled).isTrue()

    try {
      runBlocking { call.awaitResponse() }
      fail()
    } catch (expected: IOException) {
      assertThat(expected).hasMessageThat().contains("Canceled")
    }
  }

  @Test
  fun awaitResponseThrowsOnAlreadyExecutedCall() {
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()
    val call = service.response()

    server.enqueue(MockResponse().setBody("First"))

    val first = runBlocking { call.awaitResponse() }
    assertThat(first.isSuccessful).isTrue()
    assertThat(call.isExecuted).isTrue()

    try {
      runBlocking { call.awaitResponse() }
      fail()
    } catch (expected: IllegalStateException) {
      assertThat(expected).hasMessageThat().isEqualTo("Already executed.")
    }
  }

  @Test
  fun awaitResponseCancellationWorks() {
    lateinit var rawCall: okhttp3.Call

    val okHttpClient = OkHttpClient()
    val retrofit =
      Retrofit.Builder()
        .baseUrl(server.url("/"))
        .callFactory {
          val newCall = okHttpClient.newCall(it)
          rawCall = newCall
          newCall
        }
        .addConverterFactory(ToStringConverterFactory())
        .build()
    val service = retrofit.create<Service>()
    val call = service.response()

    server.enqueue(MockResponse().setSocketPolicy(NO_RESPONSE))

    val deferred = CoroutineScope(Dispatchers.Default).async { call.awaitResponse() }

    server.takeRequest()
    deferred.cancel()

    assertThat(call.isCanceled).isTrue()
    assertThat(rawCall.isCanceled()).isTrue()
  }
}
