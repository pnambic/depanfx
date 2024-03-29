package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.perspective.plugins.DepanFxResourceMenuRegistry;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneStarterContribution;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.control.Tab;

@Component
public class DepanFxWorkspaceSceneContribution implements DepanFxSceneStarterContribution {

  public static final String WORKSPACE_TAB = "Workspace";

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxResourceMenuRegistry rsrcMenuRegistry;

  @Autowired
  public DepanFxWorkspaceSceneContribution(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxResourceMenuRegistry rsrcMenuRegistry) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.rsrcMenuRegistry = rsrcMenuRegistry;
  }

  @Override
  public String getLabel() {
    return WORKSPACE_TAB;
  }

  @Override
  public Tab createStarterTab(String label, DepanFxSceneController scene) {
    DepanFxProjectListViewer workspaceViewer =
        new DepanFxProjectListViewer(
          workspace, dialogRunner, rsrcMenuRegistry, scene);
    return workspaceViewer.createWorkspaceTab(WORKSPACE_TAB);
  }
}
