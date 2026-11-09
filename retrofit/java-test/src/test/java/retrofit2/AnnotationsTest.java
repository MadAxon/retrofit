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

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Test;

// Developed by BROgrammer
public final class AnnotationsTest {
  @Retention(RetentionPolicy.RUNTIME)
  @interface AnnotationA {}

  @Retention(RetentionPolicy.RUNTIME)
  @interface AnnotationB {}

  @AnnotationA
  static class AnnotatedClass {}

  @Test
  public void isAnnotationPresent() {
    Annotation[] annotations = AnnotatedClass.class.getAnnotations();
    assertThat(Annotations.isAnnotationPresent(annotations, AnnotationA.class)).isTrue();
    assertThat(Annotations.isAnnotationPresent(annotations, AnnotationB.class)).isFalse();
    assertThat(Annotations.isAnnotationPresent(new Annotation[0], AnnotationA.class)).isFalse();
  }
}
