package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxTreeForkItem extends DepanFxNodeListItem {

  private boolean freshTree = false;

  public DepanFxTreeForkItem(DepanFxTreeFork folder) {
    super(folder);
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
    DepanFxTreeFork folder = (DepanFxTreeFork) getValue();

    Collection<GraphNode> nodes = folder.getMembers();
    List<TreeItem<DepanFxNodeListMember>> result =
        new ArrayList<>(nodes.size());

    for (GraphNode node : nodes) {
      result.add(buildNodeItem(node, folder));
    }
    result.sort(folder.getOrderBy());
    // result.sort(folder.getOrderBy()DepanFxNodeListSections.COMPARE_BY_SORT_KEY);

    return FXCollections.observableList(result);
  }

  private TreeItem<DepanFxNodeListMember> buildNodeItem(
      GraphNode node, DepanFxTreeFork fork) {
    return fork.buildTreeMember(node);
  }
}
