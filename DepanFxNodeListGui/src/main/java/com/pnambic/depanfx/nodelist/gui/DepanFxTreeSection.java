package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModelBuilder;

public class DepanFxTreeSection extends DepanFxNodeListSection {

  private final DepanFxLinkMatcher linkMatcher;

  public DepanFxTreeSection(DepanFxLinkMatcher linkMatcher) {
    this.linkMatcher = linkMatcher;
  }

  @Override
  public String getDisplayName() {
    return "Tree";
  }

  @Override
  public String getDisplayName(GraphNode node) {
    return node.getId().getNodeKey();
  }

  @Override
  public String getSortKey(GraphNode node) {
    return GraphContextKeys.toNodeKey(node.getId());
  }

  public DepanFxNodeListSectionItem buildTreeItem(DepanFxNodeList baseNodes) {
    DepanFxTreeModelBuilder builder = new DepanFxTreeModelBuilder(linkMatcher);
    GraphDocument graphModel =
        (GraphDocument) baseNodes.getWorkspaceResource().getResource();
    DepanFxTreeModel treeModel =
        builder.traverseGraph(graphModel.getGraph(), baseNodes);

    return new DepanFxTreeSectionItem(this, treeModel);
  }
}
