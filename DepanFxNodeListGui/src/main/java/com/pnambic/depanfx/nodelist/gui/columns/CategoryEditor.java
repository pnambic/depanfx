package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxCategoryColumnData.CategoryEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Organizes modifications to the set of nodes assigned to categories.
 */
public class CategoryEditor {

  private List<CategoryEntry> categories;

  private Map<CategoryEntry, Collection<GraphNode>> sourceNodes;

  private Map<CategoryEntry, Collection<GraphNode>> currentNodes;

  public CategoryEditor(List<CategoryEntry> categories) {
    this.categories = categories;
    updateNodeMaps();
  }

  public List<CategoryEntry> getCategoryList() {
    return categories;
  }

  public boolean hasEdits() {
    return !getChangedCategories().isEmpty();
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

  public void adddListMembership(
      GraphNode graphNode, Collection<CategoryEntry> enabledCategories) {
    // Leave existing memberships in place.
    enabledCategories.stream()
        .map(currentNodes::get)  // Avoid unknown categories.
        .forEach(c -> c.add(graphNode));
  }

  public Collection<GraphNode> getCurrentNodes(CategoryEntry entry) {
    return new ArrayList<>(currentNodes.get(entry));
  }

  public List<CategoryEntry> getChangedCategories() {
    // Should not need to check that the two maps have the same key set.
    return sourceNodes.entrySet().stream()
        .filter(e -> !areSame(e.getValue(), currentNodes.get(e.getKey())))
        .map(e -> e.getKey())
        .collect(Collectors.toList());
  }

  private void updateNodeMaps() {
    sourceNodes = categories.stream()
        .collect(Collectors.toMap(c -> c, this::getEntryNodes));
    currentNodes = sourceNodes.entrySet().stream()
        .collect(Collectors.toMap(
            e -> e.getKey(), e -> new HashSet<>(e.getValue())));
  }

  private Collection<GraphNode> getEntryNodes(CategoryEntry entry) {
    DepanFxNodeList nodeList =
        (DepanFxNodeList) entry.getNodeListRsrc().getResource();
    return nodeList.getNodes();
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
