package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

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
@FxmlView("save-node-list-dialog.fxml")
public class DepanFxSaveNodeListDialog {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxSaveNodeListDialog.class.getName());

  private static final String PREFIX = "nodes";

  private static final String EXT = DepanFxNodeList.NODE_LIST_EXT;

  private static final ExtensionFilter EXT_FILTER =
      DepanFxSceneControls.buildExtFilter("Node List", EXT);

  private DepanFxNodeListViewer viewer;

  @Autowired
  public DepanFxSaveNodeListDialog() {
  }

  public void setNodeListView(DepanFxNodeListViewer viewer) {
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

    File dstFile = new File(destinationField.getText());
    DepanFxProjectDocument projDoc =
        viewer.getWorkspace().toProjectDocument(dstFile.toURI()).get();
    viewer.saveNodeList(projDoc);
  }

  private FileChooser prepareFileChooser() {
    FileChooser result =
        DepanFxSceneControls.prepareFileChooser(
            destinationField,
            () -> new File(
                getWorkspaceDestination(),
                buildTimestampName(PREFIX, EXT)));
    result.getExtensionFilters().add(EXT_FILTER);
    result.setSelectedExtensionFilter(EXT_FILTER);

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
