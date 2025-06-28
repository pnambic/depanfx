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
package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DepanFxNodeParentsToTreeModelBuilder {

  private final DepanFxWorkspaceResource<GraphDocument> graphRsrc;

  private final Map<GraphNode, GraphNode> nodeParents = new HashMap<>();

  public DepanFxNodeParentsToTreeModelBuilder(
      DepanFxWorkspaceResource<GraphDocument> graphRsrc) {
    this.graphRsrc = graphRsrc;
  }

  public void importNodeParents(
      Stream<DepanFxNodeFoldData.NodeNest> parentInfo) {
    parentInfo.forEach(np ->
        nodeParents.computeIfAbsent(
            np.getMemberNode(), n -> np.getNestNode()));
  }

  public DepanFxTreeModel build() {
    DepanFxSimpleAdjacencyModel adjInfo = new DepanFxSimpleAdjacencyModel();
    nodeParents.entrySet().stream()
        .forEach(e -> adjInfo.addAdjacency(e.getValue(), e.getKey()));

    List<GraphNode> headNodes = nodeParents.values().stream()
        .filter(n -> ! nodeParents.containsKey(n))
        .collect(Collectors.toList());

    return new DepanFxSimpleTreeModel(graphRsrc, adjInfo, headNodes);
  }
}
