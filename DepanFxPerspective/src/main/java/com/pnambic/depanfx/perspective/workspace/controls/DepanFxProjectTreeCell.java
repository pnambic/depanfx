package com.pnambic.depanfx.perspective.workspace.controls;

import com.pnambic.depanfx.perspective.plugins.DepanFxResourceMenuRegistry;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistryContribution;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import java.util.function.BiConsumer;

import javafx.scene.control.TreeCell;

/**
 * 'cuz {@code DepanFxWorkspace} doesn't know anything about menus.
 */
public class DepanFxProjectTreeCell extends TreeCell<DepanFxWorkspaceMember> {

  // Our state
  private final DepanFxWorkspaceMemberCells memberCells;

  public DepanFxProjectTreeCell(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxResourceRegistry rsrcRegistry,
      DepanFxResourceMenuRegistry rsrcMenuRegistry,
      BiConsumer<DepanFxResourceRegistryContribution<?>, DepanFxProjectDocument>
          dispatcher) {
    this.memberCells = new DepanFxWorkspaceMemberCells(
        workspace, dialogRunner,
        rsrcRegistry, rsrcMenuRegistry,
        dispatcher,
        m -> null);
  }

  @Override
  protected void updateItem(DepanFxWorkspaceMember member, boolean empty) {
    memberCells.restoreFont(this);

    super.updateItem(member, empty);
    memberCells.updateItem(this, member, empty);
  }
}
