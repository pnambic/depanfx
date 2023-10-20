package com.pnambic.depanfx.nodelist.tree;

import com.pnambic.depanfx.graph.api.Edge;
import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.context.ContextRelationId;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.link.DepanFxLink;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DepanFxAdjacencyModel {

  private final DepanFxLinkMatcher linkMatcher;

  private final Map<GraphNode, Collection<GraphNode>> adjacencyData =
      new HashMap<>();

  public DepanFxAdjacencyModel(DepanFxLinkMatcher linkMatcher) {
    this.linkMatcher = linkMatcher;
  }

  public void withGraphModel(GraphModel model) {

    for (Edge<? extends ContextNodeId, ? extends ContextRelationId> edge : model.getEdges()) {
      Optional<DepanFxLink> optLink = linkMatcher.match((GraphEdge) edge);
      optLink.ifPresent(l -> addAdjacency(l));
    }
  }

  public Collection<GraphNode> getAdjacentNodes(GraphNode node) {
    Collection<GraphNode> result = adjacencyData.get(node);
    if (result != null) {
      return result;
    }
    return Collections.emptyList();
  }


  private void addAdjacency(DepanFxLink link) {
    adjacencyData
        .computeIfAbsent(link.getSource(), k-> new ArrayList<>())
        .add(link.getTarget());
  }
}
