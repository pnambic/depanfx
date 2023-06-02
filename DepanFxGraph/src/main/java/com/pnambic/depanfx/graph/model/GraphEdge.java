/*
 * Copyright 2023 The Depan Project Authors
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
package com.pnambic.depanfx.graph.model;

import java.util.Objects;

import com.pnambic.depanfx.graph.basic.BasicEdge;
import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.context.ContextRelationId;

public class GraphEdge extends BasicEdge<ContextNodeId, ContextRelationId> {

  /**
   * Creates a new instance of GraphEdge representing an edge between the
   * two provided nodes, linked together with the given relation.
   *
   * @param head first side of the edge.
   * @param tail second side of the edge.
   * @param r relation between the nodes.
   */

   public GraphEdge(GraphNode head, GraphNode tail, GraphRelation relation) {
    super(relation, head, tail);
  }

  @Override
  public String toString() {
    return "" + getHead() + " --[" + getRelation() + "]--> " + getTail();
  }

  @Override
  public GraphNode getHead() {
    return (GraphNode) super.getHead();
  }

  @Override
  public GraphNode getTail() {
    return (GraphNode) super.getTail();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getHead(), getTail(), getRelation());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof GraphEdge) {
      GraphEdge that = (GraphEdge) obj;
      return this.getHead().equals(that.getHead())
          && this.getTail().equals(that.getTail())
          && this.getRelation().equals(that.getRelation());
    }
    return super.equals(obj);
  }
}
