package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@DepanFxFxmlDialog
@FxmlView("node-list-table-view-save-dialog.fxml")
public class DepanFxNodeListTableViewSaveDialog
    extends DepanFxBaseToolDialog<DepanFxNodeListTableViewData> {

  public static final String SAVE_TABLE_VIEW =
      "Save Table view...";

  public static final String SELECT_TABLE_VIEW =
      "Select Table view...";

  public static final ExtensionFilter EXT_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Table View", DepanFxNodeListTableViewData.TABLE_VIEW_TOOL_EXT);

  public static final DepanFxResourceFilter TABLE_VIEW_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Table View", DepanFxNodeListTableViewData.TABLE_VIEW_TOOL_EXT,
          DepanFxNodeListTableViewData.class);

  @FXML
  private Label tableDetailsLabel;

  @Autowired
  public DepanFxNodeListTableViewSaveDialog(DepanFxWorkspace workspace) {
    super(workspace, DepanFxNodeListTableViewData.class);
  }

  public static Optional<DepanFxWorkspaceResource<DepanFxNodeListTableViewData>>
  runSaveTableView(
      DepanFxDialogRunner dialogRunner,
      DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc) {

    Dialog<DepanFxNodeListTableViewSaveDialog> saveDlg =
        DepanFxResourcePerspectives.runCreateDialog(
            tableViewRsrc, dialogRunner,
            DepanFxNodeListTableViewSaveDialog.class,
            "Save node list table view");
    return saveDlg.getController().getToolResource();
  }

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc) {
    super.setToolResource(tableViewRsrc);

    tableDetailsLabel.setText(buildDetailsLabel(tableViewRsrc.getResource()));
  }

  /////////////////////////////////////
  // Base Document Dialog protected overrides

  @Override
  protected DepanFxNodeListTableViewData prepareResult() {
    DepanFxNodeListTableViewData tableView =
        getToolResource().get().getResource();

    return new DepanFxNodeListTableViewData(
        getToolName(), getToolDescription(),
        tableView.getSectionResources(),
        tableView.getColumnResources());
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

  private String buildDetailsLabel(DepanFxNodeListTableViewData tableView) {
    String sectionNames = tableView.getSectionResources().stream()
        .map(r -> r.getResource().getToolName())
        .collect(Collectors.joining(", "));
    String columnNames = tableView.getColumnResources().stream()
        .map(r -> r.getResource().getToolName())
        .collect(Collectors.joining(", "));
    return MessageFormat.format(
        "Table view has {0} columns ({1}) and {2} columns ({3}).",
        tableView.getSectionResources().size(), sectionNames,
        tableView.getColumnResources().size(), columnNames);
  }
}
