package com.pnambic.depanfx.workspace.gui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pnambic.depanfx.scene.plugins.DepanFxSceneStarterContribution;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import javafx.scene.control.Tab;

@Component
public class DepanFxWorkspaceSceneContribution implements DepanFxSceneStarterContribution {

  public static final String WORKSPACE_TAB = "Workspace";

  private final DepanFxWorkspace workspace;

  @Autowired
  public DepanFxWorkspaceSceneContribution(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  @Override
  public String getLabel() {
    return WORKSPACE_TAB;
  }

  @Override
  public Tab createStarterTab(String label) {
    DepanFxProjectListViewer workspaceViewer = new DepanFxProjectListViewer(workspace);
    return workspaceViewer.createWorkspaceTab(WORKSPACE_TAB);
  }
}
