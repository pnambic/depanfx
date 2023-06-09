package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.workspace.DepanFxProjectMember;

import javafx.scene.control.TreeItem;

public class DepanFxProjectMemberItem<T extends DepanFxProjectMember>
    extends TreeItem<DepanFxProjectMember> {

  private final T member;

  public DepanFxProjectMemberItem(T member) {
    this.member = member;
  }

  public T getMember() {
    return member;
  }
}
