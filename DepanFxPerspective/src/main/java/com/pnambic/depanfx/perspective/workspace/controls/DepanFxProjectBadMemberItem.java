package com.pnambic.depanfx.perspective.workspace.controls;

import com.pnambic.depanfx.workspace.DepanFxProjectBadMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import javafx.scene.control.TreeItem;

public class DepanFxProjectBadMemberItem extends TreeItem<DepanFxWorkspaceMember> {

  public DepanFxProjectBadMemberItem(DepanFxProjectBadMember member) {
    super(member);
  }
}
