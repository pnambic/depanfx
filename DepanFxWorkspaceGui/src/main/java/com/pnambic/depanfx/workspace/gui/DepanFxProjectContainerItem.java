package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxProjectContainerItem extends TreeItem<DepanFxWorkspaceMember> {

  private boolean isFirstTimeChildren = true;

  public DepanFxProjectContainerItem(DepanFxProjectContainer container) {
    super(container);
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
