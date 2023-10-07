package com.pnambic.depanfx.workspace.gui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuContribution;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;

@Component
public class DepanFxFileImportMenuContribution
    implements DepanFxSceneMenuContribution {

  private final FxWeaver fxweaver;

  @Autowired
  public DepanFxFileImportMenuContribution(FxWeaver fxweaver) {
    this.fxweaver = fxweaver;
  }

  @Override
  public boolean acceptsEvent(ActionEvent event) {
    MenuItem item = (MenuItem) event.getSource();
    return item.idProperty().getValue().equals("fileImportItem");
  }

  @Override
  public void handleEvent(ActionEvent event) {
    runImportDialog(event);
  }

  private void runImportDialog(ActionEvent event) {
      Parent dialogPane = fxweaver.loadView(DepanFxFileImportDialog.class);
      Stage dialogStage = new Stage();
      dialogStage.initModality(Modality.APPLICATION_MODAL);
      dialogStage.setTitle("Import context");
      dialogStage.setScene(new Scene(dialogPane));
      dialogStage.showAndWait();
  }
}
