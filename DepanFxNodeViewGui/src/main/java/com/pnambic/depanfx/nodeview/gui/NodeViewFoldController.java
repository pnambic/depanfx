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
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
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
import java.util.stream.Stream;

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
      DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
      JoglPane joglPane,
      Function<GraphNode, DepanFxNodeLocationData> nodeLocationSrc) {
    super(workspace, graphDocRsrc);
    this.joglPane = joglPane;
    this.nodeLocationSrc = nodeLocationSrc;
  }

  public void openNest(GraphNode nestNode) {
    streamForkStates(nestNode)
        .forEach(s -> s.openNest(nestNode));
  }

  public void shutNest(GraphNode nestNode) {
    streamForkStates(nestNode)
        .forEach(s -> s.shutNest(nestNode));
  }

  public void toggleNest(GraphNode nestNode) {
    streamForkStates(nestNode)
        .forEach(s -> s.toggleNest(nestNode));
  }

  private Stream<ViewFoldingState> streamForkStates(GraphNode nestNode) {
    return streamStates()
        .map(ViewFoldingState.class::cast)
        .filter(s -> s.getTreeMode(nestNode) == TreeMode.FORK);
  }

  @Override
  protected FoldingState newFoldingState(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {
    return new ViewFoldingState(nodeFoldRsrc);
  }

  private class ViewFoldingState extends DepanFxNodeFoldController.FoldingState {

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

    public void toggleNest(GraphNode nestNode) {
      ExpandState nestState = getExpandState(nestNode);
      switch (nestState) {
      case OPEN:
        shutNestNode(nestNode);
        return;
      case SHUT:
        openNestNode(nestNode);
        return;
      default:
        break;
      }
      LOG.info("Unexpected nest state: {}", nestState);
    }

    public void openNest(GraphNode nestNode) {
      ExpandState nestState = nodeStates.get(nestNode);
      if (nestState == null) {
        return; // Unknown node.
      }
      if (nestState == ExpandState.OPEN) {
        return; // Already open.
      }

      openNestNode(nestNode);
    }

    public void shutNest(GraphNode nestNode) {
      ExpandState nestState = nodeStates.get(nestNode);
      if (nestState == null) {
        return; // Unknown node.
      }
      if (nestState == ExpandState.SHUT) {
        return; // Already shut.
      }

      shutNestNode(nestNode);
    }

    @Override
    protected void updateNodeFolding(GraphNode memberNode, GraphNode nestNode) {
      super.updateNodeFolding(memberNode, nestNode);

      DepanFxNodeLocationData nestPos = nodeLocationSrc.apply(nestNode);
      shutMemberNode(memberNode, nestNode, nestPos);

      // Change nest nodes rendering to shut only the first time.
      if (nodeStates.get(nestNode) == null) {
        setNestState(nestNode, ExpandState.SHUT);
      }
    }


    private void openNestNode(GraphNode nestNode) {
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

      setNestState(nestNode, ExpandState.OPEN);
    }

    private void shutNestNode(GraphNode nestNode) {
      Collection<GraphNode> members = getTreeModel().getMembers(nestNode);
      // Resursively shut any open members.
      members.stream()
          .filter(m -> getExpandState(m) == ExpandState.OPEN)
          .forEach(this::shutNestNode);

      // Hide the direct members members.
      DepanFxNodeLocationData nestPos = nodeLocationSrc.apply(nestNode);
      members.forEach(m -> shutMemberNode(m, nestNode, nestPos));

      // Mark the nest as shut.
      setNestState(nestNode, ExpandState.SHUT);
    }

    private void shutMemberNode(
        GraphNode memberNode,
        GraphNode nestNode,
        DepanFxNodeLocationData nestPos) {

      DepanFxNodeLocationData memberPos = nodeLocationSrc.apply(memberNode);

      if (memberPos != null && nestPos != null) {
        DepanFxNodeLocationData nodeDelta =
            DepanFxNodeLocationData.calcDelta(nestPos, memberPos);
        nodeDeltas.put(memberNode, nodeDelta);
      }
      JoglShapes.updateNodeFolding(joglPane, memberNode, nestNode);
    }

    private void setNestState(GraphNode nestNode, ExpandState foldState) {
      nodeStates.put(nestNode, foldState);
      JoglShapes.updateNestFoldingState(
          joglPane, nestNode, foldState == ExpandState.OPEN);
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
