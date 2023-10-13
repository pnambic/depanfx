package com.pnambic.depanfx.nodelist.gui;

import java.util.List;

import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxNodeListRootItem extends TreeItem<DepanFxNodeListMember> {

  public DepanFxNodeListRootItem(DepanFxNodeListRoot rootInfo) {
    super(rootInfo);
  }

  @Override
  public ObservableList<TreeItem<DepanFxNodeListMember>> getChildren() {
    super.getChildren().setAll(buildChildren());

    return super.getChildren();
  }

  private ObservableList<TreeItem<DepanFxNodeListMember>> buildChildren() {

    DepanFxNodeListRoot root = (DepanFxNodeListRoot) getValue();

    ObservableList<TreeItem<DepanFxNodeListMember>> result =
        FXCollections.observableArrayList();
    for (DepanFxNodeListSection section : root.getSections()) {
    }
    return result;
  }
}
