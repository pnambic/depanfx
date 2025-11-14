package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.edgematchers.link.DepanFxLinkMatchersRegistry;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxBaseMatcherDocument;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcher;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxNodeInfoStore;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodefilters.gui.DepanFxNodeFiltersDialogRegistry;
import com.pnambic.depanfx.nodefilters.model.DepanFxNodeFiltersRegistry;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxColumnRegistry;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.nodefilters.FilterMenu;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.gui.sections.folds.NodeListFoldController;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeFoldController;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;
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

  private final DepanFxLinkMatchersRegistry matcherRegistry;

  private final DepanFxNodeFiltersRegistry filterRegistry;

  private final DepanFxNodeFiltersDialogRegistry filterDialogRegistry;

  private final TreeTableView<DepanFxNodeListMember> treeTable;

  // Established after the document contents are obtained.
  private DepanFxNodeList nodeList;

  private DepanFxNodeListTableState tableState;

  /**
   * Some keys might be classes, some keys might by instance-unique strings.
   */
  private final Map<Object, DepanFxNodeInfoStore> infoProviders =
      new HashMap<>();

  public DepanFxNodeListTableController(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxColumnRegistry columnRegistry,
      DepanFxInfoRegistry infoRegistry,
      DepanFxLinkMatchersRegistry matcherRegistry,
      DepanFxNodeFiltersRegistry filterRegistry,
      DepanFxNodeFiltersDialogRegistry filterDialogRegistry,
      TreeTableView<DepanFxNodeListMember> treeTable) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.columnRegistry = columnRegistry;
    this.infoRegistry = infoRegistry;
    this.matcherRegistry = matcherRegistry;
    this.filterRegistry = filterRegistry;
    this.filterDialogRegistry = filterDialogRegistry;
    this.treeTable = treeTable;
  }

  public void initFromGraphResource(
      DepanFxWorkspaceResource<GraphDocument> graphRsrc) {
    prepareTableState(DepanFxNodeLists.buildNodeList(graphRsrc));
  }

  public void initFromNodeListResource(
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {
    initFromNodeList(nodeListRsrc.getResource());
  }

  public void initFromNodeList(DepanFxNodeList nodeList) {
    prepareTableState(nodeList);
  }

  private void prepareTableState(DepanFxNodeList nodeList) {
    initLinkedNodeList(nodeList,
        new NodeListFoldController(workspace, nodeList.getGraphDocResource()),
        DepanFxNodeListCheckBoxSelection.forNodes(nodeList.getNodes()));
  }

  public void initLinkedNodeList(
      DepanFxNodeList nodeList,
      DepanFxNodeFoldController nodeFolding,
      DepanFxNodeListCheckBoxSelection nodeSelection) {
    this.nodeList = nodeList;
    tableState = prepareNodeListTable(treeTable, nodeSelection, nodeFolding);
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

  public TreeTableView<DepanFxNodeListMember> getNodeListTable() {
    return tableState.getNodeListTable();
  }

  public DepanFxNodeListTableCommands buildTableCommands() {
    return new DepanFxNodeListTableCommands(
        workspace, dialogRunner, this, tableState);
  }

  public FilterMenu buildFilterMenu() {
    return new FilterMenu(
        workspace, dialogRunner, getScene(),
        filterRegistry, filterDialogRegistry,
        getGraphDoc().getGraph(), tableState);
  }

  @Override // DepanFxNodeListTableAdapter
  public DepanFxNodeList getSelection() {
    return tableState.getSelection();
  }

  @Override // DepanFxNodeListTableAdapter
  public boolean someSelection() {
    return tableState.someSelection();
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
  public Optional<DepanFxNodeListColumn> addColumn(
      DepanFxNodeListColumn after,
      DepanFxWorkspaceResource<? extends DepanFxBaseColumnData> columnRsrc) {
    return tableState.addColumn(after, columnRsrc);
  }

  @Override // DepanFxNodeListTableAdapter
  public Stream<DepanFxNodeListColumn> streamColumns() {
    return tableState.streamColumns();
  }

  @Override // DepanFxNodeListTableAdapter
  public Stream<DepanFxColumnRegistry.Contribution> streamColumnChoices() {
    return columnRegistry.streamContributions();
  }

  @Override // DepanFxNodeListTableAdapter
  public Optional<DepanFxNodeListColumn> toColumn(
      DepanFxWorkspaceResource<? extends DepanFxBaseColumnData> columnRsrc) {
    return columnRegistry.toColumn(this, columnRsrc);
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

  @Override
  public Stream<DepanFxWorkspaceMember> streamSectionChoices() {
    return null;
  }

  @Override
  public DepanFxNodeFoldController getNodeFolding() {
    return tableState.getNodeFolding();
  }

  @Override
  public Stream<DepanFxWorkspaceResource<DepanFxNodeFoldData>>
  streamNodeFoldResources() {
    return tableState.streamNodeFoldResources();
  }

  @Override
  public DepanFxLinkMatcher buildLinkMatcher(
      DepanFxBaseMatcherDocument matcherInfo) {
    return matcherRegistry.buildMatcher(matcherInfo);
  }

  @Override
  public Optional<DepanFxLinkMatcher> lookupMatcher(
      DepanFxBaseMatcherDocument matcherInfo) {
    return matcherRegistry.lookupMatcher(matcherInfo);
  }

  /**
   * Nodes in the collections are selected, all other nodes are not.
   */
  public void doSelectGraphNodesAction(Collection<GraphNode> nodes) {
    tableState.doSelectGraphNodesAction(nodes);
  }

  @Override
  public Stream<DepanFxInfoRegistry.Contribution> streamInfosByLabel(
      String label) {
    return infoRegistry.streamByLabel(label);
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

  private DepanFxNodeListTableState prepareNodeListTable(
      TreeTableView<DepanFxNodeListMember> treeTable,
      DepanFxNodeListCheckBoxSelection selectedNodes,
      DepanFxNodeFoldController nodeFolding) {

    return new DepanFxNodeListTableState(workspace,
        nodeList, selectedNodes, treeTable,
        new DepanFxNodeListTableFactory(this),
        nodeFolding);
  }
}
