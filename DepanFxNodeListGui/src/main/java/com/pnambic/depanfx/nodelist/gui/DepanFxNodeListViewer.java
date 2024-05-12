package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.stream.Stream;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeTableView;

public class DepanFxNodeListViewer
    implements DepanFxNodeListTableAdapter {

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxNodeList nodeList;

  private final DepanFxNodeListTableState tableState;

  public DepanFxNodeListViewer(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeList nodeList,
      DepanFxNodeListTableViewData tableView) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.nodeList = nodeList;

    tableState = prepareNodeListTable(tableView);
  }

  public Tab createWorkspaceTab(String tabName) {
    Tab result = new Tab(tabName, tableState.getNodeListTable());
    DepanFxNodeListTableCommands tableCommands =
        new DepanFxNodeListTableCommands(workspace, dialogRunner, tableState);
    result.setContextMenu(tableCommands.buildViewContextMenu());
    return result;
  }

  @Override // DepanFxNodeListTableAdapter
  public DepanFxWorkspace getWorkspace() {
    return workspace;
  }

  @Override // DepanFxNodeListTableAdapter
  public GraphDocument getGraphDoc() {
    return nodeList.getGraphDocResource().getResource();
  }

  @Override // DepanFxNodeListTableAdapter
  public DepanFxDialogRunner getDialogRunner() {
    return dialogRunner;
  }

  @Override // DepanFxNodeListTableAdapter
  public void refreshTableView() {
    tableState.refreshTableView();
  }

  @Override // DepanFxNodeListTableAdapter
  public ObservableValue<Boolean> getCheckBoxObservable(int intValue) {
    return tableState.getCheckBoxObservable(intValue);
  }

  @Override // DepanFxNodeListTableAdapter
  public void doSelectGraphNodeAction(GraphNode selectNode, boolean value) {
    tableState.doSelectGraphNodeAction(selectNode, value);
  }

  @Override // DepanFxNodeListTableAdapter
  public void doSelectGraphNodesAction(Stream<GraphNode> nodes, boolean value) {
    tableState.doSelectGraphNodesAction(nodes, value);
  }

  @Override // DepanFxNodeListTableAdapter
  public Stream<DepanFxNodeListColumn> streamColumns() {
    return tableState.streamColumns();
  }

  @Override // DepanFxNodeListTableAdapter
  public DepanFxNodeListSection insertSection(DepanFxNodeListSection before,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> sectionRsrc) {
    return tableState.insertSection(before, sectionRsrc);
  }

  @Override // DepanFxNodeListTableAdapter
  public void updateSection(DepanFxNodeListSection section,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> sectionRsrc) {
    tableState.updateSection(section, sectionRsrc);
  }

  /////////////////////////////////////

  private DepanFxNodeListTableState prepareNodeListTable(
      DepanFxNodeListTableViewData tableView) {
    DepanFxNodeListTableState result =
        new DepanFxNodeListTableState(
            workspace, nodeList,
            DepanFxNodeListSelection.forNodes(nodeList.getNodes()),
            new TreeTableView<>(),
            new DepanFxNodeListTableFactory(this));

    result.setTableView(tableView);
    return result;
  }
}
