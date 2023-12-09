package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxFlatSectionItem extends DepanFxNodeListSectionItem {

  private boolean freshSections = false;

  public DepanFxFlatSectionItem(DepanFxFlatSection section) {
    super(section);
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
    DepanFxNodeListSection section = getSection();

    Collection<GraphNode> nodes = section.getSectionNodes().getNodes();

    List<TreeItem<DepanFxNodeListMember>> result =
        new ArrayList<>(nodes.size());
    nodes.stream()
        .filter(Objects::nonNull)  // [9-Dec-2023] should not happen, but avoids a crash
        .map(section::buildNodeItem)
        .forEach(result::add);
    section.sortTreeItems(result);

    return FXCollections.observableList(result);
  }
}
