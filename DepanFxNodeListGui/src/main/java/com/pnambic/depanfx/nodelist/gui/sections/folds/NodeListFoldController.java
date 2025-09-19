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
package com.pnambic.depanfx.nodelist.gui.sections.folds;

import com.google.common.collect.Streams;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeFoldController;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Adds a separate managed list of section based folding states.
 * The node foldings held in folding sections work similar,
 * but have a separate lifecycle from the capture based foldings.
 */
public class NodeListFoldController extends DepanFxNodeFoldController {

  // Folding state for folding sections.
  private List<FoldingState> sectionStates = new ArrayList<>();

  public NodeListFoldController(
      DepanFxWorkspace workspace,
      DepanFxWorkspaceResource<GraphDocument> graphDocRsrc) {
    super(workspace, graphDocRsrc);
  }

  protected Stream<FoldingState> streamStates() {
    return Streams.concat(sectionStates.stream(), super.streamStates());
  }

  /////////////////////////////////////
  // Section foldings

  public void installSectionFoldingResourceAt(
      int foldIndex,
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {

    FoldingState foldState = buildFoldingState(nodeFoldRsrc);
    if (foldIndex < sectionStates.size()) {
      sectionStates.set(foldIndex, foldState);
      return;
    }
    sectionStates.add(foldState);
  }

  public Optional<DepanFxTreeModel> getSectionTreeModel(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {
    return findSectionFoldingState(nodeFoldRsrc)
        .map(s -> s.getTreeModel());
  }

  public void updateSectionTreeModel(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc,
      DepanFxTreeModel treeModel) {

    findSectionFoldingState(nodeFoldRsrc)
        .ifPresent(s -> s.setTreeModel(treeModel));
  }

  public Stream<DepanFxWorkspaceResource<DepanFxNodeFoldData>>
  streamSectionFoldResources() {
    return sectionStates.stream()
        .map(s -> s.getNodeFoldResource());
  }

  @Override
  protected FoldingState newFoldingState(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {
    return new ListFoldingState(nodeFoldRsrc);
  }

  private Optional<FoldingState> findSectionFoldingState(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> foldDataRsrc) {
    return sectionStates.stream()
        .filter(s -> s.getNodeFoldResource().equals(foldDataRsrc))
        .findFirst();
  }

  class ListFoldingState extends DepanFxNodeFoldController.FoldingState {

    public ListFoldingState(
        DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {
      super(nodeFoldRsrc);
    }

    @Override
    protected void updateNodeFolding(GraphNode memberNode, GraphNode nestNode) {
      // Update the node fold data with the new nest node.
    }
  }
}
