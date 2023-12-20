package com.pnambic.depanfx.nodelist.gui;

import com.google.common.base.Strings;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

@Component
@FxmlView("save-node-list-dialog.fxml")
public class DepanFxSaveNodeListDialog {

  private static final String PREFIX = "nodes";

  private static final String EXT = DepanFxNodeList.NODE_LIST_EXT;

  private static final ExtensionFilter EXT_FILTER =
      DepanFxSceneControls.buildExtFilter("Node List", EXT);

  @FXML
  private TextField destinationField;

  private final DepanFxWorkspace workspace;

  private Optional<DepanFxWorkspaceResource> savedRsrc = Optional.empty();

  private DepanFxNodeList nodeList;

  @Autowired
  public DepanFxSaveNodeListDialog(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  public void setNodeListDoc(DepanFxNodeList nodeList) {
    this.nodeList = nodeList;
  }

  public void setInitialDest(DepanFxProjectDocument document) {
    destinationField.setText(
        document.getMemberPath().toAbsolutePath().toString());
  }

  public Optional<DepanFxWorkspaceResource> getSavedResource() {
    return savedRsrc;
  }

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
  }

  @FXML
  private void handleConfirm() {
    String dstName = destinationField.getText();
    File dstFile = new File(dstName);
    if (Strings.isNullOrEmpty(dstName)) {
      Alert alert = new Alert(AlertType.ERROR);
      alert.setContentText("Destination field is not usable");
      alert.setHeaderText("Node List Save Confirmation Error");
      alert.setTitle("Blank value for destination field");
      alert.showAndWait();
      return;
    }
    closeDialog();

    DepanFxProjectDocument projDoc =
        workspace.toProjectDocument(dstFile.toURI()).get();

    try {
      savedRsrc = workspace.saveDocument(projDoc, nodeList);
    } catch (Exception errAny) {
      throw new RuntimeException(
          "Unable to save nodelist " + projDoc.getMemberPath(), errAny);
    }
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
    return DepanFxProjects.getCurrentAnalyzes(workspace);
  }

  private String buildTimestampName(String prefix, String ext) {
    return DepanFxWorkspaceFactory.buildDocumentTimestampName(prefix, ext);
  }
}
