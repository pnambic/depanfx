package com.pnambic.depanfx.nodelist.gui.columns;

import com.google.common.base.Strings;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListChooser;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFocusColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
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

  @Autowired
  public DepanFxFocusColumnToolDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, DepanFxFocusColumnData.class);
    this.dialogRunner = dialogRunner;
  }

  public static Dialog<DepanFxFocusColumnToolDialog> runEditDialog(
      DepanFxProjectDocument projDoc,
      DepanFxFocusColumnData focusColumnData,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runEditDialog(
        projDoc, focusColumnData, dialogRunner,
        DepanFxFocusColumnToolDialog.class,
        DepanFxFocusColumn.EDIT_FOCUS_COLUMN);
  }

  public static Dialog<DepanFxFocusColumnToolDialog> runCreateDialog(
      DepanFxFocusColumnData columnData, DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runCreateDialog(
        columnData, dialogRunner,
        DepanFxFocusColumnToolDialog.class,
        DepanFxFocusColumn.NEW_FOCUS_COLUMN);
  }

  public static void setFocusColumnTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(FOCUS_COLUMN_FILTER);
    result.setSelectedExtensionFilter(FOCUS_COLUMN_FILTER);
  }

  @FXML
  public void initialize() {
    focusNodeListRsrcField.setContextMenu(buildNodeListChoiceMenu());
  }

  @Override // DepanFxBaseColumnToolDialog
  public void setTooldata(DepanFxFocusColumnData columnData) {
    super.setTooldata(columnData);

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

  private ContextMenu buildNodeListChoiceMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem("Select Node List...",
        e -> runNodeListFinder());
    return builder.build();
  }

  private void runNodeListFinder() {
    DepanFxNodeListChooser.runNodeListChooser(
        getWorkspace(), dialogRunner, focusNodeListRsrcField.getScene())
        .ifPresent(this::updateNodeListFields);
  }

  private void updateNodeListFields(
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {
    focusNodeListRsrcField.setText(
        nodeListRsrc.getDocument().getMemberPath().toString());

    if (Strings.isNullOrEmpty(focusLabelField.getText())) {
      focusLabelField.setText(nodeListRsrc.getResource().getNodeListName());
    }
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxFocusColumnData prepareResult() {
    Optional<DepanFxWorkspaceResource<DepanFxNodeList>> optNodeListRsrc =
        DepanFxResourcePerspectives.toResource(
            getWorkspace(), focusNodeListRsrcField, DepanFxNodeList.class);

    return new DepanFxFocusColumnData(
        getToolName(), getToolDescription(),
        getColumnLabel(), getColumnWidthMs(),
        focusLabelField.getText(), optNodeListRsrc.get());
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
}
