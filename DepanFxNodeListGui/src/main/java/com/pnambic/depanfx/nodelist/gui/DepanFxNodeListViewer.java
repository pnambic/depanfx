package com.pnambic.depanfx.nodelist.gui;

import com.google.common.collect.ImmutableList;
import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxCategoryColumn;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxCategoryColumnToolDialog;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxFocusColumn;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxFocusColumnToolDialog;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeKeyColumn;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeKeyColumnToolDialog;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxSimpleColumn;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxSimpleColumnConfiguration;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxCategoryColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFocusColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeKeyColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxSimpleColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;

public class DepanFxNodeListViewer {

  private static final String SELECT_ALL_ITEM = "Select All";

  private static final String CLEAR_SELECTION_ITEM = "Clear Selection";

  private static final String INVERT_SELECTION_ITEM = "Invert Selection";

  private static final String ADD_COLUMN = "Add Column";

  private static final String SELECT_COLUMN = "Select Column...";

  private static final String SAVE_NODE_LIST_ITEM = "Save as node list...";

  private static final String COLUMN_TOOL_EXT = "d*cti";

  public static final ExtensionFilter NODE_KEY_COLUMN_FILTER =
      DepanFxSceneControls.buildExtFilter("Any Column", COLUMN_TOOL_EXT);

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeListViewer.class);

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

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
    result.setContextMenu(buildViewContextMenu());
    return result;
  }

  public DepanFxWorkspace getWorkspace() {
    return workspace;
  }

  public GraphDocument getGraphDoc() {
    return (GraphDocument) nodeList.getGraphDocResource().getResource();
  }

  public ContextModelId getContextModelId() {
    return getGraphDoc()
        .getContextModelId();
  }

  public <T> Dialog<T> buildDialog(Class<T> controllerType) {
    return dialogRunner.createDialogAndParent(controllerType);
  }

  public DepanFxDialogRunner getDialogRunner() {
    return dialogRunner;
  }

  public TreeItem<DepanFxNodeListMember> getTreeItem(int intValue) {
    TreeItem<DepanFxNodeListMember> item = nodeListTable.getTreeItem(intValue);
    return item;
  }

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

  public void doSelectAllAction() {
    doSelectGraphNodesAction(nodeList.getNodes(), true);
  }

  public void doClearSelectionAction() {
    doSelectGraphNodesAction(nodeList.getNodes(), false);
  }

  public void doInvertSelectionAction() {
    nodeList.getNodes().stream()
        .forEach(this::doInvertGraphNodeAction);
  }

  public void doSelectGraphNodesAction(
      Collection<GraphNode> nodes, boolean value) {
    nodes.stream().forEach(n -> setSelectGraphNode(n, value));
  }

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

  /////////////////////////////////////
  // Tree sections

  public void refreshView() {
    nodeListTable.refresh();
  }

  public void resetView() {
    TreeItem<DepanFxNodeListMember> treeRoot = createTreeRoot();
    nodeListTable.setRoot(treeRoot);
  }

  public void insertSection(
      DepanFxNodeListSection before, DepanFxNodeListSection insert) {

    // Don't default to after the last slot, 'cuz that's the catch-all section
    int index = Integer.max(0, sections.indexOf(before));
    insertSection(index, insert);
  }

  public void prependMemberTree() {
    getInitialTreeSectionResource().ifPresent(m -> {
        DepanFxTreeSection insert = new DepanFxTreeSection(this, m);
        insertSection(0, insert);
    });
  }

  public void insertMemberTreeSection(DepanFxNodeListSection before) {
    getInitialTreeSectionResource().ifPresent(m -> {
        DepanFxTreeSection insert = new DepanFxTreeSection(this, m);
        insertSection(before, insert);
    });
  }

  public List<DepanFxNodeListColumn> getColumns() {
    // Make a defensive copy.
    return new ArrayList<>(columns);
  }

  /////////////////////////////////////
  // Internal

  private void runSaveNodeListDialog() {
    Dialog<DepanFxSaveNodeListDialog> saveDlg =
        dialogRunner.createDialogAndParent(DepanFxSaveNodeListDialog.class);
    DepanFxNodeList saveList =
        DepanFxNodeLists.buildRelatedNodeList(nodeList, getSelectedNodes());
    saveDlg.getController().setNodeListDoc(saveList);
    saveDlg.runDialog("Save selection as node list");
  }

  private Optional<DepanFxWorkspaceResource> getInitialTreeSectionResource() {
    ContextModelId modelId = getGraphDoc().getContextModelId();

    return DepanFxProjects.getBuiltIn(
        workspace, DepanFxTreeSectionData.class,
        c -> this.byMemberLinkMatcherDoc(c, modelId));
  }

  private boolean byMemberLinkMatcherDoc(
      DepanFxBuiltInContribution contrib, Object modelId) {
    DepanFxTreeSectionData doc =
        (DepanFxTreeSectionData) contrib.getDocument();
    DepanFxLinkMatcherDocument linkMatchDoc =
        ((DepanFxLinkMatcherDocument) doc.getLinkMatcherRsrc(workspace)
            .getResource());
    if (!linkMatchDoc.getMatchGroups()
        .contains(DepanFxLinkMatcherGroup.MEMBER)) {
      return false;
    }
    // [29-Nov-2023] Kludge for matches any, actual matcher provided later.
    if (linkMatchDoc.getModelId() == null) {
      return true;
    }
    return linkMatchDoc.getModelId().equals(modelId);
  }

  private List<DepanFxNodeListSection> isolateSections() {
    return ImmutableList.copyOf(sections);
  }

  private void insertSection(int index, DepanFxNodeListSection insert) {

    // Don't default to after the last slot, 'cuz that's the catch-all section
    sections.add(index, insert);

    resetView();
  }

  private TreeItem<DepanFxNodeListMember> createTreeRoot() {
    DepanFxNodeListRoot rootMember =
        new DepanFxNodeListRoot(workspace, nodeList, isolateSections());
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
    nameColumn.setCellFactory(new NodeListTableCellFactory());
    nameColumn.setCellValueFactory(new NodeListTableValueFactory());
    nameColumn.setPrefWidth(DepanFxSceneControls.layoutWidthMs(30));

    result.getColumns().add(nameColumn);
    return result;
  }

  private ContextMenu buildViewContextMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(
        SELECT_ALL_ITEM, e -> doSelectAllAction());
    builder.appendActionItem(
        CLEAR_SELECTION_ITEM, e -> doClearSelectionAction());
    builder.appendActionItem(
        INVERT_SELECTION_ITEM, e -> doInvertSelectionAction());
    builder.appendSeparator();
    builder.appendSubMenu(newColumnMenu());
    builder.appendSeparator();
    builder.appendActionItem(
        SAVE_NODE_LIST_ITEM, e -> runSaveNodeListDialog());
    return builder.build();
  }

  private Menu newColumnMenu() {
    Menu result = new Menu(ADD_COLUMN);
    ObservableList<MenuItem> items = result.getItems();
    items.add(DepanFxContextMenuBuilder.createActionItem(
        SELECT_COLUMN,
        e -> doSelectColumnAction()));
    items.add(new SeparatorMenuItem());
    items.add(DepanFxContextMenuBuilder.createActionItem(
        "Simple Column", e -> doAddSimpleColumnAction()));
    items.add(DepanFxContextMenuBuilder.createActionItem(
        DepanFxNodeKeyColumn.NEW_NODE_KEY_COLUMN,
        e -> doNewNodeKeyColumnAction()));
    items.add(DepanFxContextMenuBuilder.createActionItem(
        DepanFxFocusColumn.NEW_FOCUS_COLUMN,
        e -> doNewFocusColumnAction()));
    items.add(DepanFxContextMenuBuilder.createActionItem(
        DepanFxCategoryColumn.NEW_CATEGORY_COLUMN,
        e -> doNewCategoryColumnAction()));
    return result;
  }

  private void doSelectColumnAction() {
    FileChooser fileChooser = DepanFxResourcePerspectives.prepareToolFinder(
        workspace, DepanFxNodeListColumnData.COLUMNS_TOOL_PATH);
    ObservableList<ExtensionFilter> filters = fileChooser.getExtensionFilters();
    filters.add(DepanFxCategoryColumnToolDialog.CATEGORY_COLUMN_FILTER);
    filters.add(DepanFxFocusColumnToolDialog.FOCUS_COLUMN_FILTER);
    filters.add(DepanFxNodeKeyColumnToolDialog.NODE_KEY_COLUMN_FILTER);
    filters.add(NODE_KEY_COLUMN_FILTER);
    fileChooser.setSelectedExtensionFilter(NODE_KEY_COLUMN_FILTER);

    File selectedFile =
        fileChooser.showOpenDialog(nodeListTable.getScene().getWindow());
    if (selectedFile != null) {
       workspace
          .toProjectDocument(selectedFile.getAbsoluteFile().toURI())
          .flatMap(p -> workspace.getWorkspaceResource(
              p, DepanFxBaseColumnData.class))
          .flatMap(this::toColumn)
          .ifPresent(this::addColumn);
    }
  }

  private Optional<DepanFxNodeListColumn> toColumn(
      DepanFxWorkspaceResource columnRsrc) {
    Class<?> type = columnRsrc.getResource().getClass();
    if (DepanFxCategoryColumnData.class.isAssignableFrom(type)) {
      return Optional.of(new DepanFxCategoryColumn(this, columnRsrc));
    }
    if (DepanFxFocusColumnData.class.isAssignableFrom(type)) {
      return Optional.of(new DepanFxFocusColumn(this, columnRsrc));
    }
    if (DepanFxNodeKeyColumnData.class.isAssignableFrom(type)) {
      return Optional.of(new DepanFxNodeKeyColumn(this, columnRsrc));
    }
    if (DepanFxSimpleColumnData.class.isAssignableFrom(type)) {
      return Optional.of(new DepanFxSimpleColumn(this, columnRsrc));
    }
    LOG.warn("Unknown type {} for column construction", type.getName());
    return Optional.empty();
  }

  private void doAddSimpleColumnAction() {
    DepanFxSimpleColumnConfiguration.getBuiltinSimpleColumnResource(workspace)
        .map(r -> new DepanFxSimpleColumn(this, r))
        .ifPresent(this::addColumn);
  }

  private void doNewNodeKeyColumnAction() {
    DepanFxNodeKeyColumnData initialData =
        DepanFxNodeKeyColumn.buildInitialNodeKeyColumnData();
    Dialog<DepanFxNodeKeyColumnToolDialog> createDlg =
        DepanFxNodeKeyColumnToolDialog.runCreateDialog(
            initialData, dialogRunner);
    createDlg.getController().getWorkspaceResource()
        .map(r -> new DepanFxNodeKeyColumn(this, r))
        .ifPresent(this::addColumn);
  }

  private void doNewFocusColumnAction() {
    DepanFxFocusColumnData initialData =
        DepanFxFocusColumnData.buildInitialFocusColumnData(null);
    Dialog<DepanFxFocusColumnToolDialog> createDlg =
        DepanFxFocusColumnToolDialog.runCreateDialog(
            initialData, dialogRunner);
    createDlg.getController().getWorkspaceResource()
        .map(r -> new DepanFxFocusColumn(this, r))
        .ifPresent(this::addColumn);
  }

  private void doNewCategoryColumnAction() {
    DepanFxCategoryColumnData initialData =
        DepanFxCategoryColumnData.buildInitialCategoryColumnData();
    Dialog<DepanFxCategoryColumnToolDialog> createDlg =
        DepanFxCategoryColumnToolDialog.runCreateDialog(
            initialData, dialogRunner);
    createDlg.getController().getWorkspaceResource()
        .map(r -> new DepanFxCategoryColumn(this, r))
        .ifPresent(this::addColumn);
  }

  private void addColumn(DepanFxNodeListColumn column) {
    columns.add(column);
    nodeListTable.getColumns().add(column.prepareColumn());
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

  /////////////////////////////////////
  // Internal Types

  private class NodeListTableCellFactory
      implements Callback<
          TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember>,
          TreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember>> {

    @Override
    public TreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember> call(
        TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> param) {

      return new DepanFxNodeListCell(DepanFxNodeListViewer.this);
    }
  }

  private class NodeListTableValueFactory
      implements Callback<
          CellDataFeatures<DepanFxNodeListMember, DepanFxNodeListMember>,
          ObservableValue<DepanFxNodeListMember>> {

    @Override
    public ObservableValue<DepanFxNodeListMember> call(
        CellDataFeatures<DepanFxNodeListMember, DepanFxNodeListMember> param) {
      return new ReadOnlyObjectWrapper<>(param.getValue().getValue());
    }
  }
}
