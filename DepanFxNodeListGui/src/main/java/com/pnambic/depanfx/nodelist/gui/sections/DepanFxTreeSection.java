package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxTreeSectionData.ContainerOrder;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatchers;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModelBuilder;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.TreeItem;

public class DepanFxTreeSection implements DepanFxNodeListSection {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxTreeSection.class);

  private final DepanFxNodeListTableAdapter tableAdapter;

  private DepanFxWorkspaceResource<DepanFxTreeSectionData> sectionDataRsrc;

  // Update this whenever sectionDataRsrc is revised.
  private Comparator<TreeItem<DepanFxNodeListMember>> treeMemberCompare;

  private DepanFxTreeModel treeModel;

  private DepanFxNodeList sectionNodes;

  static final String NEW_TREE_SECTION_DATA = "New Tree Section Data...";

  static final String EDIT_TREE_SECTION_DATA = "Edit Tree Section Data...";

  public DepanFxTreeSection(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxWorkspaceResource<DepanFxTreeSectionData> sectionDataRsrc) {
    this.tableAdapter = tableAdapter;
    this.sectionDataRsrc = sectionDataRsrc;

    this.treeMemberCompare = updateCompare();
  }

  public void setSectionDataRsrc(
      DepanFxWorkspaceResource<DepanFxTreeSectionData> sectionDataRsrc) {
    this.sectionDataRsrc = sectionDataRsrc;
    this.treeMemberCompare = updateCompare();
  }

  public DepanFxTreeSectionData getSectionData() {
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
    // Node registry may need to parse node to deliver best display name.
    // For example, Java method name would be better than full signature.
    // This works for current simple paths from FileSystem objects.
    String nodeKey = node.getId().getNodeKey();
    try {
      Path nodePath = Path.of(nodeKey);
      String result = nodePath.getFileName().toString();
      return result;
    } catch (Exception any) {
    }
    return nodeKey;
  }

  @Override
  public String getSortKey(GraphNode node) {
    OrderBy orderBy = getSectionData().getOrderBy();
    return DepanFxNodeListSections.getSortKey(node, orderBy);
  }

  @Override
  public DepanFxNodeListSectionItem buildSectionItem(
      DepanFxNodeList baseNodes) {
    DepanFxTreeModelBuilder builder =
        new DepanFxTreeModelBuilder(getLinkMatcher());
    treeModel = builder.traverseGraph(
        baseNodes.getGraphDocResource(), baseNodes.getNodes());
    sectionNodes = treeModel.getReachableGraphNodes(
        treeModel.getRoots(), baseNodes.getNodes());

    return new DepanFxTreeSectionItem(this);
  }

  @Override
  public TreeItem<DepanFxNodeListMember> buildNodeItem(GraphNode node) {
    switch (treeModel.getTreeMode(node)) {

    case FORK:
      DepanFxTreeFork treeFork = buildMemberTreeFork(node);
      return new DepanFxTreeForkItem(treeFork);

    case LEAF:
      DepanFxTreeLeaf treeLeaf = buildMemberTreeLeaf(node);
      return new DepanFxTreeLeafItem(treeLeaf);
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

  private DepanFxTreeFork buildMemberTreeFork(GraphNode fork) {
    return new DepanFxTreeFork(fork, this);
  }

  private DepanFxTreeLeaf buildMemberTreeLeaf(GraphNode leaf) {
    return new DepanFxTreeLeaf(leaf, this);
  }

  private DepanFxLinkMatcher getLinkMatcher() {
    DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> linkMatcherRsrc =
        getSectionData().getLinkMatcherRsrc();

    // If the section data provides a matcher, use that.
    // Otherwise, find a matcher based on the graph's context model.
    if (linkMatcherRsrc != null) {
      DepanFxLinkMatcher matcher = linkMatcherRsrc.getResource().getMatcher();
      if (matcher != null) {
        return matcher;
      }
    }

    // Use the context model from the viewer to find a good link matcher.
    ContextModelId modelId = tableAdapter.getGraphDoc().getContextModelId();
    return DepanFxLinkMatcherGroup
        .getMemberMatcherRsrc(tableAdapter.getWorkspace(), modelId)
        .map(r -> r.getResource().getMatcher())
        .orElseGet(this::getEmptyLinkMatcher);
  }

  private DepanFxLinkMatcher getEmptyLinkMatcher() {
    LOG.warn("Unable to find link matcher for {} context model",
        tableAdapter.getGraphDoc().getContextModelId().getContextModelKey());
    return DepanFxLinkMatchers.EMPTY_MATCHER;
  }

  /////////////////////////////////////
  // Sorting and ordering

  private Comparator<TreeItem<DepanFxNodeListMember>> updateCompare() {
    DepanFxTreeSectionData sectionData = getSectionData();
    return new TreeMemberCompare(
        sectionData.getOrderDirection(), sectionData.getContainerOrder());
  }

  private static class TreeMemberCompare
      extends DepanFxNodeListSections.CompareMembers {

    private final ContainerOrder containerOrder;

    private TreeMemberCompare(
        OrderDirection direction, ContainerOrder containerOrder) {
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
      ContainerOrder forkOrder = containerOrder;
      if (forkOrder.equals(ContainerOrder.FIRST)) {
        return -1; // Containers are before documents.
      }
      if (forkOrder.equals(ContainerOrder.LAST)) {
        return 1; // Containers are after documents
      }
      LOG.warn(
          "Misuse of compareContainerOrder with bad value for fork ordering {}",
          forkOrder);
      return 0;
    }
  }
}
