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

public abstract class DepanFxNodeFoldController {

  protected static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeFoldController.class);

  private final DepanFxWorkspace workspace;

  /**
   * Source, or last saved version, of the node fold data..
   */
  private List<FoldingState> foldStates = new ArrayList<>();

  public DepanFxNodeFoldController(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  public List<DepanFxWorkspaceResource<DepanFxNodeFoldData>>
  forUpdateNodeFoldResource() {
    return streamStates()
        .filter(s -> s.nodeFoldRsrc.getDocument().getProject()
            != workspace.getScratchProjectTree())
        .map(s -> s.forUpdate())
        .collect(Collectors.toList());
  }

  /**
   * Provide a stream for the current node fold resources.
   */
  public Stream<DepanFxWorkspaceResource<DepanFxNodeFoldData>>
  streamNodeFoldResources() {
    return streamStates()
        .map(s -> s.getNodeFoldResource());
  }

  public void installNodeFoldResourceAt(
      int foldIndex,
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {

    foldStates.add(foldIndex, buildFoldingState(nodeFoldRsrc));
  }

  public void appendNodeFoldResource(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {

    foldStates.add(buildFoldingState(nodeFoldRsrc));
  }

  public Optional<DepanFxTreeModel> getTreeModel(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {
    return findFoldingState(nodeFoldRsrc)
        .map(s -> s.getTreeModel());
  }

  public void updateTreeModel(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc,
      DepanFxTreeModel treeModel) {

    findFoldingState(nodeFoldRsrc)
        .ifPresent(s -> s.setTreeModel(treeModel));
  }

  public boolean addTreeModel(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc,
      DepanFxTreeModel subModel) {

    return findFoldingState(nodeFoldRsrc)
        .map(f -> f.addSubTree(subModel))
        .orElse(false);
  }

  abstract protected FoldingState newFoldingState(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc);

  protected Stream<FoldingState> streamStates() {
    return foldStates.stream();
  }

  private FoldingState buildFoldingState(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc) {

    FoldingState result = newFoldingState(nodeFoldRsrc);

    // Unpack the resource and install each node nests.
    nodeFoldRsrc.getResource().streamNodeNests()
        .forEach(result::installNodeNest);

    return result;
  }

  private Optional<FoldingState> findFoldingState(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> foldDataRsrc) {
    return streamStates()
        .filter(s -> s.getNodeFoldResource().equals(foldDataRsrc))
        .findFirst();
  }

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
        return nodeFoldRsrc.getResource().streamNodeNests()
            .collect(Collectors.toList());
    }

    private static DepanFxTreeModel buildTreeModel(DepanFxNodeFoldData foldInfo) {
      DepanFxNodeParentsToTreeModelBuilder builder =
          new DepanFxNodeParentsToTreeModelBuilder(
              foldInfo.getGraphDocResource());
      builder.importNodeParents(foldInfo.streamNodeNests());
      return builder.build();
    }
  }
}
