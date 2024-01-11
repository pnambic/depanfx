package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.graph.model.GraphNode;

import java.util.Collection;

public interface DepanFxAdjacencyModel {

  /**
   * Do not mutate these results.
   */
  Collection<GraphNode> getAdjacentNodes(GraphNode node);
}
