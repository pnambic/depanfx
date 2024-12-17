package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.gui.columns.DepanFxCategoryColumn;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxCategoryColumnToolDialog;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxFocusColumn;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxFocusColumnToolDialog;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeKeyColumn;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeKeyColumnToolDialog;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxCategoryColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFocusColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeKeyColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class DepanFxNodeListTableCommands {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeListTableCommands.class);

  public static final String SELECT_ALL_ITEM = "Select All";

  public static final String CLEAR_SELECTION_ITEM = "Clear Selection";

  public static final String INVERT_SELECTION_ITEM = "Invert Selection";

  public static final String SELECT_NODE_LIST = "Select Node List...";

  public static final String TABLE_VIEW = "Table View";

  public static final String ADD_COLUMN = "Add Column";

  public static final String SELECT_COLUMN = "Select Column...";

  private static final String COLUMN_TOOL_EXT = "d*cti";

  private static final List<Class<?>> COLUMN_TYPES =
      Arrays.asList(new Class<?>[] {
        DepanFxCategoryColumnData.class,
        DepanFxFocusColumnData.class,
        DepanFxNodeKeyColumnData.class
  });

  public static final DepanFxResourceFilter ANY_COLUMN_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Any Column", COLUMN_TOOL_EXT, COLUMN_TYPES);

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxNodeListTableAdapter tableAdapter;

  private final DepanFxNodeListTableState tableState;

  public DepanFxNodeListTableCommands(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeListTableAdapter tableAdpater,
      DepanFxNodeListTableState tableState) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.tableAdapter = tableAdpater;
    this.tableState = tableState;
  }

  /**
   * Add the table view submenu to the context menu.
   */
  public void addTableViewItems(DepanFxContextMenuBuilder builder) {
    builder.appendSeparator();
    builder.appendSubMenu(buildTableViewMenu());
  }

  /**
   * Add load and save items to the context menu.
   */
  public void addLoadSaveItems(DepanFxContextMenuBuilder builder) {
    builder.appendSeparator();
    builder.appendActionItem(
        SELECT_NODE_LIST, e -> runSelectionNodeListDialog());
    builder.appendActionItem(
        DepanFxSaveNodeListDialog.SAVE_NODE_LIST,
        e -> runSaveNodeListDialog());
  }

  /**
   * Add select all, select none, and invert selection items
   * to the context menu.
   */
  public void addSelectItems(DepanFxContextMenuBuilder builder) {
    builder.appendActionItem(
        SELECT_ALL_ITEM, e -> tableState.doSelectAllAction());
    builder.appendActionItem(
        CLEAR_SELECTION_ITEM, e -> tableState.doClearSelectionAction());
    builder.appendActionItem(
        INVERT_SELECTION_ITEM, e -> tableState.doInvertSelectionAction());
  }

  private Menu newColumnMenu() {
    Menu result = new Menu(ADD_COLUMN);
    ObservableList<MenuItem> items = result.getItems();
    items.add(DepanFxContextMenuBuilder.createActionItem(
        SELECT_COLUMN,
        e -> doSelectColumnAction()));
    items.add(new SeparatorMenuItem());
    items.add(DepanFxContextMenuBuilder.createActionItem(
        DepanFxNodeKeyColumn.NEW_NODE_KEY_COLUMN,
        e -> doNewNodeKeyColumnAction()));
    items.add(DepanFxContextMenuBuilder.createActionItem(
        DepanFxFocusColumn.NEW_FOCUS_COLUMN,
        e -> doNewFocusColumnAction()));
    items.add(DepanFxContextMenuBuilder.createActionItem(
        DepanFxCategoryColumn.NEW_CATEGORY_COLUMN,
        e -> doNewCategoryColumnAction()));
    return result;
  }

  private Menu buildTableViewMenu() {
    Menu result = new Menu(TABLE_VIEW);
    ObservableList<MenuItem> items = result.getItems();
    items.add(newColumnMenu());
    items.add(new SeparatorMenuItem());
    items.add(DepanFxContextMenuBuilder.createActionItem(
        DepanFxNodeListTableViewSaveDialog.SELECT_TABLE_VIEW,
        e -> runNodeListTableViewChooser()));
    items.add(DepanFxContextMenuBuilder.createActionItem(
        DepanFxNodeListTableViewSaveDialog.SAVE_TABLE_VIEW,
        e -> runNodeListTableViewSaveDialog()));
    return result;
  }

  private void runNodeListTableViewChooser() {
    DepanFxNodeListTableViewSaveDialog
        .runTableViewChooser(workspace, dialogRunner, tableState.getScene())
        .ifPresent(r -> tableState.setTableView(r.getResource()));
  }

  private void runSelectionNodeListDialog() {
    DepanFxNodeListChooser.runNodeListChooser(
            workspace, dialogRunner, tableState.getScene())
        .map(r -> r.getResource().getNodes())
        .ifPresent(l -> tableState.doSelectGraphNodesAction(l.stream(), true));
  }

  private void runSaveNodeListDialog() {
    DepanFxSaveNodeListDialog.runSaveNodeList(
        dialogRunner, workspace.addScratchResource(tableState.getSelection()));
  }

  private void runNodeListTableViewSaveDialog() {
    DepanFxNodeListTableViewData tableView = tableState.getTableView();
    DepanFxNodeListTableViewSaveDialog.runSaveTableView(
        dialogRunner, workspace.addScratchResource(tableView));
  }

  private void doSelectColumnAction() {
    DepanFxResourceChooser rsrcChooser =
        new DepanFxResourceChooser(workspace, dialogRunner);

    DepanFxResourcePerspectives.prepareResourceFinder(
        rsrcChooser, DepanFxNodeListColumnData.COLUMNS_TOOL_PATH);

    ObservableList<DepanFxResourceFilter> filters =
        rsrcChooser.getExtensionFilters();
    filters.add(DepanFxCategoryColumnToolDialog.CATEGORY_COLUMN__RSRC_FILTER);
    filters.add(DepanFxFocusColumnToolDialog.FOCUS_COLUMN_RSRC_FILTER);
    filters.add(DepanFxNodeKeyColumnToolDialog.NODE_KEY_COLUMN_RSRC_FILTER);
    filters.add(ANY_COLUMN_RSRC_FILTER);
    rsrcChooser.setSelectedExtensionFilter(ANY_COLUMN_RSRC_FILTER);

    rsrcChooser.showOpenDialog(tableState.getScene())
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(m -> workspace.getWorkspaceResource(
            m, DepanFxBaseColumnData.class))
        .ifPresent(tableState::addColumn);
  }

  private void doNewNodeKeyColumnAction() {
    DepanFxNodeKeyColumnData initialData =
        DepanFxNodeKeyColumn.buildInitialNodeKeyColumnData();
    Dialog<DepanFxNodeKeyColumnToolDialog> createDlg =
        DepanFxNodeKeyColumnToolDialog.runCreateDialog(
            asResource(initialData), dialogRunner);
    createDlg.getController().getToolResource()
        .ifPresent(tableState::addColumn);
  }

  private void doNewFocusColumnAction() {
    DepanFxFocusColumnData initialData =
        DepanFxFocusColumnData.buildInitialFocusColumnData(null);
    Dialog<DepanFxFocusColumnToolDialog> createDlg =
        DepanFxFocusColumnToolDialog.runCreateDialog(
            asResource(initialData), dialogRunner);
    createDlg.getController().getToolResource()
        .ifPresent(tableState::addColumn);
  }

  private void doNewCategoryColumnAction() {
    DepanFxCategoryColumnData columnData =
        DepanFxCategoryColumnData.buildInitialCategoryColumnData();
    Dialog<DepanFxCategoryColumnToolDialog> createDlg =
        DepanFxCategoryColumnToolDialog.runCreateDialog(
            asResource(columnData), dialogRunner, tableAdapter);
    createDlg.getController().getToolResource()
        .ifPresent(tableState::addColumn);
  }

  private <T> DepanFxWorkspaceResource<T> asResource(T columnData) {
    return workspace.addScratchResource(columnData);
  }
}
