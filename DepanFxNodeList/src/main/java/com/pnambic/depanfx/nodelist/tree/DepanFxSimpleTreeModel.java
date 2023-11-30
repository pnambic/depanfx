package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Collection;

public class DepanFxSimpleTreeModel implements DepanFxTreeModel, DepanFxAdjacencyModel {

  private final DepanFxWorkspaceResource workspaceResource;

  private final DepanFxAdjacencyModel nodeMembers;

  private final Collection<GraphNode> roots;

  public DepanFxSimpleTreeModel(
      DepanFxWorkspaceResource workspaceResource,
      DepanFxAdjacencyModel nodeMembers,
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
    return nodeMembers.getAdjacentNodes(node);
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
  public DepanFxNodeList getReachableGraphNodes(
      Collection<GraphNode> startNodes, Collection<GraphNode> filterNodes) {

    DepanFxDepthFirstTree treeDft = new DepanFxDepthFirstTree(this, filterNodes);
    treeDft.buildFromNodes(startNodes);
    return new DepanFxNodeList(
        workspaceResource, treeDft.getTreeMembers());
  }

  @Override // DepanFxAdjacencyModel
  public Collection<GraphNode> getAdjacentNodes(GraphNode node) {
    return getMembers(node);
  }
}
