package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.git.builder.GitCommandRunner;
import com.pnambic.depanfx.git.tooldata.DepanFxGitRepoData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import java.io.File;

import javafx.scene.control.ContextMenu;
import javafx.stage.FileChooser;

public class DepanFxGitRepoToolDialogs {

  private DepanFxGitRepoToolDialogs() {
    // Prevent instantiation.
  }

  /////////////////////////////////////
  // Context menu for creation or selection

  public static ContextMenu buildRepoChoiceMenu(
      DepanFxGitRepoToolDialog.Aware srcDlg,
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem("New git Repo...",
        e -> runCreateDialog(srcDlg, dialogRunner));
    builder.appendActionItem("Select git Repo...",
        e -> runGitRepoFinder(srcDlg, workspace));
    return builder.build();
  }

  /////////////////////////////////////
  // Git Repo Dialog

  public static DepanFxGitRepoData buildInitialGitRepoData() {
    String name = ""; // Let it default from repoName
    String description = ""; // Let it default from repoName
    String gitExe = GitCommandRunner.DEFAULT_GIT_EXE;
    String repoName = ""; // Let it default from repo directory
    String repoDirectory = "";  // Force user choice
    return new DepanFxGitRepoData(
        name , description , gitExe, repoName, repoDirectory);
  }

  /**
   * Obtain an existing git repo tooldata with file chooser.
   */
  public static void runGitRepoFinder(
      DepanFxGitRepoToolDialog.Aware srcDlg,
      DepanFxWorkspace workspace) {
    FileChooser fileChooser = prepareGitRepoFinder(workspace);
    File selectedFile =
        fileChooser.showOpenDialog(srcDlg.getChooserWindow());
    if (selectedFile != null) {
       workspace.toProjectDocument(selectedFile.getAbsoluteFile().toURI())
          .flatMap(p -> workspace.getWorkspaceResource(
              p, DepanFxGitRepoData.class))
          .map(d -> d.getResource())
          .map(DepanFxGitRepoData.class::cast)
          .ifPresent(d -> srcDlg.setTooldata(d));
    }
  }

  private static void runCreateDialog(
      DepanFxGitRepoToolDialog.Aware srcDlg, DepanFxDialogRunner dialogRunner) {
    DepanFxGitRepoData repoData = srcDlg.getTooldata();
    Dialog<DepanFxGitRepoToolDialog> repoDlg =
        DepanFxGitRepoToolDialog.runCreateDialog(repoData, dialogRunner);
    repoDlg.getController().getWorkspaceResource()
        .map(r -> r.getResource())
        .map(DepanFxGitRepoData.class::cast)
        .ifPresent(d -> srcDlg.setTooldata(d));
  }

  private static FileChooser prepareGitRepoFinder(DepanFxWorkspace workspace) {
    FileChooser result = DepanFxResourcePerspectives.prepareToolFinder(
        workspace, DepanFxGitRepoData.GIT_REPOS_TOOL_PATH);
    DepanFxGitRepoToolDialog.setGitRepoTooldataFilters(result);
    return result;
  }
}
