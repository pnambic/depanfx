package com.pnambic.depanfx.git.gui;

import com.pnambic.depanfx.git.tooldata.DepanFxGitRepoData;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceExtMenuContribution;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.stereotype.Component;

@Component
public class DepanFxGitRepoDataExtMenuContribution
    extends DepanFxResourceExtMenuContribution.Basic {

  private static final String EDIT_GIT_REPO = "Edit git Repo Data...";

  public DepanFxGitRepoDataExtMenuContribution() {
    super(DepanFxGitRepoData.class, DepanFxGitRepoGuiContribution.GIT_REPO_KEY,
        EDIT_GIT_REPO, DepanFxGitRepoData.GIT_REPO_TOOL_EXT);
  }

  @Override
  protected void runDialog(DepanFxWorkspaceResource wkspRsrc,
      DepanFxDialogRunner dialogRunner) {
    DepanFxGitRepoToolDialog.runEditDialog(
        wkspRsrc.getDocument(), (DepanFxGitRepoData) wkspRsrc.getResource(),
        dialogRunner);
  }
}
