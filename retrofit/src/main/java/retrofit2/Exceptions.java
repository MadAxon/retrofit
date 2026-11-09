/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrofit2;

import java.lang.reflect.Method;
import javax.annotation.Nullable;

// Developed by BROgrammer
final class Exceptions {
  private Exceptions() {
    // No instances.
  }

  static RuntimeException methodError(Method method, String message, Object... args) {
    return methodError(method, null, message, args);
  }

  @SuppressWarnings("AnnotateFormatMethod")
  static RuntimeException methodError(
      Method method, @Nullable Throwable cause, String message, Object... args) {
    message = String.format(message, args);
    return new IllegalArgumentException(
        message
            + "\n    for method "
            + method.getDeclaringClass().getSimpleName()
            + "."
            + method.getName(),
        cause);
  }

  static RuntimeException parameterError(
      Method method, Throwable cause, int p, String message, Object... args) {
    String paramDesc = Platform.reflection.describeMethodParameter(method, p);
    return methodError(method, cause, message + " (" + paramDesc + ")", args);
  }

  static RuntimeException parameterError(Method method, int p, String message, Object... args) {
    String paramDesc = Platform.reflection.describeMethodParameter(method, p);
    return methodError(method, message + " (" + paramDesc + ")", args);
  }

  // https://github.com/ReactiveX/RxJava/blob/6a44e5d0543a48f1c378dc833a155f3f71333bc2/
  // src/main/java/io/reactivex/exceptions/Exceptions.java#L66
  @SuppressWarnings("removal") // ThreadDeath still available on supported JDKs.
  static void throwIfFatal(Throwable t) {
    if (t instanceof VirtualMachineError) {
      throw (VirtualMachineError) t;
    } else if (t instanceof ThreadDeath) {
      throw (ThreadDeath) t;
    } else if (t instanceof LinkageError) {
      throw (LinkageError) t;
    }
  }
}
