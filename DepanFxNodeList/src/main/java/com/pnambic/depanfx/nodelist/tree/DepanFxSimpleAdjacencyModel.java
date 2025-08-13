package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLink;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData.NodeNest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DepanFxSimpleAdjacencyModel
    implements DepanFxAdjacencyModel {

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

  @Override
  public Stream<NodeNest> streamNodeParent() {
    return adjacencyData.entrySet().stream()
        .flatMap(e -> streamNodeParent(e.getKey(), e.getValue()));
  }

  private Stream<NodeNest> streamNodeParent(
      GraphNode nest, Collection<GraphNode> members) {
    return members.stream()
        .map(m -> new NodeNest(m, nest));
  }

  public void addAdjacencies(DepanFxAdjacencyModel source) {
    if (source instanceof DepanFxSimpleAdjacencyModel simple) {

      // Only import new adjacency data.
      simple.adjacencyData.forEach((key, value) -> {
        adjacencyData.computeIfAbsent(
            key, k -> new ArrayList<>()).addAll(value);
      });
      return;
    }
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

  public Stream<GraphNode> streamHeadNodes() {
    return adjacencyData.keySet().stream();
  }

  public List<GraphNode> computeRootNodes() {
    Set<GraphNode> headNodes = adjacencyData.keySet().stream()
        .collect(Collectors.toSet());
    adjacencyData.values().stream()
        .flatMap(Collection::stream)
        .filter(c -> headNodes.contains(c))
        .forEach(n -> headNodes.remove(n));
    return new ArrayList<>(headNodes);
  }
}
