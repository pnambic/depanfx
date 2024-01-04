package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLink;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData.ContainerOrder;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModelBuilder;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.TreeItem;

public class DepanFxTreeSection implements DepanFxNodeListSection {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxTreeSection.class);

  private static final DepanFxLinkMatcher EMPTY_MATCHER =
      new DepanFxLinkMatcher() {

        @Override
        public Optional<DepanFxLink> match(GraphEdge edge) {
          return Optional.empty();
        }
      };

  private final DepanFxNodeListViewer listViewer;

  private DepanFxWorkspaceResource sectionDataRsrc;

  // Update this whenever sectionDataRsrc is revised.
  private Comparator<TreeItem<DepanFxNodeListMember>> treeMemberCompare;

  private DepanFxTreeModel treeModel;

  private DepanFxNodeList sectionNodes;

  static final String NEW_TREE_SECTION_DATA =
  "New Tree Section Data...";

  static final String EDIT_TREE_SECTION_DATA =
  "Edit Tree Section Data...";

  public DepanFxTreeSection(
      DepanFxNodeListViewer listViewer,
      DepanFxWorkspaceResource sectionDataRsrc) {
    this.listViewer = listViewer;
    this.sectionDataRsrc = sectionDataRsrc;

    this.treeMemberCompare = updateCompare();
  }

  public void setSectionDataRsrc(DepanFxWorkspaceResource sectionDataRsrc) {
    this.sectionDataRsrc = sectionDataRsrc;
    this.treeMemberCompare = updateCompare();
  }

  public DepanFxTreeSectionData getSectionData() {
    return (DepanFxTreeSectionData) sectionDataRsrc.getResource();
  }

  public DepanFxTreeModel getTreeModel() {
    return treeModel;
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
    Path nodePath = Path.of(node.getId().getNodeKey());
    String result = nodePath.getFileName().toString();
    return result;
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
    GraphDocument graphDoc =
        (GraphDocument) baseNodes.getGraphDocResource().getResource();
    treeModel = builder.traverseGraph(graphDoc.getGraph(), baseNodes);
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
    DepanFxWorkspaceResource linkMatcherRsrc =
        getSectionData().getLinkMatcherRsrc(listViewer.getWorkspace());

    // If the section data provides a matcher, use that.
    // Otherwise, find a matcher based on the graph's context model.
    if (linkMatcherRsrc != null) {
      DepanFxLinkMatcher matcher =
          ((DepanFxLinkMatcherDocument) linkMatcherRsrc.getResource())
          .getMatcher();
      if (matcher != null) {
        return matcher;
      }
    }

    // Use the context model from the viewer to find a good link matcher.
    ContextModelId modelId = listViewer.getGraphDoc().getContextModelId();
    return DepanFxProjects.getBuiltIn(
            listViewer.getWorkspace(), DepanFxLinkMatcherDocument.class,
            c -> this.byMemberLinkMatcherDoc(c, modelId))
        .map(r -> (DepanFxLinkMatcherDocument) r.getResource())
        .map(d -> d.getMatcher())
        .orElseGet(this::getEmptyLinkMatcher);
  }

  private DepanFxLinkMatcher getEmptyLinkMatcher() {
    LOG.warn("Unable to find link matcher for {} context model",
        listViewer.getGraphDoc().getContextModelId().getContextModelKey());
    return EMPTY_MATCHER;
  }

  private boolean byMemberLinkMatcherDoc(
      DepanFxBuiltInContribution contrib, Object modelId) {
    DepanFxLinkMatcherDocument linkMatchDoc =
        ((DepanFxLinkMatcherDocument) contrib.getDocument());
    if (!linkMatchDoc.getMatchGroups()
        .contains(DepanFxLinkMatcherGroup.MEMBER)) {
      return false;
    }
    // [29-Nov-2023] Kludge for matches any, actual matcher provided later.
    if (linkMatchDoc.getModelId() == null) {
      return false;  // Keep searching for a better matcher.
    }
    return linkMatchDoc.getModelId().equals(modelId);
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
