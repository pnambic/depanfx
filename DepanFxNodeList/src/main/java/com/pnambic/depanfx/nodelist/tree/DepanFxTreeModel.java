package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.graph.model.GraphNode;

import java.util.Collection;

public interface DepanFxTreeModel {

  public enum TreeMode {
    FORK,
    LEAF
  }

  TreeMode getTreeMode(GraphNode node);

  Collection<GraphNode> getRoots();

  Collection<GraphNode> getMembers(GraphNode node);
}
