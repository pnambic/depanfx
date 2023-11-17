package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModelBuilder;

import java.nio.file.Path;
import java.text.MessageFormat;

import javafx.scene.control.TreeItem;

public class DepanFxTreeSection implements DepanFxNodeListSection {

  private final DepanFxLinkMatcher linkMatcher;

  private DepanFxTreeModel treeModel;

  private DepanFxNodeList sectionNodes;

  public DepanFxTreeSection(DepanFxLinkMatcher linkMatcher) {
    this.linkMatcher = linkMatcher;
  }

  @Override
  public String getSectionLabel() {
    return "Tree";
  }

  @Override
  public String getDisplayName() {
    return fmtDisplayName();
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
    return GraphContextKeys.toNodeKey(node.getId());
  }

  public DepanFxNodeListSectionItem buildTreeItem(DepanFxNodeList baseNodes) {
    DepanFxTreeModelBuilder builder = new DepanFxTreeModelBuilder(linkMatcher);
    GraphDocument graphModel =
        (GraphDocument) baseNodes.getWorkspaceResource().getResource();
    treeModel =
        builder.traverseGraph(graphModel.getGraph(), baseNodes);
    sectionNodes = treeModel.getReachableGraphNodes(treeModel.getRoots());

    return new DepanFxTreeSectionItem(this);
  }

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

  public DepanFxTreeModel getTreeModel() {
    return treeModel;
  }

  public DepanFxNodeList getSectionNodes() {
    return sectionNodes;
  }

  private DepanFxTreeFork buildMemberTreeFork(GraphNode fork) {
    return new DepanFxTreeFork(fork, this);
  }

  private DepanFxTreeLeaf buildMemberTreeLeaf(GraphNode leaf) {
    return new DepanFxTreeLeaf(leaf, this);
  }

  private String fmtDisplayName() {
    int listSize = getSectionNodes().getNodes().size();
    if (listSize == 1) {
      return MessageFormat.format(
          "{0} ({1} node)", getSectionLabel(), listSize);

    }
    return MessageFormat.format(
        "{0} ({1} nodes)", getSectionLabel(), listSize);
  }
}
