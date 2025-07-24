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
import com.pnambic.depanfx.nodeview.jogl.JoglPane;
import com.pnambic.depanfx.nodeview.jogl.JoglShapes;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

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

  @Override
  protected FoldingState newFoldingState(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {
    return new ViewFoldingState(nodeFoldRsrc);
  }

  class ViewFoldingState extends DepanFxNodeFoldController.FoldingState {

    private final Map<GraphNode, DepanFxNodeLocationData> nodeDeltas =
        new HashMap<>();

    private ViewFoldingState(
        DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {
      super(nodeFoldRsrc);
    }

    @Override
    protected void updateNodeFolding(GraphNode memberNode, GraphNode nestNode) {
      super.updateNodeFolding(memberNode, nestNode);

      DepanFxNodeLocationData memberLocation =
          nodeLocationSrc.apply(memberNode);
      DepanFxNodeLocationData nestLocation = nodeLocationSrc.apply(nestNode);

      if (memberLocation != null && nestLocation != null) {
        DepanFxNodeLocationData nodeDelta =
            DepanFxNodeLocationData.calcDelta(nestLocation, memberLocation);
        nodeDeltas.put(memberNode, nodeDelta);
      }

      JoglShapes.updateNodeFolding(joglPane, memberNode, nestNode);
    }
  }
}
