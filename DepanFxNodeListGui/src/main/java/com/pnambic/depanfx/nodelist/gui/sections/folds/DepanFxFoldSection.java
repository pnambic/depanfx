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

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListFork;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListForkItem;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListLeafItem;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSectionItem;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSections;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeTreeSection;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFoldSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.tree.DepanFxSimpleTreeModel;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

public class DepanFxFoldSection extends DepanFxNodeTreeSection {

  public static final String SELECT_FOLD_SECTION = "Select Fold Section...";

  public static final String EDIT_FOLD_SECTION = "Edit Fold Section...";

  public static final String NEW_FOLD_SECTION_DATA = "New Fold Section Data...";

  public static final String EDIT_FOLD_SECTION_DATA = "Edit Fold Section Data...";

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxFoldSection.class);

  private DepanFxWorkspaceResource<DepanFxFoldSectionData> sectionDataRsrc;

  public DepanFxFoldSection(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxWorkspaceResource<DepanFxFoldSectionData> sectionDataRsrc) {
    super(tableAdapter);
    this.sectionDataRsrc = sectionDataRsrc;
    updateCompare();
  }

  @Override // DepanFxNodeListSection
  public String getSectionLabel() {
    return getSectionData().getSectionLabel();
  }

  @Override // DepanFxNodeListSection
  public String getDisplayName() {
    return DepanFxNodeListSections.fmtDisplayName(
        getSectionData().getSectionLabel(),
        getSectionNodes().getNodes().size(),
        getSectionData().displayNodeCount());
  }

  @Override // DepanFxNodeListSection
  public String getSortKey(GraphNode node) {
    OrderBy orderBy = getSectionData().getOrderBy();
    return DepanFxNodeListSections.getSortKey(node, orderBy);
  }

  @Override // DepanFxNodeListSection
  public DepanFxNodeListSectionItem buildSectionItem(
      DepanFxNodeList baseNodes) {

    DepanFxTreeModel treeModel = getTreeModel();
    DepanFxNodeList sectionNodes = treeModel.getReachableGraphNodes(
        treeModel.getRoots(), baseNodes.getNodes());

    updateSectionNodes(sectionNodes);

    return new FoldSectionItem(this);
  }

  @Override // DepanFxNodeListSection
  public TreeItem<DepanFxNodeListMember> buildNodeItem(GraphNode node) {
    switch (getTreeMode(node)) {

    case FORK:
      FoldFork foldFork = buildFoldFork(node);
      return new FoldForkItem(foldFork);

    case LEAF:
      FoldLeaf treeLeaf = buildFoldLeaf(node);
      return new FoldLeafItem(treeLeaf);
    }
    return null;
  }

  public void setSectionDataResource(
      DepanFxWorkspaceResource<DepanFxFoldSectionData> sectionDataRsrc) {
    this.sectionDataRsrc = sectionDataRsrc;
    updateCompare();
  }

  public DepanFxWorkspaceResource<DepanFxFoldSectionData> getSectionResource() {
    return sectionDataRsrc;
  }

  @Override
  public DepanFxTreeModel getTreeModel() {
    return getSectionFolding().getSectionTreeModel(getNodeFoldResource()).get();
  }

  @Override
  protected void updateTreeModel(
      DepanFxTreeModel treeModel, DepanFxNodeList sectionNodes) {
    DepanFxWorkspaceResource<DepanFxNodeFoldData> foldRsrc =
        getNodeFoldResource();
    getSectionFolding().updateSectionTreeModel(foldRsrc, treeModel);
    super.updateSectionNodes(sectionNodes);
  }

  public void addTreeModel(DepanFxTreeModel subModel) {
    if (getNodeFolding().addTreeModel(getNodeFoldResource(), subModel)) {
      resetTableView();
      return;
    }

    // Waiting for a more generic modifiable tree model.
    LOG.info(
        "Cannot add section model to section {}", getSectionLabel());
  }

  public void addTreeModels(Stream<DepanFxTreeModel> subModels) {
    if (getTreeModel() instanceof DepanFxSimpleTreeModel simple) {
      subModels.forEach(simple::addTreeModel);
      resetTableView();
      return;
    }
    // Waiting for a more generic modifiable tree model.
    LOG.info(
        "Cannot add section model to section {}", getSectionLabel());
  }

  public Optional<DepanFxWorkspaceResource<DepanFxNodeFoldData>>
  saveFoldInfoResource() {

    List<DepanFxNodeFoldData.NodeNest> nodeParents =
        getTreeModel().streamNodeParent()
        .collect(Collectors.toList());
    DepanFxProjectDocument srcFoldDoc =
        getSectionData().getNodeFoldResource().getDocument();
    DepanFxNodeFoldData srcFoldInfo =
        getSectionData().getNodeFoldResource().getResource();
    DepanFxNodeFoldData updateFoldInfo = new DepanFxNodeFoldData(
        srcFoldInfo.getToolName(),
        srcFoldInfo.getToolDescription(),
        srcFoldInfo.getGraphDocResource(),
        nodeParents);
    try {
      return getWorkspace().saveDocument(srcFoldDoc, updateFoldInfo);
    } catch (IOException err) {
      LOG.error("Unable to save fold info resource for section {}",
          getSectionLabel(), err);
    }
    return Optional.empty();
  }

  private NodeListFoldController getSectionFolding() {
    return (NodeListFoldController) getNodeFolding();
  }

  private DepanFxFoldSectionData getSectionData() {
    return sectionDataRsrc.getResource();
  }

  private void updateCompare() {
    DepanFxFoldSectionData sectionData = sectionDataRsrc.getResource();
    updateTreeCompare(
        sectionData.getOrderDirection(), sectionData.getContainerOrder());
  }

  private DepanFxWorkspaceResource<DepanFxNodeFoldData> getNodeFoldResource() {
    return sectionDataRsrc.getResource().getNodeFoldResource();
  }

  private FoldFork buildFoldFork(GraphNode fork) {
    return new FoldFork(fork, this);
  }

  private FoldLeaf buildFoldLeaf(GraphNode leaf) {
    return new FoldLeaf(leaf, this);
  }

  /////////////////////////////////////
  // Section components

  private static class FoldSectionItem extends DepanFxNodeListSectionItem {

    public FoldSectionItem(DepanFxFoldSection depanFxFoldSection) {
      super(depanFxFoldSection);
    }

    @Override
    public void fillNodeContextMenu(
        ContextMenu contextMenu,
        Scene scene,
        DepanFxNodeListTableAdapter tableAdapter,
        GraphNode node) {
      DepanFxContextMenuBuilder builder =
          new DepanFxContextMenuBuilder(contextMenu);

      builder.appendActionItem(SELECT_FOLD_SECTION,
          e -> openFoldSectionFinder(scene, tableAdapter));
      builder.appendActionItem(EDIT_FOLD_SECTION,
          e -> openFoldSectionEditor(tableAdapter));

      builder.appendSubMenu(
          buildNewSectionMenu(scene, tableAdapter, getSection()));

      builder.appendSeparator();
      builder.appendActionItem(
          EXPORT_TO_CSV,
          e -> runExportToCsvAction(tableAdapter));

      builder.appendSeparator();
      builder.appendActionItem(
          "Save Fold Info",
          e -> getFoldSection().saveFoldInfoResource());
    }

    @Override
    protected ObservableList<TreeItem<DepanFxNodeListMember>> buildChildren() {
      DepanFxFoldSection foldSection = getFoldSection();

      DepanFxTreeModel treeModel = foldSection.getTreeModel();
      Collection<GraphNode> nodes = treeModel.getRoots();

      List<TreeItem<DepanFxNodeListMember>> result =
          new ArrayList<>(nodes.size());
      nodes.stream()
          .map(foldSection::buildNodeItem)
          .forEach(result::add);
      foldSection.sortTreeItems(result);

      return FXCollections.observableList(result);
    }

    private void openFoldSectionFinder(
        Scene scene, DepanFxNodeListTableAdapter tableAdapter) {
      DepanFxWorkspace workspace = tableAdapter.getWorkspace();

      prepareSectionChooser(
          tableAdapter, DepanFxFoldSectionToolDialog.FOLD_SECTION_RSRC_FILTER)
          .showOpenDialog(scene)
          .map(DepanFxProjectDocument.class::cast)
          .flatMap(p -> workspace.getWorkspaceResource(
              p, DepanFxFoldSectionData.class))
          .ifPresent(d -> updateSectionDataResource(tableAdapter, d));
    }

    private void openFoldSectionEditor(
        DepanFxNodeListTableAdapter tableAdapter) {

      DepanFxFoldSection foldSection = getFoldSection();
      DepanFxFoldSectionToolDialog.runEditDialog(
          foldSection.getSectionResource(),
          tableAdapter.getDialogRunner())
        .getController()
        .getToolResource()
        .ifPresent(d -> tableAdapter.updateSection(foldSection, d));
    }

    private void runExportToCsvAction(
        DepanFxNodeListTableAdapter tableAdapter) {
      LOG.info("Fold section to CVS export not yet implemented");
    }

    private DepanFxFoldSection getFoldSection() {
      return (DepanFxFoldSection) getSection();
    }
  }

  /////////////////////////////////////
  // Fork components

  public static class FoldFork extends DepanFxNodeListFork {

    public FoldFork(GraphNode node, DepanFxFoldSection section) {
      super(node, section);
    }
  }

  private static class FoldForkItem extends DepanFxNodeListForkItem {

    public FoldForkItem(FoldFork fork) {
      super(fork);
    }

    @Override
    public void fillNodeContextMenu(
        ContextMenu contextMenu,
        Scene scene,
        DepanFxNodeListTableAdapter tableAdapter,
        GraphNode node) {
      DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder(contextMenu);

      appendRecursiveActionItems(builder, tableAdapter);

      builder.appendSeparator();
      appendCopyActionItems(builder);

      builder.appendSeparator();
      appendExpandTreeActionItems(builder);
    }

    @Override
    public void fillMultiContextMenu(
        ContextMenu contextMenu, Scene scene,
        DepanFxNodeListTableAdapter tableAdapter,
        ObservableList<TreeItem<DepanFxNodeListMember>> choices) {
      DepanFxContextMenuBuilder builder =
          new DepanFxContextMenuBuilder(contextMenu);

      appendRecursiveMultiActionItems(builder, tableAdapter, choices);
    }
  }

  /////////////////////////////////////
  // Item components

  public static class FoldLeaf extends DepanFxNodeListGraphNode {

    public FoldLeaf(GraphNode node, DepanFxFoldSection depanFxFoldSection) {
      super(node, depanFxFoldSection);
    }
  }

  private static class FoldLeafItem extends DepanFxNodeListLeafItem {

    public FoldLeafItem(FoldLeaf leaf) {
      super(leaf);
    }
  }
}
