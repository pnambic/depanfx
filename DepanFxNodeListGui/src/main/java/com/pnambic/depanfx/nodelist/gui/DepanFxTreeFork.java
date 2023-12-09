package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javafx.scene.control.TreeItem;

public class DepanFxTreeFork extends DepanFxNodeListGraphNode {

  public DepanFxTreeFork(GraphNode node, DepanFxTreeSection section) {
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
    return ((DepanFxTreeSection) getSection()).getTreeModel();
  }

  public void sortTreeItems(
      List<TreeItem<DepanFxNodeListMember>> items) {
    getSection().sortTreeItems(items);
  }

  public TreeItem<DepanFxNodeListMember> buildTreeMember(GraphNode node) {
    return getSection().buildNodeItem(node);
  }
}
