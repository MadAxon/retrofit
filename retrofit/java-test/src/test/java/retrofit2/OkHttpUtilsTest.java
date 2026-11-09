/*
 * Copyright (C) 2013 Square, Inc.
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
package retrofit2;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.Test;

// Developed by BROgrammer
public final class OkHttpUtilsTest {
  @Test
  public void bufferBuffersResponseBody() throws IOException {
    MediaType mediaType = MediaType.get("text/plain; charset=utf-8");
    ResponseBody body = ResponseBody.create(mediaType, "hello world");
    ResponseBody buffered = OkHttpUtils.buffer(body);

    assertThat(buffered.string()).isEqualTo("hello world");
    assertThat(buffered.contentType()).isEqualTo(mediaType);
    assertThat(buffered.contentLength()).isEqualTo(11);
  }

  @Test
  public void bufferEmptyBody() throws IOException {
    ResponseBody body = ResponseBody.create((MediaType) null, new byte[0]);
    ResponseBody buffered = OkHttpUtils.buffer(body);

    assertThat(buffered.bytes()).hasLength(0);
    assertThat(buffered.contentLength()).isEqualTo(0);
  }
}
