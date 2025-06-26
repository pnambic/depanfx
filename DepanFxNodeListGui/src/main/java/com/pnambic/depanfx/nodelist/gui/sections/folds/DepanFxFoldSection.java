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
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListItem;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSectionItem;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSections;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFoldSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.nodelist.tree.DepanFxNodeParentsToTreeModelBuilder;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxFoldSection implements DepanFxNodeListSection {

  public static final String NEW_TREE_SECTION_DATA = "New Fold Section Data...";

  public static final String EDIT_TREE_SECTION_DATA = "Edit Fold Section Data...";

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

    this.treeMemberCompare = updateCompare();
  }

  public void setSectionDataRsrc(
      DepanFxWorkspaceResource<DepanFxFoldSectionData> sectionDataRsrc) {
    this.sectionDataRsrc = sectionDataRsrc;
    this.treeMemberCompare = updateCompare();
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
    DepanFxFoldSectionData foldInfo = getSectionResource().getResource();
    DepanFxNodeParentsToTreeModelBuilder builder = 
        new DepanFxNodeParentsToTreeModelBuilder(foldInfo.getGraphResource());
    builder.importNodeParents(foldInfo.streamNodeNests());
    treeModel = builder.build();
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

    private boolean treeLoaded = false;

    public FoldSectionItem(DepanFxFoldSection depanFxFoldSection) {
      super(depanFxFoldSection);
    }

    @Override
    public boolean isLeaf() {
      return false;
    }

    @Override
    public ObservableList<TreeItem<DepanFxNodeListMember>> getChildren() {
      if (!treeLoaded) {
        treeLoaded = true;
        super.getChildren().setAll(buildChildren());
      }

      return super.getChildren();
    }

    private ObservableList<TreeItem<DepanFxNodeListMember>> buildChildren() {
      DepanFxNodeListSection section = getSection();

      DepanFxTreeModel treeModel = ((DepanFxFoldSection) section).getTreeModel();
      Collection<GraphNode> nodes = treeModel.getRoots();

      List<TreeItem<DepanFxNodeListMember>> result =
          new ArrayList<>(nodes.size());
      nodes.stream()
      .map(section::buildNodeItem)
      .forEach(result::add);
      section.sortTreeItems(result);

      return FXCollections.observableList(result);
    }
  }

  /////////////////////////////////////
  // Fork components

  private static class FoldForkItem extends DepanFxNodeListItem {

    private static final Logger LOG =
        LoggerFactory.getLogger(FoldForkItem.class);

    private boolean treeLoaded = false;

    public FoldForkItem(FoldFork fork) {
      super(fork);
    }

    @Override
    public boolean isLeaf() {
      return false;
    }

    @Override
    public ObservableList<TreeItem<DepanFxNodeListMember>> getChildren() {
      if (!treeLoaded) {
        treeLoaded = true;
        super.getChildren().setAll(buildChildren());
      }

      return super.getChildren();
    }

    private ObservableList<TreeItem<DepanFxNodeListMember>> buildChildren() {
      FoldFork folder = (FoldFork) getValue();
      LOG.debug("building children for {}", folder.getDisplayName());

      Collection<GraphNode> nodes = folder.getMembers();

      List<TreeItem<DepanFxNodeListMember>> result =
          new ArrayList<>(nodes.size());
      nodes.stream()
          .map(folder::buildTreeMember)
          .forEach(result::add);
      folder.sortTreeItems(result);

      return FXCollections.observableList(result);
    }
  }

  public static class FoldFork extends DepanFxNodeListGraphNode {

    public FoldFork(GraphNode node, DepanFxFoldSection section) {
      super(node, section);
    }

    /**
     * Direct members of graph node for this tree fork.
     */
    public Collection<GraphNode> getMembers() {
      return getTreeModel().getMembers(getGraphNode());
    }

    /**
     * Transitive collection of all members below the graph node for this
     * tree fork.  The graph node for this tree fork will not included
     * (unless there is a loop in the graph .. oops).
     */
    public Collection<GraphNode> getDecendants() {

      Set<GraphNode> roots = Collections.singleton(getGraphNode());
      Collection<GraphNode> filter =
          getSection().getSectionNodes().getNodes();
      return getTreeModel()
          .getReachableGraphNodes(roots, filter)
          .getNodes();
    }

    public DepanFxTreeModel getTreeModel() {
      return ((DepanFxFoldSection) getSection()).getTreeModel();
    }

    public void sortTreeItems(
        List<TreeItem<DepanFxNodeListMember>> items) {
      getSection().sortTreeItems(items);
    }

    public TreeItem<DepanFxNodeListMember> buildTreeMember(GraphNode node) {
      return getSection().buildNodeItem(node);
    }
  }

  /////////////////////////////////////
  // Item components

  private static class FoldLeafItem extends DepanFxNodeListItem {

    public FoldLeafItem(FoldLeaf leaf) {
      super(leaf);
    }

    @Override
    public boolean isLeaf() {
      return true;
    }
  }

  public static class FoldLeaf extends DepanFxNodeListGraphNode {

    public FoldLeaf(GraphNode node, DepanFxFoldSection depanFxFoldSection) {
      super(node, depanFxFoldSection);
    }
  }
}
