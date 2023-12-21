package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxCategoryColumnData.CategoryEntry;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoryHandler {

  private List<CategoryEntry> categories;

  private Map<CategoryEntry, Collection<GraphNode>> sourceNodes;

  private Map<CategoryEntry, Collection<GraphNode>> currentNodes;

  public CategoryHandler(List<CategoryEntry> categories) {
    this.categories = categories;
    updateNodeMaps();
  }

  public List<CategoryEntry> getCategoryList() {
    return categories;
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
}
