package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.git.builder.GitCommandRunner;
import com.pnambic.depanfx.git.tooldata.DepanFxGitRepoData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;

public class DepanFxGitRepoToolDialogs {

  private DepanFxGitRepoToolDialogs() {
    // Prevent instantiation.
  }

  /////////////////////////////////////
  // Context menu for creation or selection

  public static ContextMenu buildRepoChoiceMenu(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      Scene scene,
      Supplier<DepanFxWorkspaceResource<DepanFxGitRepoData>> srcRepoRsrc,
      Consumer<DepanFxWorkspaceResource<DepanFxGitRepoData>> dstRepoRsrc) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem("Select git Repo...", e ->
        runGitRepoChooser(workspace, dialogRunner, scene)
            .ifPresent(dstRepoRsrc));
    builder.appendActionItem("New git Repo...", e ->
        runCreateDialog(workspace, dialogRunner, scene, srcRepoRsrc.get())
            .ifPresent(dstRepoRsrc));
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
   * Obtain an existing git repo tooldata with a resource chooser.
   */
  public static Optional<DepanFxWorkspaceResource<DepanFxGitRepoData>>
      runGitRepoChooser(
            DepanFxWorkspace workspace,
            DepanFxDialogRunner dialogRunner,
            Scene scene) {
    DepanFxResourceChooser chooser =
        new DepanFxResourceChooser(workspace, dialogRunner);

    DepanFxResourcePerspectives.prepareResourceFinder(
        chooser, DepanFxGitRepoData.GIT_REPOS_TOOL_PATH);
    chooser.getExtensionFilters().add(
        DepanFxGitRepoToolDialog.GIT_REPO_RSRC_FILTER);
    chooser.setSelectedExtensionFilter(
        DepanFxGitRepoToolDialog.GIT_REPO_RSRC_FILTER);

    return chooser.showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
            p, DepanFxGitRepoData.class));
  }

  private static Optional<DepanFxWorkspaceResource<DepanFxGitRepoData>>
      runCreateDialog(
          DepanFxWorkspace workspace,
          DepanFxDialogRunner dialogRunner,
          Scene scene,
          DepanFxWorkspaceResource<DepanFxGitRepoData> repoRsrc) {
    Dialog<DepanFxGitRepoToolDialog> repoDlg =
        DepanFxGitRepoToolDialog.runCreateDialog(repoRsrc, dialogRunner);
    return repoDlg.getController().getToolResource();
  }
}
