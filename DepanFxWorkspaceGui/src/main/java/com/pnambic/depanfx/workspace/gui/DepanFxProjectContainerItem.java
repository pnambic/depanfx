package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxProjectContainerItem extends TreeItem<DepanFxWorkspaceMember> {

  private boolean projectLoaded = false;

  public DepanFxProjectContainerItem(DepanFxProjectContainer container) {
    super(container);
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  @Override
  public ObservableList<TreeItem<DepanFxWorkspaceMember>> getChildren() {
    if (!projectLoaded) {
      projectLoaded = true;
      DepanFxProjectMemberItemBuilder builder =
          new DepanFxProjectMemberItemBuilder(
              (DepanFxProjectContainer) getValue());
      super.getChildren().setAll(builder.buildChildren());
    }

    return super.getChildren();
  }
}
