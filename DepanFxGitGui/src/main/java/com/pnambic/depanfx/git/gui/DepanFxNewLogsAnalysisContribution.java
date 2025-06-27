package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.git.tooldata.DepanFxGitRepoData;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxMenuItemFactory;
import com.pnambic.depanfx.scene.plugins.DepanFxNewAnalysisContribution;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.control.MenuItem;

@Component
public class DepanFxNewLogsAnalysisContribution
    implements DepanFxNewAnalysisContribution {

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  @Autowired
  public DepanFxNewLogsAnalysisContribution(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
  }

  @Override
  public MenuItem createNewResourceMenuItem() {
    return DepanFxMenuItemFactory.createActionItem(
        "Git Logs", e -> runGitLogsDialog());
  }

  private void runGitLogsDialog() {
    DepanFxGitRepoData repoData =
        DepanFxGitRepoToolDialogs.buildInitialGitRepoData();
    DepanFxWorkspaceResource<DepanFxGitRepoData> repoRsrc =
        workspace.addScratchResource(repoData);

    Dialog<DepanFxNewGitLogsDialog> newLogsDialog =
        dialogRunner.createDialogAndParent(DepanFxNewGitLogsDialog.class);
    newLogsDialog.getController().setRepoResource(repoRsrc);
    newLogsDialog.runDialog("Create new theory from git logs");
  }
}
