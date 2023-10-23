package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.graph.model.GraphNode;

import java.util.Collection;

public interface DepanFxAdjacencyModel {

  Collection<GraphNode> getAdjacentNodes(GraphNode node);
}