package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListConfiguration;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableCommands;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableFactory;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableState;
import com.pnambic.depanfx.nodelist.gui.DepanFxSaveNodeListDialog;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxNodeListSection;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListTableViewData;
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

import java.util.Optional;
import java.util.stream.Stream;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

  private DepanFxNodeListTableState tableState;

  /**
   * Place holder for the context menu.
   */
  @FXML
  private Label nodeTableCommands;

  /**
   * Let FXML place the table.  Other behavior is implemented by the
   * {@link}.
   */
  @FXML
  private TreeTableView<DepanFxNodeListMember> nodeSelectTable;

  /**
   * Where live changes happen.
   * Source of the displayed node list, and the keeper of the current selection.
   */
  private DepanFxNodeViewPanel viewPanel;

  private DepanFxProjectDocument destDoc;

  private DepanFxNodeListTableViewData tableView;

  public DepanFxNodeViewNodeSelectDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner) {
    super(workspace);
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

    tableState = new DepanFxNodeListTableState(
        workspace,
        viewPanel.getViewNodesAsNodeList(),
        viewPanel.getNodeSelection(),
        nodeSelectTable,
        new DepanFxNodeListTableFactory(this));
    tableState.setTableView(tableView);

    DepanFxNodeListTableCommands tableCommands =
        new DepanFxNodeListTableCommands(
            viewPanel.getWorkspace(),
            viewPanel.getDialogRunner(),
            tableState);
    nodeTableCommands.setContextMenu(tableCommands.buildViewContextMenu());
  }

  public void setTableView(DepanFxNodeListTableViewData tableView) {
    this.tableView = tableView;
    if (tableState != null) {
      tableState.setTableView(tableView);
    }
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
  public DepanFxDialogRunner getDialogRunner() {
    return viewPanel.getDialogRunner();
  }

  @Override // DepanFxNodeListTableAdapter
  public ObservableValue<Boolean> getCheckBoxObservable(int treeIndex) {
    return tableState.getCheckBoxObservable(treeIndex);
  }

  @Override // DepanFxNodeListTableAdapter
  public void doSelectGraphNodeAction(GraphNode selectNode, boolean value) {
    tableState.doSelectGraphNodeAction(selectNode, value);
  }

  @Override // DepanFxNodeListTableAdapter
  public void doSelectGraphNodesAction(
      Stream<GraphNode> nodes, boolean value) {
    tableState.doSelectGraphNodesAction(nodes, value);
  }

  @Override // DepanFxNodeListTableAdapter
  public void refreshTableView() {
    tableState.refreshTableView();
  }

  @Override
  public Stream<DepanFxNodeListColumn> streamColumns() {
    return tableState.streamColumns();
  }

  @Override // DepanFxNodeListTableAdapter
  public DepanFxNodeListSection insertSection(
      DepanFxNodeListSection before,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> sectionRsrc) {
    return tableState.insertSection(before, sectionRsrc);
  }

  @Override // DepanFxNodeListTableAdapter
  public void updateSection(DepanFxNodeListSection section,
      DepanFxWorkspaceResource<? extends DepanFxBaseSectionData> dataRsrc) {
    tableState.updateSection(section, dataRsrc);
  }
}
