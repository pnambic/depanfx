package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeKeyColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeKeyColumnData.KeyChoice;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
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
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
@FxmlView("node-key-column-tool-dialog.fxml")
public class DepanFxNodeKeyColumnToolDialog
    extends DepanFxBaseColumnToolDialog {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeKeyColumnToolDialog.class);

  public static final ExtensionFilter NODE_KEY_COLUMN_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Node Key Columns", DepanFxNodeKeyColumnData.NODE_KEY_COLUMN_TOOL_EXT);

  public static final DepanFxResourceFilter NODE_KEY_COLUMN_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Node Key Columns",
          DepanFxNodeKeyColumnData.NODE_KEY_COLUMN_TOOL_EXT,
          DepanFxNodeKeyColumnData.class);

  @FXML
  private ComboBox<KeyChoice> keyChoiceField;

  @Autowired
  public DepanFxNodeKeyColumnToolDialog(DepanFxWorkspace workspace) {
    super(workspace);
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

  public void setTooldata(DepanFxNodeKeyColumnData columnData) {
    toolNameField.setText(columnData.getToolName());
    toolDescriptionField.setText(columnData.getToolDescription());

    columnLabelField.setText(columnData.getColumnLabel());
    widthMsField.setText(Integer.toString(columnData.getWidthMs()));

    keyChoiceField.setValue(columnData.getKeyChoice());
  }

  @Override
  protected Optional<DepanFxWorkspaceResource> prepareResult() {

    DepanFxNodeKeyColumnData nodeKeyColumnData = new DepanFxNodeKeyColumnData(
        toolNameField.getText(), toolDescriptionField.getText(),
        columnLabelField.getText(), parseWidthMs(widthMsField.getText()),
        keyChoiceField.getValue());

    File dstDocFile = new File(destinationField.getText());
    return getWorkspace().toProjectDocument(dstDocFile.toURI())
        .flatMap(d -> saveDocument(d, nodeKeyColumnData));
  }

  @Override
  protected File buildInitialDestinationFile() {
    return DepanFxWorkspaceFactory.bestDocumentFile(
        toolNameField.getText(), DepanFxNodeKeyColumnData.NODE_KEY_COLUMN_TOOL_EXT,
        getWorkspace(), DepanFxNodeListColumnData.COLUMNS_TOOL_PATH,
        DepanFxProjects.getCurrentTools(getWorkspace()));
  }

  @Override
  protected void setColumnTooldataFilters(FileChooser result) {
    DepanFxNodeKeyColumnToolDialog.setNodeKeyColumnTooldataFilters(result);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Node Key Column Save Confirmation Error";
  }
}
