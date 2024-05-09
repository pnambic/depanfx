package com.pnambic.depanfx.nodelist.gui;

import com.google.common.collect.ImmutableList;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxFlatSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxTreeSection;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFlatSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

public class DepanFxNodeListViewer
    implements DepanFxNodeListTableAdapter {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeListViewer.class);

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxNodeListTableCommands tableCommands;

  private DepanFxNodeList nodeList;

  private List<DepanFxNodeListSection> sections;

  private List<DepanFxNodeListColumn> columns;

  private TreeTableView<DepanFxNodeListMember> nodeListTable;

  private Map<DepanFxNodeListSection, BooleanProperty>
      sectionsCheckBoxStates = new HashMap<>();

  private Map<GraphNode, BooleanProperty> nodesCheckBoxStates;

  public DepanFxNodeListViewer(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeList nodeList,
      List<DepanFxNodeListSection> sections,
      List<DepanFxNodeListColumn> columns) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.nodeList = nodeList;

    // Make defensive copies
    this.sections = new ArrayList<>(sections);
    this.columns = new ArrayList<>(columns);

    nodesCheckBoxStates = buildNodesCheckBoxStates(nodeList.getNodes());
    nodeListTable = createTable();
    tableCommands =
        new DepanFxNodeListTableCommands(workspace, dialogRunner, this);
  }

  public DepanFxNodeListViewer(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeList nodeList,
      List<DepanFxNodeListSection> sections) {
    this(workspace, dialogRunner, nodeList, sections, Collections.emptyList());
  }

  public Tab createWorkspaceTab(String tabName) {
    Tab result = new Tab(tabName, nodeListTable);
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
  public void refreshTableView() {
    nodeListTable.refresh();
  }

  @Override // DepanFxNodeListTableAdapter
  public <T> Dialog<T> buildDialog(Class<T> controllerType) {
    return dialogRunner.createDialogAndParent(controllerType);
  }

  @Override // DepanFxNodeListTableAdapter
  public Scene getScene() {
    return nodeListTable.getScene();
  }

  @Override // DepanFxNodeListTableAdapter
  public DepanFxDialogRunner getDialogRunner() {
    return dialogRunner;
  }

  @Override // DepanFxNodeListTableAdapter
  public TreeItem<DepanFxNodeListMember> getTreeItem(int intValue) {
    TreeItem<DepanFxNodeListMember> item = nodeListTable.getTreeItem(intValue);
    return item;
  }

  @Override // DepanFxNodeListTableAdapter
  public ObservableValue<Boolean> getCheckBoxObservable(
      DepanFxNodeListMember member) {
    if (member instanceof DepanFxNodeListSection) {
      return sectionsCheckBoxStates.computeIfAbsent(
          (DepanFxNodeListSection) member,
          s -> new SimpleBooleanProperty(false));
    }
    if (member instanceof DepanFxNodeListGraphNode) {
      return nodesCheckBoxStates
          .get(((DepanFxNodeListGraphNode) member).getGraphNode());
    }
    return null;
  }

  @Override // DepanFxNodeListTableAdapter
  public void doSelectAllAction() {
    doSelectGraphNodesAction(nodeList.streamNodes(), true);
  }

  @Override // DepanFxNodeListTableAdapter
  public void doClearSelectionAction() {
    doSelectGraphNodesAction(nodeList.streamNodes(), false);
  }

  @Override // DepanFxNodeListTableAdapter
  public void doInvertSelectionAction() {
    nodeList.getNodes().stream()
        .forEach(this::doInvertGraphNodeAction);
  }

  @Override // DepanFxNodeListTableAdapter
  public void doSelectGraphNodesAction(Stream<GraphNode> nodes, boolean value) {
    nodes.forEach(n -> setSelectGraphNode(n, value));
  }

  @Override // DepanFxNodeListTableAdapter
  public void doSelectGraphNodeAction(GraphNode node, boolean value) {
    setSelectGraphNode(node, value);
  }

  /**
   * Since the previous state may have been unknown, provide the final
   * state for interested parties.
   */
  public boolean doInvertGraphNodeAction(GraphNode node) {
    return invertSelectGraphNode(node);
  }

  @Override // DepanFxNodeListTableAdapter
  public DepanFxNodeList getSelection() {
    return DepanFxNodeLists.buildRelatedNodeList(nodeList, getSelectedNodes());
  }

  /////////////////////////////////////
  // Tree sections

  @Override // DepanFxNodeListTableAdapter
  public void insertSection(
      DepanFxNodeListSection before, DepanFxNodeListSection insert) {

    // Don't default to after the last slot, 'cuz that's the catch-all section
    int index = Integer.max(0, sections.indexOf(before));
    sections.add(index, insert);

    resetTableRoot();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void updateSection(DepanFxNodeListSection section,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> dataRsrc) {
    switch (section) {
    case DepanFxTreeSection tree:
      tree.setSectionDataRsrc(
          (DepanFxWorkspaceResource<DepanFxTreeSectionData>) dataRsrc);
      break;
    case DepanFxFlatSection flat:
      flat.setSectionDataRsrc(
          (DepanFxWorkspaceResource<DepanFxFlatSectionData>) dataRsrc);
      break;
    default:
      // no need to refresh
      return;
    }
    resetTableRoot();
  }

  /////////////////////////////////////
  // Table columns

  @Override // DepanFxNodeListTableAdapter
  public void addColumn(DepanFxNodeListColumn column) {
    columns.add(column);
    nodeListTable.getColumns().add(column.prepareColumn());
  }

  @Override // DepanFxNodeListTableAdapter
  public Stream<DepanFxNodeListColumn> streamColumns() {
    return columns.stream();
  }

  /////////////////////////////////////
  // Tree root and table construction

  private void resetTableRoot() {
    TreeItem<DepanFxNodeListMember> treeRoot = createTreeRoot();
    nodeListTable.setRoot(treeRoot);
  }

  private TreeItem<DepanFxNodeListMember> createTreeRoot() {
    DepanFxNodeListRoot rootMember = new DepanFxNodeListRoot(
        workspace, nodeList, ImmutableList.copyOf(sections));
    return new DepanFxNodeListRootItem(rootMember);
  }

  private TreeTableView<DepanFxNodeListMember> createTable() {
    TreeTableView<DepanFxNodeListMember> result =
        new TreeTableView<>(createTreeRoot());
    result.setShowRoot(false);
    result.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    result.setEditable(true);

    TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> nameColumn =
        new TreeTableColumn<>("Node Name");
    nameColumn.setCellFactory(p -> new DepanFxNodeListCell(this));
    nameColumn.setCellValueFactory(
        p -> new ReadOnlyObjectWrapper<>(p.getValue().getValue()));
    nameColumn.setPrefWidth(DepanFxSceneControls.layoutWidthMs(30));

    result.getColumns().add(nameColumn);
    return result;
  }

  /////////////////////////////////////
  // Selected nodes

  private Map<GraphNode, BooleanProperty>
      buildNodesCheckBoxStates(Collection<GraphNode> nodes) {
    Map<GraphNode, BooleanProperty> result = new HashMap<>();
    nodes.forEach(
      n -> result.put(n, new SimpleBooleanProperty(false)));
    return result;
  }

  private Collection<GraphNode> getSelectedNodes() {
    return nodesCheckBoxStates.entrySet().stream()
        .filter(e -> e.getValue().getValue().booleanValue())
        .map(e -> e.getKey())
        .collect(Collectors.toList());
  }

  private BooleanProperty setSelectGraphNode(GraphNode node, boolean value) {
    BooleanProperty result = nodesCheckBoxStates.get(node);
    result.set(value);
    return result;
  }

  /**
   * Since the previous state may have been unknown, provide the final
   * state for interested parties.
   */
  private boolean invertSelectGraphNode(GraphNode node) {
    BooleanProperty checkedProperty = nodesCheckBoxStates.get(node);
    boolean result = !checkedProperty.get();
    checkedProperty.set(result);
    return result;
  }
}
