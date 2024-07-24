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
package com.pnambic.depanfx.nodefilters.model;

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;

import java.util.Optional;

/**
 * Indicates whether a node filter can be used as a closable element.
 *
 * The {@link DepanFxBaseFilter} only uses closure when a derived class
 * also implements this interface and provides a {@code true} value for
 * {@link #useClosure()}.
 */
public interface DepanFxClosableFilter {

  boolean useClosure();

  public static Optional<Boolean> getClosure(DepanFxBaseFilterData filter) {
    if (filter instanceof DepanFxClosableFilter closable) {
      return Optional.of(closable.useClosure());
    }
    return Optional.empty();
  }
}
