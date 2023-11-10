package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.plugins.DepanFxNewAnalysisContribution;

import org.springframework.stereotype.Component;

import javafx.scene.control.MenuItem;

@Component
public class DepanFxNewLogsAnalysisContribution
    implements DepanFxNewAnalysisContribution {

  private final DepanFxDialogRunner dialogRunner;

  public DepanFxNewLogsAnalysisContribution(DepanFxDialogRunner dialogRunner) {
    this.dialogRunner = dialogRunner;
  }

  @Override
  public MenuItem createNewResourceMenuItem() {
    return DepanFxContextMenuBuilder.createActionItem(
        "Git Logs", e -> runDialog());
  }

  private void runDialog() {
    dialogRunner.runDialog(
        DepanFxNewGitLogsDialog.class, "Create new theory from git logs");
  }
}
