package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxNodeListFlatSectionItem extends DepanFxNodeListSectionItem {

  private final DepanFxNodeList nodeList;

  private boolean freshSections = false;

  public DepanFxNodeListFlatSectionItem(
      DepanFxNodeListFlatSection section,
      DepanFxNodeList nodeList) {
    super(section);
    this.nodeList = nodeList;
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

    DepanFxNodeListFlatSection section =
        (DepanFxNodeListFlatSection) getValue();

    Collection<GraphNode> nodes = nodeList.getNodes();
    List<TreeItem<DepanFxNodeListMember>> result =
        new ArrayList<>(nodes.size());

    for (GraphNode node : nodeList.getNodes()) {
      DepanFxNodeListLeafNode nodeView =
          new DepanFxNodeListLeafNode(node, section);
      DepanFxNodeListLeafNodeItem nodeItem =
          new DepanFxNodeListLeafNodeItem(nodeView);
      result.add(nodeItem);
    }
    result.sort(DepanFxNodeListSections.COMPARE_BY_SORT_KEY);

    return FXCollections.observableList(result);
  }

  public DepanFxNodeList getSectionNodes() {
    return nodeList;
  }
}
