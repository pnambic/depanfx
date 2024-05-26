package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Collection;
import java.util.stream.Stream;

import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.TreeTableView;

/**
 * Encapsulate context and gui behaviors with the node table state.
 */
public class DepanFxNodeListTableController
    implements DepanFxNodeListTableAdapter {

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxNodeList nodeList;

  private final DepanFxNodeListTableState tableState;

  public DepanFxNodeListTableController(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeList nodeList,
      DepanFxNodeListSelection selectedNodes,
      DepanFxNodeListTableViewData tableView,
      TreeTableView<DepanFxNodeListMember> treeTable) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.nodeList = nodeList;

    tableState = prepareNodeListTable(tableView, treeTable, selectedNodes);
  }

  @Override // DepanFxNodeListTableAdapter
  public DepanFxWorkspace getWorkspace() {
    return workspace;
  }

  @Override // DepanFxNodeListTableAdapter
  public DepanFxDialogRunner getDialogRunner() {
    return dialogRunner;
  }

  @Override // DepanFxNodeListTableAdapter
  public GraphDocument getGraphDoc() {
    return nodeList.getGraphDocResource().getResource();
  }

  public DepanFxNodeList buildRelatedNodeList(Collection<GraphNode> relatedNodes) {
    return DepanFxNodeLists.buildRelatedNodeList(nodeList, relatedNodes);
  }

  public Collection<GraphNode> getNodes() {
    return nodeList.getNodes();
  }

  public TreeTableView<DepanFxNodeListMember> getNodeListTable() {
    return tableState.getNodeListTable();
  }

  public DepanFxNodeListTableCommands buildTableCommands() {
    return new DepanFxNodeListTableCommands(
        workspace, dialogRunner, tableState);
  }

  public void setTableView(DepanFxNodeListTableViewData tableView) {
    tableState.setTableView(tableView);
  }

  public void doSelectAllAction() {
    tableState.doSelectAllAction();
  }

  public void doClearSelectionAction() {
    tableState.doClearSelectionAction();
  }

  public void doInvertSelectionAction() {
    tableState.doInvertSelectionAction();
  }

  public DepanFxNodeList getSelection() {
    return tableState.getSelection();
  }

  public Scene getScene() {
    return tableState.getScene();
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

  /**
   * Nodes in the collections are selected, all other nodes are not.
   */
  public void doSelectGraphNodesAction(Collection<GraphNode> nodes) {
    tableState.doSelectGraphNodesAction(nodes);
  }

  private DepanFxNodeListTableState prepareNodeListTable(
      DepanFxNodeListTableViewData tableView,
      TreeTableView<DepanFxNodeListMember> treeTable,
      DepanFxNodeListSelection selectedNodes) {

    DepanFxNodeListTableState result =
        new DepanFxNodeListTableState(
            workspace, nodeList, selectedNodes,
            treeTable, new DepanFxNodeListTableFactory(this));

    result.setTableView(tableView);
    return result;
  }
}
