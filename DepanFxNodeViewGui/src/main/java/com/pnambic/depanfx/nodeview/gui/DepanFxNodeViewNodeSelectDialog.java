package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListConfiguration;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableCommands;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableController;
import com.pnambic.depanfx.nodelist.gui.DepanFxSaveNodeListDialog;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.perspective.DepanFxWorkspaceDialog;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

@Component
@FxmlView("node-view-node-select-dialog.fxml")
public class DepanFxNodeViewNodeSelectDialog
    extends DepanFxWorkspaceDialog {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeViewNodeSelectDialog.class);

  public static final String EDIT_NODE_SELECTION = "Edit Node Selection...";

  public static final String NEW_LINK_DISPLAY = "New Link Display...";

  private static final ExtensionFilter NODE_LIST_FILTER =
      DepanFxSaveNodeListDialog.EXT_FILTER;

  private final DepanFxDialogRunner dialogRunner;

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

  private DepanFxNodeListTableViewData tableView;

  private DepanFxNodeListTableController tableControl;

  @Autowired
  public DepanFxNodeViewNodeSelectDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner) {
    super(workspace);
    this.dialogRunner = dialogRunner;
  }

  /**
   * Node Selection Editor is a modeless dialog coupled to the graph view.
   */
  static Stage runEditDialog(
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeViewPanel viewPanel,
      DepanFxNodeListTableViewData tableView) {

    Dialog<DepanFxNodeViewNodeSelectDialog> dlg =
        dialogRunner.createDialogAndParent(
            DepanFxNodeViewNodeSelectDialog.class);
    dlg.getController().setTableView(tableView);
    dlg.getController().setViewPanel(viewPanel);
    return dlg.runModeless(EDIT_NODE_SELECTION);
  }

  public static void setNodeViewNodeListFilters(FileChooser chooser) {
    chooser.getExtensionFilters().add(NODE_LIST_FILTER);
    chooser.setSelectedExtensionFilter(NODE_LIST_FILTER);
  }

  public void setViewPanel(DepanFxNodeViewPanel viewPanel) {
    if (tableView == null) {
      Optional<DepanFxWorkspaceResource<DepanFxNodeListTableViewData>> optFlatView =
          ((DepanFxBuiltInProject) workspace.getBuiltInProject())
              .getResource(DepanFxNodeListConfiguration.FLAT_TABLE_VIEW_PATH);
      optFlatView.ifPresent(r -> tableView = r.getResource());
    }

    tableControl = new DepanFxNodeListTableController(
        workspace, dialogRunner,
        viewPanel.getViewNodesAsNodeList(), viewPanel.getNodeSelection(),
        tableView, nodeSelectTable);

    nodeTableCommands.setContextMenu(buildContextMenu());
  }

  public void setTableView(DepanFxNodeListTableViewData tableView) {
    this.tableView = tableView;

    if (tableControl != null) {
      tableControl.setTableView(tableView);
    }
  }

  @FXML
  public void handleSaveSelection() {
    DepanFxSaveNodeListDialog.runSaveNodeList(
        dialogRunner, tableControl.getSelection());
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
}
