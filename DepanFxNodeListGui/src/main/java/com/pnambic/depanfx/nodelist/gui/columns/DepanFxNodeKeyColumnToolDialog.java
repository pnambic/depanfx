package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeKeyColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeKeyColumnData.KeyChoice;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
@FxmlView("node-key-column-tool-dialog.fxml")
public class DepanFxNodeKeyColumnToolDialog
    extends DepanFxBaseColumnToolDialog<DepanFxNodeKeyColumnData> {

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
    super(workspace, DepanFxNodeKeyColumnData.class);
  }

  public static Dialog<DepanFxNodeKeyColumnToolDialog> runEditDialog(
      DepanFxProjectDocument projDoc,
      DepanFxNodeKeyColumnData columnData,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runEditDialog(
        projDoc, columnData, dialogRunner,
        DepanFxNodeKeyColumnToolDialog.class,
        DepanFxNodeKeyColumn.EDIT_NODE_KEY_COLUMN);
  }

  public static Dialog<DepanFxNodeKeyColumnToolDialog> runCreateDialog(
      DepanFxNodeKeyColumnData columnData, DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runCreateDialog(
        columnData, dialogRunner,
        DepanFxNodeKeyColumnToolDialog.class,
        DepanFxNodeKeyColumn.NEW_NODE_KEY_COLUMN);
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

  @Override // DepanFxBaseColumnToolDialog
  public void setTooldata(DepanFxNodeKeyColumnData columnData) {
    super.setTooldata(columnData);

    keyChoiceField.setValue(columnData.getKeyChoice());
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxNodeKeyColumnData prepareResult() {

    return new DepanFxNodeKeyColumnData(
        toolNameField.getText(), toolDescriptionField.getText(),
        columnLabelField.getText(), parseWidthMs(widthMsField.getText()),
        keyChoiceField.getValue());
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxNodeKeyColumnData.NODE_KEY_COLUMN_TOOL_EXT,
        DepanFxNodeListColumnData.COLUMNS_TOOL_PATH);
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
