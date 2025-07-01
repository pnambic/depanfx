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
import com.pnambic.depanfx.nodeview.jogl.JoglPane;
import com.pnambic.depanfx.nodeview.jogl.JoglShapes;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private static final Logger LOG =
      LoggerFactory.getLogger(NodeFoldController.class);

  private final DepanFxWorkspace workspace;

  private final JoglPane joglPane;

  private final Function<GraphNode, DepanFxNodeLocationData> nodeLocationSrc;

  /**
   * Source, or last saved version, of the node fold data..
   */
  private DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc;

  private Map<GraphNode, DepanFxNodeLocationData> nodeDeltas = new HashMap<>();

  private boolean hasNodeFoldChanges = false;

  public NodeFoldController(
      DepanFxWorkspace workspace,
      JoglPane joglPane,
      Function<GraphNode, DepanFxNodeLocationData> nodeLocationSrc) {
    this.workspace = workspace;
    this.joglPane = joglPane;
    this.nodeLocationSrc = nodeLocationSrc;
  }

  public void installNodeFoldResource(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {
    this.nodeFoldRsrc = nodeFoldRsrc;

    // Clear any previous deltas
    nodeDeltas.clear();

    // Install the initial deltas
    nodeFoldRsrc.getResource().streamNodeNests()
        .forEach(this::installNodeNest);

    hasNodeFoldChanges = false;
  }

  /**
   * Returns {@code null} if
   * - no node folds are installed,
   * - no node persistent fold resource is installed. (i.e. is scratch).
   *
   * The behaviors are appropriate for the methods intended use in container
   * serialization methods.
   */
  public DepanFxWorkspaceResource<DepanFxNodeFoldData>
  forUpdateNodeFoldResource() {
    if (nodeFoldRsrc == null) {
      return null;
    }

    // No persistent node fold data, no update.
    if (nodeFoldRsrc.getDocument().getProject() ==
        workspace.getScratchProjectTree()) {
      return null;
    }

    if (hasNodeFoldChanges) {
      DepanFxNodeFoldData nodeFoldInfo = nodeFoldRsrc.getResource();
      DepanFxNodeFoldData updateFoldInfo =
          new DepanFxNodeFoldData(
              nodeFoldInfo.getToolName(),
              nodeFoldInfo.getToolDescription(),
              nodeFoldInfo.getGraphDocResource(),
              forUpdateNodeNests());

      return DepanFxWorkspaceResource.forUpdate(nodeFoldRsrc, updateFoldInfo);
    }

    // Just use the original resource.
    return nodeFoldRsrc;
  }

  private void installNodeNest(DepanFxNodeFoldData.NodeNest nodeNest) {
    LOG.info("folding node {} into {}",
        nodeNest.getMemberNode().getId().getSimpleName(),
        nodeNest.getNestNode().getId().getSimpleName());
    GraphNode memberNode = nodeNest.getMemberNode();
    DepanFxNodeLocationData memberLocation = nodeLocationSrc.apply(memberNode);

    GraphNode nestNode = nodeNest.getNestNode();
    DepanFxNodeLocationData nestLocation = nodeLocationSrc.apply(nestNode);

    DepanFxNodeLocationData nodeDelta =
        DepanFxNodeLocationData.calcDelta(nestLocation, memberLocation);
    nodeDeltas.put(memberNode, nodeDelta);
    updateNodeFolding(memberNode, nestNode);
  }

  private List<DepanFxNodeFoldData.NodeNest> forUpdateNodeNests() {
    if (nodeFoldRsrc != null) {
      return nodeFoldRsrc.getResource().streamNodeNests()
          .collect(Collectors.toList());
    }
    return null;
  }

  private void updateNodeFolding(GraphNode nodeMember, GraphNode nodeNest) {
    LOG.debug("folding node shape {} into {}",
        nodeMember.getId().getSimpleName(),
        nodeNest.getId().getSimpleName());
    JoglShapes.updateNodeFolding(joglPane, nodeMember, nodeNest);
  }
}
