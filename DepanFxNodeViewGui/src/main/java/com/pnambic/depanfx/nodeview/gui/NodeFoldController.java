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
package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Manages the active state of node nesting in the user interface.
 *
 * Based on the initial node folding data, the position deltas for folded nodes
 * are captured, and the folding animation is triggered.  Various user actions
 * to expand or collapse nodes will mutate the folding state.
 *
 * The current set of node folding can be obtained view the forUpdate() method.
 */
public class NodeFoldController {

  private final Function<GraphNode, DepanFxNodeLocationData> nodeLocation;

  /**
   * Source, or last saved version, of the node fold data..
   */
  private DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc;

  private Map<GraphNode, DepanFxNodeLocationData> nodeDeltas = new HashMap<>();

  public NodeFoldController(
      Function<GraphNode, DepanFxNodeLocationData> nodeLocation) {
    this.nodeLocation = nodeLocation;
  }

  public void installNodeFoldResource(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {
    this.nodeFoldRsrc = nodeFoldRsrc;

    // Clear any previous deltas
    nodeDeltas.clear();

    // Install the initial deltas
    nodeFoldRsrc.getResource().streamNodeNests()
        .forEach(this::installDelta);
  }

  private void installDelta(DepanFxNodeFoldData.NodeNest nodeNest) {
    GraphNode memberNode = nodeNest.getMemberNode();
    DepanFxNodeLocationData memberLocation = nodeLocation.apply(memberNode);

    GraphNode nestNode = nodeNest.getNestNode();
    DepanFxNodeLocationData nestLocation = nodeLocation.apply(nestNode);

    DepanFxNodeLocationData nodeDelta =
        DepanFxNodeLocationData.calcDelta(nestLocation, memberLocation);
    nodeDeltas.put(memberNode, nodeDelta);
  }

  public DepanFxWorkspaceResource<DepanFxNodeFoldData> forUpdateNodeFoldResource() {
    DepanFxNodeFoldData nodeFoldInfo = nodeFoldRsrc.getResource();
    DepanFxNodeFoldData updateFoldInfo =
        new DepanFxNodeFoldData(
            nodeFoldInfo.getToolName(),
            nodeFoldInfo.getToolDescription(),
            nodeFoldInfo.getGraphDocResource(),
            updateNodeNests());

    return DepanFxWorkspaceResource.forUpdate(nodeFoldRsrc, updateFoldInfo);
  }

  private List<DepanFxNodeFoldData.NodeNest> updateNodeNests() {
    if (nodeFoldRsrc != null) {
      return nodeFoldRsrc.getResource().streamNodeNests()
          .collect(Collectors.toList());
    }
    return null;
  }
}
