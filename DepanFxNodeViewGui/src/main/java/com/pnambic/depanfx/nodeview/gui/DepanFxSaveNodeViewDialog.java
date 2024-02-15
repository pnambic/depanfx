package com.pnambic.depanfx.nodeview.gui;

import com.google.common.base.Strings;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
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
import java.text.MessageFormat;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

@Component
@FxmlView("save-node-view-dialog.fxml")
public class DepanFxSaveNodeViewDialog {

  private static final String EXT = DepanFxNodeViewData.NODE_VIEW_TOOL_EXT;

  private static final ExtensionFilter EXT_FILTER =
      DepanFxSceneControls.buildExtFilter("Node View", EXT);

  @FXML
  private Label nodeViewDetailsLabel;

  @FXML
  private TextField nodeViewNameField;

  @FXML
  private TextField nodeViewDescriptionField;

  @FXML
  private TextField destinationField;

  private final DepanFxWorkspace workspace;

  private Optional<DepanFxWorkspaceResource> savedRsrc = Optional.empty();

  private DepanFxNodeViewData viewDoc;

  @Autowired
  public DepanFxSaveNodeViewDialog(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  public void setNodeViewDoc(DepanFxNodeViewData viewDoc) {
    this.viewDoc = viewDoc;
    nodeViewNameField.setText(viewDoc.getToolName());
    nodeViewDescriptionField.setText(viewDoc.getToolDescription());
    nodeViewDetailsLabel.setText(buildDetailsLabel(viewDoc));
  }

  private String buildDetailsLabel(DepanFxNodeViewData viewDoc) {
    String graphSource =
        viewDoc.getGraphDocRsrc().getDocument().getMemberName();
    int nodeCount = viewDoc.getViewNodes().size();
    return MessageFormat.format("Node view from {0} with {1} nodes.",
        graphSource, nodeCount);
  }

  public void setDestination(DepanFxProjectDocument document) {
    destinationField.setText(document.getMemberPath().toString());
  }

  public Optional<DepanFxWorkspaceResource> getSavedResource() {
    return savedRsrc;
  }

  @FXML
  private void openFileChooser() {
    FileChooser fileChooser = prepareFileChooser();
    File selectedFile =
        fileChooser.showSaveDialog(destinationField.getScene().getWindow());
    if (selectedFile != null) {
      destinationField.setText(selectedFile.getAbsolutePath());
    }
  }

  @FXML
  private void handleCancel() {
    closeDialog();
    savedRsrc = Optional.empty();
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
    DepanFxNodeViewData saveDoc =
        DepanFxNodeViews.updateNameDescr(
            viewDoc,
            nodeViewNameField.getText(),
            nodeViewDescriptionField.getText());

    try {
      savedRsrc = workspace.saveDocument(projDoc, saveDoc);
    } catch (Exception errAny) {
      throw new RuntimeException(
          "Unable to save node view " + projDoc.getMemberPath(), errAny);
    }
  }

  private FileChooser prepareFileChooser() {
    String baseName = nodeViewNameField.getText();
    FileChooser result =
        DepanFxSceneControls.prepareFileChooser(
            destinationField,
            () -> new File(
                getWorkspaceDestination(),
                buildTimestampName(baseName, EXT)));
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
