package com.pnambic.depanfx.nodeview.gui;

import com.google.common.collect.ImmutableList;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListCell;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableCommands;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListRoot;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListRootItem;
import com.pnambic.depanfx.nodelist.gui.DepanFxSaveNodeListDialog;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxFlatSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxTreeSection;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxFlatSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxTreeSectionData;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.perspective.DepanFxWorkspaceDialog;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

@Component
@FxmlView("node-view-node-select-dialog.fxml")
public class DepanFxNodeViewNodeSelectDialog
    extends DepanFxWorkspaceDialog
    implements DepanFxNodeListTableAdapter {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeViewNodeSelectDialog.class);

  public static final String EDIT_NODE_SELECTION = "Edit Node Selection...";

  public static final String NEW_LINK_DISPLAY = "New Link Display...";

  private static final ExtensionFilter NODE_LIST_FILTER =
      DepanFxSaveNodeListDialog.EXT_FILTER;

  private final DepanFxNodeListTableCommands tableCommand;

  @FXML
  private Label nodeTableCommands;

  @FXML
  private TreeTableView<DepanFxNodeListMember> nodeSelectTable;

  private List<DepanFxNodeListSection> sections = new ArrayList<>();

  private List<DepanFxNodeListColumn> columns = new ArrayList<>();

  // Bound in initialize().
  private TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> nameColumn;

  /**
   * Where live changes happen.
   * Source of the displayed node list, and the keeper of the current selection.
   */
  private DepanFxNodeViewPanel viewPanel;

  public DepanFxNodeViewNodeSelectDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace);
    tableCommand =
        new DepanFxNodeListTableCommands(workspace, dialogRunner, this);
  }

  /**
   * Node Selection Editor is a modeless dialog coupled to the graph view.
   */
  public static Stage runEditDialog(
      DepanFxNodeViewPanel viewPanel,
      DepanFxProjectDocument projDoc,
      DepanFxDialogRunner dialogRunner) {

    Dialog<DepanFxNodeViewNodeSelectDialog> dlg =
        dialogRunner.createDialogAndParent(
            DepanFxNodeViewNodeSelectDialog.class);
    dlg.getController().setViewPanel(viewPanel);
    return dlg.runModeless(EDIT_NODE_SELECTION);
  }

  public static void setNodeViewNodeListFilters(FileChooser chooser) {
    chooser.getExtensionFilters().add(NODE_LIST_FILTER);
    chooser.setSelectedExtensionFilter(NODE_LIST_FILTER);
  }

  @FXML
  @SuppressWarnings("unchecked")
  public void initialize() {
    nodeTableCommands.setContextMenu(tableCommand.buildViewContextMenu());

    // In FXML
    // nodeSelectTable.setShowRoot(false);
    // nodeSelectTable.setEditable(true);
    nodeSelectTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    // In FXML
    // nameColumn.setPrefWidth(DepanFxSceneControls.layoutWidthMs(30));
    nameColumn = (TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember>)
        nodeSelectTable.getColumns().get(0);
    nameColumn.setCellFactory(c -> new DepanFxNodeListCell(this));
    nameColumn.setCellValueFactory(
        p -> new ReadOnlyObjectWrapper<DepanFxNodeListMember>(
            p.getValue().getValue()));
  }

  public void setViewPanel(DepanFxNodeViewPanel viewPanel) {
    this.viewPanel = viewPanel;
    populateNodeTable();
  }

  private void populateNodeTable() {
    nodeSelectTable.setRoot(createTreeRoot());

    DepanFxNodeListSectionData.getBuiltinSimpleSectionResource(getWorkspace())
        .ifPresent(r -> sections.add(new DepanFxFlatSection(r)));
  }

  private TreeItem<DepanFxNodeListMember> createTreeRoot() {
    DepanFxNodeList nodeList = viewPanel.buildViewNodesAsNodeList();
    DepanFxNodeListRoot rootMember = new DepanFxNodeListRoot(
        getWorkspace(), nodeList, ImmutableList.copyOf(sections));
    return new DepanFxNodeListRootItem(rootMember);
  }

  /////////////////////////////////////
  // Workspace dialog protected overrides

  @Override // DepanFxWorkspaceDialog
  public Scene getScene() {
    return nodeSelectTable.getScene();
  }

  ////////////////////////////////////////////
  // DepanFxNodeListTableAdapter

  @Override // DepanFxNodeListTableAdapter
  public GraphDocument getGraphDoc() {
    return viewPanel.getGraphDoc();
  }

  @Override // DepanFxNodeListTableAdapter
  public Stream<DepanFxNodeListColumn> streamColumns() {
    return columns.stream();
  }

  @Override // DepanFxNodeListTableAdapter
  public void insertSection(
      DepanFxNodeListSection before, DepanFxNodeListSection insert) {

    // Don't default to after the last slot, 'cuz that's the catch-all section
    int index = Integer.max(0, sections.indexOf(before));
    sections.add(index, insert);
    resetTableRoot();
  }

  @Override // DepanFxNodeListTableAdapter
  public DepanFxDialogRunner getDialogRunner() {
    return viewPanel.getDialogRunner();
  }

  @Override // DepanFxNodeListTableAdapter
  public <T> Dialog<T> buildDialog(Class<T> controllerType) {
    return viewPanel.buildDialog(controllerType);
  }

  @Override // DepanFxNodeListTableAdapter
  public void doSelectGraphNodeAction(GraphNode selectNode, boolean value) {
    viewPanel.doSelectGraphNodeAction(selectNode, value);
  }

  @Override // DepanFxNodeListTableAdapter
  public void doSelectGraphNodesAction(
      Stream<GraphNode> nodes, boolean value) {
    viewPanel.doSelectGraphNodesAction(nodes, value);
  }

  @Override // DepanFxNodeListTableAdapter
  public ObservableValue<Boolean> getCheckBoxObservable(
      DepanFxNodeListMember member) {
    return viewPanel.getCheckBoxObservable(member);
  }

  @Override // DepanFxNodeListTableAdapter
  public TreeItem<DepanFxNodeListMember> getTreeItem(int intValue) {
    TreeItem<DepanFxNodeListMember> item =
        nodeSelectTable.getTreeItem(intValue);
    return item;
  }

  @Override // DepanFxNodeListTableAdapter
  public void addColumn(DepanFxNodeListColumn column) {
    columns.add(column);
    nodeSelectTable.getColumns().add(column.prepareColumn());
  }

  @Override // DepanFxNodeListTableAdapter
  public void doSelectAllAction() {
    viewPanel.doSelectAllAction();
  }

  @Override // DepanFxNodeListTableAdapter
  public void doClearSelectionAction() {
    viewPanel.doClearSelectionAction();
  }

  @Override // DepanFxNodeListTableAdapter
  public void doInvertSelectionAction() {
    viewPanel.doInvertSelectionAction();
  }

  @Override // DepanFxNodeListTableAdapter
  public DepanFxNodeList getSelection() {
    return viewPanel.buildSelectedAsNodeList();
  }

  private void resetTableRoot() {
    TreeItem<DepanFxNodeListMember> treeRoot = createTreeRoot();
    nodeSelectTable.setRoot(treeRoot);
  }

  @Override
  public void refreshTableView() {
    nodeSelectTable.refresh();
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
}
