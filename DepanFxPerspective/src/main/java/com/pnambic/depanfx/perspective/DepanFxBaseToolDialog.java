package com.pnambic.depanfx.perspective;

import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public abstract class DepanFxBaseToolDialog<T extends DepanFxBaseToolData> {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxBaseToolDialog.class);

  private final DepanFxWorkspace workspace;

  // Allow for future casts, type checks.
  @SuppressWarnings("unused")
  private final Class<T> dataType;

  private Optional<DepanFxWorkspaceResource<T>> optResource;

  @FXML
  private TextField toolNameField;

  @FXML
  private TextField toolDescriptionField;

  @FXML
  private TextField destinationField;

  public DepanFxBaseToolDialog(DepanFxWorkspace workspace, Class<T> dataType) {
    this.workspace = workspace;
    this.dataType = dataType;
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

  public Optional<DepanFxWorkspaceResource<T>> getWorkspaceResource() {
    return optResource;
  }

  /////////////////////////////////////
  // Hook methods for derived classes.

  /**
   * Extendible, {@code @Override} with {@code super.setTooldata()}.
   */
  public void setTooldata(T toolData) {
    toolNameField.setText(toolData.getToolName());
    toolDescriptionField.setText(toolData.getToolDescription());
  }

  protected String getToolName() {
    return toolNameField.getText();
  }

  protected void updateBlankToolName(String newValue) {
    updateBlankField(toolNameField, newValue);
  }

  protected String getToolDescription() {
    return toolDescriptionField.getText();
  }

  protected void updateBlankToolDescription(String newValue) {
    updateBlankField(toolDescriptionField, newValue);
  }

  protected String getDestination() {
    return destinationField.getText();
  }

  protected void setDestinationField(TextField destinationField) {
    this.destinationField = destinationField;
  }

  protected abstract T prepareResult();

  protected abstract void setTooldataFilters(FileChooser result);

  protected abstract File buildInitialDestinationFile();

  protected abstract String getInputCheckFailureText();

  /**
   * Extendible, {@code @Override} with {@code super.checkInput()}.
   */
  protected void checkInput(DepanFxProctor proctor) {
    DepanFxDialogChecks.checkDestinationFile(
        proctor, destinationField.getText());
  }

  /**
   * For now, all analysis files go at the root of the current analysis
   * tree.  Future may define an analysis sub-container, as TBD.
   */
  protected File buildAnalysisInitialDestination(String targetExt) {
    return DepanFxWorkspaceFactory.bestDocumentFile(
        toolNameField.getText(), targetExt, workspace,
        DepanFxProjects.getCurrentAnalyzesPath(workspace).orElse(null),
        DepanFxProjects.getCurrentAnalyzes(workspace));
  }

  protected File buildToolInitialDestination(
      String targetExt, Path targetPath) {
    return DepanFxWorkspaceFactory.bestDocumentFile(
        toolNameField.getText(), targetExt, workspace, targetPath,
        DepanFxProjects.getCurrentTools(workspace));
  }

  /////////////////////////////////////
  // FXML handlers.

  @FXML
  protected void handleCancel() {
    closeDialog();
    optResource = Optional.empty();
  }

  @FXML
  protected void handleConfirm() {
    DepanFxProctor proctor = new DepanFxProctor.Simple();
    checkInput(proctor);
    if (DepanFxResourcePerspectives.errorAlert(
        proctor, getInputCheckFailureText())) {
      return;
    }
    closeDialog();

    // Make sure we can get a result before trying to save
    T toolData = prepareResult();

    optResource =
        DepanFxResourcePerspectives.toProjDoc(workspace, destinationField)
        .flatMap(d -> saveDocument(d, toolData));
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

  private Optional<DepanFxWorkspaceResource<T>> saveDocument(
      DepanFxProjectDocument projDoc, T docData) {
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

  private void closeDialog() {
    ((Stage) destinationField.getScene().getWindow()).close();
  }

  private FileChooser prepareDestinationFileChooser() {
    FileChooser result =
        DepanFxSceneControls.prepareFileChooser(
            destinationField, () -> buildInitialDestinationFile());
    setTooldataFilters(result);
    return result;
  }

  protected void updateBlankField(TextField updateField, String newValue) {
    DepanFxSceneControls.updateBlankField(updateField, newValue);
  }
}
