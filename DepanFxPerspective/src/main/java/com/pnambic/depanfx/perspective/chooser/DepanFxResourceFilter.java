/*
 * Copyright 2024 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.perspective.chooser;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class DepanFxResourceFilter implements DepanFxResourceFilterModel {

  private final String description;

  private final List<String> extensions;

  private final List<Class<?>> types;

  public DepanFxResourceFilter(
      String description, List<String> extensions, List<Class<?>> types) {
    this.description = description;
    this.extensions = extensions;
    this.types = types;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public List<PathMatcher> getPathMatchers() {
    List<PathMatcher> result = new ArrayList<>(extensions.size());
    FileSystem fileSys = FileSystems.getDefault();

    extensions.stream()
        .map(ext -> fileSys.getPathMatcher("glob:" + ext))
        .forEach(result::add);

    return result;
  }

  @Override
  public boolean matchDocument(Object content) {
    return types.stream()
        .filter(t -> t.isAssignableFrom(content.getClass()))
        .findFirst()
        .isPresent();
  }

  @Override
  public Stream<Class<?>> streamTypes() {
    return types.stream();
  }

  public static DepanFxResourceFilter buildResourceFilter(
      String label, String rsrcExt, List<Class<?>> rsrcTypes) {
    String matchGlob = "*." + rsrcExt;

    return new DepanFxResourceFilter(
        label + "(" + matchGlob + ")",
        Collections.singletonList(matchGlob),
        rsrcTypes);
  }

  public static DepanFxResourceFilter buildResourceFilter(
      String label, String rsrcExt, Class<?> rsrcType) {

    return buildResourceFilter(label, rsrcExt,
        Collections.singletonList(rsrcType));
  }
}
