package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.link.DepanFxLink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DepanFxSimpleAdjacencyModel implements DepanFxAdjacencyModel {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxSimpleAdjacencyModel.class);

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
    LOG.debug("Add adjacency {} to {}",
        link.getSource().getId().getNodeKey(),
        link.getTarget().getId().getNodeKey());
    addAdjacency(link.getSource(), link.getTarget());
  }
}
