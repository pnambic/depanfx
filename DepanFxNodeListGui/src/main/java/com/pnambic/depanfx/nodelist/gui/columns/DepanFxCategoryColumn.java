package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListViewer;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxCategoryColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxCategoryColumnData.CategoryEntry;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class DepanFxCategoryColumn extends DepanFxAbstractColumn {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxCategoryColumn.class);

  public static final String EDIT_CATEGORY_COLUMN =
      "Edit Category Column...";

  public static final String NEW_CATEGORY_COLUMN =
      "New Category Column...";

  public static final String SELECT_CATEGORY_COLUMN =
      "Select Category Column...";

  public static final String SAVE_NODE_LISTS =
      "Save Node Lists...";

  private DepanFxWorkspaceResource columnDataRsrc;

  private CategoryHandler categories;

  private SeparatorMenuItem saveSeparator;

  private MenuItem saveAction;

  public DepanFxCategoryColumn(
      DepanFxNodeListViewer listViewer,
      DepanFxWorkspaceResource columnDataRsrc) {
    super(listViewer);
    this.columnDataRsrc = columnDataRsrc;
    updateCategories(getColumnData().getCategories());
  }

  public DepanFxCategoryColumnData getColumnData() {
    return (DepanFxCategoryColumnData) columnDataRsrc.getResource();
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
    builder.appendActionItem(EDIT_CATEGORY_COLUMN,
        e -> openColumnEditor(dialogRunner));
    builder.appendActionItem(SELECT_CATEGORY_COLUMN,
        e -> openColumnFinder());

    // These actions are hidden if the node list is unchanged.
    saveSeparator = builder.appendSeparator();
    saveAction = builder.appendActionItem(
        SAVE_NODE_LISTS, e1 -> runSaveNodeList());

    // Ensure initial visibility is correct.
    updateActions();
    return builder.build();
  }

  public static void addNewColumnAction(
      DepanFxContextMenuBuilder builder, DepanFxDialogRunner dialogRunner) {
    builder.appendActionItem(NEW_CATEGORY_COLUMN,
        e -> openColumnCreate(dialogRunner));
  }

  @Override
  public String toString(DepanFxNodeListGraphNode member) {
    GraphNode graphNode = member.getGraphNode();
    List<CategoryEntry> nodeCategories =
        categories.getCurrentCategories(graphNode);
    int categoryCount = nodeCategories.size();
    if (categoryCount > 1) {
      return String.valueOf(categoryCount);
    }

    if (categoryCount == 1) {
      return nodeCategories.get(0).getCategoryLabel();
    }
    // Not in any collections.
    return "";
  }

  @Override
  protected Callback<TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember>,
      TreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember>>
      buildCellFactory() {
    return new CategoryCellFactory();
  }

  private void updateActions() {
    boolean hasEdits = hasNodeListEdits();
    saveSeparator.setVisible(hasEdits);
    saveAction.setVisible(hasEdits);
  }

  private boolean hasNodeListEdits() {
    return false;
  }

  private void runSaveNodeList() {
    LOG.info("save node lists not implemented");
  }

  private static void openColumnCreate(DepanFxDialogRunner dialogRunner) {
    DepanFxCategoryColumnData initialData =
        DepanFxCategoryColumnData.buildInitialCategoryColumnData();
    DepanFxCategoryColumnToolDialog.runCreateDialog(
        initialData, dialogRunner, DepanFxCategoryColumn.NEW_CATEGORY_COLUMN);
  }

  private void openColumnEditor(DepanFxDialogRunner dialogRunner) {
    DepanFxWorkspaceResource editRsrc = buildEditResource();
    Dialog<DepanFxCategoryColumnToolDialog> categoryColumnEditor =
          DepanFxCategoryColumnToolDialog.runEditDialog(
              editRsrc, dialogRunner,
              DepanFxCategoryColumn.NEW_CATEGORY_COLUMN);

    categoryColumnEditor.getController().getWorkspaceResource()
        .ifPresent(this::updateColumnDataRsrc);
  }

  private DepanFxWorkspaceResource buildEditResource() {
    DepanFxCategoryColumnData columnData = getColumnData();
    int widthMs = (int) Math.round(
        column.getWidth() / DepanFxSceneControls.layoutWidthMs(1));
    DepanFxCategoryColumnData editColumn = new DepanFxCategoryColumnData(
        columnData.getToolName(), columnData.getToolDescription(),
        columnData.getColumnLabel(), widthMs, categories.getCategoryList());

    DepanFxProjectDocument editDoc = columnDataRsrc.getDocument();
    return new DepanFxWorkspaceResource.Simple(editDoc, editColumn);
  }

  private void openColumnFinder() {
    DepanFxWorkspace workspace = listViewer.getWorkspace();
    FileChooser fileChooser = prepareCategoryColumnFinder(workspace);
    File selectedFile =
        fileChooser.showOpenDialog(getScene().getWindow());
    if (selectedFile != null) {
       workspace
          .toProjectDocument(selectedFile.getAbsoluteFile().toURI())
          .flatMap(p -> DepanFxWorkspaceFactory.loadDocument(
              workspace, p, DepanFxCategoryColumnData.class))
          .ifPresent(this::updateColumnDataRsrc);
    }
  }

  private void updateColumnDataRsrc(DepanFxWorkspaceResource columnDataRsrc) {
    this.columnDataRsrc = columnDataRsrc;
    updateCategories(getColumnData().getCategories());
    refreshColumn();
  }

  private void updateCategories(List<CategoryEntry> categories) {
    this.categories = new CategoryHandler(categories);
  }

  private FileChooser prepareCategoryColumnFinder(DepanFxWorkspace workspace) {
    FileChooser result = DepanFxResourcePerspectives.prepareToolFinder(
        workspace, DepanFxNodeListColumnData.COLUMNS_TOOL_PATH);
    DepanFxCategoryColumnToolDialog.setCategoryColumnTooldataFilters(result);
    return result;
  }

  private class CategoryCellFactory implements
      Callback<TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember>,
      TreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember>> {

    @Override
    public TreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember> call(
        TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> param) {
      return new DepanFxCategoryColumnCell(DepanFxCategoryColumn.this);
    }
  }
}
