/*
 * Copyright 2006, 2023 The Depan Project Authors
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
package com.pnambic.depanfx.graph.basic;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.pnambic.depanfx.graph.api.Edge;
import com.pnambic.depanfx.graph.api.Graph;
import com.pnambic.depanfx.graph.api.Node;
import com.pnambic.depanfx.graph.api.Relation;

/*
 * @param <N> Node ID type.
 * @param <R> Relation ID type.
 */
public class BasicGraph<N,R> implements Graph<N,R> {

  private final Map<N, Node<? extends N>> nodes;

  private final Set<Edge<? extends N, ? extends R>> edges;

  /**
   * 
   */
  public BasicGraph(
      Map<N, Node<? extends N>> nodes,
      Set<Edge<? extends N, ? extends R>> edges) {
    this.nodes = nodes;
    this.edges = edges;
  }

  /////////////////////////////////////
  // Inherited Graph<> methods

  @Override
  public Node<? extends N> findNode(N id) {
    Node<? extends N> result = nodes.get(id);
    return result;
  }

  @Override
  public Edge<? extends N, ? extends R> findEdge(Relation<? extends R> relation,
      final Node<? extends N> head, final Node<? extends N> tail) {
    for (Edge<? extends N, ? extends R> edge : edges) {
      if ((relation == edge.getRelation()) &&
          (head == edge.getHead()) &&
          (tail == edge.getTail())) {
        return edge;
      }
    }

    // Not found
    return null;
  }

  /////////////////////////////////////
  // BasicGraph methods

  /**
   * Returns the collection of Nodes in this graph.
   * 
   * @return the collection of Nodes in this graph.
   */
  public Collection<? extends Node<? extends N>> getNodes() {
    return Collections.unmodifiableCollection(nodes.values());
  }

  /**
   * Returns the collection of Nodes in this graph.
   * 
   * @return the collection of Nodes in this graph.
   */
  public Collection<? extends Edge<? extends N, ? extends R>> getEdges() {
    return Collections.unmodifiableCollection(edges);
  }
}
