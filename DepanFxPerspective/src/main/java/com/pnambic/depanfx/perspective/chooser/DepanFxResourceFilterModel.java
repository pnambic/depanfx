/*
 * Copyright 2025 The Depan Project Authors
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

import java.nio.file.PathMatcher;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Base form for a resource filter, and a composite for combining
 * well-formed filters.
 */
public interface DepanFxResourceFilterModel {

  String getDescription();

  List<PathMatcher> getPathMatchers();

  boolean matchDocument(Object content);

  Stream<Class<?>> streamTypes();

  /**
   * A composite filter that combines multiple filters into a single
   * logical unit.
   */
  public static class Composite implements DepanFxResourceFilterModel {

    private final String description;

    private final List<DepanFxResourceFilterModel> filters;

    private final List<PathMatcher> matchers;

    public Composite(
        String description, List<DepanFxResourceFilterModel> filters) {
      this.description = description;
      this.filters = filters;

      this.matchers = filters.stream()
          .flatMap(f -> f.getPathMatchers().stream())
          .collect(Collectors.toList());
    }

    public Composite(String description, DepanFxResourceFilter[] filters) {
      this(description, List.of(filters));
    }

    @Override
    public boolean matchDocument(Object content) {
      return filters.stream()
          .anyMatch(f -> f.matchDocument(content));
    }

    @Override
    public Stream<Class<?>> streamTypes() {
      return filters.stream()
          .flatMap(f -> f.streamTypes());
    }

    @Override
    public String getDescription() {
      return description;
    }

    @Override
    public List<PathMatcher> getPathMatchers() {
      return matchers;
    }
  }
}
