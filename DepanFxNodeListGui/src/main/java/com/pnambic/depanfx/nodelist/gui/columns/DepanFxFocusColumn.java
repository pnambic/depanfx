package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListViewer;
import com.pnambic.depanfx.nodelist.gui.DepanFxSaveNodeListDialog;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxTreeFork;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxCategoryColumnData.CategoryEntry;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFocusColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.nodelist.tree.DepanFxTreeModel;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

public class DepanFxFocusColumn
    extends DepanFxAbstractColumn<DepanFxFocusColumnData> {

  public static final String EDIT_FOCUS_COLUMN =
      "Edit Focus Column...";

  public static final String NEW_FOCUS_COLUMN =
      "New Focus Column...";

  public static final String SELECT_FOCUS_COLUMN =
      "Select Focus Column...";

  public static final String SAVE_NODE_LIST =
      "Save Node List...";

  private DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc;

  private CategoryEditor categories;

  // Synthetic Category entry for this column
  private CategoryEntry focusEntry;

  private SeparatorMenuItem saveSeparator;

  private MenuItem saveAction;

  public DepanFxFocusColumn(
      DepanFxNodeListViewer listViewer,
      DepanFxWorkspaceResource<DepanFxFocusColumnData> columnDataRsrc) {
    super(listViewer, columnDataRsrc);
    updateNodeListRsrc(getColumnData().getNodeListRsrc());
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
    builder.appendActionItem(
        SELECT_FOCUS_COLUMN, e -> openColumnChooser(dialogRunner));
    builder.appendActionItem(
        EDIT_FOCUS_COLUMN, e -> openColumnEditor(dialogRunner));

    // These actions are hidden if the node list is unchanged.
    saveSeparator = builder.appendSeparator();
    saveAction = builder.appendActionItem(
        SAVE_NODE_LIST, e -> runSaveNodeList());

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
    DepanFxNodeList nodeList = nodeListRsrc.getResource();
    Collection<GraphNode> editNodes = categories.getCurrentNodes(focusEntry);
    DepanFxNodeList saveList =
        DepanFxNodeLists.buildRelatedNodeList(nodeList, editNodes);

    DepanFxSaveNodeListDialog
        .runUpdateNodeList(
            listViewer.getDialogRunner(), nodeListRsrc.getDocument(), saveList)
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
              getColumnProjectDoc(), buildEditData(), dialogRunner);

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

  private void openColumnChooser(DepanFxDialogRunner dialogRunner) {
    DepanFxWorkspace workspace = listViewer.getWorkspace();
    DepanFxResourceChooser columnChooser =
        prepareChooser(workspace, dialogRunner);
    columnChooser.showOpenDialog(getScene())
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
              p, DepanFxFocusColumnData.class))
        .ifPresent(this::updateColumnDataRsrc);
  }

  @Override
  protected void updateColumnDataRsrc(
      DepanFxWorkspaceResource<DepanFxFocusColumnData> columnDataRsrc) {
    updateNodeListRsrc(getColumnData().getNodeListRsrc());
    super.updateColumnDataRsrc(columnDataRsrc);
  }

  private void updateNodeListRsrc(
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {
    this.nodeListRsrc = nodeListRsrc;
    updateCategories();
  }

  private void updateCategories() {
    this.focusEntry = new CategoryEntry(
        getColumnData().getFocusLabel(), nodeListRsrc);
    this.categories = new CategoryEditor(
        Collections.singletonList(focusEntry));
  }

  private static DepanFxResourceChooser prepareChooser(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxResourceChooser result =
        new DepanFxResourceChooser(workspace, dialogRunner);
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxNodeListColumnData.COLUMNS_TOOL_PATH);

    result.getExtensionFilters().add(
        DepanFxFocusColumnToolDialog.FOCUS_COLUMN_RSRC_FILTER);
    result.setSelectedExtensionFilter(
        DepanFxFocusColumnToolDialog.FOCUS_COLUMN_RSRC_FILTER);
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
