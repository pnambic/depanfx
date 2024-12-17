package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.git.tooldata.DepanFxGitRepoData;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceContribution;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.control.MenuItem;

@Component
public class DepanFxNewRepoGraphContribution
    implements DepanFxNewResourceContribution {

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  @Autowired
  public DepanFxNewRepoGraphContribution(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
  }

  @Override
  public MenuItem createNewResourceMenuItem() {
    return DepanFxContextMenuBuilder.createActionItem(
        "Git Repo", e -> runDialog());
  }

  private void runDialog() {
    Dialog<DepanFxNewGitRepoDialog> newRepoDlg =
        dialogRunner.createDialogAndParent(DepanFxNewGitRepoDialog.class);

    DepanFxGitRepoData initialGitRepoData = DepanFxGitRepoToolDialogs.buildInitialGitRepoData();
    DepanFxWorkspaceResource<DepanFxGitRepoData> initialGitRepoRsrc =
        workspace.addScratchResource(initialGitRepoData);
    newRepoDlg.getController().setRepoResource(initialGitRepoRsrc );
    newRepoDlg.runDialog("Create new graph from git repository");
  }
}
