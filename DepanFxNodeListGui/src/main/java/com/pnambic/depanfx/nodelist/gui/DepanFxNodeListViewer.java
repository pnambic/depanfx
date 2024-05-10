package com.pnambic.depanfx.nodelist.gui;

import com.google.common.collect.ImmutableList;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxColumnRegistry;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxFlatSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxSectionRegistry;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFlatSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

  private final DepanFxNodeList nodeList;

  private final DepanFxNodeListTableCommands tableCommands;

  private DepanFxNodeListTableViewData tableView;

  private List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>>
      sectionResources;

  private List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>>
      columnResources;

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
      DepanFxNodeListTableViewData tableView) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.nodeList = nodeList;
    this.tableView = tableView;

    nodesCheckBoxStates = buildNodesCheckBoxStates(nodeList.getNodes());
    nodeListTable = createTable();
    prepareTableView();

    tableCommands =
        new DepanFxNodeListTableCommands(workspace, dialogRunner, this);
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
  // Tree view

  @Override // DepanFxNodeListTableAdapter
  public void setTableView(DepanFxNodeListTableViewData tableView) {
    this.tableView = tableView;
    prepareTableView();
  }

  @Override // DepanFxNodeListTableAdapter
  public DepanFxNodeListTableViewData getTableView() {
    return new DepanFxNodeListTableViewData(
        tableView.getToolName(), tableView.getToolDescription(),
        sectionResources, columnResources);
  }

  /////////////////////////////////////
  // Tree sections

  @Override // DepanFxNodeListTableAdapter
  public DepanFxNodeListSection insertSection(
      DepanFxNodeListSection before,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> sectionRsrc) {

    // Don't default to after the last slot, 'cuz that's the catch-all section
    int index = Integer.max(0, sections.indexOf(before));
    DepanFxNodeListSection result = installSectionAt(index, sectionRsrc);

    resetTableRoot();
    return result;
  }

  @Override
  public void updateSection(DepanFxNodeListSection section,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> dataRsrc) {
    if (DepanFxSectionRegistry.updateSection(section, dataRsrc)) {
      resetTableRoot();
    }
  }

  /////////////////////////////////////
  // Table columns

  @Override // DepanFxNodeListTableAdapter
  public void addColumn(
      DepanFxWorkspaceResource<? extends DepanFxBaseColumnData> columnRsrc) {

    columnResources.add(columnRsrc);
    DepanFxNodeListColumn column =
        DepanFxColumnRegistry.toColumn(this, columnRsrc);
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

  private DepanFxNodeListSection installSectionAt(int index,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> sectionRsrc) {
    DepanFxNodeListSection result =
        DepanFxSectionRegistry.createSection(this, sectionRsrc);

    sections.add(index, result);
    sectionResources.add(index, sectionRsrc);

    return result;
  }

  private TreeItem<DepanFxNodeListMember> createTreeRoot() {
    DepanFxNodeListRoot rootMember = new DepanFxNodeListRoot(
        workspace, nodeList, ImmutableList.copyOf(sections));
    return new DepanFxNodeListRootItem(rootMember);
  }

  private TreeTableView<DepanFxNodeListMember> createTable() {
    TreeTableView<DepanFxNodeListMember> result = new TreeTableView<>();
    result.setShowRoot(false);
    result.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    result.setEditable(true);
    return result;
  }

  /**
   * Add the sections, the columns, and the tree root.
   * Needs to happen after the {@code nodeListTable} field is assigned.
   */
  private void prepareTableView() {

    // Start over with table columns.
    nodeListTable.getColumns().clear();

    // The first column is always the name, and it is not saved.
    TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> nameColumn =
        new TreeTableColumn<>("Node Name");
    nameColumn.setCellFactory(p -> new DepanFxNodeListCell(this));
    nameColumn.setCellValueFactory(
        p -> new ReadOnlyObjectWrapper<>(p.getValue().getValue()));
    nameColumn.setPrefWidth(DepanFxSceneControls.layoutWidthMs(30));
    nodeListTable.getColumns().add(nameColumn);

    // Prepare the table view's columns.
    List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>>
        srcColumnRsrcs = tableView.getColumnResources();
    int srcColumnCnt = srcColumnRsrcs.size();
    columnResources = new ArrayList<>(srcColumnCnt);
    columns = new ArrayList<>(srcColumnCnt);

    srcColumnRsrcs.stream().forEach(this::addColumn);

    // Prepare the table view's sections.
    List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>>
        srcSectionRsrcs = tableView.getSectionResources();
    int srcSectionCnt = srcSectionRsrcs.size();
    sectionResources = new ArrayList<>(srcSectionCnt);
    sections = new ArrayList<>(srcSectionCnt);

    srcSectionRsrcs.stream().forEach(this::installSection);

    // If the last one isn't a flat section, add one.
    if ( !(sections.getLast() instanceof DepanFxFlatSection)) {
      Optional<DepanFxWorkspaceResource<DepanFxFlatSectionData>> optFlatRsrc =
          ((DepanFxBuiltInProject) workspace.getBuiltInProject())
              .getResource(DepanFxNodeListSectionData.SIMPLE_SECTION_TOOL_PATH);

      optFlatRsrc.ifPresent(this::installSection);
    }

    // 'cuz we changed the table's sections
    resetTableRoot();
  }

  private void installSection(
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> sectionRsrc) {
    DepanFxNodeListSection result =
        DepanFxSectionRegistry.createSection(this, sectionRsrc);

    sections.add(result);
    sectionResources.add(sectionRsrc);
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
