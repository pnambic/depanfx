package com.pnambic.depanfx.nodelist.viewer;

import com.pnambic.depanfx.graph.nodeinfo.DepanFxCompositeInfoRegistry;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.nodefilters.gui.DepanFxNodeViewNodeFiltersDialog;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListSelection;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableCommands;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableController;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListViewBuiltIns;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxColumnRegistry;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.util.ArrayList;
import java.util.Collections;
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

  private final DepanFxInfoRegistry panelInfoRegistry;

  public DepanFxNodeListViewer(
      String viewerTitle,
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxColumnRegistry columnRegistry,
      DepanFxInfoRegistry infoRegistry,
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc,
      DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc) {

    this.viewerTitle = viewerTitle;
    this.nodeListRsrc = nodeListRsrc;
    this.panelInfoRegistry = preparePanelInfoRegistry(infoRegistry);

    DepanFxNodeList nodeList = nodeListRsrc.getResource();

    tableControl = new DepanFxNodeListTableController(
        workspace, dialogRunner, columnRegistry,
        panelInfoRegistry, nodeList,
        DepanFxNodeListSelection.forNodes(nodeList.getNodes()),
        tableViewRsrc, new TreeTableView<>());
  }

  private DepanFxInfoRegistry preparePanelInfoRegistry(
      DepanFxInfoRegistry baseRegistry) {
    return DepanFxCompositeInfoRegistry.buildInfoRegistry(
        baseRegistry, Collections.emptyList());
  }

  @Override
  public Tab getSceneTab(DepanFxSceneService sceneSrvc) {
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
            tableControl.getDialogRunner(), panelInfoRegistry,
            tableViewRsrc, tableControl.getSelection(),
            nl -> tableControl.doSelectGraphNodesAction(nl.getNodes()));

     sideViews.add(filterSelectionDialog);
     filterSelectionDialog.setOnCloseRequest(
         e -> sideViews.remove(filterSelectionDialog));
  }
}
