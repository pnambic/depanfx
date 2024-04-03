package com.pnambic.depanfx.nodelist.gui.columns;

import com.google.common.base.Strings;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListChooser;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFocusColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
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
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
@FxmlView("focus-column-tool-dialog.fxml")
public class DepanFxFocusColumnToolDialog
    extends DepanFxBaseColumnToolDialog {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxFocusColumnToolDialog.class.getName());

  public static final ExtensionFilter FOCUS_COLUMN_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Focus Columns", DepanFxFocusColumnData.FOCUS_COLUMN_TOOL_EXT);

  public static final DepanFxResourceFilter FOCUS_COLUMN_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Focus Columns",
          DepanFxFocusColumnData.FOCUS_COLUMN_TOOL_EXT,
          DepanFxFocusColumnData.class);

  @FXML
  private TextField focusLabelField;

  @FXML
  private TextField focusNodeListRsrcField;

  @Autowired
  public DepanFxFocusColumnToolDialog(DepanFxWorkspace workspace) {
    super(workspace);
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

  private ContextMenu buildNodeListChoiceMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem("Select Node List...",
        e -> runNodeListFinder());
    return builder.build();
  }

  private void runNodeListFinder() {
    DepanFxNodeListChooser.runNodeListFinder(
        getWorkspace(), focusNodeListRsrcField.getScene().getWindow())
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

  @Override
  protected Optional<DepanFxWorkspaceResource> prepareResult() {
    File nodeListFile = new File(focusNodeListRsrcField.getText());
    Optional<DepanFxWorkspaceResource> optNodeListRsrc = getWorkspace()
        .toProjectDocument(nodeListFile.toURI())
        .flatMap(p -> getWorkspace()
            .getWorkspaceResource(p, DepanFxNodeList.class));

    DepanFxFocusColumnData focusColumnData = new DepanFxFocusColumnData(
        toolNameField.getText(), toolDescriptionField.getText(),
        columnLabelField.getText(), parseWidthMs(widthMsField.getText()),
        focusLabelField.getText(), optNodeListRsrc.get());

    File dstDocFile = new File(destinationField.getText());
    return getWorkspace().toProjectDocument(dstDocFile.toURI())
        .flatMap(d -> saveDocument(d, focusColumnData));
  }

  @Override
  protected File buildInitialDestinationFile() {
    return DepanFxWorkspaceFactory.bestDocumentFile(
        toolNameField.getText(), DepanFxFocusColumnData.FOCUS_COLUMN_TOOL_EXT,
        getWorkspace(), DepanFxNodeListColumnData.COLUMNS_TOOL_PATH,
        DepanFxProjects.getCurrentTools(getWorkspace()));
  }

  @Override
  protected void setColumnTooldataFilters(FileChooser result) {
    DepanFxFocusColumnToolDialog.setFocusColumnTooldataFilters(result);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Focus Column Save Confirmation Error";
  }
}
