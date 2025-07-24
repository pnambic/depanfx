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

import java.util.ArrayList;
import java.util.Collection;
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
  private Collection<FoldingState> foldStates = new ArrayList<>();

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
    FoldingState foldState = new FoldingState(nodeFoldRsrc);
    nodeFoldRsrc.getResource().streamNodeNests()
        .forEach(foldState::installNodeNest);

    foldStates.add(foldState);
  }

  public Collection<DepanFxWorkspaceResource<DepanFxNodeFoldData>> forUpdateNodeFoldResource() {
    return foldStates.stream()
        .filter(s -> s.nodeFoldRsrc.getDocument().getProject()
            != workspace.getScratchProjectTree())
        .map(s -> s.forUpdate())
        .collect(Collectors.toList());
  }

  private class FoldingState {

    private final DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc;

    private final Map<GraphNode, DepanFxNodeLocationData> nodeDeltas =
        new HashMap<>();

    private boolean hasNodeFoldChanges = false;

    private FoldingState(
        DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {
      this.nodeFoldRsrc = nodeFoldRsrc;
    }

    public void installNodeNest(DepanFxNodeFoldData.NodeNest nodeNest) {
      LOG.debug("folding node {} into {}",
          nodeNest.getMemberNode().getId().getSimpleName(),
          nodeNest.getNestNode().getId().getSimpleName());
      GraphNode memberNode = nodeNest.getMemberNode();
      DepanFxNodeLocationData memberLocation =
          nodeLocationSrc.apply(memberNode);

      GraphNode nestNode = nodeNest.getNestNode();
      DepanFxNodeLocationData nestLocation = nodeLocationSrc.apply(nestNode);

      DepanFxNodeLocationData nodeDelta =
          DepanFxNodeLocationData.calcDelta(nestLocation, memberLocation);
      nodeDeltas.put(memberNode, nodeDelta);
      updateNodeFolding(memberNode, nestNode);
    }

    public DepanFxWorkspaceResource<DepanFxNodeFoldData> forUpdate() {
      if (hasNodeFoldChanges) {
        DepanFxNodeFoldData nodeFoldInfo = nodeFoldRsrc.getResource();
        DepanFxNodeFoldData updateFoldInfo =
            new DepanFxNodeFoldData(
                nodeFoldInfo.getToolName(),
                nodeFoldInfo.getToolDescription(),
                nodeFoldInfo.getGraphDocResource(),
                forUpdateNodeNests());

        return DepanFxWorkspaceResource.forUpdate(
            nodeFoldRsrc, updateFoldInfo);
      }

      // Just use the original resource.
      return nodeFoldRsrc;
    }

    private void updateNodeFolding(GraphNode nodeMember, GraphNode nodeNest) {
      LOG.debug("folding node shape {} into {}",
          nodeMember.getId().getSimpleName(),
          nodeNest.getId().getSimpleName());
      JoglShapes.updateNodeFolding(joglPane, nodeMember, nodeNest);
    }


    private List<DepanFxNodeFoldData.NodeNest> forUpdateNodeNests() {
        return nodeFoldRsrc.getResource().streamNodeNests()
            .collect(Collectors.toList());
    }
  }
}
