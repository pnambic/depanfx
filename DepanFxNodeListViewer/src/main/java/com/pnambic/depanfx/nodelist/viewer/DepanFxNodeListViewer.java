package com.pnambic.depanfx.nodelist.viewer;

import com.pnambic.depanfx.edgematchers.link.DepanFxLinkMatchersRegistry;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodefilters.gui.DepanFxNodeFiltersDialogRegistry;
import com.pnambic.depanfx.nodefilters.model.DepanFxNodeFiltersRegistry;
import com.pnambic.depanfx.nodelist.gui.DepanFxFilterSelectionDialog;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableCommands;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableController;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListViewBuiltIns;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxColumnRegistry;
import com.pnambic.depanfx.nodelist.gui.nodefilters.FilterMenu;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneService;
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

  private final Tab viewerTab;

  // Created in the constructor
  private final DepanFxNodeListTableController tableControl;

  /**
   * Open supplemental windows (e.g. edit edge display).
   */
  private List<Stage> sideViews = new ArrayList<Stage>();

  private DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc;

  public DepanFxNodeListViewer(
      String viewerTitle,
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxColumnRegistry columnRegistry,
      DepanFxInfoRegistry infoRegistry,
      DepanFxLinkMatchersRegistry matcherRegistry,
      DepanFxNodeFiltersRegistry filterRegistry,
      DepanFxNodeFiltersDialogRegistry filterDialogRegistry) {

    viewerTab = new Tab(viewerTitle);

    tableControl = new DepanFxNodeListTableController(
        workspace, dialogRunner,
        columnRegistry, infoRegistry, matcherRegistry,
        filterRegistry, filterDialogRegistry,
        new TreeTableView<>());
  }

  public void initFromGraphResource(
      DepanFxWorkspaceResource<GraphDocument> graphRsrc,
      DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc) {
    initFromNodeListResource(
        tableControl.getWorkspace().addScratchResource(
            DepanFxNodeLists.buildNodeList(graphRsrc)),
        tableViewRsrc);
  }

  public void initFromNodeListResource(
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc,
      DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc) {
    this.nodeListRsrc = nodeListRsrc;
    tableControl.initFromNodeListResource(nodeListRsrc);
    tableControl.setTableViewResource(tableViewRsrc);

    viewerTab.setContent(tableControl.getNodeListTable());
    viewerTab.setContextMenu(buildContextMenu());
  }

  @Override
  public Tab getSceneTab(DepanFxSceneService sceneSrvc) {
    return viewerTab;
  }

  @Override // DepanFxSceneViewer
  public void closeTab() {
    // Just JavaFX resources.
  }

  public String getViewerTitle() {
    return viewerTab.getText();
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
    FilterMenu filterMenu = tableControl.buildFilterMenu();
    cmds.addSelectItems(builder);

    builder.appendSubMenu(
        cmds.buildFilterMenu(filterMenu, e -> runFilterSelectionDialog()));

    builder.appendSeparator();
    cmds.addNodeFoldItems(builder);

    cmds.addLoadSaveItems(builder);
    cmds.addTableViewItems(builder);
    ContextMenu result = builder.build();
    result.setOnShowing(e -> cmds.updateOnShowing());
    return result;
  }

  private void runFilterSelectionDialog() {
    DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc =
        DepanFxProjects.getBuiltIn(
            tableControl.getWorkspace(), DepanFxNodeListTableViewData.class,
            DepanFxNodeListViewBuiltIns.FLAT_TABLE_VIEW_PATH).get();

    Stage filterSelectionDialog =
        DepanFxFilterSelectionDialog.runEditDialog(
            tableControl.getDialogRunner(),
            tableViewRsrc, tableControl.getNodeFolding(), tableControl.getSelection(),
            nl -> tableControl.doSelectGraphNodesAction(nl.getNodes()));

     sideViews.add(filterSelectionDialog);
     filterSelectionDialog.setOnCloseRequest(
         e -> sideViews.remove(filterSelectionDialog));
  }
}
