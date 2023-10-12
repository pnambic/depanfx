package com.pnambic.depanfx.workspace.gui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceRegistry;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneStarterContribution;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import javafx.scene.control.Tab;

@Component
public class DepanFxWorkspaceSceneContribution implements DepanFxSceneStarterContribution {

  public static final String WORKSPACE_TAB = "Workspace";

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxNewResourceRegistry newResourceRegistry;

  @Autowired
  public DepanFxWorkspaceSceneContribution(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNewResourceRegistry newResourceRegistry) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.newResourceRegistry = newResourceRegistry;
  }

  @Override
  public String getLabel() {
    return WORKSPACE_TAB;
  }

  @Override
  public Tab createStarterTab(String label) {
    DepanFxProjectListViewer workspaceViewer =
        new DepanFxProjectListViewer(workspace, dialogRunner, newResourceRegistry);
    return workspaceViewer.createWorkspaceTab(WORKSPACE_TAB);
  }
}
