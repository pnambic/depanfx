package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListViewer;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFocusColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.io.File;

import javafx.scene.control.ContextMenu;
import javafx.stage.FileChooser;

public class DepanFxFocusColumn extends DepanFxAbstractColumn {

  public static final String EDIT_FOCUS_COLUMN =
      "Edit Focus Column...";

  public static final String NEW_FOCUS_COLUMN =
      "New Focus Column...";

  public static final String SELECT_FOCUS_COLUMN =
      "Select Focus Column...";

  private DepanFxWorkspaceResource columnDataRsrc;

  public DepanFxFocusColumn(
      DepanFxNodeListViewer listViewer,
      DepanFxWorkspaceResource columnDataRsrc) {
    super(listViewer);
    this.columnDataRsrc = columnDataRsrc;
  }

  public DepanFxFocusColumnData getColumnData() {
    return (DepanFxFocusColumnData) columnDataRsrc.getResource();
  }

  @Override
  public String getColumnLabel() {
    return getColumnData().getColumnLabel();
  }

  @Override
  protected double getWidthMs() {
    return getColumnData().getWidthMs();
  }

  @Override
  public ContextMenu buildColumnContextMenu(DepanFxDialogRunner dialogRunner) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(EDIT_FOCUS_COLUMN,
        e -> openColumnEditor(dialogRunner));
    builder.appendActionItem(SELECT_FOCUS_COLUMN,
        e -> openColumnFinder());
    return builder.build();
  }

  public static void addNewColumnAction(
      DepanFxContextMenuBuilder builder, DepanFxDialogRunner dialogRunner) {
    builder.appendActionItem(NEW_FOCUS_COLUMN,
        e -> openColumnCreate(dialogRunner));
  }

  private static void openColumnCreate(DepanFxDialogRunner dialogRunner) {
    DepanFxFocusColumnData initialData =
        DepanFxFocusColumnData.buildInitialFocusColumnData(null);
    DepanFxFocusColumnToolDialog.runCreateDialog(
        initialData, dialogRunner, DepanFxFocusColumn.NEW_FOCUS_COLUMN);
  }

  private void openColumnEditor(DepanFxDialogRunner dialogRunner) {
    Dialog<DepanFxFocusColumnToolDialog> focusColumnEditor =
          DepanFxFocusColumnToolDialog.runEditDialog(
              columnDataRsrc, dialogRunner,
              DepanFxFocusColumn.NEW_FOCUS_COLUMN);

    focusColumnEditor.getController().getWorkspaceResource()
        .ifPresent(this::updateColumnDataRsrc);
  }

  private void openColumnFinder() {
    DepanFxWorkspace workspace = listViewer.getWorkspace();
    FileChooser fileChooser = prepareFocusColumnFinder(workspace);
    File selectedFile =
        fileChooser.showOpenDialog(getScene().getWindow());
    if (selectedFile != null) {
       workspace
          .toProjectDocument(selectedFile.getAbsoluteFile().toURI())
          .flatMap(p -> DepanFxWorkspaceFactory.loadDocument(
              workspace, p, DepanFxFocusColumnData.class))
          .ifPresent(this::updateColumnDataRsrc);
    }
  }

  @Override
  public String toString(DepanFxNodeListGraphNode member) {
    if (inFocus(member)) {
      return getColumnData().getFocusLabel();
    }
    return "";
  }

  private boolean inFocus(DepanFxNodeListGraphNode member) {
    DepanFxNodeList nodeList =
        (DepanFxNodeList) getColumnData().getNodeListRsrc().getResource();
    return nodeList.getNodes().contains(member.getGraphNode());
  }

  private void updateColumnDataRsrc(DepanFxWorkspaceResource columnDataRsrc) {
    this.columnDataRsrc = columnDataRsrc;
    refreshColumn();
  }

  private FileChooser prepareFocusColumnFinder(DepanFxWorkspace workspace) {
    FileChooser result = DepanFxResourcePerspectives.prepareToolFinder(
        workspace, DepanFxNodeListColumnData.COLUMNS_TOOL_PATH);
    DepanFxFocusColumnToolDialog.setFocusColumnTooldataFilters(result);
    return result;
  }
}
