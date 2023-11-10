package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceContribution;

import org.springframework.stereotype.Component;

import javafx.scene.control.MenuItem;

@Component
public class DepanFxNewRepoGraphContribution
    implements DepanFxNewResourceContribution {

  private final DepanFxDialogRunner dialogRunner;

  public DepanFxNewRepoGraphContribution(DepanFxDialogRunner dialogRunner) {
    this.dialogRunner = dialogRunner;
  }

  @Override
  public MenuItem createNewResourceMenuItem() {
    return DepanFxContextMenuBuilder.createActionItem(
        "Git Repo", e -> runDialog());
  }

  private void runDialog() {
    dialogRunner.runDialog(
        DepanFxNewGitRepoDialog.class, "Create new graph from git repository");
  }
}
