package com.pnambic.depanfx.perspective.workspace.controls;

import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import java.util.function.Predicate;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxProjectContainerItem extends TreeItem<DepanFxWorkspaceMember> {

  private final Predicate<DepanFxProjectMember> filter;

  private boolean projectLoaded = false;

  public DepanFxProjectContainerItem(
      DepanFxProjectContainer container,
      Predicate<DepanFxProjectMember> filter) {
    super(container);
    this.filter = filter;
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
              (DepanFxProjectContainer) getValue(), filter);
      super.getChildren().setAll(builder.buildChildren());
    }

    return super.getChildren();
  }
}
