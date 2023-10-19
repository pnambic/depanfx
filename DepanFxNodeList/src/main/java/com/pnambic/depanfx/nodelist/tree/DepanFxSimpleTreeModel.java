package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.graph.model.GraphNode;

import java.util.Collection;
import java.util.Map;

public class DepanFxSimpleTreeModel implements DepanFxTreeModel {

  private final Map<GraphNode, Collection<GraphNode>> nodeMembers;

  private final Collection<GraphNode> roots;

  private final Map<GraphNode, TreeMode> treeModes;

  public DepanFxSimpleTreeModel(
      Map<GraphNode, Collection<GraphNode>> nodeMembers,
      Collection<GraphNode> roots,
      Map<GraphNode, TreeMode> treeModes) {
    this.treeModes = treeModes;
    this.roots = roots;
    this.nodeMembers = nodeMembers;
  }

  @Override
  public Collection<GraphNode> getMembers(GraphNode node) {
    return nodeMembers.get(node);
  }

  @Override
  public Collection<GraphNode> getRoots() {
    return roots;
  }

  @Override
  public TreeMode getTreeMode(GraphNode node) {
    return treeModes.get(node);
  }
}
