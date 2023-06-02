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
package com.pnambic.depanfx.graph.api;

/**
 * @param <N> Node ID type.
 * @param <R> Relation ID type.
 */
public interface Edge<N, R> {

  Relation<? extends R> getRelation();

  Node<? extends N> getHead();

  Node<? extends N> getTail();

  void accept(EdgeVisitor vistor);
}
