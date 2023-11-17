package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;

import java.util.Collection;

import javafx.scene.control.TreeItem;

public class DepanFxTreeFork extends DepanFxNodeListGraphNode {

  public DepanFxTreeFork(GraphNode node, DepanFxTreeSection section) {
    super(node, section);
  }

  public Collection<GraphNode> getMembers() {
    return getTreeModel().getMembers(getGraphNode());
  }

  public DepanFxTreeModel getTreeModel() {
    return ((DepanFxTreeSection) getSection()).getTreeModel();
  }

  public TreeItem<DepanFxNodeListMember> buildTreeMember(GraphNode node) {
    return ((DepanFxTreeSection) getSection()).buildNodeItem(node);
  }
}
