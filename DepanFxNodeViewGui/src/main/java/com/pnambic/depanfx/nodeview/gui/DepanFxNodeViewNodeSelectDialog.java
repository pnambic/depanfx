package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.info.GraphNodeInfo.Listener;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.nodeinfo.DepanFxInfoRegistry;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableCommands;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableController;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListViewBuiltIns;
import com.pnambic.depanfx.nodelist.gui.DepanFxSaveNodeListDialog;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxColumnRegistry;
import com.pnambic.depanfx.nodelist.gui.columns.infos.DepanFxInfoColumnStore;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.perspective.DepanFxWorkspaceDialog;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

@DepanFxFxmlDialog
@FxmlView("node-view-node-select-dialog.fxml")
public class DepanFxNodeViewNodeSelectDialog
    extends DepanFxWorkspaceDialog {

  @SuppressWarnings("unused")
  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeViewNodeSelectDialog.class);

  public static final String EDIT_NODE_SELECTION = "Edit Node Selection";

  private static final ExtensionFilter NODE_LIST_FILTER =
      DepanFxSaveNodeListDialog.EXT_FILTER;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxColumnRegistry columnRegistry;

  private final DepanFxInfoRegistry infoRegistry;

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

  private DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc;

  private DepanFxNodeListTableController tableControl;

  @Autowired
  public DepanFxNodeViewNodeSelectDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxColumnRegistry columnRegistry,
      DepanFxInfoRegistry infoRegistry) {
    super(workspace);
    this.dialogRunner = dialogRunner;
    this.columnRegistry = columnRegistry;
    this.infoRegistry = infoRegistry;
  }

  /**
   * Node Selection Editor is a modeless dialog coupled to the graph view.
   */
  static Stage runEditDialog(
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeViewPanel viewPanel,
      DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc) {

    Dialog<DepanFxNodeViewNodeSelectDialog> dlg =
        dialogRunner.createDialogAndParent(
            DepanFxNodeViewNodeSelectDialog.class);
    dlg.getController().setTableViewResource(tableViewRsrc);
    dlg.getController().setViewPanel(viewPanel);
    return dlg.runModeless(EDIT_NODE_SELECTION);
  }

  private void setTableViewResource(
      DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc) {
    this.tableViewRsrc = tableViewRsrc;

    if (tableControl != null) {
      tableControl.setTableViewResource(tableViewRsrc);
    }
  }

  public static void setNodeViewNodeListFilters(FileChooser chooser) {
    chooser.getExtensionFilters().add(NODE_LIST_FILTER);
    chooser.setSelectedExtensionFilter(NODE_LIST_FILTER);
  }

  /**
   * @param viewPanel
   */
  public void setViewPanel(DepanFxNodeViewPanel viewPanel) {
    if (tableViewRsrc == null) {
      tableViewRsrc = DepanFxProjects.getBuiltIn(
          workspace, DepanFxNodeListTableViewData.class,
          DepanFxNodeListViewBuiltIns.FLAT_TABLE_VIEW_PATH)
          .get();
    }

    tableControl = new DepanFxNodeListTableController(
        workspace, dialogRunner, columnRegistry, infoRegistry,
        viewPanel.getViewNodesAsNodeList(),
        viewPanel.getNodeSelection(), nodeSelectTable);
    tableControl.addInfoStore(
        DepanFxNodeLocationData.class,
        new PanelLocationStore(viewPanel));
    tableControl.setTableViewResource(tableViewRsrc);

    nodeTableCommands.setContextMenu(buildContextMenu());
  }

  @FXML
  public void handleSaveSelection() {
    DepanFxSaveNodeListDialog.runSaveNodeList(
        dialogRunner,
        workspace.addScratchResource(tableControl.getSelection()));
  }

  /////////////////////////////////////
  // Workspace dialog protected overrides

  @Override // DepanFxWorkspaceDialog
  public Scene getScene() {
    return nodeSelectTable.getScene();
  }

  private ContextMenu buildContextMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    DepanFxNodeListTableCommands cmds = tableControl.buildTableCommands();
    cmds.addSelectItems(builder);
    cmds.addLoadSaveItems(builder);
    cmds.addTableViewItems(builder);
    return builder.build();
  }

  private static class PanelLocationStore implements DepanFxInfoColumnStore {

    private final DepanFxNodeViewPanel viewPanel;

    public PanelLocationStore(DepanFxNodeViewPanel viewPanel) {
      this.viewPanel = viewPanel;
    }

    @Override
    public Optional<?> getInfoProperty(GraphNode graphNode) {
      return Optional.ofNullable(viewPanel.getNodeLocation(graphNode));
    }

    @Override
    public void setPropertyValue(GraphNode graphNode, Object value) {
      if (value instanceof DepanFxNodeLocationData location) {
        viewPanel.updateNodeLocation(graphNode, location);
      }
    }

    @Override
    public void addInfoListener(GraphNode graphNode, Listener listener) {
      viewPanel.addLocationListener(graphNode, listener);
    }

    @Override
    public void removeInfoListener(GraphNode graphNode, Listener listener) {
      viewPanel.removeLocationListener(graphNode, listener);
    }
  }
}
