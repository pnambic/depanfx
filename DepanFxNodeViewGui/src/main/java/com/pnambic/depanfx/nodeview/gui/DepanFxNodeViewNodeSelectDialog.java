package com.pnambic.depanfx.nodeview.gui;

import com.google.common.collect.ImmutableList;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListCell;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListCellAdapter;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListRoot;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListRootItem;
import com.pnambic.depanfx.nodelist.gui.DepanFxSaveNodeListDialog;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxFlatSection;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.perspective.DepanFxBaseDocumentDialog;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
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
    extends DepanFxBaseDocumentDialog<DepanFxNodeList>
    implements DepanFxNodeListCellAdapter {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeViewNodeSelectDialog.class);

  public static final String EDIT_NODE_SELECTION = "Edit Node Selection...";

  public static final String NEW_LINK_DISPLAY = "New Link Display...";

  private static final ExtensionFilter NODE_LIST_FILTER =
      DepanFxSaveNodeListDialog.EXT_FILTER;

  private final DepanFxDialogRunner dialogRunner;

  private List<DepanFxNodeListSection> sections = new ArrayList<>();

  @FXML
  private TreeTableView<DepanFxNodeListMember> nodeSelectTable;

  // For now, compliance with DepanFxNodeListCellAdapter interface
  private List<DepanFxNodeListColumn> columns = Collections.emptyList();

  // Bound in initialize(), name column is not an info column.
  private TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> nameColumn;

  /**
   * Where live changes happen.
   * Source of the displayed node list, and the keeper of the current selection.
   */
  private DepanFxNodeViewPanel viewPanel;

  public DepanFxNodeViewNodeSelectDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, DepanFxNodeList.class);
    this.dialogRunner = dialogRunner;
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
    dlg.getController().setDestination(projDoc);
    return dlg.runModeless(EDIT_NODE_SELECTION);
  }

  public static void setNodeViewNodeListFilters(FileChooser chooser) {
    chooser.getExtensionFilters().add(NODE_LIST_FILTER);
    chooser.setSelectedExtensionFilter(NODE_LIST_FILTER);
  }

  @FXML
  @SuppressWarnings("unchecked")
  public void initialize() {
    // In FXML
    // nodeSelectTable.setShowRoot(false);
    // nodeSelectTable.setEditable(true);
    nodeSelectTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    // In FXML
    // nameColumn.setPrefWidth(DepanFxSceneControls.layoutWidthMs(30));
    nameColumn = (TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember>)
        nodeSelectTable.getColumns().get(0);
  }

  public void setViewPanel(DepanFxNodeViewPanel viewPanel) {
    this.viewPanel = viewPanel;
    populateNodeTable();
  }

  private void populateNodeTable() {
    nodeSelectTable.setRoot(createTreeRoot());

    nameColumn.setCellFactory(c -> new DepanFxNodeListCell(this));
    nameColumn.setCellValueFactory(
        p -> new ReadOnlyObjectWrapper<DepanFxNodeListMember>(
            p.getValue().getValue()));

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
  // Tool Dialog protected overrides

  /**
   * Provides the set of selected nodes as a node list.
   */
  @Override
  protected DepanFxNodeList prepareResult() {
    return viewPanel.buildSelectedAsNodeList();
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxNodeList.NODE_LIST_EXT, DepanFxProjects.ANALYSES_PATH);
  }

  @Override
  protected void setTooldataFilters(FileChooser chooser) {
    chooser.getExtensionFilters().add(DepanFxSaveNodeListDialog.EXT_FILTER);
    chooser.setSelectedExtensionFilter(DepanFxSaveNodeListDialog.EXT_FILTER);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Node Selection Save Confirmation Error";
  }

  /////////////////////////////////////
  // FXML handlers.

  @FXML
  protected void handleRevert() {
    closeDialog();
    // viewPanel.revertNodeSelection();
  }

  @FXML
  protected void handleApply() {
    closeDialog();
    // viewPanel.setNodeSelection(toolData);
  }

  @Override
  @FXML
  protected void handleConfirm() {
    super.handleConfirm();
    // getWorkspaceResource().ifPresent(viewPanel::setNodeSelection);
  }

  ////////////////////////////////////////////
  // DepanFxNodeListCellAdapter

  @Override
  public GraphDocument getGraphDoc() {
    return viewPanel.getGraphDoc();
  }

  @Override
  public Stream<DepanFxNodeListColumn> streamColumns() {
    return columns.stream();
  }

  @Override
  public void insertSection(
      DepanFxNodeListSection before, DepanFxNodeListSection insert) {

    // Don't default to after the last slot, 'cuz that's the catch-all section
    int index = Integer.max(0, sections.indexOf(before));
    insertSection(index, insert);
  }

  @Override
  public void resetView() {
    TreeItem<DepanFxNodeListMember> treeRoot = createTreeRoot();
    nodeSelectTable.setRoot(treeRoot);
  }

  @Override
  public DepanFxDialogRunner getDialogRunner() {
    return viewPanel.getDialogRunner();
  }

  @Override
  public <T> Dialog<T> buildDialog(Class<T> controllerType) {
    return viewPanel.buildDialog(controllerType);
  }

  @Override
  public void doSelectGraphNodeAction(GraphNode selectNode, boolean value) {
    viewPanel.doSelectGraphNodeAction(selectNode, value);
  }

  @Override
  public void doSelectGraphNodesAction(
      Stream<GraphNode> nodes, boolean value) {
    viewPanel.doSelectGraphNodesAction(nodes, value);
  }

  @Override
  public ObservableValue<Boolean> getCheckBoxObservable(
      DepanFxNodeListMember member) {
    return viewPanel.getCheckBoxObservable(member);
  }

  @Override
  public TreeItem<DepanFxNodeListMember> getTreeItem(int intValue) {
    TreeItem<DepanFxNodeListMember> item =
        nodeSelectTable.getTreeItem(intValue);
    return item;
  }

  private void insertSection(int index, DepanFxNodeListSection insert) {
    // Don't default to after the last slot, 'cuz that's the catch-all section
    sections.add(index, insert);

    resetView();
  }

  @Override
  protected String getDocumentName() {
    return viewPanel.getToolName() + " selection";
  }
}
