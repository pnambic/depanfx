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
package com.pnambic.depanfx.nodelist.tooldata;

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.FilterMergeMode;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

public class DepanFxListFilterData extends DepanFxBaseFilterData {

  public static final String LIST_FILTER_TOOL_EXT = "dlfti";

  private final DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc;

  public DepanFxListFilterData(
      String toolName, String toolDescription, FilterMergeMode mergeMode,
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {
    super(toolName, toolDescription, mergeMode);
    this.nodeListRsrc = nodeListRsrc;
  }

  public static DepanFxListFilterData createListFilterData(
      DepanFxWorkspaceResource<DepanFxNodeList> listRsrc) {
    DepanFxNodeList nodeList = listRsrc.getResource();
    return new DepanFxListFilterData(
        nodeList.getNodeListName() + " filter",
        "Filter for " + nodeList.getNodeListDescription(),
        FilterMergeMode.UNION, listRsrc);
  }

  public DepanFxWorkspaceResource<DepanFxNodeList> getNodeListResource() {
    return nodeListRsrc;
  }

  public boolean useClosure() {
    return false;
  }
}
