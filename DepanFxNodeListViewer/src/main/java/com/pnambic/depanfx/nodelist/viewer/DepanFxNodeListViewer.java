package com.pnambic.depanfx.nodelist.viewer;

import com.pnambic.depanfx.nodefilters.gui.DepanFxNodeViewNodeFiltersDialog;
import com.pnambic.depanfx.nodelist.builtins.DepanFxNodeListViewBuiltIns;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListSelection;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableCommands;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableController;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;
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
public class DepanFxNodeListViewer implements DepanFxSceneViewer {

  private static final String FILTER_SELECTION_ITEM = "Filter Selection...";

  private final String viewerTitle;

  private final DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc;

  // Created in the constructor
  private final DepanFxNodeListTableController tableControl;

  /**
   * Open supplemental windows (e.g. edit edge display).
   */
  private List<Stage> sideViews = new ArrayList<Stage>();

  public DepanFxNodeListViewer(
      String viewerTitle,
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc,
      DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc) {

    this.viewerTitle = viewerTitle;
    this.nodeListRsrc = nodeListRsrc;

    DepanFxNodeList nodeList = nodeListRsrc.getResource();

    tableControl = new DepanFxNodeListTableController(
        workspace, dialogRunner, nodeList,
        DepanFxNodeListSelection.forNodes(nodeList.getNodes()),
        tableViewRsrc, new TreeTableView<>());
  }

  @Override
  public Tab getSceneTab(DepanFxSceneController scene) {
    Tab result = new Tab(viewerTitle, tableControl.getNodeListTable());
    result.setContextMenu(buildContextMenu());
    return result;
  }

  @Override // DepanFxSceneViewer
  public void closeTab() {
    // Just JavaFX resources.
  }

  public String getViewerTitle() {
    return viewerTitle;
  }

  public DepanFxWorkspaceResource<DepanFxNodeList> getNodeListResource() {
    return nodeListRsrc;
  }

  public DepanFxWorkspaceResource<DepanFxNodeListTableViewData>
      getTableViewResource() {
    return tableControl.getTableViewResource();
  }

  public void doSelectAllAction() {
    tableControl.doSelectAllAction();
  }

  public void doClearSelectionAction() {
    tableControl.doClearSelectionAction();
  }

  public void doInvertSelectionAction() {
    tableControl.doInvertSelectionAction();
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
            DepanFxNodeListViewBuiltIns.FLAT_TABLE_VIEW_PATH).get();

    Stage filterSelectionDialog =
        DepanFxNodeViewNodeFiltersDialog.runEditDialog(
            tableControl.getDialogRunner(), tableViewRsrc, tableControl.getSelection(),
            nl -> tableControl.doSelectGraphNodesAction(nl.getNodes()));

     sideViews.add(filterSelectionDialog);
     filterSelectionDialog.setOnCloseRequest(
         e -> sideViews.remove(filterSelectionDialog));
  }
}
