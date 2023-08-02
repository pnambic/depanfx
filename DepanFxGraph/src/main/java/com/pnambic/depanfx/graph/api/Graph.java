/*
 * Copyright 2006, 2008, 2023 The Depan Project Authors
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
package com.pnambic.depanfx.graph.api;

/*
 * @param <N> Node ID type.
 * @param <R> Relation ID type.
 */
public interface Graph<N, R> {
  
  /**
   * Find an existing Node.
   * 
   * @param id identifier for existing Node
   * @return an existing Node, or <code>null</code> if not in Graph
   */
  Node<? extends N> findNode(N id);

  /**
   * Find an edge given the relation, head, and tail.
   * 
   * @param relation connection between head and tail
   * @param head starting node
   * @param tail final node
   * @return Edge for relationship, or <code>null</code> if not in Graph
   */
  Edge<?extends N, ? extends R> findEdge(Relation<? extends R> relation,
      Node<? extends N> head, Node<? extends N> tail);
}
