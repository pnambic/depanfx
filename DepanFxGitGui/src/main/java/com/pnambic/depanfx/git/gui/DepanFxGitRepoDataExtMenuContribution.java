package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.git.tooldata.DepanFxGitRepoData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceExtMenuContribution;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

import javafx.scene.control.Cell;

@Component
public class DepanFxGitRepoDataExtMenuContribution
    implements DepanFxResourceExtMenuContribution {

  private static Logger LOG =
      LoggerFactory.getLogger(DepanFxGitRepoDataExtMenuContribution.class);

  private static final String EDIT_GIT_REPO = "Edit git Repo Data...";

  @Override
  public boolean acceptsExt(String ext) {
    return DepanFxGitRepoData.GIT_REPO_TOOL_EXT.equals(ext);
  }

  @Override
  public void prepareCell(
      DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
      Cell<DepanFxWorkspaceMember> cell, String ext,
      DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {
    Path docPath = member.getMemberPath();
    DepanFxResourcePerspectives.installOnOpen(cell, docPath,
        p -> runEditGitRepoAction(dialogRunner, workspace, p));
    builder.appendActionItem(
        EDIT_GIT_REPO,
        e -> runEditGitRepoAction(dialogRunner, workspace, docPath));
  }

  private void runEditGitRepoAction(
      DepanFxDialogRunner dialogRunner,
      DepanFxWorkspace workspace,
      Path gitRepoPath) {
    try {
      workspace.toProjectDocument(gitRepoPath.toUri())
          .flatMap(workspace::getWorkspaceResource)
          .ifPresent( r -> DepanFxGitRepoToolDialog.runEditDialog(
              r, dialogRunner));
    } catch (RuntimeException errCaught) {
      LOG.error("Unable to open git repository data {} for edit",
          gitRepoPath.toUri(), errCaught);
    }
  }
}
