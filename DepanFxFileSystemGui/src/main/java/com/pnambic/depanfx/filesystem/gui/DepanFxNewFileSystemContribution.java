package com.pnambic.depanfx.filesystem.gui;

import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceContribution;

import org.springframework.stereotype.Component;

import javafx.scene.control.MenuItem;

@Component
public class DepanFxNewFileSystemContribution
    implements DepanFxNewResourceContribution {

  private final DepanFxDialogRunner dialogRunner;

  public DepanFxNewFileSystemContribution(DepanFxDialogRunner dialogRunner) {
    this.dialogRunner = dialogRunner;
  }

  @Override
  public MenuItem createNewResourceMenuItem() {
    return DepanFxContextMenuBuilder.createActionItem(
        "File System", e -> runDialog());
  }

  private void runDialog() {
    dialogRunner.runDialog(
        DepanFxNewFileSystemDialog.class, "Create new file system graph");
  }
}
