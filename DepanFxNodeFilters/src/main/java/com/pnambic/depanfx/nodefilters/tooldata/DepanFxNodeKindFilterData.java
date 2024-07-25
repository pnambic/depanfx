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

import com.pnambic.depanfx.graph.context.ContextNodeKindId;

public class DepanFxNodeKindFilterData extends DepanFxBaseFilterData {

  public static final String NODE_KIND_FILTER_TOOL_EXT = "dnkfti";

  private final ContextNodeKindId nodeKind;

  private final boolean exclusionFilter;

  public DepanFxNodeKindFilterData(
      String toolName, String toolDescription,
      FilterMergeMode mergeMode,
      ContextNodeKindId nodeKind, boolean exclusionFilter) {
    super(toolName, toolDescription, mergeMode);
    this.nodeKind = nodeKind;
    this.exclusionFilter = exclusionFilter;
  }

  public static DepanFxNodeKindFilterData createNodeKindFilterData(
      ContextNodeKindId nodeKind) {
    String nodeKindKey = nodeKind.getNodeKindKey();
    return new DepanFxNodeKindFilterData(
        nodeKindKey + " node kind filter",
        "Node kind filter for " + nodeKindKey,
        FilterMergeMode.REPLACE, nodeKind, false);
  }

  public ContextNodeKindId getNodeKind() {
    return nodeKind;
  }

  public boolean isExclusionFilter() {
    return exclusionFilter;
  }
}
