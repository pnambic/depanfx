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

import com.pnambic.depanfx.graph.api.Edge;
import com.pnambic.depanfx.graph.api.EdgeVisitor;
import com.pnambic.depanfx.graph.api.Node;
import com.pnambic.depanfx.graph.api.Relation;

/**
 * @param <N> Node ID type.
 * @param <R> Relation ID type.
 */
public class BasicEdge<N,R> implements Edge<N,R> {

  private final Node<? extends N> head;

  private final Node<? extends N> tail;

  private final Relation<? extends R> relation;

  public BasicEdge(Relation <? extends R> relation,
     Node<? extends N> head, Node<? extends N> tail) {
    this.relation = relation;
    this.head = head;
    this.tail = tail;
  }

  @Override
  public Relation <? extends R> getRelation() {
    return relation;
  }

  @Override
  public Node<? extends N> getHead() {
    return head;
  }

  @Override
  public Node<? extends N> getTail() {
    return tail;
  }

  @Override
  public void accept(EdgeVisitor vistor) {
    // Ignore unrecognized visitors.
  }
}
