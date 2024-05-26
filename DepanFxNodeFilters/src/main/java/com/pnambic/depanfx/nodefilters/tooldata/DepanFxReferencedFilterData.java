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

import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

public class DepanFxReferencedFilterData extends DepanFxBaseFilterData {

  public static final String REFERENCED_FILTER_TOOL_EXT = "drfti";

  private final DepanFxWorkspaceResource<? extends DepanFxBaseFilterData>
      filterRsrc;

  private final boolean sequenceClosure;

  public DepanFxReferencedFilterData(
      String toolName, String toolDescription, FilterMergeMode mergeMode,
      DepanFxWorkspaceResource<? extends DepanFxBaseFilterData> filterRsrc,
      boolean sequenceClosure) {
    super(toolName, toolDescription, mergeMode);
    this.filterRsrc = filterRsrc;
    this.sequenceClosure = sequenceClosure;
  }

  public DepanFxWorkspaceResource<? extends DepanFxBaseFilterData> getFilterResource() {
    return filterRsrc;
  }

  public DepanFxBaseFilterData getFilter() {
    return filterRsrc.getResource();
  }

  public boolean useClosure() {
    return sequenceClosure;
  }
}
