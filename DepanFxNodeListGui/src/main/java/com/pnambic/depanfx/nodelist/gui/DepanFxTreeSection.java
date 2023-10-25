package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModelBuilder;

import java.nio.file.Path;

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
    DepanFxTreeModel treeModel =
        builder.traverseGraph(graphModel.getGraph(), baseNodes);

    return new DepanFxTreeSectionItem(this, treeModel);
  }
}
