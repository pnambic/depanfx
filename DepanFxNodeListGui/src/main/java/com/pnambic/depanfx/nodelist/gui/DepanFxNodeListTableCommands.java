package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumns;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSections;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxMenuBuilder;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.Menu;

public class DepanFxNodeListTableCommands {

  @SuppressWarnings("unused")
  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeListTableCommands.class);

  public static final String SELECT_ALL_ITEM = "Select All";

  public static final String CLEAR_SELECTION_ITEM = "Clear Selection";

  public static final String INVERT_SELECTION_ITEM = "Invert Selection";

  public static final String SELECT_NODE_LIST = "Select Node List...";

  public static final String TABLE_VIEW = "Table View";

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxNodeListTableAdapter tableAdapter;

  private final DepanFxNodeListTableState tableState;

  // Column needs to be refreshed due to columns being added or removed.
  private Menu newColumnMenu;

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

  public void updateOnShowing() {

    // The last column may have changed.
    DepanFxNodeListColumn after = tableAdapter.streamColumns()
        .reduce((first, second) -> second).get();
    DepanFxNodeListColumns.updateNewColumnMenu(
        newColumnMenu, after, tableAdapter);
  }

  /////////////////////////////////////
  // Columns

  private Menu buildNewColumnMenu() {

    // There should always be a last column in the table.
    DepanFxNodeListColumn after = tableAdapter.streamColumns()
        .reduce((first, second) -> second).get();
    return DepanFxNodeListColumns.newColumnMenu(after, tableAdapter);
  }

  /////////////////////////////////////
  // Sections

  private Menu buildNewSectionMenu() {
    // Should never get a table with no sections.
    DepanFxNodeListSection topSection =
        tableAdapter.streamSections().findFirst().get();
    return DepanFxNodeListSections.newSectionMenu(
        tableState.getScene(), tableAdapter, topSection);
  }

  /////////////////////////////////////
  // Table

  private Menu buildTableViewMenu() {

    DepanFxMenuBuilder menuBuilder = new DepanFxMenuBuilder(TABLE_VIEW);

    newColumnMenu = buildNewColumnMenu();
    menuBuilder.appendSubMenu(newColumnMenu);
    menuBuilder.appendSubMenu(buildNewSectionMenu());

    menuBuilder.appendSeparator();
    menuBuilder.appendActionItem(
        DepanFxNodeListTableViewSaveDialog.SELECT_TABLE_VIEW,
        e -> runNodeListTableViewChooser());
    menuBuilder.appendActionItem(
        DepanFxNodeListTableViewSaveDialog.SAVE_TABLE_VIEW,
        e -> runNodeListTableViewSaveDialog());

    return menuBuilder.build();
  }

  private void runNodeListTableViewChooser() {
    DepanFxNodeListTableViewSaveDialog
        .runTableViewChooser(workspace, dialogRunner, tableState.getScene())
        .ifPresent(tableState::setTableViewResource);
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
    DepanFxWorkspaceResource<DepanFxNodeListTableViewData> updateViewRsrc =
        DepanFxWorkspaceResource.forUpdate(
            tableState.getTableViewResource(), tableState.getTableView());

    DepanFxNodeListTableViewSaveDialog
        .runSaveTableView(dialogRunner, updateViewRsrc)
        .ifPresent(tableAdapter::setTableViewResource);
  }
}
