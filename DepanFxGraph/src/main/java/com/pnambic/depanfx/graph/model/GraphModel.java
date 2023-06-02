package com.pnambic.depanfx.graph.model;

import java.util.Map;
import java.util.Set;

import com.pnambic.depanfx.graph.api.Edge;
import com.pnambic.depanfx.graph.api.Node;
import com.pnambic.depanfx.graph.basic.BasicGraph;
import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.context.ContextRelationId;

public class GraphModel extends BasicGraph<ContextNodeId, ContextRelationId> {

  public GraphModel(Map<ContextNodeId, Node<? extends ContextNodeId>> nodes,
      Set<Edge<? extends ContextNodeId, ? extends ContextRelationId>> edges) {
    super(nodes, edges);
  }
}
