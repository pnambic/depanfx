package com.pnambic.depanfx.perspective.workspace.controls;

import com.pnambic.depanfx.perspective.plugins.DepanFxResourceMenuRegistry;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import javafx.scene.control.TreeCell;

/**
 * 'cuz {@code DepanFxWorkspace} doesn't know anything about menus.
 */
public class DepanFxProjectTreeCell extends TreeCell<DepanFxWorkspaceMember> {

  // Our state
  private final DepanFxWorkspaceMemberCells memberCells;

  public DepanFxProjectTreeCell(
      DepanFxWorkspace workspace,
      DepanFxWorkspaceMemberCells.DocumentDispatch dispatch,
      DepanFxResourceRegistry rsrcRegistry,
      DepanFxResourceMenuRegistry rsrcMenuRegistry) {
    this.memberCells = new DepanFxWorkspaceMemberCells(
        workspace, dispatch,
        rsrcRegistry, rsrcMenuRegistry,
        m -> null);
  }

  @Override
  protected void updateItem(DepanFxWorkspaceMember member, boolean empty) {
    memberCells.restoreFont(this);

    super.updateItem(member, empty);
    memberCells.updateItem(this, member, empty);
  }
}
