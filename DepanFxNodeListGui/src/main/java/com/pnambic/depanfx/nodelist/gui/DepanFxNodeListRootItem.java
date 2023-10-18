package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxNodeListRootItem extends TreeItem<DepanFxNodeListMember> {

  private boolean freshSections = false;

  public DepanFxNodeListRootItem(DepanFxNodeListRoot rootInfo) {
    super(rootInfo);
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

    DepanFxNodeListRoot root = (DepanFxNodeListRoot) getValue();

    ObservableList<TreeItem<DepanFxNodeListMember>> result =
        FXCollections.observableArrayList();

    DepanFxNodeList baseNodes = root.getNodeList();

    for (DepanFxNodeListSection section : root.getSections()) {
        DepanFxNodeList sectionNodes = section.pickNodes(baseNodes);
        baseNodes = DepanFxNodeLists.remove(baseNodes, sectionNodes);
        DepanFxNodeListSectionItem sectionItem =
            new DepanFxNodeListSectionItem(section, sectionNodes);
        result.add(sectionItem);
    }

    // If there are any left, drop them in a special section.
    if (!baseNodes.getNodes().isEmpty()) {
      DepanFxNodeListSectionItem sectionItem =
          new DepanFxNodeListSectionItem(null, baseNodes);
      result.add(sectionItem);
    }
    return result;
  }
}