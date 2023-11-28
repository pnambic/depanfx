package com.pnambic.depanfx.nodelist.gui;

import com.google.common.collect.ImmutableList;
import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherGroup;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

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

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private DepanFxNodeList nodeList;

  private List<DepanFxNodeListSection> sections;

  private TreeTableView<DepanFxNodeListMember> nodeListTable;

  private Map<DepanFxNodeListSection, BooleanProperty>
      sectionsCheckBoxStates = new HashMap<>();

  private Map<GraphNode, BooleanProperty> nodesCheckBoxStates;

  public DepanFxNodeListViewer(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeList nodeList,
      List<DepanFxNodeListSection> sections) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.nodeList = nodeList;
    this.sections = sections;

    nodesCheckBoxStates = buildNodesCheckBoxStates(nodeList.getNodes());
    nodeListTable = createTable();
  }

  public Tab createWorkspaceTab(String tabName) {
    Tab result = new Tab(tabName, nodeListTable);
    result.setContextMenu(buildViewContextMenu());
    return result;
  }

  public Optional<DepanFxWorkspaceResource> saveNodeList(
      DepanFxProjectDocument projDoc) {
    DepanFxNodeList saveList =
        DepanFxNodeLists.buildRelatedNodeList(nodeList, getSelectedNodes());
    try {
      return getWorkspace().saveDocument(projDoc, saveList);
    } catch (Exception errAny) {
      throw new RuntimeException(
          "Unable to save nodelist " + projDoc.getMemberPath(), errAny);
    }
  }

  public DepanFxWorkspace getWorkspace() {
    return workspace;
  }

  public GraphDocument getGraphDoc() {
    return (GraphDocument) nodeList.getWorkspaceResource().getResource();
  }

  public ContextModelId getContextModelId() {
    return getGraphDoc()
        .getContextModelId();
  }

  public <T> Dialog<T> buildDialog(Class<T> controllerType) {
    return dialogRunner.createDialogAndParent(controllerType);
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

  /////////////////////////////////////
  // Internal

  private void runSaveNodeListDialog() {
    Dialog<DepanFxSaveNodeListDialog> saveDlg =
        dialogRunner.createDialogAndParent(DepanFxSaveNodeListDialog.class);
    saveDlg.getController().setNodeListView(this);
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
