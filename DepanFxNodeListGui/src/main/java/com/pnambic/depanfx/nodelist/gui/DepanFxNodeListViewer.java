package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import javafx.scene.control.Tab;
import javafx.scene.control.TreeTableView;

/**
 * Places a node list table into a workspace tab.
 */
public class DepanFxNodeListViewer {

  private final DepanFxNodeListTableController tableControl;

  public DepanFxNodeListViewer(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeList nodeList,
      DepanFxNodeListTableViewData tableView) {

    tableControl = new DepanFxNodeListTableController(
        workspace, dialogRunner, nodeList, tableView, new TreeTableView<>());
  }

  public Tab createWorkspaceTab(String tabName) {
    Tab result = new Tab(tabName, tableControl.getNodeListTable());
    result.setContextMenu(tableControl.buildViewContextMenu());
    return result;
  }
}
