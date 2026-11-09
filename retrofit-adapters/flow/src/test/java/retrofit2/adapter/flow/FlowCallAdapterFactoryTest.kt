/*
 * Copyright (C) 2026 Square, Inc.
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
package retrofit2.adapter.flow

import com.google.common.truth.Truth.assertThat
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AFTER_REQUEST
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.helpers.ToStringConverterFactory
import retrofit2.http.GET

// Developed by BROgrammer
class FlowCallAdapterFactoryTest {
  @get:Rule val server = MockWebServer()

  // Developed by BROgrammer
  interface Service {
    @GET("/") fun body(): Flow<String>

    @GET("/") fun response(): Flow<Response<String>>

    @GET("/") fun unit(): Flow<Unit>
  }

  // Developed by BROgrammer
  private class TestingParameterizedType(
    private val rawType: Type,
    private val actualTypeArguments: Array<Type>,
  ) : ParameterizedType {
    override fun getRawType(): Type = rawType

    override fun getOwnerType(): Type? = null

    override fun getActualTypeArguments(): Array<Type> = actualTypeArguments
  }

  private fun createRetrofit(client: OkHttpClient = OkHttpClient()): Retrofit {
    return Retrofit.Builder()
      .baseUrl(server.url("/"))
      .client(client)
      .addConverterFactory(ToStringConverterFactory())
      .addCallAdapterFactory(FlowCallAdapterFactory.create())
      .build()
  }

  @Test
  fun bodySuccess() = runBlocking {
    val service = createRetrofit().create(Service::class.java)
    server.enqueue(MockResponse().setBody("Hello World"))

    val result = service.body().first()
    assertThat(result).isEqualTo("Hello World")
  }

  @Test
  fun body404ThrowsHttpException() = runBlocking {
    val service = createRetrofit().create(Service::class.java)
    server.enqueue(MockResponse().setResponseCode(404).setBody("Not Found"))

    try {
      service.body().first()
      fail("Expected HttpException")
    } catch (expected: HttpException) {
      assertThat(expected.code()).isEqualTo(404)
    }
  }

  @Test
  fun bodyFailureThrowsIOException() = runBlocking {
    val service = createRetrofit().create(Service::class.java)
    server.enqueue(MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST))

    try {
      service.body().first()
      fail("Expected IOException")
    } catch (expected: IOException) {
      // Expected
    }
  }

  @Test
  fun bodyThrowsOnNull() = runBlocking {
    val service = createRetrofit().create(Service::class.java)
    server.enqueue(MockResponse().setResponseCode(204))

    try {
      service.body().first()
      fail("Expected KotlinNullPointerException")
    } catch (expected: KotlinNullPointerException) {
      val message = expected.message ?: (expected.cause as? KotlinNullPointerException)?.message
      assertThat(message).contains("was null but response body type was declared as non-null")
    }
  }

  @Test
  fun responseSuccess() = runBlocking {
    val service = createRetrofit().create(Service::class.java)
    server.enqueue(MockResponse().setBody("Hello Response"))

    val response = service.response().first()
    assertThat(response.isSuccessful).isTrue()
    assertThat(response.code()).isEqualTo(200)
    assertThat(response.body()).isEqualTo("Hello Response")
  }

  @Test
  fun response404DoesNotThrow() = runBlocking {
    val service = createRetrofit().create(Service::class.java)
    server.enqueue(MockResponse().setResponseCode(404).setBody("Not Found"))

    val response = service.response().first()
    assertThat(response.isSuccessful).isFalse()
    assertThat(response.code()).isEqualTo(404)
    assertThat(response.errorBody()?.string()).isEqualTo("Not Found")
  }

  @Test
  fun responseFailureThrowsIOException() = runBlocking {
    val service = createRetrofit().create(Service::class.java)
    server.enqueue(MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST))

    try {
      service.response().first()
      fail("Expected IOException")
    } catch (expected: IOException) {
      // Expected
    }
  }

  @Test
  fun unitSuccess() = runBlocking {
    val service = createRetrofit().create(Service::class.java)
    server.enqueue(MockResponse().setResponseCode(204))

    val result = service.unit().first()
    assertThat(result).isEqualTo(Unit)
  }

  @Test
  fun coldStreamExecutesPerCollect() = runBlocking {
    val service = createRetrofit().create(Service::class.java)
    server.enqueue(MockResponse().setBody("First"))
    server.enqueue(MockResponse().setBody("Second"))

    val flow = service.body()
    // No request made yet
    assertThat(server.requestCount).isEqualTo(0)

    val first = flow.first()
    assertThat(first).isEqualTo("First")
    assertThat(server.requestCount).isEqualTo(1)

    val second = flow.first()
    assertThat(second).isEqualTo("Second")
    assertThat(server.requestCount).isEqualTo(2)
  }

  @Test
  fun cancellationCancelsOkHttpCall() = runBlocking {
    val client = OkHttpClient()
    val service = createRetrofit(client).create(Service::class.java)

    val job =
      launch(Dispatchers.IO) {
        service.body().first()
      }

    while (client.dispatcher.runningCalls().isEmpty()) {
      Thread.sleep(10)
    }

    val calls = client.dispatcher.runningCalls()
    assertThat(calls).hasSize(1)
    val okHttpCall = calls[0]
    assertThat(okHttpCall.isCanceled()).isFalse()

    job.cancel()
    job.join()

    assertThat(okHttpCall.isCanceled()).isTrue()
  }

  @Test
  fun rawFlowTypeThrows() {
    val factory = FlowCallAdapterFactory.create()
    val retrofit = createRetrofit()
    try {
      factory.get(Flow::class.java, emptyArray(), retrofit)
      fail("Expected IllegalStateException")
    } catch (expected: IllegalStateException) {
      assertThat(expected)
        .hasMessageThat()
        .isEqualTo("Flow return type must be parameterized as Flow<Foo> or Flow<? extends Foo>")
    }
  }

  @Test
  fun rawResponseTypeThrows() {
    val factory = FlowCallAdapterFactory.create()
    val retrofit = createRetrofit()
    val rawResponseType = TestingParameterizedType(Flow::class.java, arrayOf(Response::class.java))
    try {
      factory.get(rawResponseType, emptyArray(), retrofit)
      fail("Expected IllegalStateException")
    } catch (expected: IllegalStateException) {
      assertThat(expected)
        .hasMessageThat()
        .isEqualTo("Response must be parameterized as Response<Foo> or Response<? extends Foo>")
    }
  }

  @Test
  fun nonFlowTypeReturnsNull() {
    val factory = FlowCallAdapterFactory.create()
    val retrofit = createRetrofit()
    val adapter = factory.get(String::class.java, emptyArray(), retrofit)
    assertThat(adapter).isNull()
  }
}
