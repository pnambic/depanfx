package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

@Component
@FxmlView("file-import-dialog.fxml")
public class DepanFxFileImportDialog {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxFileImportDialog.class.getName());

  private static final String EXT = "dgi";

  private static final ExtensionFilter EXT_FILTER =
      new ExtensionFilter("Graph Info (*.dgi)", "*." + EXT);

  private final DepanFxWorkspace workspace;

  @Autowired
  public DepanFxFileImportDialog(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  @FXML
  private TextField sourceField;

  @FXML
  private void openFileChooser() {
    FileChooser fileChooser = prepareFileChooser();
    File selectedFile = fileChooser.showOpenDialog(sourceField.getScene().getWindow());
    if (selectedFile != null) {
      sourceField.setText(selectedFile.getAbsolutePath());
    }
  }

  @FXML
  private void handleCancel() {
    closeDialog();
    LOG.info("cancelled graph import request");
  }

  @FXML
  private void handleConfirm() {
    closeDialog();

    File srcFile = new File(sourceField.getText());
    DepanFxProjectDocument projDoc =
        workspace.toProjectDocument(srcFile.getAbsoluteFile().toURI()).get();
    workspace.getWorkspaceResource(projDoc, GraphDocument.class);
  }

  private FileChooser prepareFileChooser() {
    FileChooser result = DepanFxSceneControls.prepareFileChooser(sourceField);
    result.getExtensionFilters().add(EXT_FILTER);
    result.setSelectedExtensionFilter(EXT_FILTER);

    return result;
  }

  private void closeDialog() {
    ((Stage) sourceField.getScene().getWindow()).close();
  }
}
