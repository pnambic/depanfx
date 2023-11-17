package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * Provides top-level rendering of a tree section.
 * Used as the mechanism to provide {@link DepanFxTreeFork} elements with
 * the section's {@link DepanFxTreeModel}.
 */
public class DepanFxTreeSectionItem
    extends DepanFxNodeListSectionItem {

  private boolean freshTree = false;

  public DepanFxTreeSectionItem(DepanFxTreeSection section) {
    super(section);
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  @Override
  public ObservableList<TreeItem<DepanFxNodeListMember>> getChildren() {
    if (!freshTree) {
      super.getChildren().setAll(buildChildren());
      freshTree = true;
    }

    return super.getChildren();
  }

  private ObservableList<TreeItem<DepanFxNodeListMember>> buildChildren() {
    DepanFxTreeSection section = (DepanFxTreeSection) getValue();

    DepanFxTreeModel treeModel = section.getTreeModel();
    Collection<GraphNode> nodes = treeModel.getRoots();

    List<TreeItem<DepanFxNodeListMember>> result =
        new ArrayList<>(nodes.size());

    for (GraphNode node : nodes) {
      result.add(buildNodeItem(node, section));
    }
    result.sort(DepanFxNodeListSections.COMPARE_BY_SORT_KEY);

    return FXCollections.observableList(result);
  }

  private TreeItem<DepanFxNodeListMember> buildNodeItem(
      GraphNode node, DepanFxTreeSection section) {
    return section.buildNodeItem(node);
  }

  @Override
  public DepanFxNodeList getSectionNodes() {
    DepanFxTreeSection section = (DepanFxTreeSection) getValue();
    return section.getSectionNodes();
  }
}
