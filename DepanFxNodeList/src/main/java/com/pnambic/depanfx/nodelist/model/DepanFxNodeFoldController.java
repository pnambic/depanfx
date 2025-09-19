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
package com.pnambic.depanfx.nodelist.model;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData;
import com.pnambic.depanfx.nodelist.tree.DepanFxNodeParentsToTreeModelBuilder;
import com.pnambic.depanfx.nodelist.tree.DepanFxSimpleTreeModel;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Base controller for node folding state.
 */
public abstract class DepanFxNodeFoldController {

  protected static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeFoldController.class);

  private final DepanFxWorkspace workspace;

  // Folding state for capture resources.
  private List<FoldingState> captureStates = new ArrayList<>();

  private final DepanFxWorkspaceResource<GraphDocument> graphDocRsrc;

  public DepanFxNodeFoldController(
      DepanFxWorkspace workspace,
      DepanFxWorkspaceResource<GraphDocument> graphDocRsrc) {
    this.workspace = workspace;
    this.graphDocRsrc = graphDocRsrc;
  }

  public DepanFxWorkspaceResource<DepanFxNodeFoldData>
  createFoldResource(
      DepanFxWorkspace workspace) {

    return workspace.addScratchResource(
            DepanFxNodeFoldData.emptyNodeFoldData(graphDocRsrc));
  }

  public Stream<DepanFxWorkspaceResource<DepanFxNodeFoldData>>
  streamUpdateNodeFoldResource() {
    return streamStates()
        .filter(s -> s.nodeFoldRsrc.getDocument().getProject()
            != workspace.getScratchProjectTree())
        .map(s -> s.forUpdate());
  }

  public DepanFxWorkspaceResource<GraphDocument> getGraphDocResource() {
    return graphDocRsrc;
  }

  /////////////////////////////////////
  // Unified foldings
  //
  // Unified foldings are primarily in support of UX with
  // all installed node foldings available for user selection.
  //
  // Derived classes should ensure that their streamStates() method
  // returns the union of their own states and any parent classes.

  /**
   * Provide a stream for all node fold resources.
   */
  public Stream<DepanFxWorkspaceResource<DepanFxNodeFoldData>>
  streamNodeFoldResources() {
    return streamStates()
        .map(s -> s.getNodeFoldResource());
  }

  /**
   * Supports add to menu item which lists all node foldings,
   * whether section or capture.
   */
  public boolean addTreeModel(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc,
      DepanFxTreeModel subModel) {

    return findNodeFoldingState(nodeFoldRsrc)
        .map(f -> f.addSubTree(subModel))
        .orElse(false);
  }

  protected Stream<FoldingState> streamStates() {
    return captureStates.stream();
  }

  private Optional<FoldingState> findNodeFoldingState(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> foldDataRsrc) {
    return streamStates()
        .filter(s -> s.getNodeFoldResource().equals(foldDataRsrc))
        .findFirst();
  }

  /////////////////////////////////////
  // Capture foldings

  public void installCaptureFoldResourceAt(
      int foldIndex,
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {

    captureStates.set(foldIndex, buildFoldingState(nodeFoldRsrc));
  }

  public void appendCaptureFoldResource(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {

    captureStates.add(buildFoldingState(nodeFoldRsrc));
  }

  public DepanFxWorkspaceResource<DepanFxNodeFoldData>
  forUpdateCaptureFoldResourceAt(
      int index) {

    return captureStates.get(index).forUpdate();
  }

  public Stream<DepanFxWorkspaceResource<DepanFxNodeFoldData>>
  streamCaptureFoldResources() {
    return captureStates.stream()
        .map(s -> s.getNodeFoldResource());
  }

  /////////////////////////////////////
  // Other

  abstract protected FoldingState newFoldingState(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc);

  protected FoldingState buildFoldingState(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {

    FoldingState result = newFoldingState(nodeFoldRsrc);

    // Unpack the resource and install each node nests.
    nodeFoldRsrc.getResource().streamNodeNests()
        .forEach(result::installNodeNest);

    return result;
  }

  // Unified stream

  public static class FoldingState {

    private final DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc;

    private DepanFxTreeModel treeModel;

    private boolean hasNodeFoldChanges = false;

    public FoldingState(
        DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {
      this.nodeFoldRsrc = nodeFoldRsrc;
      treeModel = buildTreeModel(nodeFoldRsrc.getResource());
    }

    public DepanFxWorkspaceResource<DepanFxNodeFoldData> getNodeFoldResource() {
      return nodeFoldRsrc;
    }

    public void setTreeModel(DepanFxTreeModel treeModel) {
      this.treeModel = treeModel;
    }

    public DepanFxTreeModel getTreeModel() {
      return treeModel;
    }

    public boolean addSubTree(DepanFxTreeModel subModel) {

      if (treeModel instanceof DepanFxSimpleTreeModel simple) {
        simple.addTreeModel(subModel);
        hasNodeFoldChanges = true;
        return true;
      }
      return false;
    }

    public void installNodeNest(DepanFxNodeFoldData.NodeNest nodeNest) {
      LOG.debug("folding node {} into {}",
          nodeNest.getMemberNode().getId().getSimpleName(),
          nodeNest.getNestNode().getId().getSimpleName());

      GraphNode memberNode = nodeNest.getMemberNode();
      GraphNode nestNode = nodeNest.getNestNode();
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

    protected void updateNodeFolding(GraphNode nodeMember, GraphNode nodeNest) {
      LOG.debug("folding node shape {} into {}",
          nodeMember.getId().getSimpleName(),
          nodeNest.getId().getSimpleName());
    }

    private List<DepanFxNodeFoldData.NodeNest> forUpdateNodeNests() {
      return treeModel.streamNodeParent()
          .collect(Collectors.toList());
    }

    private static DepanFxTreeModel buildTreeModel(
        DepanFxNodeFoldData foldInfo) {
      DepanFxNodeParentsToTreeModelBuilder builder =
          new DepanFxNodeParentsToTreeModelBuilder(
              foldInfo.getGraphDocResource());
      builder.importNodeParents(foldInfo.streamNodeNests());
      return builder.build();
    }
  }
}
