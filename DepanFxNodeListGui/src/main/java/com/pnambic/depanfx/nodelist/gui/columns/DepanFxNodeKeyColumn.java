package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.context.ContextNodeKindId;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeKeyColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeKeyColumnData.KeyChoice;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import javafx.scene.control.ContextMenu;

public class DepanFxNodeKeyColumn
    extends DepanFxAbstractColumn<DepanFxNodeKeyColumnData> {

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

  public DepanFxNodeKeyColumn(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxWorkspaceResource<DepanFxNodeKeyColumnData> columnDataRsrc) {
    super(tableAdapter, columnDataRsrc);
  }

  @Override
  public ContextMenu buildColumnContextMenu(DepanFxDialogRunner dialogRunner) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(SELECT_NODE_KEY_COLUMN,
        e -> openColumnChooser(dialogRunner));
    builder.appendActionItem(EDIT_NODE_KEY_COLUMN,
        e -> openColumnEditor(dialogRunner));
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
      DepanFxContextMenuBuilder builder,
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner) {
    builder.appendActionItem(NEW_NODE_KEY_COLUMN,
        e -> openColumnCreate(workspace, dialogRunner));
  }

  private static void openColumnCreate(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxNodeKeyColumnData initialData = buildInitialNodeKeyColumnData();
    DepanFxWorkspaceResource<DepanFxNodeKeyColumnData> columnRsrc =
        workspace.addScratchResource(initialData);
    DepanFxNodeKeyColumnToolDialog.runCreateDialog(columnRsrc, dialogRunner);
  }

  private void openColumnEditor(DepanFxDialogRunner dialogRunner) {
    Dialog<DepanFxNodeKeyColumnToolDialog> nodeKeyColumnEditor =
          DepanFxNodeKeyColumnToolDialog.runEditDialog(
              forUpdate(buildEditData()), dialogRunner);

    nodeKeyColumnEditor.getController().getToolResource()
        .ifPresent(this::updateColumnDataRsrc);
  }

  private DepanFxNodeKeyColumnData buildEditData() {
    DepanFxNodeKeyColumnData columnData = getColumnData();

    int widthMs = (int) Math.round(
        column.getWidth() / DepanFxSceneControls.layoutWidthMs(1));
    return new DepanFxNodeKeyColumnData(
        columnData.getToolName(), columnData.getToolDescription(),
        columnData.getColumnLabel(), widthMs, columnData.getKeyChoice());
  }

  private void openColumnChooser(DepanFxDialogRunner dialogRunner) {
    DepanFxWorkspace workspace = tableAdapter.getWorkspace();
    DepanFxResourceChooser columnChooser =
        prepareChooser(workspace, dialogRunner);
    columnChooser.showOpenDialog(getScene())
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
              p, DepanFxNodeKeyColumnData.class))
        .ifPresent(this::updateColumnDataRsrc);
  }

  private static DepanFxResourceChooser prepareChooser(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxResourceChooser result =
        new DepanFxResourceChooser(workspace, dialogRunner);
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxNodeListColumnData.COLUMNS_TOOL_PATH);

    result.getExtensionFilters().add(
        DepanFxNodeKeyColumnToolDialog.NODE_KEY_COLUMN_RSRC_FILTER);
    result.setSelectedExtensionFilter(
        DepanFxNodeKeyColumnToolDialog.NODE_KEY_COLUMN_RSRC_FILTER);
    return result;
  }
}
