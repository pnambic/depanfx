/*
 * Copyright 2007, 2023 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.graph_doc.builder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pnambic.depanfx.graph.api.Edge;
import com.pnambic.depanfx.graph.api.Node;
import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.context.ContextRelationId;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphNode;

/**
 * The obvious imputed implementation for a {@link GraphBuilder}.
 * The internal data structures align with the normal {@link GraphModel}
 * constructors, so generating the result is simple.  The methods here
 * handle the routine generic type conversions.
 *
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class SimpleGraphModelBuilder implements DepanFxGraphModelBuilder {

  private final Map<ContextNodeId, Node<? extends ContextNodeId>> nodes
      = new HashMap<>();

  private final Set<Edge<? extends ContextNodeId, ? extends ContextRelationId>>
      edges = new HashSet<>();

  @SuppressWarnings("serial")
  public static class DuplicateNodeException
      extends IllegalArgumentException {

    /**
     * @param id
     */
    public DuplicateNodeException(String nodeId) {
      super("duplicate node id " + nodeId);
    }
  }

  @Override
  public GraphEdge addEdge(GraphEdge edge) {
    edges.add(edge);
    return edge;
  }

  @Override
  public GraphNode findNode(ContextNodeId id) {
    return (GraphNode) nodes.get(id);
  }

  @Override
  public GraphNode newNode(GraphNode node) {
    if (null != findNode(node.getId())) {
      throw new DuplicateNodeException(node.getId().toString());
    }

    nodes.put(node.getId(), node);
    return node;
  }

  @Override
  public GraphNode mapNode(GraphNode mapNode) {
    GraphNode result = findNode(mapNode.getId());
    if (null != result) {
      return result;
    }

    // Yes, findNode() is called twice if this path is taken.
    // A missing node should be a cheap lookup, and it ensures no
    // duplicates.
    return newNode(mapNode);
  }

  @Override
  public GraphModel createGraphModel() {
    return new GraphModel(nodes, edges);
  }
}
