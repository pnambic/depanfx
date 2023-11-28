package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.git.builder.GitCommandRunner;
import com.pnambic.depanfx.git.tooldata.DepanFxGitRepoData;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import javafx.scene.control.ContextMenu;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class DepanFxGitRepoToolDialogs {

  public static final String GIT_REPOS_TOOL_DIR = "Git Repos";

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxGitRepoToolDialogs.class.getName());

  private static final ExtensionFilter GIT_REPO_TOOL_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Git Repo Tool", DepanFxGitRepoData.GIT_REPO_TOOL_EXT);

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
        e -> runGitRepoCreate(srcDlg, dialogRunner));
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
   * Create a new git repo tooldata with the git repo tool dialog.
   */
  public static void runGitRepoCreate(
      DepanFxGitRepoToolDialog.Aware srcDlg,
      DepanFxDialogRunner dialogRunner) {
    Dialog<DepanFxGitRepoToolDialog> repoChooser =
        dialogRunner.createDialogAndParent(DepanFxGitRepoToolDialog.class);

    repoChooser.getController().setTooldata(srcDlg.getTooldata());
    repoChooser.runDialog("Create git Repository");
    repoChooser.getController().getTooldata()
        .ifPresent(d -> srcDlg.setTooldata(d));
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
       workspace
          .toProjectDocument(selectedFile.getAbsoluteFile().toURI())
          .flatMap(p -> DepanFxWorkspaceFactory.loadDocument(
              workspace, p, DepanFxGitRepoData.class))
          .map(d -> d.getResource())
          .map(DepanFxGitRepoData.class::cast)
          .ifPresent(d -> srcDlg.setTooldata(d));
    }
  }

  /**
   * Modify an existing git repo tooldata with the git repo tool dialog.
   */
  public static void runGitRepoEdit(
      DepanFxWorkspaceResource wkspRsrc,
      DepanFxDialogRunner dialogRunner) {
    Dialog<DepanFxGitRepoToolDialog> repoChooser =
        dialogRunner.createDialogAndParent(DepanFxGitRepoToolDialog.class);
    repoChooser.getController().setWorkspaceResource(wkspRsrc);
    repoChooser.runDialog("Edit git Repository");
  }

  public static void setGitRepoTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(GIT_REPO_TOOL_FILTER);
    result.setSelectedExtensionFilter(GIT_REPO_TOOL_FILTER);
  }

  private static FileChooser prepareGitRepoFinder(DepanFxWorkspace workspace) {
    File toolsDir = DepanFxProjects.getCurrentTools(workspace);
    File gitRepoDir = new File(toolsDir, GIT_REPOS_TOOL_DIR);
    File gitDataFile = new File(gitRepoDir, "repo");
    FileChooser result =
        DepanFxSceneControls.prepareFileChooser(gitDataFile);
    result.setInitialFileName("");
    setGitRepoTooldataFilters(result);
    return result;
  }
}
