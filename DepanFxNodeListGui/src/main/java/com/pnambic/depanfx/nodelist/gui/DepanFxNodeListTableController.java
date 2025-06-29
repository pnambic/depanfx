package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxNodeInfoStore;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxColumnRegistry;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.TreeTableView;

/**
 * Encapsulate context and gui behaviors with the node table state.
 */
public class DepanFxNodeListTableController
    implements DepanFxNodeListTableAdapter {

  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory
      .getLogger(DepanFxNodeListTableController.class);

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxColumnRegistry columnRegistry;

  private final DepanFxInfoRegistry infoRegistry;

  private final DepanFxNodeList nodeList;

  private final DepanFxNodeListTableState tableState;

  /**
   * Some keys might be classes, some keys might by instance-unique strings.
   */
  private final Map<Object, DepanFxNodeInfoStore> infoProviders =
      new HashMap<>();

  public DepanFxNodeListTableController(DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner, DepanFxColumnRegistry columnRegistry,
      DepanFxInfoRegistry infoRegistry, DepanFxNodeList nodeList,
      DepanFxNodeListSelection selectedNodes,
      TreeTableView<DepanFxNodeListMember> treeTable) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.columnRegistry = columnRegistry;
    this.infoRegistry = infoRegistry;
    this.nodeList = nodeList;

    tableState = prepareNodeListTable(treeTable, selectedNodes);
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

  @Override // DepanFxNodeListTableAdapter
  public DepanFxWorkspaceResource<GraphDocument> getGraphDocResource() {
    return nodeList.getGraphDocResource();
  }

  @Override // DepanFxNodeListTableAdapter
  public DepanFxNodeList buildEmptyList() {
    return DepanFxNodeLists.buildEmptyNodeList(nodeList);
  }

  public DepanFxNodeList buildRelatedNodeList(
      Collection<GraphNode> relatedNodes) {
    return DepanFxNodeLists.buildRelatedNodeList(nodeList, relatedNodes);
  }

  public Collection<GraphNode> getNodes() {
    return nodeList.getNodes();
  }

  @Override // DepanFxNodeListTableAdapter
  public DepanFxWorkspaceResource<DepanFxNodeListTableViewData> getTableViewResource() {
    return tableState.getTableViewResource();
  }

  @Override // DepanFxNodeListTableAdapter
  public void setTableViewResource(
      DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc) {
    tableState.setTableViewResource(tableViewRsrc);
  }

  @Override // DepanFxNodeListTableAdapter
  public void addInfoStore(
      Object infoKey, DepanFxNodeInfoStore infoStore) {
    infoProviders.put(infoKey, infoStore);
  }

  @Override // DepanFxNodeListTableAdapter
  public Optional<DepanFxNodeInfoStore> getInfoStore(Object infoKey) {
    return Optional.ofNullable(infoProviders.get(infoKey));
  }

  public TreeTableView<DepanFxNodeListMember> getNodeListTable() {
    return tableState.getNodeListTable();
  }

  public DepanFxNodeListTableCommands buildTableCommands() {
    return new DepanFxNodeListTableCommands(workspace, dialogRunner, this,
        tableState);
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

  public Scene getScene() {
    return tableState.getScene();
  }

  @Override // DepanFxNodeListTableAdapter
  public void refreshTableView() {
    tableState.refreshTableView();
  }

  @Override // DepanFxNodeListTableAdapter
  public void resetTableView() {
    tableState.resetTableView();
  }

  @Override // DepanFxNodeListTableAdapter
  public DepanFxNodeList getSelection() {
    return tableState.getSelection();
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

  @Override // DepanFxNodeListTableAdapter
  public Stream<DepanFxNodeListSection> streamSections() {
    return tableState.streamSections();
  }

  /**
   * Nodes in the collections are selected, all other nodes are not.
   */
  public void doSelectGraphNodesAction(Collection<GraphNode> nodes) {
    tableState.doSelectGraphNodesAction(nodes);
  }

  private DepanFxNodeListTableState prepareNodeListTable(
      // DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc,
      TreeTableView<DepanFxNodeListMember> treeTable,
      DepanFxNodeListSelection selectedNodes) {

    return new DepanFxNodeListTableState(workspace,
        nodeList, selectedNodes, treeTable,
        new DepanFxNodeListTableFactory(this));
  }

  @Override
  public Stream<DepanFxColumnRegistry.Contribution> streamColumnChoices() {
    return columnRegistry.streamContributions();
  }

  @Override
  public Stream<DepanFxInfoRegistry.Contribution> streamInfosByLabel(
      String label) {
    return infoRegistry.streamByLabel(label);
  }

  @Override
  public Optional<DepanFxNodeListColumn> toColumn(
      DepanFxWorkspaceResource<? extends DepanFxBaseColumnData> columnRsrc) {
    return columnRegistry.toColumn(this, columnRsrc);
  }
}
