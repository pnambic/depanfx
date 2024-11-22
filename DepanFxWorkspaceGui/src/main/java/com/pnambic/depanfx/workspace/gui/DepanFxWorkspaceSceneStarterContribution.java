package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.perspective.plugins.DepanFxResourceMenuRegistry;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneStarterContribution;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DepanFxWorkspaceSceneStarterContribution
    implements DepanFxSceneStarterContribution {

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxResourceMenuRegistry rsrcMenuRegistry;

  @Autowired
  public DepanFxWorkspaceSceneStarterContribution(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxResourceMenuRegistry rsrcMenuRegistry) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.rsrcMenuRegistry = rsrcMenuRegistry;
  }

  @Override
  public String getLabel() {
    return DepanFxWorkspaceViewer.WORKSPACE_TAB;
  }

  @Override
  public DepanFxSceneViewer getSceneViewer() {

    return new DepanFxWorkspaceViewer(
        workspace, dialogRunner, rsrcMenuRegistry, getLabel());
  }
}
