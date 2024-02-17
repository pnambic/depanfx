package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListViewer;
import com.pnambic.depanfx.nodelist.gui.DepanFxSaveNodeListDialog;
import com.pnambic.depanfx.nodelist.gui.DepanFxTreeFork;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFocusColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxCategoryColumnData.CategoryEntry;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

  private CategoryEditor categories;

  // Synthetic Category entry for this column
  private CategoryEntry focusEntry;

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
    List<CategoryEntry> nodeCats = categories.getCurrentCategories(graphNode);
    if (nodeCats.isEmpty()) {
      categories.setListMembership(graphNode, categories.getCategoryList());
    } else {
      categories.setListMembership(graphNode, Collections.emptyList());
    }
    updateActions();
  }

  @Override
  public String toString(DepanFxNodeListGraphNode member) {
    GraphNode graphNode = member.getGraphNode();
    int currentSize = categories.getCurrentCategories(graphNode).size();
    int sourceSize = categories.getSourceCategories(graphNode).size();

    if (currentSize == 1) {
      String focusLabel = getColumnData().getFocusLabel();
      // Added
      if (sourceSize == 0) {
        return focusLabel + "+";
      }
      return focusLabel;
    }

    // Not now, but previously
    if (currentSize == 0 && sourceSize == 1) {
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

  public void setDecendantsCategories(DepanFxTreeFork forkItem) {
    forkItem.getDecendants()
        .forEach(n -> categories.setListMembership(
            n, categories.getCategoryList()));
    listViewer.refreshView();
  }

  public void clearDecendantsCategories(DepanFxTreeFork forkItem) {
    forkItem.getDecendants()
        .forEach(n -> categories.setListMembership(
              n, Collections.emptyList()));
    listViewer.refreshView();
  }

  public void hoistMemberships(DepanFxTreeFork forkItem) {
    Collection<GraphNode> sourceNodes = forkItem.getDecendants();
    DepanFxTreeModel treeModel = forkItem.getTreeModel();

    CategoryWinch winch = new CategoryWinch(
        sourceNodes, categories.getCategoryList(), categories, treeModel);
    winch.hoistCategories();
    listViewer.refreshView();
  }

  private void updateActions() {
    boolean hasEdits = hasNodeListEdits();
    saveSeparator.setVisible(hasEdits);
    saveAction.setVisible(hasEdits);
  }

  private boolean hasNodeListEdits() {
    return categories.hasEdits();
  }

  private void runSaveNodeList() {
    DepanFxNodeList nodeList = (DepanFxNodeList) nodeListRsrc.getResource();
    Collection<GraphNode> editNodes = categories.getCurrentNodes(focusEntry);
    DepanFxNodeList saveList =
        DepanFxNodeLists.buildRelatedNodeList(nodeList, editNodes);

    Dialog<DepanFxSaveNodeListDialog> saveDlg =
        listViewer.buildDialog(DepanFxSaveNodeListDialog.class);
    saveDlg.getController().setNodeListDoc(saveList);
    saveDlg.getController().setDestination(nodeListRsrc.getDocument());
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
    DepanFxFocusColumnToolDialog.runCreateDialog(initialData, dialogRunner);
  }

  private void openColumnEditor(DepanFxDialogRunner dialogRunner) {
    Dialog<DepanFxFocusColumnToolDialog> focusColumnEditor =
          DepanFxFocusColumnToolDialog.runEditDialog(
              columnDataRsrc.getDocument(), buildEditData(),
              dialogRunner);

    focusColumnEditor.getController().getWorkspaceResource()
        .ifPresent(this::updateColumnDataRsrc);
  }

  private DepanFxFocusColumnData buildEditData() {
    DepanFxFocusColumnData columnData = getColumnData();
    int widthMs = (int) Math.round(
        column.getWidth() / DepanFxSceneControls.layoutWidthMs(1));
    return new DepanFxFocusColumnData(
        columnData.getToolName(), columnData.getToolDescription(),
        columnData.getColumnLabel(), widthMs,
        columnData.getFocusLabel(), nodeListRsrc);
  }

  private void openColumnFinder() {
    DepanFxWorkspace workspace = listViewer.getWorkspace();
    FileChooser fileChooser = prepareFocusColumnFinder(workspace);
    File selectedFile =
        fileChooser.showOpenDialog(getScene().getWindow());
    if (selectedFile != null) {
       workspace
          .toProjectDocument(selectedFile.getAbsoluteFile().toURI())
          .flatMap(p -> workspace.getWorkspaceResource(
              p, DepanFxFocusColumnData.class))
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
    updateCategories();
  }

  private void updateCategories() {
    this.focusEntry = new CategoryEntry(
        getColumnData().getFocusLabel(), nodeListRsrc);
    this.categories = new CategoryEditor(
        Collections.singletonList(focusEntry));
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
