package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.git.tooldata.DepanFxGitRepoData;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourcePathMenuContribution;
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
import javafx.stage.Window;

@Component
public class DepanFxGitRepoDataPathMenuContribution
implements DepanFxResourcePathMenuContribution {

  private static final String NEW_GIT_REPO = "New git Repo Data...";

  private static Logger LOG =
      LoggerFactory.getLogger(DepanFxGitRepoDataPathMenuContribution.class);

  @Override
  public boolean acceptsPath(Path rsrcPath) {
    return DepanFxGitRepoData.GIT_REPOS_TOOL_PATH.equals(rsrcPath);
  }

  @Override
  public void prepareCell(
      DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace,
      Cell<DepanFxWorkspaceMember> cell,
      DepanFxProjectMember member, DepanFxContextMenuBuilder builder) {

    DepanFxGitRepoToolDialog.Aware srcDlg =
        new DepanFxGitRepoToolDialog.Aware() {

      @Override
      public Window getChooserWindow() {
        return cell.getScene().getWindow();
      }

      @Override
      public DepanFxGitRepoData getTooldata() {
        return DepanFxGitRepoToolDialogs.buildInitialGitRepoData();
      }

      @Override
      public void setTooldata(DepanFxGitRepoData repoData) {
        // Not used.
      }
    };

    builder.appendActionItem(
        NEW_GIT_REPO,
        e -> runNewGitRepoAction(dialogRunner, srcDlg));
  }

  private void runNewGitRepoAction(
      DepanFxDialogRunner dialogRunner,
      DepanFxGitRepoToolDialog.Aware srcDlg) {
    try {
      DepanFxGitRepoToolDialog.runCreateDialog(srcDlg , dialogRunner);
    } catch (RuntimeException errCaught) {
      LOG.error("Unable to create git repo data", errCaught);
    }
  }
}
