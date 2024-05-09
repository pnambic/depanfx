package com.pnambic.depanfx.perspective;

import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public abstract class DepanFxWorkspaceDialog {

  protected final DepanFxWorkspace workspace;

  public DepanFxWorkspaceDialog(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  public DepanFxWorkspace getWorkspace() {
    return workspace;
  }

  /**
   * Any field on the form should do.
   */
  public abstract Scene getScene();

  protected void closeDialog() {
    ((Stage) getScene().getWindow()).close();
  }

  protected void updateBlankField(TextField updateField, String newValue) {
    DepanFxSceneControls.updateBlankField(updateField, newValue);
  }

  @FXML
  protected void handleCancel() {
    closeDialog();
  }
}
