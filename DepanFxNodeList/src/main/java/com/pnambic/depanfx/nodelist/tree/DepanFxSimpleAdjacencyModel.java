package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.link.DepanFxLink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DepanFxSimpleAdjacencyModel implements DepanFxAdjacencyModel {

  private final Map<GraphNode, Collection<GraphNode>> adjacencyData;

  public DepanFxSimpleAdjacencyModel() {
    this(new HashMap<>());
  }

  public DepanFxSimpleAdjacencyModel(
      Map<GraphNode, Collection<GraphNode>> adjacencyData) {
    this.adjacencyData = adjacencyData;
  }

  @Override
  public Collection<GraphNode> getAdjacentNodes(GraphNode node) {
    Collection<GraphNode> result = adjacencyData.get(node);
    if (result != null) {
      return result;
    }
    return Collections.emptyList();
  }

  public void addAdjacency(GraphNode source, GraphNode target) {
    adjacencyData
        .computeIfAbsent(source, k-> new ArrayList<>())
        .add(target);
  }

  public void addAdjacency(DepanFxLink link) {
    addAdjacency(link.getSource(), link.getTarget());
  }
}
