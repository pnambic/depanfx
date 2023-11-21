package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import javafx.scene.control.ContextMenu;

public class DepanFxGitRepoMenu {

  public static ContextMenu buildRepoChoiceMenu(
      DepanFxGitRepoToolDialog.Aware srcDlg,
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem("New git Repo...",
        e -> openGitRepoChooser(srcDlg, dialogRunner));
    builder.appendActionItem("Select git Repo...",
        e -> DepanFxGitRepoToolDialogs.runGitRepoFinder(srcDlg, workspace));
    return builder.build();
  }

  private static void openGitRepoChooser(
      DepanFxGitRepoToolDialog.Aware srcDlg,
      DepanFxDialogRunner dialogRunner) {
    DepanFxGitRepoToolDialogs.runGitRepoCreate(srcDlg, dialogRunner);
  }
}
