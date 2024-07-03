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
package com.pnambic.depanfx.nodefilters.tooldata;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DepanFxSequenceFilterData extends DepanFxBaseFilterData {

  public static final String SEQUENCE_FILTER_TOOL_EXT = "dsfti";

  private final List<? extends DepanFxBaseFilterData> filters;

  private final boolean sequenceClosure;

  public DepanFxSequenceFilterData(
      String toolName, String toolDescription, FilterMergeMode mergeMode,
      List<? extends DepanFxBaseFilterData> filters,
      boolean sequenceClosure) {
    super(toolName, toolDescription, mergeMode);
    this.filters = filters;
    this.sequenceClosure = sequenceClosure;
  }

  public static DepanFxSequenceFilterData createSequenceFilterData() {
    List<? extends DepanFxBaseFilterData> filterSeq = new ArrayList<>();
    return new DepanFxSequenceFilterData(
        "Sequence filter",
        "Sequence filter description",
        FilterMergeMode.REPLACE, filterSeq, false);
  }

  public Stream<? extends DepanFxBaseFilterData> streamFilters() {
    return filters.stream();
  }

  public boolean useClosure() {
    return sequenceClosure;
  }
}
