package com.pnambic.depanfx.nodeview.gui;

import com.google.common.collect.ImmutableList;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListCell;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListConfiguration;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListRoot;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListRootItem;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableCommands;
import com.pnambic.depanfx.nodelist.gui.DepanFxSaveNodeListDialog;
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
import com.pnambic.depanfx.perspective.DepanFxWorkspaceDialog;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

  private final DepanFxNodeListTableCommands tableCommands;

  private DepanFxNodeListTableViewData tableView;

  private List<DepanFxWorkspaceResource<? extends DepanFxBaseSectionData>>
      sectionResources;

  private List<DepanFxWorkspaceResource<? extends DepanFxBaseColumnData>>
      columnResources;

  private List<DepanFxNodeListSection> sections;

  private List<DepanFxNodeListColumn> columns;

  @FXML
  private Label nodeTableCommands;

  @FXML
  private TreeTableView<DepanFxNodeListMember> nodeSelectTable;

  // Bound in initialize().
  private TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> nameColumn;

  /**
   * Where live changes happen.
   * Source of the displayed node list, and the keeper of the current selection.
   */
  private DepanFxNodeViewPanel viewPanel;

  private DepanFxProjectDocument destDoc;

  public DepanFxNodeViewNodeSelectDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner) {
    super(workspace);
    tableCommands =
        new DepanFxNodeListTableCommands(workspace, dialogRunner, this);
  }

  /**
   * Node Selection Editor is a modeless dialog coupled to the graph view.
   */
  static Stage runEditDialog(
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeViewPanel viewPanel,
      DepanFxNodeListTableViewData tableView,
      DepanFxProjectDocument destDoc) {

    Dialog<DepanFxNodeViewNodeSelectDialog> dlg =
        dialogRunner.createDialogAndParent(
            DepanFxNodeViewNodeSelectDialog.class);
    dlg.getController().setDestinationDocument(destDoc);
    dlg.getController().setTableView(tableView);
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
    nodeTableCommands.setContextMenu(tableCommands.buildViewContextMenu());

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

  public void setDestinationDocument(DepanFxProjectDocument destDoc) {
    this.destDoc = destDoc;
  }

  public void setViewPanel(DepanFxNodeViewPanel viewPanel) {
    this.viewPanel = viewPanel;
    if (tableView == null) {
      Optional<DepanFxWorkspaceResource<DepanFxNodeListTableViewData>> optFlatView =
          ((DepanFxBuiltInProject) workspace.getBuiltInProject())
              .getResource(DepanFxNodeListConfiguration.FLAT_TABLE_VIEW_PATH);
      optFlatView.ifPresent(r -> tableView = r.getResource());
    }
    populateNodeTable();
  }

  private void populateNodeTable() {

    // Reset the table columns.
    nodeSelectTable.getColumns().clear();
    nodeSelectTable.getColumns().add(nameColumn);

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

  private TreeItem<DepanFxNodeListMember> createTreeRoot() {
    DepanFxNodeList nodeList = viewPanel.buildViewNodesAsNodeList();
    DepanFxNodeListRoot rootMember = new DepanFxNodeListRoot(
        getWorkspace(), nodeList, ImmutableList.copyOf(sections));
    return new DepanFxNodeListRootItem(rootMember);
  }

  private void installSection(
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> sectionRsrc) {
    DepanFxNodeListSection result =
        DepanFxSectionRegistry.createSection(this, sectionRsrc);

    sections.add(result);
    sectionResources.add(sectionRsrc);
  }

  /////////////////////////////////////
  // Workspace dialog protected overrides

  @Override // DepanFxWorkspaceDialog, DepanFxNodeListTableAdapter
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
  public void setTableView(DepanFxNodeListTableViewData tableView) {
    this.tableView = tableView;
    if (viewPanel != null) {
      populateNodeTable();
    }
  }

  @Override // DepanFxNodeListTableAdapter
  public DepanFxNodeListTableViewData getTableView() {
    return new DepanFxNodeListTableViewData(
        tableView.getToolName(), tableView.getToolDescription(),
        sectionResources, columnResources);
  }

  @Override // DepanFxNodeListTableAdapter
  public Stream<DepanFxNodeListColumn> streamColumns() {
    return columns.stream();
  }

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
  public void addColumn(
      DepanFxWorkspaceResource<? extends DepanFxBaseColumnData> columnRsrc) {

    columnResources.add(columnRsrc);
    DepanFxNodeListColumn column =
        DepanFxColumnRegistry.toColumn(this, columnRsrc);
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

  @Override // DepanFxNodeListTableAdapter
  public void refreshTableView() {
    nodeSelectTable.refresh();
  }

  @Override
  public void updateSection(DepanFxNodeListSection section,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> dataRsrc) {
    if (DepanFxSectionRegistry.updateSection(section, dataRsrc)) {
      resetTableRoot();
    }
  }

  private void resetTableRoot() {
    TreeItem<DepanFxNodeListMember> treeRoot = createTreeRoot();
    nodeSelectTable.setRoot(treeRoot);
  }

  private DepanFxNodeListSection installSectionAt(int index,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> sectionRsrc) {
    DepanFxNodeListSection result =
        DepanFxSectionRegistry.createSection(this, sectionRsrc);

    sections.add(index, result);
    sectionResources.add(index, sectionRsrc);

    return result;
  }
}
