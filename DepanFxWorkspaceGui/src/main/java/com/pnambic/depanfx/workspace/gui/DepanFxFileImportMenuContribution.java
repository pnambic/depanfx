package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuContribution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;

@Component
public class DepanFxFileImportMenuContribution
    implements DepanFxSceneMenuContribution {

  private final DepanFxDialogRunner dialogRunner;

  @Autowired
  public DepanFxFileImportMenuContribution(DepanFxDialogRunner dialogRunner) {
    this.dialogRunner = dialogRunner;
  }

  @Override
  public boolean acceptsEvent(ActionEvent event) {
    MenuItem item = (MenuItem) event.getSource();
    return item.idProperty().getValue().equals("fileImportItem");
  }

  @Override
  public void handleEvent(ActionEvent event) {
    runImportDialog();
  }

  private void runImportDialog() {
    dialogRunner.runDialog(DepanFxFileImportDialog.class, "Import context");
  }
}
