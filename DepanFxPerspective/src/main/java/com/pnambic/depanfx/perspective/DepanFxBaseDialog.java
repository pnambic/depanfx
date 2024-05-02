package com.pnambic.depanfx.perspective;

import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Base definitions for close, handleCancel, handleConfirm.
 */
public abstract class DepanFxBaseDialog {

  protected final DepanFxWorkspace workspace;

  public DepanFxBaseDialog(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  public DepanFxWorkspace getWorkspace() {
    return workspace;
  }

  /**
   * Any field on the form should do.
   */
  public abstract Scene getScene();

  /**
   * The main label to show if the input contains an error.
   */
  protected abstract String getInputCheckFailureText();

  /**
   * All validation errors should to added to
   * the supplied test (@link #proctor).
   */
  protected abstract void checkInput(DepanFxProctor proctor);

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

  /**
   * If the input fails validation ({@link #checkInput(DepanFxProctor)}),
   * a user error dialog is shown before the method returns.
   */
  protected boolean hasInputErrors() {
    DepanFxProctor proctor = new DepanFxProctor.Simple();
    checkInput(proctor);
    if ((!proctor.hasErrors())) {
      return false;
    }
    DepanFxResourcePerspectives.errorAlert(proctor, getInputCheckFailureText());
    return true;
  }
}
