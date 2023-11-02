package com.pnambic.depanfx.nodelist.gui;

import com.google.common.collect.ImmutableList;
import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherRegistry;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;

public class DepanFxNodeListViewer {

  private static final String SAVE_NODE_LIST_ITEM = "Save as node list...";

  private static final String SELECT_ALL_ITEM = "Select All";

  private static final String CLEAR_SELECTION_ITEM = "Clear Selection";

  private static final String INVERT_SELECTION_ITEM = "Invert Selection";

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxLinkMatcherRegistry linkMatcherRegistry;

  private DepanFxNodeList nodeList;

  private List<DepanFxNodeListSection> sections;

  private TreeTableView<DepanFxNodeListMember> nodeListTable;

  private Map<DepanFxNodeListSection, BooleanProperty>
      sectionsCheckboxStates = new HashMap<>();

  private Map<GraphNode, BooleanProperty> nodesCheckboxStates;

  public DepanFxNodeListViewer(
      DepanFxDialogRunner dialogRunner,
      DepanFxLinkMatcherRegistry linkMatcherRegistry,
      DepanFxNodeList nodeList,
      List<DepanFxNodeListSection> sections) {
    this.dialogRunner = dialogRunner;
    this.linkMatcherRegistry = linkMatcherRegistry;
    this.nodeList = nodeList;
    this.sections = sections;
    this.nodeListTable = createTable();

    // After createTable(), or there are stack overflow problems.
    nodesCheckboxStates = new HashMap<>(nodeList.getNodes().size());
  }

  public Tab createWorkspaceTab(String tabName) {
    Tab result = new Tab(tabName, nodeListTable);
    result.setContextMenu(buildViewContextMenu());
    return result;
  }

  public void saveNodeList(File dstFile) {
    DepanFxNodeList saveList =
        DepanFxNodeLists.buildRelatedNodeList(nodeList, getSelectedNodes());
    try {
      getWorkspace().saveDocument(dstFile.toURI(), saveList);
    } catch (IOException errIo) {
      throw new RuntimeException(
          "Unable to save nodelist " + dstFile.toString(), errIo);
    }
  }

  public DepanFxWorkspace getWorkspace() {
    return nodeList.getWorkspaceResource().getWorkspace();
  }

  public TreeItem<DepanFxNodeListMember> getTreeItem(int intValue) {
    TreeItem<DepanFxNodeListMember> item = nodeListTable.getTreeItem(intValue);
    return item;
  }

  public ObservableValue<Boolean> getCheckbooxObservable(
      DepanFxNodeListMember member) {
    if (member instanceof DepanFxNodeListSection) {
      return sectionsCheckboxStates.computeIfAbsent(
          (DepanFxNodeListSection) member,
          s -> new SimpleBooleanProperty(false));
    }
    if (member instanceof DepanFxGraphNodeProvider) {
      return nodesCheckboxStates.computeIfAbsent(
          ((DepanFxGraphNodeProvider) member).getGraphNode(),
          n -> new SimpleBooleanProperty(false));
    }
    return null;
  }

  public void doSelectAllAction() {
    nodesCheckboxStates.values().stream()
    .forEach(s -> s.set(true));
  }

  public void doClearSelectionAction() {
    nodesCheckboxStates.values().stream()
        .forEach(s -> s.set(false));
  }

  public void doInvertSelectionAction() {
    nodesCheckboxStates.values().stream()
        .forEach(s -> s.set(!s.get()));
  }

  public void insertSection(
      DepanFxNodeListSection before, DepanFxNodeListSection insert) {

    // Don't default to after the last slot, 'cuz that's the catch-all section
    int index = Integer.max(0, sections.indexOf(before));
    insertSection(index, insert);
  }

  /////////////////////////////////////
  // Support for built-in member tree sections

  public void prependMemberTree() {
    getMemberLinkMatcher().ifPresent(m -> {
        DepanFxTreeSection insert = new DepanFxTreeSection(m);
        insertSection(0, insert);});
  }

  public void insertMemberTreeSection(DepanFxNodeListSection before) {
    getMemberLinkMatcher().ifPresent(m -> {
        DepanFxTreeSection insert = new DepanFxTreeSection(m);
        insertSection(before, insert);});
  }

  /////////////////////////////////////
  // Internal

  private void runSaveNodeListDialog() {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("save-node-list-dialog.fxml"));
    loader.setController(new DepanFxSaveNodeListDialog(this));
    try {
      Parent dialog = loader.load();
      dialogRunner.runDialog(dialog, "Save selection as node list");
    } catch (IOException errIo) {
      throw new RuntimeException("Unable to load save node list dialog", errIo);
    }
  }

  private Collection<GraphNode> getSelectedNodes() {
    return nodesCheckboxStates.entrySet().stream()
        .filter(e -> e.getValue().getValue().booleanValue())
        .map(e -> e.getKey())
        .collect(Collectors.toList());
  }

  private Optional<DepanFxLinkMatcher> getMemberLinkMatcher() {
    ContextModelId modelId =
        ((GraphDocument) nodeList.getWorkspaceResource().getResource())
        .getContextModelId();
    return linkMatcherRegistry.getMatcherByGroup(
        modelId, DepanFxLinkMatcherGroup.MEMBER);
  }

  private List<DepanFxNodeListSection> isolateSections() {
    return ImmutableList.copyOf(sections);
  }

  private void insertSection(int index, DepanFxNodeListSection insert) {

    // Don't default to after the last slot, 'cuz that's the catch-all section
    sections.add(index, insert);

    TreeItem<DepanFxNodeListMember> treeRoot = createTreeRoot();
    nodeListTable.setRoot(treeRoot);
  }

  private TreeItem<DepanFxNodeListMember> createTreeRoot() {
    DepanFxNodeListRoot rootMember =
        new DepanFxNodeListRoot(nodeList, isolateSections());
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

  /////////////////////////////////////
  // Tab Context Menu

  private ContextMenu buildViewContextMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(
        SELECT_ALL_ITEM, e -> doSelectAllAction());
    builder.appendActionItem(
        CLEAR_SELECTION_ITEM, e -> doClearSelectionAction());
    builder.appendActionItem(
        INVERT_SELECTION_ITEM, e -> doInvertSelectionAction());
    builder.appendSeparator();
    builder.appendActionItem(
        SAVE_NODE_LIST_ITEM, e -> runSaveNodeListDialog());
    return builder.build();
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
