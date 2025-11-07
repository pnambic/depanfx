/*
 * Copyright 2024 The Depan Project Authors
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
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class DepanFxNodeListCheckBoxSelection implements DepanFxNodeListSelection {

  private final Collection<GraphNode> nodes;

  private final Map<GraphNode, BooleanProperty> nodesCheckBoxStates;

  public DepanFxNodeListCheckBoxSelection(
      Collection<GraphNode> nodes,
      Map<GraphNode, BooleanProperty> initialStates) {
    this.nodes = nodes;
    this.nodesCheckBoxStates = initialStates;
  }

  public static DepanFxNodeListCheckBoxSelection forNodes(
      Collection<GraphNode> nodes) {
    Map<GraphNode, BooleanProperty> initialStates = new HashMap<>(nodes.size());
    nodes.forEach(n -> initialStates.put(n, buildNodeSelectState(n)));
    return new DepanFxNodeListCheckBoxSelection(nodes, initialStates);
  }

  public void setOnSelectionChange(
      BiConsumer<GraphNode, Boolean> onSelectionChange) {
    nodesCheckBoxStates.entrySet().forEach(
        e -> e.getValue().addListener(
            (v, o, n) -> onSelectionChange.accept(e.getKey(), n)));
  }

  public BooleanProperty getSelected(DepanFxNodeListGraphNode member) {
    return nodesCheckBoxStates
        .get(((DepanFxNodeListGraphNode) member).getGraphNode());
  }

  @Override
  public Stream<GraphNode> streamSelectedNodes() {
    return nodesCheckBoxStates.entrySet().stream()
        .filter(e -> e.getValue().getValue().booleanValue())
        .map(e -> e.getKey());
  }

  /**
   * Might be selected nodes, or might by all nodes.
   */
  @Override
  public Stream<GraphNode> streamChosenNodes() {
    return nodesCheckBoxStates.values().stream()
        .filter(b -> b.get())
        .findFirst()
        .map(v -> streamSelectedNodes())
        .orElseGet(() -> nodes.stream());
  }

  @Override
  public void doSelectAllAction() {
    doSelectGraphNodesAction(streamNodes(), true);
  }

  @Override
  public void doClearSelectionAction() {
    doSelectGraphNodesAction(streamNodes(), false);
  }

  @Override
  public void doInvertSelectionAction() {
    streamNodes()
        .forEach(this::invertSelectGraphNode);
  }

  /**
   * Make the node selection be only supplied nodes.  Any nodes not
   * in the supplied collections should be unselected.
   */
  @Override
  public void doSelectGraphNodesAction(Collection<GraphNode> nodes) {
    streamNodes().forEach(n -> setSelectGraphNode(n, nodes.contains(n)));
  }

  @Override
  public boolean isSelected(GraphNode node) {
    BooleanProperty selectedProperty = nodesCheckBoxStates.get(node);
    if (selectedProperty != null) {
      return selectedProperty.get();
    }
    return false;
  }

  @Override
  public void setSelectGraphNode(GraphNode node, boolean value) {
    BooleanProperty result = nodesCheckBoxStates.get(node);
    result.set(value);
  }

  /**
   * Since the previous state may have been unknown, provide the final
   * state for interested parties.
   */
  @Override
  public boolean invertSelectGraphNode(GraphNode node) {
    BooleanProperty checkedProperty = nodesCheckBoxStates.get(node);
    boolean result = !checkedProperty.get();
    checkedProperty.set(result);
    return result;
  }

  public Stream<GraphNode> streamUnselectedOf(Stream<GraphNode> src) {
    return src.filter(n -> !isSelected(n));
  }

  public DepanFxNodeList getSelection(DepanFxNodeList nodeList) {
    return DepanFxNodeLists.buildRelatedNodeList(
        nodeList, streamSelectedNodes().collect(Collectors.toList()));
  }

  /**
   * View nodes in the supplied object list are selected based on the supplied
   * value, and other view nodes are not changed.
   */
  public void doSelectGraphNodesAction(
      Stream<GraphNode> nodes, boolean value) {
    nodes.forEach(n -> setSelectGraphNode(n, value));
  }

  private Stream<GraphNode> streamNodes() {
    return nodes.stream();
  }

  // Prolly add saved selections in the future.
  private static BooleanProperty buildNodeSelectState(GraphNode node) {
    return new SimpleBooleanProperty(false);
  }
}
