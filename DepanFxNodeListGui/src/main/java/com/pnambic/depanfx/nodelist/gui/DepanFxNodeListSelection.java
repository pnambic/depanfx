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

public class DepanFxNodeListSelection {

  private final Collection<GraphNode> nodes;

  private final Map<GraphNode, BooleanProperty> nodesCheckBoxStates;

  public DepanFxNodeListSelection(
      Collection<GraphNode> nodes,
      Map<GraphNode, BooleanProperty> initialStates) {
    this.nodes = nodes;
    this.nodesCheckBoxStates = initialStates;
  }

  public static DepanFxNodeListSelection forNodes(
      Collection<GraphNode> nodes) {
    Map<GraphNode, BooleanProperty> initialStates = new HashMap<>(nodes.size());
    nodes.forEach(n -> initialStates.put(n, buildNodeSelectState(n)));
    return new DepanFxNodeListSelection(nodes, initialStates);
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

  public Stream<GraphNode> streamSelectedNodes() {
    return nodesCheckBoxStates.entrySet().stream()
        .filter(e -> e.getValue().getValue().booleanValue())
        .map(e -> e.getKey());
  }

  public Stream<GraphNode> streamUnselectedOf(Stream<GraphNode> src) {
    return src.filter(n -> !isSelected(n));
  }

  /**
   * Might be selected nodes, or might by all nodes.
   */
  public Stream<GraphNode> streamChosenNodes() {
    return nodesCheckBoxStates.values().stream()
        .filter(b -> b.get())
        .findFirst()
        .map(v -> streamSelectedNodes())
        .orElseGet(() -> nodes.stream());
  }

  public DepanFxNodeList getSelection(DepanFxNodeList nodeList) {
    return DepanFxNodeLists.buildRelatedNodeList(
        nodeList, streamSelectedNodes().collect(Collectors.toList()));
  }

  public void doSelectAllAction() {
    doSelectGraphNodesAction(streamNodes(), true);
  }

  public void doClearSelectionAction() {
    doSelectGraphNodesAction(streamNodes(), false);
  }

  /**
   * Make the node selection be only supplied nodes.  Any nodes not
   * in the supplied collections should be unselected.
   */
  public void doSelectGraphNodesAction(Collection<GraphNode> nodes) {
    streamNodes().forEach(n -> setSelectGraphNode(n, nodes.contains(n)));
  }

  public void doInvertSelectionAction() {
    streamNodes()
        .forEach(this::invertSelectGraphNode);
  }

  /**
   * View nodes in the supplied object list are selected based on the supplied
   * value, and other view nodes are not changed.
   */
  public void doSelectGraphNodesAction(
      Stream<GraphNode> nodes, boolean value) {
    nodes.forEach(n -> setSelectGraphNode(n, value));
  }

  public BooleanProperty setSelectGraphNode(GraphNode node, boolean value) {
    BooleanProperty result = nodesCheckBoxStates.get(node);
    result.set(value);
    return result;
  }

  /**
   * Since the previous state may have been unknown, provide the final
   * state for interested parties.
   */
  public boolean invertSelectGraphNode(GraphNode node) {
    BooleanProperty checkedProperty = nodesCheckBoxStates.get(node);
    boolean result = !checkedProperty.get();
    checkedProperty.set(result);
    return result;
  }

  private boolean isSelected(GraphNode node) {
    return nodesCheckBoxStates.get(node).get();
  }

  private Stream<GraphNode> streamNodes() {
    return nodes.stream();
  }

  // Prolly add saved selections in the future.
  private static BooleanProperty buildNodeSelectState(GraphNode node) {
    return new SimpleBooleanProperty(false);
  }
}
