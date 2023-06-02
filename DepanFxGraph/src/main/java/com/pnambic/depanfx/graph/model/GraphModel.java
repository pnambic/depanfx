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

import java.util.Map;
import java.util.Set;

import com.pnambic.depanfx.graph.api.Edge;
import com.pnambic.depanfx.graph.api.Node;
import com.pnambic.depanfx.graph.basic.BasicGraph;
import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.context.ContextRelationId;

public class GraphModel extends BasicGraph<ContextNodeId, ContextRelationId> {

  public GraphModel(
      Map<ContextNodeId, Node<? extends ContextNodeId>> nodes,
      Set<Edge<? extends ContextNodeId, ? extends ContextRelationId>> edges) {
    super(nodes, edges);
  }
}
