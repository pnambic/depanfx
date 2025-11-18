package com.pnambic.depanfx.perspective.workspace.controls;

import com.pnambic.depanfx.perspective.plugins.DepanFxResourceMenuRegistry;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import java.util.Optional;

import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * 'cuz {@code DepanFxWorkspace} doesn't know anything about menus.
 */
public class DepanFxProjectListCell extends ListCell<DepanFxWorkspaceMember> {

  private final DepanFxWorkspaceMemberCells memberCells;

  public DepanFxProjectListCell(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxResourceRegistry rsrcRegistry,
      DepanFxResourceMenuRegistry rsrcMenuRegistry) {
    this.memberCells = new DepanFxWorkspaceMemberCells(
            workspace, dialogRunner,
            rsrcRegistry, rsrcMenuRegistry,
            (c, r) -> rsrcRegistry.acceptDialog(workspace, dialogRunner, c, r),
            this::getCellIcon);
  }

  @Override
  protected void updateItem(DepanFxWorkspaceMember member, boolean empty) {
    memberCells.restoreFont(this);

    super.updateItem(member, empty);
    memberCells.updateItem(this, member, empty);
  }

  private ImageView getCellIcon(DepanFxWorkspaceMember member) {
    return getIconImage(member)
        .map(i -> new ImageView(i))
        .orElse(null);
  }

  private Optional<Image> getIconImage(DepanFxWorkspaceMember member) {
    switch (member) {
    case DepanFxProjectDocument document:
      return DepanFxWorkspaceIcons.loadDepanIcon(
          DepanFxWorkspaceIcons.Icon.DOCUMENT);
    case DepanFxProjectContainer container:
      return DepanFxWorkspaceIcons.loadDepanIcon(
          DepanFxWorkspaceIcons.Icon.CONTAINER);
    default:
      break;
    }
    return DepanFxWorkspaceIcons.loadDepanIcon(
        DepanFxWorkspaceIcons.Icon.DOCUMENT);
  }
}
