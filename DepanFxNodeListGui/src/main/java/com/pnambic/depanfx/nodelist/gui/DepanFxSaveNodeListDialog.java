package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class DepanFxSaveNodeListDialog {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxSaveNodeListDialog.class.getName());

  private static final String PREFIX = "nodes ";

  private static final String EXT = "dnli";

  private static final ExtensionFilter EXT_FILTER =
      new ExtensionFilter("Node List (*.dnli)", "*." + EXT);

  private final DepanFxNodeListViewer viewer;

  public DepanFxSaveNodeListDialog(DepanFxNodeListViewer viewer) {
    this.viewer = viewer;
  }

  @FXML
  private TextField destinationField;

  @FXML
  private void openFileChooser() {
    FileChooser fileChooser = prepareFileChooser();
    File selectedFile = fileChooser.showSaveDialog(destinationField.getScene().getWindow());
    if (selectedFile != null) {
      destinationField.setText(selectedFile.getAbsolutePath());
    }
  }

  @FXML
  private void handleCancel() {
    closeDialog();
    LOG.info("cancelled request");
  }

  @FXML
  private void handleConfirm() {
    closeDialog();

    System.out.println("Destination file: " + destinationField.getText());
    File dstFile = new File(destinationField.getText());
    viewer.saveNodeList(dstFile);
  }

  private FileChooser prepareFileChooser() {
    FileChooser result = new FileChooser();
    result.getExtensionFilters().add(EXT_FILTER);
    result.setSelectedExtensionFilter(EXT_FILTER);

    String destFileName = destinationField.getText();
    if (destFileName.isBlank()) {
      result.setInitialFileName(buildTimestampName(PREFIX, EXT));
      result.setInitialDirectory(getWorkspaceDestination());
      return result;
    }

    File location = new File(destFileName);
    result.setInitialFileName(location.getName());
    result.setInitialDirectory(location.getParentFile());
    return result;
  }

  private void closeDialog() {
    ((Stage) destinationField.getScene().getWindow()).close();
  }

  private File getWorkspaceDestination() {
    return DepanFxProjects.getCurrentAnalyzes(viewer.getWorkspace());
  }

  private String buildTimestampName(String prefix, String ext) {
    return DepanFxWorkspaceFactory.buildDocumentTimestampName(prefix, ext);
  }
}
