package com.pnambic.depanfx.nodelist.adjacency;

import com.pnambic.depanfx.graph.api.Edge;
import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.context.ContextRelationId;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.link.DepanFxLink;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DepanFxAdjacencyModel {

  private final DepanFxLinkMatcher linkMatcher;

  private final Map<GraphNode, List<GraphNode>> adjacencyData = new HashMap<>();

  public DepanFxAdjacencyModel(DepanFxLinkMatcher linkMatcher) {
    this.linkMatcher = linkMatcher;
  }

  public void withGraphModel(GraphModel model) {

    for (Edge<? extends ContextNodeId, ? extends ContextRelationId> edge : model.getEdges()) {
      Optional<DepanFxLink> optLink = linkMatcher.match((GraphEdge) edge);
      optLink.ifPresent(l -> addAdjacency(l));
    }
  }

  private void addAdjacency(DepanFxLink link) {
    GraphNode source = link.getSource();
    GraphNode target = link.getTarget();
    List<GraphNode> links = adjacencyData.get(source);

    if (links == null) {
      links = new ArrayList<>();
      adjacencyData.put(source, links);
    }
    links.add(target);
  }

  public Map<GraphNode, List<GraphNode>> getAdjacencyData() {
    return adjacencyData;
  }
}
