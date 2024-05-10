package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.MessageFormat;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
@FxmlView("node-list-table-view-save-dialog.fxml")
public class DepanFxNodeListTableViewSaveDialog
    extends DepanFxBaseToolDialog<DepanFxNodeListTableViewData> {

  public static final String SAVE_TABLE_VIEW =
      "Save Table view...";

  public static final ExtensionFilter EXT_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Table View", DepanFxNodeListTableViewData.TABLE_VIEW_TOOL_EXT);

  @FXML
  private Label tableDetailsLabel;

  private Optional<DepanFxWorkspaceResource<DepanFxNodeListTableViewData>>
      savedRsrc = Optional.empty();

  private DepanFxNodeListTableViewData tableView;

  @Autowired
  public DepanFxNodeListTableViewSaveDialog(DepanFxWorkspace workspace) {
    super(workspace, DepanFxNodeListTableViewData.class);
  }

  public static Optional<DepanFxWorkspaceResource<DepanFxNodeListTableViewData>>
      runSaveTableView(
          DepanFxDialogRunner dialogRunner,
          DepanFxNodeListTableViewData tableView) {

    Dialog<DepanFxNodeListTableViewSaveDialog> saveDlg =
        dialogRunner.createDialogAndParent(
             DepanFxNodeListTableViewSaveDialog.class);
    saveDlg.getController().setTableViewDoc(tableView);
    saveDlg.runDialog("Save node list table view");
    return saveDlg.getController().getSavedResource();
  }

  public void setTableViewDoc(DepanFxNodeListTableViewData tableView) {
    this.tableView = tableView;
    tableDetailsLabel.setText(buildDetailsLabel(tableView));
    super.setTooldata(tableView);
  }

  private String buildDetailsLabel(DepanFxNodeListTableViewData tableView) {
    return MessageFormat.format("Table view {0}: {1}.",
        tableView.getToolName(), tableView.getToolDescription());
  }

  public Optional<DepanFxWorkspaceResource<DepanFxNodeListTableViewData>>
      getSavedResource() {
        return savedRsrc;
  }

  /////////////////////////////////////
  // Base Document Dialog protected overrides

  @Override
  protected DepanFxNodeListTableViewData prepareResult() {
    return new DepanFxNodeListTableViewData(
        getToolName(), getToolDescription());
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxNodeListTableViewData.TABLE_VIEW_TOOL_EXT,
        DepanFxNodeListTableViewData.TABLE_VIEW_TOOL_PATH);
  }

  @Override
  protected void setTooldataFilters(FileChooser chooser) {
    chooser.getExtensionFilters().add(EXT_FILTER);
    chooser.setSelectedExtensionFilter(EXT_FILTER);
  }

  @Override
  protected String getInputCheckFailureText() {
    return "Node List Table View Save Confirmation Error";
  }
}
