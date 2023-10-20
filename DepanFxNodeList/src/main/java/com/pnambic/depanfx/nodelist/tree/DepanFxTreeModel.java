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

  TreeMode getTreeMode(GraphNode node);

  Collection<GraphNode> getRoots();

  Collection<GraphNode> getMembers(GraphNode node);

  DepanFxNodeList getReachableGraphNodes(Collection<GraphNode> start);
}
