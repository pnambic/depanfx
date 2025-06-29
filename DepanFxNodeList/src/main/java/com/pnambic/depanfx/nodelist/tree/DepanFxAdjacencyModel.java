package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData.NodeNest;

import java.util.Collection;
import java.util.stream.Stream;

public interface DepanFxAdjacencyModel {

  /**
   * Do not mutate these results.
   */
  Collection<GraphNode> getAdjacentNodes(GraphNode node);

  Stream<NodeNest> streamNodeParent();
}
