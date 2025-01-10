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
package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxNodeFilterSequenceData;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DepanFxNodeViewNodeDisplayData extends DepanFxBaseToolData {

  public static class NodeDisplayEntry {

    private final String nodeLabel;

    private final DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc;

    private final DepanFxNodeDisplayData nodeDisplay;

    public NodeDisplayEntry(
        String nodeLabel,
        DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc,
        DepanFxNodeDisplayData nodeDisplay) {
      this.nodeLabel = nodeLabel;
      this.filterRsrc = filterRsrc;
      this.nodeDisplay = nodeDisplay;
    }

    public String getNodeLabel() {
      return nodeLabel;
    }

    public DepanFxWorkspaceResource<DepanFxBaseFilterData> getFilterResource() {
      return filterRsrc;
    }

    public DepanFxNodeDisplayData getNodeDisplay() {
      return nodeDisplay;
    }
  }

  public static final String NODE_VIEW_NODE_DISPLAY_EXT = "dnvndi";

  public static final String NODE_DISPLAY_CONTEXT_RESOURCE_NAME = "Node Display";

  public static final String NODE_DISPLAY_DIR = "Node Display";

  public static final Path NODE_DISPLAY_TOOL_PATH =
      DepanFxNodeViewData.NODE_VIEW_TOOL_PATH.resolve(NODE_DISPLAY_DIR);

  private final ContextModelId contextModelId;

  private final List<NodeDisplayEntry> nodeDisplayEntries;

  public DepanFxNodeViewNodeDisplayData(
      String toolName, String toolDescription,
      ContextModelId contextModelId,
      List<NodeDisplayEntry> nodeDisplayEntries) {
    super(toolName, toolDescription);
    this.contextModelId = contextModelId;
    this.nodeDisplayEntries = nodeDisplayEntries;
  }

  public ContextModelId getContextModelId() {
    return contextModelId;
  }

  public boolean isFor(ContextModelId modelId) {
    return this.contextModelId.equals(modelId);
  }

  public DepanFxNodeFilterSequenceData asNodeFilterSequenceDoc() {
    List<DepanFxWorkspaceResource<DepanFxBaseFilterData>> filterSeq =
        new ArrayList<>(nodeDisplayEntries.size());
    streamNodeDisplay()
        .map(d -> d.getFilterResource())
        .forEach(filterSeq::add);

    return new DepanFxNodeFilterSequenceData(
        getToolName() + " Filter Sequence",
        "Filter sequence from " + getToolDescription(),
        getContextModelId(), filterSeq);
  }

  public Stream<NodeDisplayEntry> streamNodeDisplay() {
    return nodeDisplayEntries.stream();
  }

  public int countFilters() {
    return nodeDisplayEntries.size();
  }
}
