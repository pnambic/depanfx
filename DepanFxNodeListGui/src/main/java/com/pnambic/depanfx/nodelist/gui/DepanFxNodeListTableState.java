package com.pnambic.depanfx.nodelist.gui;

import com.google.common.collect.ImmutableList;
import com.pnambic.depanfx.graph.model.GraphNode;
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
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

/**
 * Handles the rendering of a node list in a table of sections and columns.
 */
public class DepanFxNodeListTableState {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeListTableState.class);

  private final DepanFxWorkspace workspace;

  private final DepanFxNodeList nodeList;

  private final DepanFxNodeListSelection selectedNodes;

  private final TreeTableView<DepanFxNodeListMember> nodeListTable;

  private final DepanFxNodeListTableFactory tableFactory;

  // Table view columns and sections can be reloaded.
  private DepanFxNodeListTableViewData tableView;

  private List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>>
      sectionResources;

  private List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>>
      columnResources;

  private List<DepanFxNodeListSection> sections;

  private List<DepanFxNodeListColumn> columns;

  // 'cuz Sections have checkboxes for selection, too.
  private Map<DepanFxNodeListSection, BooleanProperty>
      sectionsCheckBoxStates = new HashMap<>();

  /**
   * @param nodeListTable should be placed into a container by the caller.
   *   The table's internal properties, including root display,
   *   selection mode, and mutability are managed by this instance.
   */
  public DepanFxNodeListTableState(
      DepanFxWorkspace workspace,
      DepanFxNodeList nodeList,
      DepanFxNodeListSelection selectedNodes,
      TreeTableView<DepanFxNodeListMember> nodeListTable,
      DepanFxNodeListTableFactory tableFactory) {
    this.workspace = workspace;
    this.nodeList = nodeList;
    this.selectedNodes = selectedNodes;
    this.nodeListTable = nodeListTable;
    this.tableFactory = tableFactory;

    // Force the provided table into the required behaviors.
    configTable();
  }

  public TreeTableView<DepanFxNodeListMember> getNodeListTable() {
    return nodeListTable;
  }

  public void refreshTableView() {
    nodeListTable.refresh();
  }

  public ObservableValue<Boolean> getCheckBoxObservable(int treeIndex) {
    return getCheckBoxObservable(getTreeItem(treeIndex).getValue());
  }

  /////////////////////////////////////
  // For DepanFxNodeListTableCommands

  public Scene getScene() {
    return nodeListTable.getScene();
  }

  public void doSelectAllAction() {
    selectedNodes.doSelectAllAction();
  }

  public void doClearSelectionAction() {
    selectedNodes.doClearSelectionAction();
  }

  public void doInvertSelectionAction() {
    selectedNodes.doInvertSelectionAction();
  }

  /**
   * For performance, a {@code HashSet<GraphNode>} is preferred.
   */
  public void doSelectGraphNodesAction(Collection<GraphNode> nodes) {
    selectedNodes.doSelectGraphNodesAction(nodes);
  }

  public void doSelectGraphNodesAction(Stream<GraphNode> nodes, boolean value) {
    nodes
        .filter(nodeList.getNodes()::contains)
        .forEach(n -> setSelectGraphNode(n, value));
  }

  public void doSelectGraphNodeAction(GraphNode node, boolean value) {
    setSelectGraphNode(node, value);
  }

  public DepanFxNodeList getSelection() {
    return selectedNodes.getSelection(nodeList);
  }

  /**
   * Since the previous state may have been unknown, provide the final
   * state for interested parties.
   */
  public boolean doInvertGraphNodeAction(GraphNode node) {
    return selectedNodes.invertSelectGraphNode(node);
  }

  /////////////////////////////////////
  // Tree view

  public void setTableView(DepanFxNodeListTableViewData tableView) {
    this.tableView = tableView;
    prepareTableView();
  }

  public DepanFxNodeListTableViewData getTableView() {
    return new DepanFxNodeListTableViewData(
        tableView.getToolName(), tableView.getToolDescription(),
        sectionResources, columnResources);
  }

  /////////////////////////////////////
  // Tree sections

  public DepanFxNodeListSection insertSection(
      DepanFxNodeListSection before,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> sectionRsrc) {

    // Don't default to after the last slot, 'cuz that's the catch-all section
    int index = Integer.max(0, sections.indexOf(before));
    DepanFxNodeListSection result = installSectionAt(index, sectionRsrc);

    resetTableRoot();
    return result;
  }

  public void updateSection(DepanFxNodeListSection section,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> dataRsrc) {
    if (DepanFxSectionRegistry.updateSection(section, dataRsrc)) {
      resetTableRoot();
    }
  }

  /////////////////////////////////////
  // Table columns

  public void addColumn(
      DepanFxWorkspaceResource<? extends DepanFxBaseColumnData> columnRsrc) {

    columnResources.add(columnRsrc);
    DepanFxNodeListColumn column = tableFactory.createTableColumn(columnRsrc);
    columns.add(column);
    nodeListTable.getColumns().add(column.prepareColumn());
  }

  public Stream<DepanFxNodeListColumn> streamColumns() {
    return columns.stream();
  }

  /////////////////////////////////////
  // Tree root and table construction

  private void configTable() {
    nodeListTable.setShowRoot(false);
    nodeListTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    nodeListTable.setEditable(true);
  }

  private void resetTableRoot() {
    TreeItem<DepanFxNodeListMember> treeRoot = createTreeRoot();
    nodeListTable.setRoot(treeRoot);
  }

  private TreeItem<DepanFxNodeListMember> createTreeRoot() {
    DepanFxNodeListRoot rootMember = new DepanFxNodeListRoot(
        workspace, nodeList, ImmutableList.copyOf(sections));
    return new DepanFxNodeListRootItem(rootMember);
  }

  /**
   * Add the sections, the columns, and the tree root.
   * Needs to happen after the {@code nodeListTable} field is assigned.
   */
  private void prepareTableView() {
    // Preserve the name column
    ObservableList<TreeTableColumn<DepanFxNodeListMember, ?>> tableColumns =
        nodeListTable.getColumns();
    TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember>
        nameColumn = getNameColumn(tableColumns);

    // Start over with table columns.
    tableColumns.clear();
    tableColumns.add(nameColumn);
    // Update name column cell to use new table controller.
    nameColumn.setCellFactory(p -> tableFactory.createTableCell());

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

    // If the last section isn't a flat section, add one.
    if ( !(sections.getLast() instanceof DepanFxFlatSection)) {
      Optional<DepanFxWorkspaceResource<DepanFxFlatSectionData>> optFlatRsrc =
          DepanFxProjects.getBuiltIn(
              workspace, DepanFxFlatSectionData.class,
              DepanFxNodeListSectionData.SIMPLE_SECTION_TOOL_PATH);

      optFlatRsrc.ifPresent(this::installSection);
    }

    // 'cuz we changed the table's sections
    resetTableRoot();
  }

  @SuppressWarnings("unchecked")
  private TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember>
      getNameColumn(
            ObservableList<TreeTableColumn<DepanFxNodeListMember, ?>> tableColumns) {

    // Preserve the name column if it is present.
    if (!tableColumns.isEmpty()) {
      return (TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember>)
          tableColumns.get(0);
    }

    // Ensure a name column to start the table.
    return tableFactory.createNameColumn();
  }

  private TreeItem<DepanFxNodeListMember> getTreeItem(int intValue) {
    TreeItem<DepanFxNodeListMember> item = nodeListTable.getTreeItem(intValue);
    return item;
  }

  private ObservableValue<Boolean> getCheckBoxObservable(
      DepanFxNodeListMember member) {
    switch (member) {
    case DepanFxNodeListSection section:
      return sectionsCheckBoxStates.computeIfAbsent(
          (DepanFxNodeListSection) member,
          s -> new SimpleBooleanProperty(false));
    case DepanFxNodeListGraphNode node:
      return selectedNodes.getSelected(node);
    default:
      LOG.warn("Unexpected list member {} for getCheckBoxObservable",
          member.getClass().getSimpleName());
    }
    return null;
  }

  private BooleanProperty setSelectGraphNode(GraphNode node, boolean value) {
    return selectedNodes.setSelectGraphNode(node, value);
  }

  private void installSection(
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> sectionRsrc) {
    DepanFxNodeListSection result =
        tableFactory.createTableSection(sectionRsrc);

    sections.add(result);
    sectionResources.add(sectionRsrc);
  }

  private DepanFxNodeListSection installSectionAt(int index,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> sectionRsrc) {
    DepanFxNodeListSection result =
        tableFactory.createTableSection(sectionRsrc);

    sections.add(index, result);
    sectionResources.add(index, sectionRsrc);

    return result;
  }
}
