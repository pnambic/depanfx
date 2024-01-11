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
package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxCategoryColumnData.CategoryEntry;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A bit on the cute side, but a winch is used to hoist things.
 */
public class CategoryWinch {

  private static final Logger LOG =
      LoggerFactory.getLogger(CategoryWinch.class);

  private final Set<GraphNode> hoistNodes;

  private final Set<CategoryEntry> hoistCategories;

  private final CategoryEditor categories;

  private final DepanFxTreeModel treeModel;

  private Map<GraphNode, NodeData> hoistData;

  public CategoryWinch(
      Collection<GraphNode> hoistNodes,
      Collection<CategoryEntry> hoistCategories,
      CategoryEditor categories,
      DepanFxTreeModel treeModel) {
    // Ensure some efficiency.
    this.hoistNodes = new HashSet<>(hoistNodes);
    this.hoistCategories = new HashSet<>(hoistCategories);
    this.categories = categories;
    this.treeModel = treeModel;
  }

  public void hoistCategories() {
    hoistData =
        new HashMap<GraphNode, CategoryWinch.NodeData>(hoistNodes.size());
    hoistNodes.forEach(this::populateHoistData);
    hoistNodes.forEach(this::buildHoistGraph);
    hoistMembers();

    // Sanity check that all nodes were hoisted.
    List<GraphNode> unhoisted = hoistData.entrySet().stream()
        .filter(e -> !e.getValue().isReady())
        .map(e -> e.getKey())
        .collect(Collectors.toList());
    if (!unhoisted.isEmpty()) {
      LOG.warn("Unhoisted nodes remain.", unhoisted.size());
    }
  }

  private void populateHoistData(GraphNode node) {
    List<CategoryEntry> nodeCats =
        categories.getCurrentCategories(node).stream()
        .filter(hoistCategories::contains)
        .collect(Collectors.toList());

    hoistData.put(node, new NodeData(nodeCats));
  }

  private void buildHoistGraph(GraphNode node) {
    treeModel.getMembers(node).stream()
        .filter(hoistNodes::contains)
        .forEach(n -> addTarget(node, n));
  }

  private void hoistMembers() {
    List<GraphNode> nextBatch = new ArrayList<>();
    hoistData.entrySet().stream()
        .filter(e -> e.getValue().isReady())
        .map(e -> e.getKey())
        .forEach(nextBatch::add);

    // Avoid concurrent modification exceptions.
    while (!nextBatch.isEmpty()) {
      nextBatch = hoistBatch(nextBatch);
    }
  }

  private void addTarget(GraphNode source, GraphNode target) {
    hoistData.get(source).addHoistTarget(target);
    hoistData.get(target).addHoistSource(source);
  }

  private List<GraphNode> hoistBatch(List<GraphNode> batch) {
    LOG.info("Hoisting categories from {} nodes", batch.size());
    List<GraphNode> result = new ArrayList<>();
    for (GraphNode node : batch) {
      NodeData hoistInfo = hoistData.get(node);
      List<CategoryEntry> nodeCategories = hoistInfo.getCategories();
      hoistInfo.streamHoistSource()
        .filter(n -> updateSource(n, nodeCategories))
        .forEach(result::add);
    }
    return result;
  }

  private boolean updateSource(
      GraphNode node, List<CategoryEntry> nodeCategories) {
    categories.adddListMembership(node, nodeCategories);
    NodeData nodeData = hoistData.get(node);
    nodeData.addCategories(nodeCategories);
    nodeData.countTarget();

    return nodeData.isReady();
  }

  private static class NodeData {

    private final List<CategoryEntry> nodeCategories;

    private final List<GraphNode> hoistSources;

    private int targetCnt;

    public NodeData(List<CategoryEntry> nodeCategories) {
      this.nodeCategories = nodeCategories;
      targetCnt = 0;
      hoistSources = new ArrayList<GraphNode>();
    }

    public boolean isReady() {
      return targetCnt == 0;
    }

    public void countTarget() {
      if (targetCnt == 0) {
        LOG.error("Attempt to decrement target below zero.");
      }
      targetCnt --;
    }

    public void addCategories(List<CategoryEntry> hoistCategories) {
      hoistCategories.removeAll(nodeCategories);  // Avoid duplicates
      nodeCategories.addAll(hoistCategories);
    }

    public List<CategoryEntry> getCategories() {
      return nodeCategories;
    }

    public void addHoistSource(GraphNode source) {
      hoistSources.add(source);
    }

    public void addHoistTarget(GraphNode target) {
      targetCnt++;
    }

    private Stream<GraphNode> streamHoistSource() {
      return hoistSources.stream();
    }
  }
}
