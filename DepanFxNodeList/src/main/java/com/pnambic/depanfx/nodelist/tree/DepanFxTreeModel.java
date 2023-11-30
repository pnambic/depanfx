package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Collection;

public interface DepanFxTreeModel {

  public enum TreeMode {
    FORK,
    LEAF
  }

  DepanFxWorkspaceResource getWorkspaceResource();

  /**
   * Provide the {@link TreeMode} for the supplied graph node.
   */
  TreeMode getTreeMode(GraphNode node);

  /**
   * Provides the collection of graph nodes that have no containers.
   * 
   * These graph nodes may not have any members.
   */
  Collection<GraphNode> getRoots();

  /**
   * Direct members of the supplied graph node.
   */
  Collection<GraphNode> getMembers(GraphNode node);

  /**
   * Transitively reachable nodes from a start group.  The start nodes are
   * the roots of disjoint trees, none of them will be included in the result.
   *
   * The collection of filter nodes should include the set of start nodes.
   * This is enforced via {@code DepanFxDepthFirstTree.buildFromNodes()}.
   */
  DepanFxNodeList getReachableGraphNodes(
      Collection<GraphNode> startNodes, Collection<GraphNode> filterNodes);
}
