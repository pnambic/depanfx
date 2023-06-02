package com.pnambic.depanfx.graph.model;

import java.util.Map;
import java.util.Set;

import com.pnambic.depanfx.graph.api.Edge;
import com.pnambic.depanfx.graph.api.Node;
import com.pnambic.depanfx.graph.basic.BasicGraph;
import com.pnambic.depanfx.graph.context.ContextNodeKindId;
import com.pnambic.depanfx.graph.context.ContextRelationId;

public class GraphModel extends BasicGraph<ContextNodeKindId, ContextRelationId> {

  public GraphModel(Map<ContextNodeKindId, Node<? extends ContextNodeKindId>> nodes,
      Set<Edge<? extends ContextNodeKindId, ? extends ContextRelationId>> edges) {
    super(nodes, edges);
  }
}
