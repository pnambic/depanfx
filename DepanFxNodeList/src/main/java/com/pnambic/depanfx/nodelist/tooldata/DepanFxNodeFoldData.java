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

import com.pnambic.depanfx.base.tooldata.DepanFxBaseToolData;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Persisted data for fold sections.
 */
public class DepanFxNodeFoldData extends DepanFxBaseToolData {

  public static final String NODE_FOLD_TOOL_EXT = "dnfti";

  public static final String NODE_FOLD_LABEL = "Fold";

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

  private final DepanFxWorkspaceResource<GraphDocument> graphRsrc;

  /*
   * In use, this is logically a map from member to containing node.
   * For persistence, we use a list of NodeNest objects.
   */
  private final List<NodeNest> foldNests;

  public DepanFxNodeFoldData(
      String toolName, String toolDescription,
      DepanFxWorkspaceResource<GraphDocument> graphRsrc,
      List<NodeNest> foldNests) {
    super(toolName, toolDescription);

    // Folding is over a specific graph document.
    this.graphRsrc = graphRsrc;
    this.foldNests = foldNests;
  }

  public static DepanFxNodeFoldData emptyNodeFoldData(
      DepanFxWorkspaceResource<GraphDocument> graphRsrc) {
    return new DepanFxNodeFoldData(
        "Empty Fold Data", "Empty fold data.",
        graphRsrc, Collections.emptyList());
  }

  public DepanFxNodeFoldData forUpdate(
      String toolName, String toolDescription) {
    return new DepanFxNodeFoldData(toolName, toolDescription,
        graphRsrc, new ArrayList<>(foldNests));
  }

  public DepanFxWorkspaceResource<GraphDocument> getGraphDocResource() {
    return graphRsrc;
  }

  public Stream<NodeNest> streamNodeNests() {
    // Deserialization of empty fold data creates a null list.
    if (foldNests != null) {
      return foldNests.stream();
    }
    return Stream.empty();
  }

  public static File buildCurrentToolFile(
      Path parentPath, String dataLabel) {
    String toolName = DepanFxWorkspaceFactory.buildDocumentTimestampName(
        dataLabel, NODE_FOLD_TOOL_EXT);
    return parentPath.resolve(toolName).toFile();
  }
}
