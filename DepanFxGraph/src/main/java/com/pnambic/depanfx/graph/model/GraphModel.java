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

import com.pnambic.depanfx.graph.api.Edge;
import com.pnambic.depanfx.graph.api.Node;
import com.pnambic.depanfx.graph.basic.BasicGraph;
import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.context.ContextRelationId;
import com.pnambic.depanfx.graph.info.GraphEdgeInfo;
import com.pnambic.depanfx.graph.info.GraphModelInfo;
import com.pnambic.depanfx.graph.info.GraphNodeInfo;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class GraphModel extends BasicGraph<ContextNodeId, ContextRelationId> {

  private final Map<GraphEdge, Map<Class<?>, GraphEdgeInfo>> edgeInfoMap;

  private final Map<GraphNode, Map<Class<?>, GraphNodeInfo>> nodeInfoMap;

  private final Map<Class<?>, GraphModelInfo> modelInfoMap;

  public GraphModel(
      Map<ContextNodeId, Node<? extends ContextNodeId>> nodes,
      Set<Edge<? extends ContextNodeId, ? extends ContextRelationId>> edges,
      Map<GraphEdge, Map<Class<?>, GraphEdgeInfo>> edgeInfoMap,
      Map<GraphNode, Map<Class<?>, GraphNodeInfo>> nodeInfoMap,
      Map<Class<?>, GraphModelInfo> modelInfoMap) {
    super(nodes, edges);
    this.edgeInfoMap = edgeInfoMap;
    this.nodeInfoMap = nodeInfoMap;
    this.modelInfoMap = modelInfoMap;
  }

  public Optional<GraphEdgeInfo> getEdgeInfo(
      GraphEdge edge, Class<?> infoType) {
    return Optional.ofNullable(edgeInfoMap.get(edge))
        .map(m -> m.get(infoType));
  }

  public boolean hasEdgeInfo(GraphEdge edge) {
    return hasInfo(edgeInfoMap.get(edge));
  }

  public Stream<GraphEdgeInfo> streamEdgeInfo(GraphEdge edge) {
    Map<Class<?>, GraphEdgeInfo> result = edgeInfoMap.get(edge);
    if (result != null) {
      return result.values().stream();
    }
    return Stream.empty();
  }

  public Optional<GraphNodeInfo> getNodeInfo(
      GraphNode node, Class<?> infoType) {
    return Optional.ofNullable(nodeInfoMap.get(node))
        .map(m -> m.get(infoType));
  }

  public boolean hasNodeInfo(GraphNode node) {
    return hasInfo(nodeInfoMap.get(node));
  }

  public Stream<GraphNodeInfo> streamNodeInfo(GraphNode node) {
    Map<Class<?>, GraphNodeInfo> result = nodeInfoMap.get(node);
    if (result != null) {
      return result.values().stream();
    }
    return Stream.empty();
  }

  public Optional<GraphModelInfo> getModelInfo(Class<?> infoType) {
    return Optional.ofNullable(modelInfoMap.get(infoType));
  }

  public boolean hasModelInfo() {
    return hasInfo(modelInfoMap);
  }

  public Stream<GraphModelInfo> streamModelInfo() {
    return modelInfoMap.values().stream();
  }

  private boolean hasInfo(Map<Class<?>, ?> data) {
    if (data != null && !data.isEmpty()) {
      return true;
    }
    return false;
  }
}
