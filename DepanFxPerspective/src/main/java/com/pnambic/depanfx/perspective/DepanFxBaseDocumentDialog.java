package com.pnambic.depanfx.perspective;

import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public abstract class DepanFxBaseDocumentDialog<T> extends DepanFxBaseDialog {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxBaseDocumentDialog.class);

  // Allow for future casts, type checks.
  @SuppressWarnings("unused")
  private final Class<T> dataType;

  /**
   * The resource associated with the destination is empty
   * unless there has been a successful save.
   */
  private Optional<DepanFxWorkspaceResource<T>> optResource =
      Optional.empty();

  @FXML
  private TextField destinationField;

  public DepanFxBaseDocumentDialog(
      DepanFxWorkspace workspace, Class<T> dataType) {
    super(workspace);
    this.dataType = dataType;
  }

  public Optional<DepanFxWorkspaceResource<T>> getToolResource() {
    return optResource;
  }

  /**
   * Extendible, {@code @Override} with {@code super.setTooldata()}.
   */
  public void setToolResource(DepanFxWorkspaceResource<T> toolRsrc) {
    optResource = Optional.of(toolRsrc);
    updateDestinationField();
  }

  /**
   * When there is no saved resource, such as canceling the dialog.
   */
  public void clearToolResource() {
    optResource = Optional.empty();
    updateDestinationField();
  }

  /////////////////////////////////////
  // Hook methods for derived classes.

  private void updateDestinationField() {
    if (optResource.isEmpty()) {
      destinationField.setText(null);
      return;
    }

    DepanFxWorkspaceResource<T> resource = optResource.get();
    DepanFxProjectDocument document = resource.getDocument();

    // Don't allow a destination in the built-in or scratch project.
    if (document.getProject().equals(workspace.getBuiltInProjectTree())) {
      destinationField.setText(null);
      return;
    }
    if (document.getProject().equals(workspace.getScratchProjectTree())) {
      destinationField.setText(null);
      return;
    }
    destinationField.setText(DepanFxProjects.getDocumentLabel(document));
  }

  protected String getDestination() {
    return destinationField.getText();
  }

  protected void setDestinationField(TextField destinationField) {
    this.destinationField = destinationField;
  }

  protected abstract String getDocumentName();

  protected abstract T prepareResult();

  protected abstract void setTooldataFilters(FileChooser result);

  protected abstract File buildInitialDestinationFile();

  protected File buildGraphInitialDestination(String targetExt) {
    return DepanFxWorkspaceFactory.bestDocumentFile(
        getDocumentName(), targetExt, workspace,
        DepanFxProjects.getActiveGraphsPath(workspace),
        DepanFxProjects.getCurrentGraphs(workspace));
  }

  /**
   * For now, all analysis files go at the root of the current analysis
   * tree.  Future may define an analysis sub-container, as TBD.
   */
  protected File buildAnalysisInitialDestination(String targetExt) {
    return DepanFxWorkspaceFactory.bestDocumentFile(
        getDocumentName(), targetExt, workspace,
        DepanFxProjects.getActiveAnalyzesPath(workspace),
        DepanFxProjects.getCurrentAnalyzes(workspace));
  }

  protected File buildAnalysisInitialDestination(
      DepanFxProjectDocument relatedDoc, String targetExt) {
    Path initialDir = relatedDoc.getParent()
        .map(c -> c.getMemberPath())
        .filter(p -> p.startsWith(DepanFxProjects.ANALYZES_PATH))
        .orElse(DepanFxProjects.getActiveAnalyzesPath(workspace));

    return DepanFxWorkspaceFactory.bestDocumentFile(
        getDocumentName(), targetExt, workspace, initialDir,
        DepanFxProjects.getCurrentAnalyzes(workspace));
  }

  protected File buildToolInitialDestination(
      String targetExt, Path targetPath) {
    return DepanFxWorkspaceFactory.bestDocumentFile(
        getDocumentName(), targetExt, workspace, targetPath,
        DepanFxProjects.getCurrentTools(workspace));
  }

  /////////////////////////////////////
  // Available to modeless dialog which have a different protocol
  // for window closing.

  protected Optional<DepanFxWorkspaceResource<T>> saveProjectDoc(T toolData) {
    return DepanFxResourcePerspectives.toProjDoc(workspace, destinationField)
        .flatMap(d -> saveDocument(d, toolData));
  }

  /**
   * Extendible, {@code @Override} with {@code super.checkInput()}.
   */
  @Override // DepanFxBaseDialog
  protected void checkInput(DepanFxProctor proctor) {
    DepanFxDialogChecks.checkDestinationFile(
        proctor, destinationField.getText());
  }

  /////////////////////////////////////
  // FXML handlers.

  @Override
  @FXML
  protected void handleCancel() {
    super.handleCancel();
    clearToolResource();
  }

  @FXML
  protected void handleConfirm() {
    if (hasInputErrors()) {
      return;
    }
    closeDialog();

    optResource = saveProjectDoc(prepareResult());
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

  /////////////////////////////////////
  // Internal

  @Override // DepanFxBaseDialog
  public Scene getScene() {
    return destinationField.getScene();
  }

  private FileChooser prepareDestinationFileChooser() {
    FileChooser result =
        DepanFxSceneControls.prepareFileChooser(
            destinationField, () -> buildInitialDestinationFile());
    setTooldataFilters(result);
    return result;
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
}
