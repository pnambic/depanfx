package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeKeyColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeKeyColumnData.KeyChoice;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

@Component
@FxmlView("node-key-column-tool-dialog.fxml")
public class DepanFxNodeKeyColumnToolDialog {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeKeyColumnToolDialog.class.getName());

  public static final ExtensionFilter NODE_KEY_COLUMN_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Node Key Columns", DepanFxNodeKeyColumnData.NODE_KEY_COLUMN_TOOL_EXT);

  private final DepanFxWorkspace workspace;

  private Optional<DepanFxWorkspaceResource> optNodeKeyColumnRsrc;

  @FXML
  private TextField columnLabelField;

  @FXML
  private TextField widthMsField;

  @FXML
  private ComboBox<KeyChoice> keyChoiceField;

  @FXML
  private TextField toolNameField;

  @FXML
  private TextField toolDescriptionField;

  @FXML
  private TextField destinationField;

  @Autowired
  public DepanFxNodeKeyColumnToolDialog(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  public static Dialog<DepanFxNodeKeyColumnToolDialog> runEditDialog(
      DepanFxProjectDocument projDoc,
      DepanFxNodeKeyColumnData columnData,
      DepanFxDialogRunner dialogRunner) {
    Dialog<DepanFxNodeKeyColumnToolDialog> dlg =
        dialogRunner.createDialogAndParent(
            DepanFxNodeKeyColumnToolDialog.class);
    dlg.getController().setDestination(projDoc);
    dlg.getController().setTooldata(columnData);
    dlg.runDialog(DepanFxNodeKeyColumn.EDIT_NODE_KEY_COLUMN);
    return dlg;
  }

  public static Dialog<DepanFxNodeKeyColumnToolDialog> runCreateDialog(
      DepanFxNodeKeyColumnData columnData, DepanFxDialogRunner dialogRunner) {
    Dialog<DepanFxNodeKeyColumnToolDialog> dlg =
        dialogRunner.createDialogAndParent(
            DepanFxNodeKeyColumnToolDialog.class);
    dlg.getController().setTooldata(columnData);
    dlg.runDialog(DepanFxNodeKeyColumn.NEW_NODE_KEY_COLUMN);
    return dlg;
  }

  public static void setNodeKeyColumnTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(NODE_KEY_COLUMN_FILTER);
    result.setSelectedExtensionFilter(NODE_KEY_COLUMN_FILTER);
  }

  @FXML
  public void initialize() {
    keyChoiceField.getItems().add(KeyChoice.MODEL_KEY);
    keyChoiceField.getItems().add(KeyChoice.KIND_KEY);
    keyChoiceField.getItems().add(KeyChoice.NODE_KEY);
  }

  public void setDestination(DepanFxProjectDocument projDoc) {
    destinationField.setText(projDoc.getMemberPath().toString());
  }

  public void setTooldata(DepanFxNodeKeyColumnData columnData) {
    toolNameField.setText(columnData.getToolName());
    toolDescriptionField.setText(columnData.getToolDescription());

    columnLabelField.setText(columnData.getColumnLabel());
    widthMsField.setText(Integer.toString(columnData.getWidthMs()));

    keyChoiceField.setValue(columnData.getKeyChoice());
  }

  public void setNodeKeyColumnResource(
      DepanFxWorkspaceResource nodeKeyColumnRsrc) {
    this.optNodeKeyColumnRsrc = Optional.of(nodeKeyColumnRsrc);
    setTooldata(((DepanFxNodeKeyColumnData) nodeKeyColumnRsrc.getResource()));
    destinationField.setText(
        nodeKeyColumnRsrc.getDocument().getMemberPath().toString());
  }

  public Optional<DepanFxWorkspaceResource> getWorkspaceResource() {
    return optNodeKeyColumnRsrc;
  }

  @FXML
  private void handleCancel() {
    closeDialog();
    optNodeKeyColumnRsrc = Optional.empty();
  }

  @FXML
  private void handleConfirm() {
    closeDialog();

    DepanFxNodeKeyColumnData nodeKeyColumnData = new DepanFxNodeKeyColumnData(
        toolNameField.getText(), toolDescriptionField.getText(),
        columnLabelField.getText(), parseWidthMs(widthMsField.getText()),
        keyChoiceField.getValue());

    File dstDocFile = new File(destinationField.getText());
    optNodeKeyColumnRsrc = workspace.toProjectDocument(dstDocFile.toURI())
        .flatMap(d -> saveDocument(d, nodeKeyColumnData));
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

  private Optional<DepanFxWorkspaceResource> saveDocument(
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

  private int parseWidthMs(String widthMs) {
    int result = 10; // nominal value
    try {
      result = Integer.parseUnsignedInt(widthMs);
    } catch (NumberFormatException errFmt) {
      LOG.warn("Bad user value for widthMs {}", widthMs, errFmt);
    }
    return Math.min(200, Math.max(5, result));
  }

  private void closeDialog() {
    ((Stage) destinationField.getScene().getWindow()).close();
  }

  private FileChooser prepareDestinationFileChooser() {
    FileChooser result =
        DepanFxSceneControls.prepareFileChooser(
            destinationField, () -> buildInitialDestinationFile());
    setNodeKeyColumnTooldataFilters(result);
    return result;
  }

  private File buildInitialDestinationFile() {
    return DepanFxWorkspaceFactory.bestDocumentFile(
        toolNameField.getText(), DepanFxNodeKeyColumnData.NODE_KEY_COLUMN_TOOL_EXT,
        workspace, DepanFxNodeListColumnData.COLUMNS_TOOL_PATH,
        DepanFxProjects.getCurrentTools(workspace));
  }
}
