package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.gui.columns.DepanFxCategoryColumn;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxCategoryColumnToolDialog;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxFocusColumn;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxFocusColumnToolDialog;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeKeyColumn;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeKeyColumnToolDialog;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxCategoryColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFocusColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeKeyColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListColumnData;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class DepanFxNodeListTableCommands {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeListTableCommands.ADD_COLUMN);

  public static final String SELECT_ALL_ITEM = "Select All";

  public static final String CLEAR_SELECTION_ITEM = "Clear Selection";

  public static final String INVERT_SELECTION_ITEM = "Invert Selection";

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

  public DepanFxNodeListTableCommands(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeListTableAdapter tableAdapter) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.tableAdapter = tableAdapter;
  }

  public ContextMenu buildViewContextMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(
        SELECT_ALL_ITEM, e -> tableAdapter.doSelectAllAction());
    builder.appendActionItem(
        CLEAR_SELECTION_ITEM, e -> tableAdapter.doClearSelectionAction());
    builder.appendActionItem(
        INVERT_SELECTION_ITEM, e -> tableAdapter.doInvertSelectionAction());
    builder.appendSeparator();
    builder.appendSubMenu(newColumnMenu());
    builder.appendSeparator();
    builder.appendActionItem(
        DepanFxSaveNodeListDialog.SAVE_NODE_LIST,
        e -> runSaveNodeListDialog());
    return builder.build();
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

  private void runSaveNodeListDialog() {
    DepanFxSaveNodeListDialog.runSaveNodeList(
        dialogRunner, tableAdapter.getSelection());
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

    rsrcChooser.showOpenDialog(tableAdapter.getScene())
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(m -> workspace.getWorkspaceResource(m, "Column Definition"))
        .map(this::toColumn)
        .ifPresent(tableAdapter::addColumn);
  }

  private void doNewNodeKeyColumnAction() {
    DepanFxNodeKeyColumnData initialData =
        DepanFxNodeKeyColumn.buildInitialNodeKeyColumnData();
    Dialog<DepanFxNodeKeyColumnToolDialog> createDlg =
        DepanFxNodeKeyColumnToolDialog.runCreateDialog(
            initialData, dialogRunner);
    createDlg.getController().getWorkspaceResource()
        .map(r -> new DepanFxNodeKeyColumn(tableAdapter, r))
        .ifPresent(tableAdapter::addColumn);
  }

  private void doNewFocusColumnAction() {
    DepanFxFocusColumnData initialData =
        DepanFxFocusColumnData.buildInitialFocusColumnData(null);
    Dialog<DepanFxFocusColumnToolDialog> createDlg =
        DepanFxFocusColumnToolDialog.runCreateDialog(
            initialData, dialogRunner);
    createDlg.getController().getWorkspaceResource()
        .map(r -> new DepanFxFocusColumn(tableAdapter, r))
        .ifPresent(tableAdapter::addColumn);
  }

  private void doNewCategoryColumnAction() {
    DepanFxCategoryColumnData initialData =
        DepanFxCategoryColumnData.buildInitialCategoryColumnData();
    Dialog<DepanFxCategoryColumnToolDialog> createDlg =
        DepanFxCategoryColumnToolDialog.runCreateDialog(
            initialData, dialogRunner);
    createDlg.getController().getWorkspaceResource()
        .map(r -> new DepanFxCategoryColumn(tableAdapter, r))
        .ifPresent(tableAdapter::addColumn);
  }

  @SuppressWarnings("unchecked")
  private DepanFxNodeListColumn toColumn(
      DepanFxWorkspaceResource<?> columnRsrc) {
    switch (columnRsrc.getResource()) {
    case DepanFxCategoryColumnData val:
      return new DepanFxCategoryColumn(tableAdapter,
          (DepanFxWorkspaceResource<DepanFxCategoryColumnData>) columnRsrc);
    case DepanFxFocusColumnData val:
      return new DepanFxFocusColumn(tableAdapter,
          (DepanFxWorkspaceResource<DepanFxFocusColumnData>) columnRsrc);
    case DepanFxNodeKeyColumnData val:
      return new DepanFxNodeKeyColumn(tableAdapter,
          (DepanFxWorkspaceResource<DepanFxNodeKeyColumnData>) columnRsrc);

    default:
      break;
    }
    LOG.warn("Unknown type {} for column construction",
        columnRsrc.getResource().getClass().getName());
    return null;
  }
}
