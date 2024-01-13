package com.pnambic.depanfx.nodelist.gui.columns;

import com.google.common.base.Strings;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListChooser;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFocusColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

@Component
@FxmlView("focus-column-tool-dialog.fxml")
public class DepanFxFocusColumnToolDialog {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxFocusColumnToolDialog.class.getName());

  public static final ExtensionFilter FOCUS_COLUMN_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Focus Columns", DepanFxFocusColumnData.FOCUS_COLUMN_TOOL_EXT);

  private final DepanFxWorkspace workspace;

  private Optional<DepanFxWorkspaceResource> optFocusColumnRsrc;

  @FXML
  private TextField columnLabelField;

  @FXML
  private TextField widthMsField;

  @FXML
  private TextField focusLabelField;

  @FXML
  private TextField focusNodeListRsrcField;

  @FXML
  private TextField toolNameField;

  @FXML
  private TextField toolDescriptionField;

  @FXML
  private TextField destinationField;

  @Autowired
  public DepanFxFocusColumnToolDialog(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  public static Dialog<DepanFxFocusColumnToolDialog> runEditDialog(
      DepanFxProjectDocument projDoc,
      DepanFxFocusColumnData focusColumnData,
      DepanFxDialogRunner dialogRunner) {
    Dialog<DepanFxFocusColumnToolDialog> dlg =
        dialogRunner.createDialogAndParent(DepanFxFocusColumnToolDialog.class);
    dlg.getController().setDestination(projDoc);
    dlg.getController().setTooldata(focusColumnData);
    dlg.runDialog(DepanFxFocusColumn.EDIT_FOCUS_COLUMN);
    return dlg;
  }

  public static Dialog<DepanFxFocusColumnToolDialog> runCreateDialog(
      DepanFxFocusColumnData columnData, DepanFxDialogRunner dialogRunner) {
    Dialog<DepanFxFocusColumnToolDialog> dlg =
        dialogRunner.createDialogAndParent(DepanFxFocusColumnToolDialog.class);
    dlg.getController().setTooldata(columnData);
    dlg.runDialog(DepanFxFocusColumn.NEW_FOCUS_COLUMN);
    return dlg;
  }

  public static void setFocusColumnTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(FOCUS_COLUMN_FILTER);
    result.setSelectedExtensionFilter(FOCUS_COLUMN_FILTER);
  }

  @FXML
  public void initialize() {
    focusNodeListRsrcField.setContextMenu(buildNodeListChoiceMenu());
  }

  public void setDestination(DepanFxProjectDocument projDoc) {
    destinationField.setText(projDoc.getMemberPath().toString());
  }

  public void setTooldata(DepanFxFocusColumnData columnData) {
    toolNameField.setText(columnData.getToolName());
    toolDescriptionField.setText(columnData.getToolDescription());

    columnLabelField.setText(columnData.getColumnLabel());
    widthMsField.setText(Integer.toString(columnData.getWidthMs()));

    focusLabelField.setText(columnData.getFocusLabel());
    focusNodeListRsrcField.setText(getNodeListRsrcName(columnData));
  }

  private String getNodeListRsrcName(DepanFxFocusColumnData columnData) {
    if (columnData.getNodeListRsrc() != null) {
      return columnData.getNodeListRsrc().getDocument()
          .getMemberPath().toString();
    }
    // Let the text input field show a prompt text.
    return null;
  }

  public Optional<DepanFxWorkspaceResource> getWorkspaceResource() {
    return optFocusColumnRsrc;
  }

  @FXML
  private void handleCancel() {
    closeDialog();
    optFocusColumnRsrc = Optional.empty();
  }

  @FXML
  private void handleConfirm() {
    closeDialog();
    File nodeListFile = new File(focusNodeListRsrcField.getText());
    Optional<DepanFxWorkspaceResource> optNodeListRsrc = workspace
        .toProjectDocument(nodeListFile.toURI())
        .flatMap(p -> workspace.getWorkspaceResource(p, DepanFxNodeList.class));

    DepanFxFocusColumnData focusColumnData = new DepanFxFocusColumnData(
        toolNameField.getText(), toolDescriptionField.getText(),
        columnLabelField.getText(), parseWidthMs(widthMsField.getText()),
        focusLabelField.getText(), optNodeListRsrc.get());

    File dstDocFile = new File(destinationField.getText());
    optFocusColumnRsrc = workspace.toProjectDocument(dstDocFile.toURI())
        .flatMap(d -> saveDocument(d, focusColumnData));
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

  private ContextMenu buildNodeListChoiceMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem("Select Node List...",
        e -> runNodeListFinder());
    return builder.build();
  }

  private void runNodeListFinder() {
    DepanFxNodeListChooser.runNodeListFinder(
        workspace, focusNodeListRsrcField.getScene().getWindow())
        .ifPresent(this::updateNodeListFields);
  }

  private void updateNodeListFields(DepanFxWorkspaceResource nodeListRsrc) {
    focusNodeListRsrcField.setText(
        nodeListRsrc.getDocument().getMemberPath().toString());

    if (Strings.isNullOrEmpty(focusLabelField.getText())) {
      focusLabelField.setText((
          (DepanFxNodeList) nodeListRsrc.getResource()).getNodeListName());
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
    setFocusColumnTooldataFilters(result);
    return result;
  }

  private File buildInitialDestinationFile() {
    return DepanFxWorkspaceFactory.bestDocumentFile(
        toolNameField.getText(), DepanFxFocusColumnData.FOCUS_COLUMN_TOOL_EXT,
        workspace, DepanFxNodeListColumnData.COLUMNS_TOOL_PATH,
        DepanFxProjects.getCurrentTools(workspace));
  }
}
