package com.pnambic.depanfx.nodelist.gui;

import java.util.Collection;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxNodeListSectionItem extends TreeItem<DepanFxNodeListMember> {

  private final DepanFxNodeList nodeList;

  private boolean freshSections = false;

  public DepanFxNodeListSectionItem(
      DepanFxNodeListSection section,
      DepanFxNodeList nodeList) {
    super(section);
    this.nodeList = nodeList;
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  @Override
  public ObservableList<TreeItem<DepanFxNodeListMember>> getChildren() {
    if (!freshSections) {
      super.getChildren().setAll(buildChildren());
      freshSections = true;
    }

    return super.getChildren();
  }

  private ObservableList<TreeItem<DepanFxNodeListMember>> buildChildren() {

    ObservableList<TreeItem<DepanFxNodeListMember>> result =
        FXCollections.observableArrayList();
    
    Collection<GraphNode> nodes = nodeList.getNodes();
    for (GraphNode node : nodes) {
      DepanFxNodeListLeafNode nodeView = new DepanFxNodeListLeafNode(node);
      DepanFxNodeListLeafNodeItem nodeItem =
          new DepanFxNodeListLeafNodeItem(nodeView);
      result.add(nodeItem);
    }
    return result;
  }
}
