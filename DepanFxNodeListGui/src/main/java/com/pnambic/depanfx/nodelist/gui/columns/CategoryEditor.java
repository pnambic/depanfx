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
package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxCategoryColumnData.CategoryEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Organizes modifications to the set of nodes assigned to categories.
 */
public class CategoryEditor {

  private List<CategoryEntry> categories;

  private Map<CategoryEntry, Collection<GraphNode>> sourceNodes =
      new HashMap<>();

  private Map<CategoryEntry, Collection<GraphNode>> currentNodes =
      new HashMap<>();

  public CategoryEditor(List<CategoryEntry> categories) {
    this.categories = categories;
    categories.forEach(this::updateNodeMaps);
  }

  public List<CategoryEntry> getCategoryList() {
    return categories;
  }

  public void updateCategory(
      CategoryEntry sourceEntry, CategoryEntry updateEntry) {
    int entryIndex = categories.indexOf(sourceEntry);
    if (entryIndex >= 0) {
      categories.set(entryIndex, updateEntry);
      sourceNodes.remove(sourceEntry);
      currentNodes.remove(sourceEntry);
      updateNodeMaps(updateEntry);
    }
  }

  public boolean hasEdits() {
    return streamChangedCategories().findAny().isPresent();
  }

  public List<CategoryEntry> getSourceCategories(GraphNode node) {
    return sourceNodes.entrySet().stream()
        .filter(e -> e.getValue().contains(node))
        .map(e -> e.getKey())
        .collect(Collectors.toList());
  }

  public List<CategoryEntry> getCurrentCategories(GraphNode node) {
    return currentNodes.entrySet().stream()
        .filter(e -> e.getValue().contains(node))
        .map(e -> e.getKey())
        .collect(Collectors.toList());
  }

  public void setListMembership(GraphNode graphNode, CategoryEntry entry) {
    currentNodes.values().forEach(n -> n.remove(graphNode));
    if (entry != null) {
      Collection<GraphNode> categoryNodes = currentNodes.get(entry);
      if (categoryNodes != null) {  // Avoid unknown categories.
        categoryNodes.add(graphNode);
      }
    }
  }

  public void setListMembership(
      GraphNode graphNode, Collection<CategoryEntry> enabledCategories) {
    currentNodes.values().forEach(n -> n.remove(graphNode));
    enabledCategories.stream()
        .map(currentNodes::get)  // Avoid unknown categories.
        .forEach(c -> c.add(graphNode));
  }

  public void addListMembership(
      GraphNode graphNode, Collection<CategoryEntry> enabledCategories) {
    // Leave existing memberships in place.
    enabledCategories.stream()
        .map(currentNodes::get)  // Avoid unknown categories.
        .forEach(c -> c.add(graphNode));
  }

  public Collection<GraphNode> getCurrentNodes(CategoryEntry entry) {
    return new ArrayList<>(currentNodes.get(entry));
  }

  /**
   * Provide a stream snapshots from the changed categories.
   */
  public Stream<CategoryEntry> streamChangedCategories() {
    // Should not need to check that the two maps have the same key set.
    return sourceNodes.entrySet().stream()
        .filter(e -> !areSame(e.getValue(), currentNodes.get(e.getKey())))
        .map(e -> e.getKey());
  }

  private void updateNodeMaps(CategoryEntry updateEntry) {

    Collection<GraphNode> entryNodes =
        updateEntry.getNodeListRsrc().getResource().getNodes();
    sourceNodes.put(updateEntry, entryNodes);
    currentNodes.put(updateEntry, new HashSet<>(entryNodes));
  }

  private static boolean areSame(
      Collection<GraphNode> sourceNodes,
      Collection<GraphNode> currentNodes) {
    return hasAllBase(sourceNodes, currentNodes)
        && hasAllBase(currentNodes, sourceNodes);
  }

  private static boolean hasAllBase(
      Collection<GraphNode> testNodes,
      Collection<GraphNode> baseNodes) {
    return testNodes.stream()
        .filter(n -> !baseNodes.contains(n))
        .findFirst()
        .isEmpty();
  }
}
