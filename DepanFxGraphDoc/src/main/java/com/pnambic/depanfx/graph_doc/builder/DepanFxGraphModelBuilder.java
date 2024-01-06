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

import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.info.GraphEdgeInfo;
import com.pnambic.depanfx.graph.info.GraphModelInfo;
import com.pnambic.depanfx.graph.info.GraphNodeInfo;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphNode;

/**
 * Interface for a Class capable of constructing a graph.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public interface DepanFxGraphModelBuilder {
  /**
   * Add the given Edge to the list of edges in the graph.
   * 
   * @param edge new Edge.
   * @return the newly inserted Edge.
   */
  public GraphEdge addEdge(GraphEdge edge);

  /**
   * Provide a {@link GraphNode} that a matches the supplied id.
   * A {@code null} indicates that the supplied {@code node} is not part of the
   * graph.
   *
   * @param id Node to find
   * @return a for the supplied id, or {@code null} if no {@link GraphNode}
   *   for the id is present in the graph.
   */
  public GraphNode findNode(ContextNodeId id);

  /**
   * Insert the given Node in the graph.  The node must not exist in the graph
   * prior to this call.  Use {@link #mapNode(GraphNode)} if an
   * {@link GraphNode} for the supplied node is required to add an edge.
   *
   * @param node new Node.
   * @return the newly inserted Node.
   */
  public GraphNode newNode(GraphNode node);

  /**
   * Return an existing node if the newNode is already known to the graph.
   *
   * @param newNode new Node.
   * @return if newNode matches a known node, the known node is returned.
   *   Otherwise, newNode added to the graph and returned.
   */
  public GraphNode mapNode(GraphNode newNode);

  /**
   * Provide the complete GraphModel containing the added nodes and edges.
   * Multiple calls have undefined results.  A {@link DepanFxGraphModelBuilder} is expected
   * to be used once, and return an immutable graph.
   */
  public GraphModel createGraphModel();

  /**
   * Add supplemental information for an edge.
   *
   * @param edge primary key for data.
   * @param infoType
   *    Explicit type, since {@code edgeInfo} might be derived from type key.
   * @param edgeInfo typed data to associate with this edge.
   */
  public void addEdgeInfo(
      GraphEdge edge, Class<?> infoType, GraphEdgeInfo edgeInfo);

  /**
   * Add supplemental information for an node.
   *
   * @param node primary key for data.
   * @param infoType
   *    Explicit type, since {@code nodeInfo} might be derived from type key.
   * @param nodeInfo typed data to associate with this node.
   */
  public void addNodeInfo(
      GraphNode node, Class<?> infoType, GraphNodeInfo nodeInfo);

  /**
   * Add supplemental information for the model.
   *
   * @param infoType
   *    Explicit type, since {@code modelInfo} might be derived from type key.
   * @param nodeInfo typed data to associate with this model.
*/
  public void addModelInfo(Class<?> infoType, GraphModelInfo modelInfo);
}
