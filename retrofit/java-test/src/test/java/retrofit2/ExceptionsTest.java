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
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Method;
import org.junit.Test;

// Developed by BROgrammer
public final class ExceptionsTest {
  interface SampleService {
    void sampleMethod(String param);
  }

  @Test
  public void methodErrorFormatsMessage() throws NoSuchMethodException {
    Method method = SampleService.class.getDeclaredMethod("sampleMethod", String.class);
    RuntimeException exception = Exceptions.methodError(method, "Failed %s", "reason");

    assertThat(exception).isInstanceOf(IllegalArgumentException.class);
    assertThat(exception)
        .hasMessageThat()
        .isEqualTo("Failed reason\n    for method SampleService.sampleMethod");
  }

  @Test
  public void methodErrorWithCause() throws NoSuchMethodException {
    Method method = SampleService.class.getDeclaredMethod("sampleMethod", String.class);
    Throwable cause = new IOException("root cause");
    RuntimeException exception = Exceptions.methodError(method, cause, "Failed %s", "reason");

    assertThat(exception).isInstanceOf(IllegalArgumentException.class);
    assertThat(exception.getCause()).isSameInstanceAs(cause);
    assertThat(exception)
        .hasMessageThat()
        .isEqualTo("Failed reason\n    for method SampleService.sampleMethod");
  }

  @Test
  public void parameterErrorFormatsMessage() throws NoSuchMethodException {
    Method method = SampleService.class.getDeclaredMethod("sampleMethod", String.class);
    RuntimeException exception = Exceptions.parameterError(method, 0, "Invalid param");

    assertThat(exception).isInstanceOf(IllegalArgumentException.class);
    assertThat(exception).hasMessageThat().contains("Invalid param");
    assertThat(exception).hasMessageThat().contains("for method SampleService.sampleMethod");
  }

  @Test
  public void parameterErrorWithCause() throws NoSuchMethodException {
    Method method = SampleService.class.getDeclaredMethod("sampleMethod", String.class);
    Throwable cause = new IOException("param cause");
    RuntimeException exception = Exceptions.parameterError(method, cause, 0, "Invalid param");

    assertThat(exception).isInstanceOf(IllegalArgumentException.class);
    assertThat(exception.getCause()).isSameInstanceAs(cause);
    assertThat(exception).hasMessageThat().contains("Invalid param");
    assertThat(exception).hasMessageThat().contains("for method SampleService.sampleMethod");
  }

  @Test
  public void throwIfFatalRethrowsVirtualMachineError() {
    try {
      Exceptions.throwIfFatal(new OutOfMemoryError());
      fail();
    } catch (VirtualMachineError expected) {
      assertThat(expected).isNotNull();
    }
  }

  @Test
  public void throwIfFatalRethrowsLinkageError() {
    try {
      Exceptions.throwIfFatal(new LinkageError());
      fail();
    } catch (LinkageError expected) {
      assertThat(expected).isNotNull();
    }
  }

  @Test
  @SuppressWarnings("removal")
  public void throwIfFatalRethrowsThreadDeath() {
    try {
      Exceptions.throwIfFatal(new ThreadDeath());
      fail();
    } catch (ThreadDeath expected) {
      assertThat(expected).isNotNull();
    }
  }

  @Test
  public void throwIfFatalIgnoresNonFatalExceptions() {
    Exceptions.throwIfFatal(new IllegalArgumentException());
    Exceptions.throwIfFatal(new IOException());
    Exceptions.throwIfFatal(new Exception());
  }
}
