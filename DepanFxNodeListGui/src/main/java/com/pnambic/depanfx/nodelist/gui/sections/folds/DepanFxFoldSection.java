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
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListFork;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListForkItem;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListLeafItem;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSectionItem;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSections;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFoldSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.nodelist.tree.DepanFxNodeParentsToTreeModelBuilder;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

public class DepanFxFoldSection implements DepanFxNodeListSection {

  public static final String SELECT_FOLD_SECTION = "Select Fold Section...";

  public static final String EDIT_FOLD_SECTION = "Edit Fold Section...";

  public static final String NEW_FOLD_SECTION_DATA = "New Fold Section Data...";

  public static final String EDIT_FOLD_SECTION_DATA = "Edit Fold Section Data...";

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxFoldSection.class);

  private final DepanFxNodeListTableAdapter tableAdapter;

  private DepanFxWorkspaceResource<DepanFxFoldSectionData> sectionDataRsrc;

  // Update this whenever sectionDataRsrc is revised.
  private Comparator<TreeItem<DepanFxNodeListMember>> treeMemberCompare;

  private DepanFxTreeModel treeModel;

  private DepanFxNodeList sectionNodes;

  public DepanFxFoldSection(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxWorkspaceResource<DepanFxFoldSectionData> sectionDataRsrc) {
    this.tableAdapter = tableAdapter;
    this.sectionDataRsrc = sectionDataRsrc;
    this.treeModel = buildTreeModel(sectionDataRsrc.getResource());

    this.treeMemberCompare = updateCompare();
  }

  public void setSectionDataResource(
      DepanFxWorkspaceResource<DepanFxFoldSectionData> sectionDataRsrc) {
    this.sectionDataRsrc = sectionDataRsrc;
    this.treeModel = buildTreeModel(sectionDataRsrc.getResource());

    this.treeMemberCompare = updateCompare();
  }

  private DepanFxTreeModel buildTreeModel(DepanFxFoldSectionData resource) {
    DepanFxNodeFoldData foldInfo = resource.getNodeFoldResource().getResource();
    DepanFxNodeParentsToTreeModelBuilder builder =
        new DepanFxNodeParentsToTreeModelBuilder(foldInfo.getGraphDocResource());
    builder.importNodeParents(foldInfo.streamNodeNests());
    return builder.build();
  }

  public DepanFxWorkspaceResource<DepanFxFoldSectionData> getSectionResource() {
    return sectionDataRsrc;
  }

  public DepanFxFoldSectionData getSectionData() {
    return sectionDataRsrc.getResource();
  }

  public DepanFxProjectDocument getProjDoc() {
    return sectionDataRsrc.getDocument();
  }

  public DepanFxTreeModel getTreeModel() {
    return treeModel;
  }

  public List<DepanFxNodeListColumn> getColumns() {
    return tableAdapter.streamColumns().collect(Collectors.toList());
  }

  @Override
  public String getSectionLabel() {
    return getSectionData().getSectionLabel();
  }

  @Override
  public String getDisplayName() {
    return DepanFxNodeListSections.fmtDisplayName(
        getSectionData().getSectionLabel(),
        sectionNodes.getNodes().size(),
        getSectionData().displayNodeCount());
  }

  @Override
  public String getDisplayName(GraphNode node) {
    if (treeModel.getRoots().contains(node)) {
      return node.getId().getNodeKey();
    }

    return node.getId().getSimpleName();
  }

  @Override
  public String getSortKey(GraphNode node) {
    OrderBy orderBy = getSectionData().getOrderBy();
    return DepanFxNodeListSections.getSortKey(node, orderBy);
  }

  @Override
  public DepanFxNodeListSectionItem buildSectionItem(
      DepanFxNodeList baseNodes) {
    sectionNodes = treeModel.getReachableGraphNodes(
        treeModel.getRoots(), baseNodes.getNodes());

    return new FoldSectionItem(this);
  }

  @Override
  public TreeItem<DepanFxNodeListMember> buildNodeItem(GraphNode node) {
    switch (treeModel.getTreeMode(node)) {

    case FORK:
      FoldFork foldFork = buildFoldFork(node);
      return new FoldForkItem(foldFork);

    case LEAF:
      FoldLeaf treeLeaf = buildFoldLeaf(node);
      return new FoldLeafItem(treeLeaf);
    }
    return null;
  }

  @Override
  public void sortTreeItems(List<TreeItem<DepanFxNodeListMember>> items) {
    items.sort(treeMemberCompare);
  }

  @Override
  public DepanFxNodeList getSectionNodes() {
    return sectionNodes;
  }

  public void addTreeModel(DepanFxTreeModel subModel) {
    if (treeModel instanceof DepanFxSimpleTreeModel simple) {
      simple.addTreeModel(subModel);
      tableAdapter.resetTableView();
      return;
    }
    // Waiting for a more generic modifiable tree model.
    LOG.info(
        "Cannot add section model to section {}", getSectionLabel());
  }

  public void addTreeModels(Stream<DepanFxTreeModel> subModels) {
    if (treeModel instanceof DepanFxSimpleTreeModel simple) {
      subModels.forEach(simple::addTreeModel);
      tableAdapter.resetTableView();
      return;
    }
    // Waiting for a more generic modifiable tree model.
    LOG.info(
        "Cannot add section model to section {}", getSectionLabel());
  }

  public Optional<DepanFxWorkspaceResource<DepanFxNodeFoldData>>
  saveFoldInfoResource() {

    List<DepanFxNodeFoldData.NodeNest> nodeParents =
        treeModel.streamNodeParent()
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
      return tableAdapter.getWorkspace().saveDocument(srcFoldDoc, updateFoldInfo);
    } catch (IOException err) {
      LOG.error("Unable to save fold info resource for section {}",
          getSectionLabel(), err);
    }
    return Optional.empty();
  }

  private FoldFork buildFoldFork(GraphNode fork) {
    return new FoldFork(fork, this);
  }

  private FoldLeaf buildFoldLeaf(GraphNode leaf) {
    return new FoldLeaf(leaf, this);
  }

  /////////////////////////////////////
  // Sorting and ordering

  private Comparator<TreeItem<DepanFxNodeListMember>> updateCompare() {
    DepanFxFoldSectionData sectionData = getSectionData();
    return new FoldMemberCompare(
        sectionData.getOrderDirection(), sectionData.getContainerOrder());
  }

  private static class FoldMemberCompare
  extends DepanFxNodeListSections.CompareMembers {

    private final DepanFxFoldSectionData.ContainerOrder containerOrder;

    private FoldMemberCompare(
        OrderDirection direction,
        DepanFxFoldSectionData.ContainerOrder containerOrder) {
      super(direction);
      this.containerOrder = containerOrder;
    }

    @Override
    protected int compareMembers(
        DepanFxNodeListMember memberOne, DepanFxNodeListMember memberTwo) {
      switch (containerOrder) {
      case FIRST:
        return compareByMemberKind(memberOne, memberTwo);
      case LAST:
        return compareByMemberKind(memberOne, memberTwo);
      case MIXED:
        return compareBySortKey(memberOne, memberTwo);
      }
      return 0;
    }

    private int compareByMemberKind(
        DepanFxNodeListMember memberOne, DepanFxNodeListMember memberTwo) {
      if (memberOne instanceof FoldFork) {
        if (memberTwo instanceof FoldFork) {
          // Both members are forks.
          return compareBySortKey(memberOne, memberTwo);
        }
        // Only memberOne is a fork.
        return compareContainerOrder();
      }
      // Only memberTwo is a fork.
      if (memberTwo instanceof FoldFork) {
        return - compareContainerOrder();
      }
      // Both members are leafs.
      return compareBySortKey(memberOne, memberTwo);
    }

    private int compareBySortKey(
        DepanFxNodeListMember memberOne, DepanFxNodeListMember memberTwo) {
      String oneKey = ((DepanFxNodeListGraphNode) memberOne).getSortKey();
      String twoKey = ((DepanFxNodeListGraphNode) memberTwo).getSortKey();
      return oneKey.compareTo(twoKey);
    }

    private int compareContainerOrder() {
      DepanFxFoldSectionData.ContainerOrder forkOrder = containerOrder;
      if (forkOrder.equals(DepanFxFoldSectionData.ContainerOrder.FIRST)) {
        return -1; // Containers are before documents.
      }
      if (forkOrder.equals(DepanFxFoldSectionData.ContainerOrder.LAST)) {
        return 1; // Containers are after documents
      }
      LOG.warn(
          "Misuse of compareContainerOrder with bad value for fork ordering {}",
          forkOrder);
      return 0;
    }
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

      appendRecursiveMulitActionItems(builder, tableAdapter, choices);
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
