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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;

// Developed by BROgrammer
public final class TypesTest {
  @Test
  public void getRawTypeClass() {
    assertThat(Types.getRawType(String.class)).isEqualTo(String.class);
    assertThat(Types.getRawType(int.class)).isEqualTo(int.class);
    assertThat(Types.getRawType(int[].class)).isEqualTo(int[].class);
  }

  @Test
  public void getRawTypeParameterizedType() {
    ParameterizedType type = new Types.ParameterizedTypeImpl(null, List.class, String.class);
    assertThat(Types.getRawType(type)).isEqualTo(List.class);
  }

  @Test
  public void getRawTypeGenericArrayType() {
    ParameterizedType listType = new Types.ParameterizedTypeImpl(null, List.class, String.class);
    GenericArrayType arrayType = new Types.GenericArrayTypeImpl(listType);
    assertThat(Types.getRawType(arrayType)).isEqualTo(List[].class);
  }

  @Test
  public void getRawTypeWildcardType() {
    WildcardType wildcardType =
        new Types.WildcardTypeImpl(new Type[] {CharSequence.class}, Types.EMPTY_TYPE_ARRAY);
    assertThat(Types.getRawType(wildcardType)).isEqualTo(CharSequence.class);
  }

  @Test
  public void getRawTypeNullThrows() {
    try {
      Types.getRawType(null);
      fail();
    } catch (NullPointerException expected) {
      assertThat(expected).hasMessageThat().isEqualTo("type == null");
    }
  }

  @Test
  public void parameterizedTypeEqualsAndHashCode() {
    ParameterizedType a = new Types.ParameterizedTypeImpl(null, List.class, String.class);
    ParameterizedType b = new Types.ParameterizedTypeImpl(null, List.class, String.class);
    ParameterizedType c = new Types.ParameterizedTypeImpl(null, List.class, Integer.class);

    assertThat(Types.equals(a, b)).isTrue();
    assertThat(Types.equals(a, c)).isFalse();
    assertThat(a.equals(b)).isTrue();
    assertThat(a.equals(c)).isFalse();
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
    assertThat(a.toString()).isEqualTo("java.util.List<java.lang.String>");
  }

  @Test
  public void parameterizedTypePrimitiveArgumentThrows() {
    try {
      new Types.ParameterizedTypeImpl(null, List.class, int.class);
      fail();
    } catch (IllegalArgumentException expected) {
      assertThat(expected).isNotNull();
    }
  }

  @Test
  public void genericArrayTypeEqualsAndHashCode() {
    GenericArrayType a = new Types.GenericArrayTypeImpl(String.class);
    GenericArrayType b = new Types.GenericArrayTypeImpl(String.class);
    GenericArrayType c = new Types.GenericArrayTypeImpl(Integer.class);

    assertThat(Types.equals(a, b)).isTrue();
    assertThat(Types.equals(a, c)).isFalse();
    assertThat(a.equals(b)).isTrue();
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
    assertThat(a.toString()).isEqualTo("java.lang.String[]");
  }

  @Test
  public void wildcardTypeUpperAndLowerBounds() {
    WildcardType extendsCharSequence =
        new Types.WildcardTypeImpl(new Type[] {CharSequence.class}, Types.EMPTY_TYPE_ARRAY);
    WildcardType superString =
        new Types.WildcardTypeImpl(new Type[] {Object.class}, new Type[] {String.class});

    assertThat(extendsCharSequence.toString()).isEqualTo("? extends java.lang.CharSequence");
    assertThat(superString.toString()).isEqualTo("? super java.lang.String");
    assertThat(Types.equals(extendsCharSequence, superString)).isFalse();

    WildcardType extendsCharSequence2 =
        new Types.WildcardTypeImpl(new Type[] {CharSequence.class}, Types.EMPTY_TYPE_ARRAY);
    assertThat(extendsCharSequence.equals(extendsCharSequence2)).isTrue();
  }

  @Test
  public void wildcardTypeInvalidBoundsThrow() {
    try {
      new Types.WildcardTypeImpl(
          new Type[] {CharSequence.class, Number.class}, Types.EMPTY_TYPE_ARRAY);
      fail();
    } catch (IllegalArgumentException expected) {
      assertThat(expected).isNotNull();
    }

    try {
      new Types.WildcardTypeImpl(
          new Type[] {CharSequence.class}, new Type[] {String.class, Integer.class});
      fail();
    } catch (IllegalArgumentException expected) {
      assertThat(expected).isNotNull();
    }
  }

  @Test
  public void getParameterUpperBound_plainType() {
    ParameterizedType mapType =
        new Types.ParameterizedTypeImpl(null, Map.class, String.class, Integer.class);
    assertThat(Types.getParameterUpperBound(0, mapType)).isEqualTo(String.class);
    assertThat(Types.getParameterUpperBound(1, mapType)).isEqualTo(Integer.class);
    assertThat(Utils.getParameterUpperBound(0, mapType)).isEqualTo(String.class);
  }

  @Test
  public void getParameterUpperBound_wildcardType() {
    WildcardType extendsCharSequence =
        new Types.WildcardTypeImpl(new Type[] {CharSequence.class}, Types.EMPTY_TYPE_ARRAY);
    ParameterizedType listType =
        new Types.ParameterizedTypeImpl(null, List.class, extendsCharSequence);

    assertThat(Types.getParameterUpperBound(0, listType)).isEqualTo(CharSequence.class);
    assertThat(Utils.getParameterUpperBound(0, listType)).isEqualTo(CharSequence.class);
  }

  @Test
  public void getParameterUpperBound_outOfBoundsThrows() {
    ParameterizedType listType = new Types.ParameterizedTypeImpl(null, List.class, String.class);
    try {
      Types.getParameterUpperBound(1, listType);
      fail();
    } catch (IllegalArgumentException expected) {
      assertThat(expected).isNotNull();
    }

    try {
      Types.getParameterUpperBound(-1, listType);
      fail();
    } catch (IllegalArgumentException expected) {
      assertThat(expected).isNotNull();
    }
  }

  @Test
  public void getParameterLowerBound_plainType() {
    ParameterizedType mapType =
        new Types.ParameterizedTypeImpl(null, Map.class, String.class, Integer.class);
    assertThat(Types.getParameterLowerBound(0, mapType)).isEqualTo(String.class);
    assertThat(Types.getParameterLowerBound(1, mapType)).isEqualTo(Integer.class);
    assertThat(Utils.getParameterLowerBound(0, mapType)).isEqualTo(String.class);
  }

  @Test
  public void getParameterLowerBound_wildcardType() {
    WildcardType superString =
        new Types.WildcardTypeImpl(new Type[] {Object.class}, new Type[] {String.class});
    ParameterizedType listType = new Types.ParameterizedTypeImpl(null, List.class, superString);

    assertThat(Types.getParameterLowerBound(0, listType)).isEqualTo(String.class);
    assertThat(Utils.getParameterLowerBound(0, listType)).isEqualTo(String.class);
  }

  @Test
  public void checkNotPrimitive() {
    Types.checkNotPrimitive(String.class);
    try {
      Types.checkNotPrimitive(int.class);
      fail();
    } catch (IllegalArgumentException expected) {
      assertThat(expected).isNotNull();
    }
  }

  @Test
  public void hasUnresolvableType() {
    assertThat(Types.hasUnresolvableType(String.class)).isFalse();
    ParameterizedType type = new Types.ParameterizedTypeImpl(null, List.class, String.class);
    assertThat(Types.hasUnresolvableType(type)).isFalse();

    WildcardType wildcardType =
        new Types.WildcardTypeImpl(new Type[] {CharSequence.class}, Types.EMPTY_TYPE_ARRAY);
    assertThat(Types.hasUnresolvableType(wildcardType)).isTrue();
  }

  @Test
  public void getSupertype() {
    ParameterizedType arrayListString =
        new Types.ParameterizedTypeImpl(null, ArrayList.class, String.class);
    Type listSupertype = Types.getSupertype(arrayListString, ArrayList.class, List.class);
    ParameterizedType expected = new Types.ParameterizedTypeImpl(null, List.class, String.class);
    assertThat(listSupertype).isEqualTo(expected);
  }
}
