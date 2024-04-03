package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.perspective.DepanFxDialogChecks;
import com.pnambic.depanfx.perspective.DepanFxProctor;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public abstract class DepanFxBaseColumnToolDialog {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxBaseColumnToolDialog.class);

  public static final int BASE_COLUMN_WIDTH = 10;

  public static final int MAX_COLUMN_WIDTH = 200;

  public static final int MIN_COLUMN_WIDTH = 5;

  private final DepanFxWorkspace workspace;

  private Optional<DepanFxWorkspaceResource> optColumnRsrc;

  @FXML
  protected TextField columnLabelField;

  @FXML
  protected TextField widthMsField;

  @FXML
  protected TextField toolNameField;

  @FXML
  protected TextField toolDescriptionField;

  @FXML
  protected TextField destinationField;

  public DepanFxBaseColumnToolDialog(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  public void setDestination(DepanFxProjectDocument projDoc) {
    // Don't allow a destination in the built-in project.
    if (workspace.getBuiltInProjectTree().equals(projDoc.getProject())) {
      destinationField.setText(null);
      return;
    }
    destinationField.setText(projDoc.getMemberPath().toString());
  }

  public DepanFxWorkspace getWorkspace() {
    return workspace;
  }

  public Optional<DepanFxWorkspaceResource> getWorkspaceResource() {
    return optColumnRsrc;
  }

  /////////////////////////////////////
  // Hook methods for derived classes.

  protected abstract Optional<DepanFxWorkspaceResource> prepareResult();

  protected abstract void setColumnTooldataFilters(FileChooser result);

  protected abstract File buildInitialDestinationFile();

  protected abstract String getInputCheckFailureText();

  /**
   * Extendable, {@code @Override} with {@code super.checkInput()}.
   */
  protected void checkInput(DepanFxProctor proctor) {
    DepanFxDialogChecks.checkDestinationFile(
        proctor, destinationField.getText());
  }

  @FXML
  protected void handleCancel() {
    closeDialog();
    optColumnRsrc = Optional.empty();
  }

  @FXML
  protected void handleConfirm() {
    DepanFxProctor proctor = new DepanFxProctor.Simple();
    checkInput(proctor);
    if (DepanFxResourcePerspectives.errorAlert(
        proctor, getInputCheckFailureText())) { // "Category Column Save Confirmation Error")) {
      return;
    }
    closeDialog();

    optColumnRsrc = prepareResult();
  }

  @FXML
  private void openDestinationChooser() {
    FileChooser fileChooser = prepareDestinationFileChooser();
    File selectedFile =
        fileChooser.showSaveDialog(destinationField.getScene().getWindow());
    if (selectedFile != null) {
      destinationField.setText(selectedFile.getAbsolutePath());
    }
  }

  /**
   * Available to implement {@link #prepareResult()}.
   */
  protected Optional<DepanFxWorkspaceResource> saveDocument(
      DepanFxProjectDocument projDoc, Object docData) {
    try {
      return workspace.saveDocument(projDoc, docData);
    } catch (IOException errIo) {
      LOG.error("Unable to save {}, type {}",
          projDoc.toString(), docData.getClass().getName(), errIo);
      throw new RuntimeException(
          "Unable to save " + projDoc.toString()
          + ", type " + docData.getClass().getName(),
          errIo);
    }
  }

  /**
   * Available to implement {@link #prepareResult()}.
   */
  protected int parseWidthMs(String widthMs) {
    int result = BASE_COLUMN_WIDTH;
    try {
      result = Integer.parseUnsignedInt(widthMs);
    } catch (NumberFormatException errFmt) {
      LOG.warn("Bad user value for widthMs {}", widthMs, errFmt);
    }
    return Math.min(MAX_COLUMN_WIDTH, Math.max(MIN_COLUMN_WIDTH, result));
  }

  private void closeDialog() {
    ((Stage) destinationField.getScene().getWindow()).close();
  }

  private FileChooser prepareDestinationFileChooser() {
    FileChooser result =
        DepanFxSceneControls.prepareFileChooser(
            destinationField, () -> buildInitialDestinationFile());
    setColumnTooldataFilters(result);
    return result;
  }
}
