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
package com.pnambic.depanfx.nodelist.tooldata;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Persisted data for fold sections.
 */
public class DepanFxFoldSectionData extends DepanFxBaseSectionData {

  public static final String FOLD_SECTION_TOOL_EXT = "dfsti";

  public static final String FOLD_SECTION_LABEL = "Fold";

  public enum ContainerOrder { FIRST, MIXED, LAST };

  public static class NodeNest {

    private final GraphNode memberNode;

    private final GraphNode nestNode;

    public NodeNest(GraphNode memberNode, GraphNode nestNode) {
      this.memberNode = memberNode;
      this.nestNode = nestNode;
    }

    public GraphNode getMemberNode() {
      return memberNode;
    }

    public GraphNode getNestNode() {
      return nestNode;
    }
  }

  private final ContainerOrder containerOrder;

  private final DepanFxWorkspaceResource<GraphDocument> graphRsrc;

  /*
   * In use, this is logically a map from member to containing node.
   * For persistence, we use a list of NodeNest objects.
   */
  private final List<NodeNest> foldNests;

  public DepanFxFoldSectionData(
      String toolName, String toolDescription,
      String sectionLabel, boolean displayNodeCount,
      OrderBy orderBy,
      ContainerOrder containerOrder,
      OrderDirection orderDirection,
      DepanFxWorkspaceResource<GraphDocument> graphRsrc,
      List<NodeNest> foldNests) {
    super(toolName, toolDescription,
        sectionLabel, displayNodeCount, orderBy, orderDirection);

    // Folding is over a specific graph doucment.
    this.graphRsrc = graphRsrc;
    this.foldNests = foldNests;

    // Collation criteria
    this.containerOrder = containerOrder;
  }

  public static DepanFxFoldSectionData emptyFoldSectionData(
      DepanFxWorkspaceResource<GraphDocument> graphRsrc) {
    return new DepanFxFoldSectionData(
        "Empty Fold Section", "Empty fold section.",
        FOLD_SECTION_LABEL, true,
        OrderBy.NODE_KEY, ContainerOrder.FIRST,
        OrderDirection.FORWARD, graphRsrc, Collections.emptyList());
  }

  public ContainerOrder getContainerOrder() {
    return containerOrder;
  }

  public DepanFxWorkspaceResource<GraphDocument> getGraphResource() {
    return graphRsrc;
  }

  public Stream<NodeNest> streamNodeNests() {
    return foldNests.stream();
  }

  public static File buildCurrentToolFile(
      Path parentPath, String dataLabel) {
    String toolName = DepanFxWorkspaceFactory.buildDocumentTimestampName(
        dataLabel, FOLD_SECTION_TOOL_EXT);
    return parentPath.resolve(toolName).toFile();
  }
}
