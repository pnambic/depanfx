package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuContribution;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;

@Component
public class DepanFxFileOpenProjectMenuContribution
    implements DepanFxSceneMenuContribution {

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  @Autowired
  public DepanFxFileOpenProjectMenuContribution(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
  }

  @Override
  public boolean acceptsEvent(ActionEvent event) {
    MenuItem item = (MenuItem) event.getSource();
    return item.idProperty().getValue().equals("fileOpenProjectItem");
  }

  @Override
  public void handleEvent(ActionEvent event) {
    DepanFxProjectChooser.runProjectFinder()
        .ifPresent(p -> {
            workspace.addProject(p);
            workspace.setCurrentProject(p);
        });
  }
}
