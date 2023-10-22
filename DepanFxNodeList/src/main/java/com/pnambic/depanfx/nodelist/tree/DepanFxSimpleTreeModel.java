package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class DepanFxSimpleTreeModel implements DepanFxTreeModel {

  private final DepanFxWorkspaceResource workspaceResource;

  private final Map<GraphNode, Collection<GraphNode>> nodeMembers;

  private final Collection<GraphNode> roots;

  public DepanFxSimpleTreeModel(
      DepanFxWorkspaceResource workspaceResource,
      Map<GraphNode, Collection<GraphNode>> nodeMembers,
      Collection<GraphNode> roots) {
    this.workspaceResource = workspaceResource;
    this.nodeMembers = nodeMembers;
    this.roots = roots;
  }

  public DepanFxWorkspaceResource getWorkspaceResource() {
    return workspaceResource;
  }

  @Override
  public Collection<GraphNode> getMembers(GraphNode node) {
    Collection<GraphNode> result = nodeMembers.get(node);
    if (result != null) {
      return result;
    }
    return Collections.emptyList();
  }

  @Override
  public Collection<GraphNode> getRoots() {
    return roots;
  }

  @Override
  public TreeMode getTreeMode(GraphNode node) {
    if (getMembers(node).size() > 0) {
      return TreeMode.FORK;
    }
    return TreeMode.LEAF;
  }

  @Override
  public DepanFxNodeList getReachableGraphNodes(Collection<GraphNode> start) {

    DepanFxTreeDepthFirstTraversal treeDft =
        new DepanFxTreeDepthFirstTraversal(this);
    treeDft.buildFromNodes(start);
    return new DepanFxNodeList(
        workspaceResource, treeDft.getTreeMembers());
  }
}
