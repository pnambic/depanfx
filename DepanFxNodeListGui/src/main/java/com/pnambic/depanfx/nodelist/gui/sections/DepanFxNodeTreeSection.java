package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.edgematchers.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.edgematchers.link.DepanFxLinkMatchers;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxBaseMatcherDocument;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcher;
import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeFoldController;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxContainerOrder;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel.TreeMode;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.scene.control.TreeItem;

public abstract class DepanFxNodeTreeSection implements DepanFxNodeListSection {

  static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeTreeSection.class);

  private final DepanFxNodeListTableAdapter tableAdapter;

  private Comparator<TreeItem<DepanFxNodeListMember>> treeMemberCompare;

  private DepanFxNodeList sectionNodes;

  public DepanFxNodeTreeSection(
      DepanFxNodeListTableAdapter tableAdapter) {
    this.tableAdapter = tableAdapter;
  }

  public abstract DepanFxTreeModel getTreeModel();

  public List<DepanFxNodeListColumn> getColumns() {
    return tableAdapter.streamColumns().collect(Collectors.toList());
  }

  @Override
  abstract public DepanFxNodeListSectionItem buildSectionItem(
      DepanFxNodeList baseNodes);

  @Override
  abstract public TreeItem<DepanFxNodeListMember> buildNodeItem(GraphNode node);

  @Override
  public String getDisplayName(GraphNode node) {
    if (getTreeModel().getRoots().contains(node)) {
      return node.getId().getNodeKey();
    }

    return node.getId().getSimpleName();
  }

  @Override
  public void sortTreeItems(List<TreeItem<DepanFxNodeListMember>> items) {
    items.sort(treeMemberCompare);
  }

  @Override
  public DepanFxNodeList getSectionNodes() {
    return sectionNodes;
  }

  /////////////////////////////////////
  // For derived classes.

  protected DepanFxWorkspace getWorkspace() {
    return tableAdapter.getWorkspace();
  }

  protected GraphDocument getGraphDoc() {
    return tableAdapter.getGraphDoc();
  }

  protected TreeMode getTreeMode(GraphNode node) {
    return getTreeModel().getTreeMode(node);
  }

  protected void updateSectionNodes(DepanFxNodeList sectionNodes) {
    this.sectionNodes = sectionNodes;
  }

  protected DepanFxLinkMatcher buildLinkMatcher(
      DepanFxBaseMatcherDocument matcherInfo) {
    Optional<DepanFxLinkMatcher> foundInfo =
        tableAdapter.lookupMatcher(matcherInfo);
    if (foundInfo.isPresent()) {
      return foundInfo.get();
    }

    // Use the context model from the viewer to find a good link matcher.
    // Should check that matcherInfo part of matcher group Members.
    ContextModelId modelId = getGraphDoc().getContextModelId();
    return DepanFxLinkMatcherGroup.getMemberMatcherRsrc(
        getWorkspace(), modelId)
        .map(r -> tableAdapter.buildLinkMatcher(r.getResource()))
        .orElseGet(this::getEmptyLinkMatcher);
  }

  private DepanFxLinkMatcher getEmptyLinkMatcher() {
    LOG.warn("Unable to find link matcher for {} context model",
        getGraphDoc().getContextModelId().getContextModelKey());
    return DepanFxLinkMatchers.EMPTY_MATCHER;
  }

  protected DepanFxNodeFoldController getNodeFolding() {
    return tableAdapter.getNodeFolding();
  }

  protected void updateTreeCompare(
      OrderDirection direction, DepanFxContainerOrder cntrOrder) {
    treeMemberCompare = new TreeMemberCompare(direction, cntrOrder);
  }

  protected void resetTableView() {
    tableAdapter.resetTableView();
  }

  protected abstract void updateTreeModel(
      DepanFxTreeModel treeModel, DepanFxNodeList sectionNodes);

  private static class TreeMemberCompare
      extends DepanFxNodeListSections.CompareMembers {

    private final DepanFxContainerOrder containerOrder;

    private TreeMemberCompare(
        OrderDirection direction, DepanFxContainerOrder containerOrder) {
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
      if (memberOne instanceof DepanFxTreeFork) {
        if (memberTwo instanceof DepanFxTreeFork) {
          // Both members are forks.
          return compareBySortKey(memberOne, memberTwo);
        }
        // Only memberOne is a fork.
        return compareContainerOrder();
      }
      // Only memberTwo is a fork.
      if (memberTwo instanceof DepanFxTreeFork) {
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
      DepanFxContainerOrder forkOrder = containerOrder;
      if (forkOrder.equals(DepanFxContainerOrder.FIRST)) {
        return -1; // Containers are before documents.
      }
      if (forkOrder.equals(DepanFxContainerOrder.LAST)) {
        return 1; // Containers are after documents
      }
      LOG.warn(
          "Misuse of compareContainerOrder with bad value for fork ordering {}",
          forkOrder);
      return 0;
    }
  }
}
