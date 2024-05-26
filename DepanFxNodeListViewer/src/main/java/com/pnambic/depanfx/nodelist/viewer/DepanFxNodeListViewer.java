package com.pnambic.depanfx.nodelist.viewer;

import com.pnambic.depanfx.nodefilters.gui.DepanFxNodeViewNodeFiltersDialog;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListConfiguration;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListSelection;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableCommands;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableController;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;

/**
 * Places a node list table into a workspace tab.
 */
public class DepanFxNodeListViewer {

  private static final String FILTER_SELECTION_ITEM = "Filter Selection...";

  private final DepanFxNodeListTableController tableControl;

  /**
   * Open supplemental windows (e.g. edit edge display).
   */
  private List<Stage> sideViews = new ArrayList<Stage>();

  public DepanFxNodeListViewer(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeList nodeList,
      DepanFxNodeListSelection selectedNodes,
      DepanFxNodeListTableViewData tableView) {

    tableControl = new DepanFxNodeListTableController(
        workspace, dialogRunner, nodeList, selectedNodes,
        tableView, new TreeTableView<>());
  }

  public Tab createWorkspaceTab(String tabName) {
    Tab result = new Tab(tabName, tableControl.getNodeListTable());
    result.setContextMenu(buildContextMenu());
    return result;
  }

  private ContextMenu buildContextMenu() {

    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    DepanFxNodeListTableCommands cmds = tableControl.buildTableCommands();
    cmds.addSelectItems(builder);
    builder.appendActionItem(
        FILTER_SELECTION_ITEM,
        e -> runFilterSelectionDialog());

    cmds.addLoadSaveItems(builder);
    cmds.addTableViewItems(builder);
    return builder.build();
  }

  private void runFilterSelectionDialog() {
    DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc =
        DepanFxProjects.getBuiltIn(
            tableControl.getWorkspace(), DepanFxNodeListTableViewData.class,
            DepanFxNodeListConfiguration.FLAT_TABLE_VIEW_PATH).get();

    Stage filterSelctionDialog =
        DepanFxNodeViewNodeFiltersDialog.runEditDialog(
            tableControl.getDialogRunner(), null,
            tableViewRsrc.getResource(), tableControl.getSelection(),
            nl -> tableControl.doSelectGraphNodesAction(nl.getNodes()));

     sideViews.add(filterSelctionDialog);
     filterSelctionDialog.setOnCloseRequest(
         e -> sideViews.remove(filterSelctionDialog));
  }
}
