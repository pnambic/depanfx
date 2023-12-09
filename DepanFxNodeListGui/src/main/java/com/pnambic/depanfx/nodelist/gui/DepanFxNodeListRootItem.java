package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxNodeListRootItem extends DepanFxNodeListItem {

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
      DepanFxNodeListSectionItem sectionItem =
          section.buildSectionItem(baseNodes);
      result.add(sectionItem);

      DepanFxNodeList sectionNodes = section.getSectionNodes();
      baseNodes = DepanFxNodeLists.remove(baseNodes, sectionNodes);
    }

    // If any nodes are left, drop them in a special section.
    if (!baseNodes.getNodes().isEmpty()) {

      // Capture a snapshot of baseNodes.
      DepanFxNodeList remainer = baseNodes;
      DepanFxNodeListSectionData
          .getBuiltinSimpleSectionResource(root.getWorkspace())
          .map(r -> new DepanFxFlatSection(r))
          .map(s -> s.buildSectionItem(remainer))
          .ifPresent(result::add);;
    }
    return result;
  }
}
