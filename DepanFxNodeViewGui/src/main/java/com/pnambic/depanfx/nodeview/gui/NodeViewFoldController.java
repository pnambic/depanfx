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
import com.pnambic.depanfx.nodelist.model.DepanFxNodeFoldController;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel.TreeMode;
import com.pnambic.depanfx.nodeview.jogl.JoglPane;
import com.pnambic.depanfx.nodeview.jogl.JoglShapes;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Manages the active state of node nesting in the user interface.
 *
 * Based on the initial node folding data, the position deltas for folded nodes
 * are captured, and the folding animation is triggered.  Various user actions
 * to expand or collapse nodes will mutate the folding state.
 *
 * The current set of node folding can be obtained view the forUpdate() method.
 */
public class NodeViewFoldController extends DepanFxNodeFoldController {

  public enum ExpandState {
    OPEN, SHUT
  }

  private final JoglPane joglPane;

  private final Function<GraphNode, DepanFxNodeLocationData> nodeLocationSrc;

  public NodeViewFoldController(
      DepanFxWorkspace workspace,
      JoglPane joglPane,
      Function<GraphNode, DepanFxNodeLocationData> nodeLocationSrc) {
    super(workspace);
    this.joglPane = joglPane;
    this.nodeLocationSrc = nodeLocationSrc;
  }

  public void expandNode(GraphNode nestNode) {
    streamStates()
        .map(ViewFoldingState.class::cast)
        .filter(s -> s.getTreeMode(nestNode) == TreeMode.FORK)
        .forEach(s -> s.expandNode(nestNode));
  }

  @Override
  protected FoldingState newFoldingState(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {
    return new ViewFoldingState(nodeFoldRsrc);
  }

  class ViewFoldingState extends DepanFxNodeFoldController.FoldingState {

    private final Map<GraphNode, ExpandState> nodeStates =
        new HashMap<>();

    private final Map<GraphNode, DepanFxNodeLocationData> nodeDeltas =
        new HashMap<>();

    private ViewFoldingState(
        DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {
      super(nodeFoldRsrc);
    }

    public ExpandState getExpandState(GraphNode node) {
      return nodeStates.getOrDefault(node, ExpandState.SHUT);
    }

    public TreeMode getTreeMode(GraphNode node) {
      return getTreeModel().getTreeMode(node);
    }

    public void expandNode(GraphNode nestNode) {
      ExpandState nestState = nodeStates.get(nestNode);
      if (nestState == null) {
        return; // Unknown node.
      }
      if (nestState == ExpandState.OPEN) {
        return; // Already expanded
      }

      Collection<GraphNode> members = getTreeModel().getMembers(nestNode);

      DepanFxNodeLocationData nestPos = nodeLocationSrc.apply(nestNode);
      if (nestPos != null) {
        members.forEach(m -> {
          updateMemberLocation(m, nestPos);
          JoglShapes.clearNodeFolding(joglPane, m);
        });
      } else {
        // No position for the nest node, so just clear folding.
        members.forEach(m -> JoglShapes.clearNodeFolding(joglPane, m));
      }

      nodeStates.put(nestNode, ExpandState.OPEN);
    }

    @Override
    protected void updateNodeFolding(GraphNode memberNode, GraphNode nestNode) {
      super.updateNodeFolding(memberNode, nestNode);

      DepanFxNodeLocationData memberPos = nodeLocationSrc.apply(memberNode);
      DepanFxNodeLocationData nestPos = nodeLocationSrc.apply(nestNode);

      if (memberPos != null && nestPos != null) {
        DepanFxNodeLocationData nodeDelta =
            DepanFxNodeLocationData.calcDelta(nestPos, memberPos);
        nodeDeltas.put(memberNode, nodeDelta);
      }

      nodeStates.computeIfAbsent(nestNode, n -> ExpandState.SHUT);
      JoglShapes.updateNodeFolding(joglPane, memberNode, nestNode);
    }

    private void updateMemberLocation(
        GraphNode memberNode, DepanFxNodeLocationData nestPos) {

      DepanFxNodeLocationData delta = nodeDeltas.get(memberNode);
      if (delta == null) {
        return; // No delta for this node.
      }

      JoglShapes.updateLocation(
          joglPane, memberNode,
          DepanFxNodeLocationData.applyDelta(nestPos, delta));

      nodeDeltas.remove(memberNode);
    }
  }
}
