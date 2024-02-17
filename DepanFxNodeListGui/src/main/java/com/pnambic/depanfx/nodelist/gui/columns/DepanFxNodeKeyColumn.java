package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.context.ContextNodeKindId;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListViewer;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeKeyColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeKeyColumnData.KeyChoice;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.io.File;

import javafx.scene.control.ContextMenu;
import javafx.stage.FileChooser;

public class DepanFxNodeKeyColumn extends DepanFxAbstractColumn {

  public static final String EDIT_NODE_KEY_COLUMN =
      "Edit Node Key Column...";

  public static final String NEW_NODE_KEY_COLUMN =
      "New Node Key Column...";

  public static final String SELECT_NODE_KEY_COLUMN =
      "Select Node Key Column...";

  private static final String NEW_NODE_KEY_COLUMN_NAME = "Column";

  private static final String NEW_NODE_KEY_COLUMN_DESCR = "New node key column.";

  private static final String NEW_NODE_KEY_COLUMN_LABEL = "Node Id";

  private static final int COLUMN_WIDTH_MS = 15;

  private DepanFxWorkspaceResource columnDataRsrc;

  public DepanFxNodeKeyColumn(
      DepanFxNodeListViewer listViewer,
      DepanFxWorkspaceResource columnDataRsrc) {
    super(listViewer);
    this.columnDataRsrc = columnDataRsrc;
  }

  public DepanFxNodeKeyColumnData getColumnData() {
    return (DepanFxNodeKeyColumnData) columnDataRsrc.getResource();
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
    builder.appendActionItem(EDIT_NODE_KEY_COLUMN,
        e -> openColumnEditor(dialogRunner));
    builder.appendActionItem(SELECT_NODE_KEY_COLUMN,
        e -> openColumnFinder());
    return builder.build();
  }

  @Override
  public String toString(DepanFxNodeListGraphNode member) {
    return toString(member.getGraphNode().getId());
  }

  public String toString(ContextNodeId nodeId) {
    ContextNodeKindId kindId = nodeId.getContextNodeKindId();
    switch (getColumnData().getKeyChoice()) {
    case MODEL_KEY:
      return kindId.getContextModelId().getContextModelKey();
    case KIND_KEY:
      return kindId.getNodeKindKey();
    case NODE_KEY:
      return nodeId.getNodeKey();
    }
    return "<unknown key>";
  }

  public static DepanFxNodeKeyColumnData buildInitialNodeKeyColumnData() {
    return new DepanFxNodeKeyColumnData(
        NEW_NODE_KEY_COLUMN_NAME, NEW_NODE_KEY_COLUMN_DESCR,
        NEW_NODE_KEY_COLUMN_LABEL, COLUMN_WIDTH_MS, KeyChoice.KIND_KEY);
  }

  public static void addNewColumnAction(
      DepanFxContextMenuBuilder builder, DepanFxDialogRunner dialogRunner) {
    builder.appendActionItem(NEW_NODE_KEY_COLUMN,
        e -> openColumnCreate(dialogRunner));
  }

  private static void openColumnCreate(DepanFxDialogRunner dialogRunner) {
    DepanFxNodeKeyColumnData initialData = buildInitialNodeKeyColumnData();
    DepanFxNodeKeyColumnToolDialog.runCreateDialog(initialData, dialogRunner);
  }

  private void openColumnEditor(DepanFxDialogRunner dialogRunner) {
    Dialog<DepanFxNodeKeyColumnToolDialog> nodeKeyColumnEditor =
          DepanFxNodeKeyColumnToolDialog.runEditDialog(
              columnDataRsrc.getDocument(), buildEditResource(),
              dialogRunner);

    nodeKeyColumnEditor.getController().getWorkspaceResource()
        .ifPresent(this::updateColumnDataRsrc);
  }

  private DepanFxNodeKeyColumnData buildEditResource() {
    DepanFxNodeKeyColumnData columnData = getColumnData();

    int widthMs = (int) Math.round(
        column.getWidth() / DepanFxSceneControls.layoutWidthMs(1));
    return new DepanFxNodeKeyColumnData(
        columnData.getToolName(), columnData.getToolDescription(),
        columnData.getColumnLabel(), widthMs, columnData.getKeyChoice());
  }

  private void openColumnFinder() {
    DepanFxWorkspace workspace = listViewer.getWorkspace();
    FileChooser fileChooser = prepareNodeKeyColumnFinder(workspace);
    File selectedFile =
        fileChooser.showOpenDialog(getScene().getWindow());
    if (selectedFile != null) {
       workspace.toProjectDocument(selectedFile.getAbsoluteFile().toURI())
          .flatMap(p -> workspace.getWorkspaceResource(
              p, DepanFxNodeKeyColumnData.class))
          .ifPresent(this::updateColumnDataRsrc);
    }
  }

  private void updateColumnDataRsrc(DepanFxWorkspaceResource columnDataRsrc) {
    this.columnDataRsrc = columnDataRsrc;
    refreshColumn();
  }

  private FileChooser prepareNodeKeyColumnFinder(DepanFxWorkspace workspace) {
    FileChooser result = DepanFxResourcePerspectives.prepareToolFinder(
        workspace, DepanFxNodeListColumnData.COLUMNS_TOOL_PATH);
    DepanFxNodeKeyColumnToolDialog.setNodeKeyColumnTooldataFilters(result);
    return result;
  }
}
