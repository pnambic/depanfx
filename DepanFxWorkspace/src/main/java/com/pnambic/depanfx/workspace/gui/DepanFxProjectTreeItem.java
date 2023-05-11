package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxProjectTreeItem extends TreeItem<DepanFxWorkspaceMember> {

  private boolean isFirstTimeChildren = true;

  public DepanFxProjectTreeItem(DepanFxProjectTree project) {
    super(project);
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  @Override
  public ObservableList<TreeItem<DepanFxWorkspaceMember>> getChildren() {
    if (isFirstTimeChildren) {
      isFirstTimeChildren = false;
      DepanFxProjectMemberItemBuilder builder =
          new DepanFxProjectMemberItemBuilder((DepanFxProjectMember) getValue());
      super.getChildren().setAll(builder.buildChildren());
    }

    return super.getChildren();
  }
}
