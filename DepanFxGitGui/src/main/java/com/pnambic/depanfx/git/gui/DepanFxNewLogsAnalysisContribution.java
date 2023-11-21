package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.git.tooldata.DepanFxGitRepoData;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
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
        "Git Logs", e -> runGitLogsDialog());
  }

  private void runGitLogsDialog() {
    Dialog<DepanFxNewGitLogsDialog> newLogsDialog =
        dialogRunner.createDialogAndParent(DepanFxNewGitLogsDialog.class);
    DepanFxGitRepoData repoData =
        DepanFxGitRepoToolDialogs.buildInitialGitRepoData();
    newLogsDialog.getController().setTooldata(repoData);
    newLogsDialog.runDialog("Create new theory from git logs");
  }
}
