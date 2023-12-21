package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListViewer;
import com.pnambic.depanfx.nodelist.gui.DepanFxSaveNodeListDialog;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFocusColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class DepanFxFocusColumn extends DepanFxAbstractColumn {

  public static final String EDIT_FOCUS_COLUMN =
      "Edit Focus Column...";

  public static final String NEW_FOCUS_COLUMN =
      "New Focus Column...";

  public static final String SELECT_FOCUS_COLUMN =
      "Select Focus Column...";

  public static final String SAVE_NODE_LIST =
      "Save Node List...";

  private DepanFxWorkspaceResource columnDataRsrc;

  private DepanFxWorkspaceResource nodeListRsrc;

  private Collection<GraphNode> saveNodes;

  private Set<GraphNode> editNodes;

  private SeparatorMenuItem saveSeparator;

  private MenuItem saveAction;

  public DepanFxFocusColumn(
      DepanFxNodeListViewer listViewer,
      DepanFxWorkspaceResource columnDataRsrc) {
    super(listViewer);
    this.columnDataRsrc = columnDataRsrc;
    updateNodeListRsrc(getColumnData().getNodeListRsrc());
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

    // These actions are hidden if the node list is unchanged.
    saveSeparator = builder.appendSeparator();
    saveAction = builder.appendActionItem(
        SAVE_NODE_LIST, e1 -> runSaveNodeList());

    // Ensure initial visibility is correct.
    updateActions();
    return builder.build();
  }

  public static void addNewColumnAction(
      DepanFxContextMenuBuilder builder, DepanFxDialogRunner dialogRunner) {
    builder.appendActionItem(NEW_FOCUS_COLUMN,
        e -> openColumnCreate(dialogRunner));
  }

  public void toggleNode(GraphNode graphNode) {
    if (editNodes.contains(graphNode)) {
      editNodes.remove(graphNode);
    } else {
      editNodes.add(graphNode);
    }
    updateActions();
  }

  @Override
  public String toString(DepanFxNodeListGraphNode member) {
    GraphNode graphNode = member.getGraphNode();
    if (editNodes.contains(graphNode)) {
      if (saveNodes.contains(graphNode)) {
        return getColumnData().getFocusLabel();
      }
      return getColumnData().getFocusLabel() + "+";
    }
    // Not now, but previously
    if (saveNodes.contains(graphNode)) {
      return "-";
    }
    // Not in either collection.
    return "";
  }

  @Override
  protected Callback<TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember>,
      TreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember>>
      buildCellFactory() {
    return new FocusCellFactory();
  }

  private void updateActions() {
    boolean hasEdits = hasNodeListEdits();
    saveSeparator.setVisible(hasEdits);
    saveAction.setVisible(hasEdits);
  }

  private boolean hasNodeListEdits() {
    // not the same size, must be edits
    if (editNodes.size() != saveNodes.size()) {
      return true;
    }
    if (!editNodes.containsAll(saveNodes)) {
      return true;
    }
    return !saveNodes.containsAll(editNodes);
  }

  private void runSaveNodeList() {
    DepanFxNodeList nodeList = (DepanFxNodeList) nodeListRsrc.getResource();
    DepanFxNodeList saveList =
        DepanFxNodeLists.buildRelatedNodeList(nodeList, editNodes);

    Dialog<DepanFxSaveNodeListDialog> saveDlg =
        listViewer.buildDialog(DepanFxSaveNodeListDialog.class);
    saveDlg.getController().setNodeListDoc(saveList);
    saveDlg.getController().setInitialDest(nodeListRsrc.getDocument());
    saveDlg.runDialog("Save changes to node list");
    saveDlg.getController().getSavedResource()
        .ifPresent(r -> {
            updateNodeListRsrc(r);
            refreshColumn();
        });
  }

  private static void openColumnCreate(DepanFxDialogRunner dialogRunner) {
    DepanFxFocusColumnData initialData =
        DepanFxFocusColumnData.buildInitialFocusColumnData(null);
    DepanFxFocusColumnToolDialog.runCreateDialog(
        initialData, dialogRunner, DepanFxFocusColumn.NEW_FOCUS_COLUMN);
  }

  private void openColumnEditor(DepanFxDialogRunner dialogRunner) {
    DepanFxWorkspaceResource editRsrc = buildEditResource();
    Dialog<DepanFxFocusColumnToolDialog> focusColumnEditor =
          DepanFxFocusColumnToolDialog.runEditDialog(
              editRsrc, dialogRunner,
              DepanFxFocusColumn.NEW_FOCUS_COLUMN);

    focusColumnEditor.getController().getWorkspaceResource()
        .ifPresent(this::updateColumnDataRsrc);
  }

  private DepanFxWorkspaceResource buildEditResource() {
    DepanFxFocusColumnData columnData = getColumnData();
    int widthMs = (int) Math.round(
        column.getWidth() / DepanFxSceneControls.layoutWidthMs(1));
    DepanFxFocusColumnData editColumn = new DepanFxFocusColumnData(
        columnData.getToolName(), columnData.getToolDescription(),
        columnData.getColumnLabel(), widthMs,
        columnData.getFocusLabel(), nodeListRsrc);

    DepanFxProjectDocument editDoc = columnDataRsrc.getDocument();
    return new DepanFxWorkspaceResource.Simple(editDoc, editColumn);
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

  private void updateColumnDataRsrc(DepanFxWorkspaceResource columnDataRsrc) {
    this.columnDataRsrc = columnDataRsrc;
    updateNodeListRsrc(getColumnData().getNodeListRsrc());
    refreshColumn();
  }

  private void updateNodeListRsrc(DepanFxWorkspaceResource nodeListRsrc) {
    this.nodeListRsrc = nodeListRsrc;
    DepanFxNodeList nodeList = (DepanFxNodeList) nodeListRsrc.getResource();
    saveNodes = nodeList.getNodes();
    editNodes = new HashSet<>(saveNodes);
  }

  private FileChooser prepareFocusColumnFinder(DepanFxWorkspace workspace) {
    FileChooser result = DepanFxResourcePerspectives.prepareToolFinder(
        workspace, DepanFxNodeListColumnData.COLUMNS_TOOL_PATH);
    DepanFxFocusColumnToolDialog.setFocusColumnTooldataFilters(result);
    return result;
  }

  private class FocusCellFactory implements
      Callback<TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember>,
      TreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember>> {

    @Override
    public TreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember> call(
        TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> param) {
      return new DepanFxFocusColumnCell(DepanFxFocusColumn.this);
    }
  }
}
