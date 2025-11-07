/*
 * Copyright 2025 The Depan Project Authors
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
package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Defines node selection operations for lists of nodes.
 *
 * Allows forwarders (and facades and delegates) to support
 * selection operations.
 */
public interface DepanFxNodeListSelection {

  Stream<GraphNode> streamSelectedNodes();

  /**
   * Might be selected nodes, or might by all nodes.
   */
  Stream<GraphNode> streamChosenNodes();

  void doSelectAllAction();

  void doClearSelectionAction();

  void doInvertSelectionAction();

  /**
   * Make the node selection be only the supplied nodes.
   * Any nodes not in the supplied collections should be unselected.
   */
  void doSelectGraphNodesAction(Collection<GraphNode> nodes);

  boolean isSelected(GraphNode member);

  /**
   * Set the selection state for the supplied node.
   */
  void setSelectGraphNode(GraphNode node, boolean value);

  /**
   * Since the previous state may have been unknown, provide the final
   * state for interested parties.
   */
  boolean invertSelectGraphNode(GraphNode node);
}
