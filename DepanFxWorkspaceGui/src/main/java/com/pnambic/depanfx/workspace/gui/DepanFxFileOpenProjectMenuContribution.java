package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuContribution;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuItems;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;

@Component
public class DepanFxFileOpenProjectMenuContribution
    extends DepanFxSceneMenuContribution.Simple {

  private final DepanFxWorkspace workspace;

  @Autowired
  public DepanFxFileOpenProjectMenuContribution(DepanFxWorkspace workspace) {
    super(DepanFxSceneMenuItems.FILE_OPEN_PROJECT_ITEM);
    this.workspace = workspace;
  }

  @Override
  public boolean forViewer(DepanFxSceneViewer viewer) {
    return true;
  }

  @Override
  public void handleEvent(
      DepanFxSceneService sceneSrvc, ActionEvent event) {
    DepanFxProjectChooser.runProjectFinder()
        .ifPresent(p -> {
            workspace.addProject(p);
            workspace.setCurrentProject(p);
        });
  }
}
