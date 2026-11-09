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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import okhttp3.ResponseBody;

final class Utils {
  static final Type[] EMPTY_TYPE_ARRAY = Types.EMPTY_TYPE_ARRAY;

  private Utils() {
    // No instances.
  }

  static RuntimeException methodError(Method method, String message, Object... args) {
    return Exceptions.methodError(method, message, args);
  }

  @SuppressWarnings("AnnotateFormatMethod")
  static RuntimeException methodError(
      Method method, @Nullable Throwable cause, String message, Object... args) {
    return Exceptions.methodError(method, cause, message, args);
  }

  static RuntimeException parameterError(
      Method method, Throwable cause, int p, String message, Object... args) {
    return Exceptions.parameterError(method, cause, p, message, args);
  }

  static RuntimeException parameterError(Method method, int p, String message, Object... args) {
    return Exceptions.parameterError(method, p, message, args);
  }

  static Class<?> getRawType(Type type) {
    return Types.getRawType(type);
  }

  /** Returns true if {@code a} and {@code b} are equal. */
  static boolean equals(Type a, Type b) {
    return Types.equals(a, b);
  }

  static Type getGenericSupertype(Type context, Class<?> rawType, Class<?> toResolve) {
    return Types.getGenericSupertype(context, rawType, toResolve);
  }

  static String typeToString(Type type) {
    return Types.typeToString(type);
  }

  static Type getSupertype(Type context, Class<?> contextRawType, Class<?> supertype) {
    return Types.getSupertype(context, contextRawType, supertype);
  }

  static Type resolve(Type context, Class<?> contextRawType, Type toResolve) {
    return Types.resolve(context, contextRawType, toResolve);
  }

  static void checkNotPrimitive(Type type) {
    Types.checkNotPrimitive(type);
  }

  /** Returns true if {@code annotations} contains an instance of {@code cls}. */
  static boolean isAnnotationPresent(Annotation[] annotations, Class<? extends Annotation> cls) {
    return Annotations.isAnnotationPresent(annotations, cls);
  }

  static ResponseBody buffer(final ResponseBody body) throws IOException {
    return OkHttpUtils.buffer(body);
  }

  static Type getParameterUpperBound(int index, ParameterizedType type) {
    return Types.getParameterUpperBound(index, type);
  }

  static Type getParameterLowerBound(int index, ParameterizedType type) {
    return Types.getParameterLowerBound(index, type);
  }

  static boolean hasUnresolvableType(@Nullable Type type) {
    return Types.hasUnresolvableType(type);
  }

  static void throwIfFatal(Throwable t) {
    Exceptions.throwIfFatal(t);
  }

  static boolean isUnit(Type type) {
    return KotlinUtils.isUnit(type);
  }

  static final class ParameterizedTypeImpl extends Types.ParameterizedTypeImpl {
    ParameterizedTypeImpl(@Nullable Type ownerType, Type rawType, Type... typeArguments) {
      super(ownerType, rawType, typeArguments);
    }
  }
}
