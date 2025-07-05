package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListChooser;
import com.pnambic.depanfx.nodelist.gui.DepanFxSaveNodeListDialog;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFocusColumnData;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@DepanFxFxmlDialog
@FxmlView("focus-column-tool-dialog.fxml")
public class DepanFxFocusColumnToolDialog
    extends DepanFxBaseColumnToolDialog<DepanFxFocusColumnData> {

  public static final ExtensionFilter FOCUS_COLUMN_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Focus Columns", DepanFxFocusColumnData.FOCUS_COLUMN_TOOL_EXT);

  public static final DepanFxResourceFilter FOCUS_COLUMN_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Focus Columns",
          DepanFxFocusColumnData.FOCUS_COLUMN_TOOL_EXT,
          DepanFxFocusColumnData.class);

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private TextField focusLabelField;

  @FXML
  private TextField focusNodeListRsrcField;

  DepanFxNodeListChooser.NodeListControl focusNodeListControl;

  @Autowired
  public DepanFxFocusColumnToolDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, DepanFxFocusColumnData.class);
    this.dialogRunner = dialogRunner;
  }

  public static Dialog<DepanFxFocusColumnToolDialog> runEditDialog(
      DepanFxWorkspaceResource<DepanFxFocusColumnData> focusColumnRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runEditDialog(
        focusColumnRsrc, dialogRunner,
        DepanFxFocusColumnToolDialog.class,
        DepanFxFocusColumn.EDIT_FOCUS_COLUMN);
  }

  public static Dialog<DepanFxFocusColumnToolDialog> runCreateDialog(
      DepanFxWorkspaceResource<DepanFxFocusColumnData> focusColumnRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runCreateDialog(
        focusColumnRsrc, dialogRunner,
        DepanFxFocusColumnToolDialog.class,
        DepanFxFocusColumn.NEW_FOCUS_COLUMN);
  }

  public static void setFocusColumnTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(FOCUS_COLUMN_FILTER);
    result.setSelectedExtensionFilter(FOCUS_COLUMN_FILTER);
  }

  @FXML
  public void initialize() {
    focusNodeListControl = new DepanFxNodeListChooser.NodeListControl(
        getWorkspace(), dialogRunner, focusNodeListRsrcField);
    focusNodeListRsrcField.textProperty().addListener(
        (v, o, n) -> updateFocusLabel());
  }

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxFocusColumnData> columnRsrc) {
    super.setToolResource(columnRsrc);

    DepanFxFocusColumnData columnData = columnRsrc.getResource();
    focusLabelField.setText(columnData.getFocusLabel());
    focusNodeListControl.setNodeListResource(
        columnData.getNodeListRsrc());
  }

  @FXML
  public void handleNodeListBrowse() {
    focusNodeListControl.runNodeListFinder();
  }

  @FXML
  public void handleNewNodeList() {
    DepanFxNodeList focusNodeList =
        getToolResource().get().getResource().getNodeListRsrc().getResource();

    DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc =
        getWorkspace().addScratchResource(
            DepanFxNodeLists.buildEmptyNodeList(focusNodeList));

    DepanFxSaveNodeListDialog.runSaveNodeList(dialogRunner, nodeListRsrc)
        .ifPresent(r -> focusNodeListControl.setNodeListResource(r));
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxFocusColumnData prepareResult() {

    return new DepanFxFocusColumnData(
        getToolName(), getToolDescription(),
        getColumnLabel(), getColumnWidthMs(),
        focusLabelField.getText(),
        focusNodeListControl.getNodeListResource());
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxFocusColumnData.FOCUS_COLUMN_TOOL_EXT,
        DepanFxNodeListColumnData.COLUMNS_TOOL_PATH);
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    DepanFxFocusColumnToolDialog.setFocusColumnTooldataFilters(result);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Focus Column Save Confirmation Error";
  }

  private void updateFocusLabel() {
    String focusLabel = focusNodeListControl.getNodeListResource()
        .getResource().getNodeListName();
    updateBlankField(focusLabelField, focusLabel);
  }
}
