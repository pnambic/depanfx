package com.pnambic.depanfx.filesystem.gui;

import org.springframework.stereotype.Component;

import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceContribution;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;

@Component
public class DepanFxNewFileSystemContribution
    implements DepanFxNewResourceContribution {
  
  private final FxWeaver fxweaver;

  public DepanFxNewFileSystemContribution(FxWeaver fxweaver) {
    this.fxweaver = fxweaver;
  }

  @Override
  public MenuItem createNewResourceMenuItem() {
    MenuItem result = new MenuItem("File System");
    result.setOnAction(this::runDialog);
    return result;
  }

  private void runDialog(ActionEvent event) {
      Parent dialogPane = fxweaver.loadView(DepanFxNewFileSystemDialog.class);
      Stage dialogStage = new Stage();
      dialogStage.initModality(Modality.APPLICATION_MODAL);
      dialogStage.setTitle("Create new file system graph");
      dialogStage.setScene(new Scene(dialogPane));
      dialogStage.showAndWait();
  }
}
