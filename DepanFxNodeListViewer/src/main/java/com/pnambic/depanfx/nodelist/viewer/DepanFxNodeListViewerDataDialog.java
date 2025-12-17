/*
 * Copyright 2025 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.nodelist.viewer;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListChooser;
import com.pnambic.depanfx.nodelist.gui.DepanFxSaveNodeListDialog;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableViewSaveDialog;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.nodelist.viewdata.DepanFxNodeListViewerData;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;

@DepanFxFxmlDialog
@FxmlView("node-list-viewer-data-dialog.fxml")
public class DepanFxNodeListViewerDataDialog {

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private TextField viewerTitleField;

  @FXML
  private TextField nodeListField;

  @FXML
  private TextField tableViewField;

  private DepanFxNodeListChooser.NodeListControl nodeListControl;

  private DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc;

  private Optional<DepanFxNodeListViewerData> viewerData = Optional.empty();

  @Autowired
  public DepanFxNodeListViewerDataDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
  }

  public static Optional<DepanFxNodeListViewerData> runEditDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner) {

    Dialog<DepanFxNodeListViewerDataDialog> editDlg =
        dialogRunner.createDialogAndParent(DepanFxNodeListViewerDataDialog.class);
    editDlg.runDialog("Node List Viewer");
    return editDlg.getController().getViewerData();
  }

  public static Optional<DepanFxNodeListViewerData> runEditDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeListViewerData initialData) {

    Dialog<DepanFxNodeListViewerDataDialog> editDlg =
        dialogRunner.createDialogAndParent(DepanFxNodeListViewerDataDialog.class);
    editDlg.getController().setViewerData(initialData);
    editDlg.runDialog("Node List Viewer");
    return editDlg.getController().getViewerData();
  }

  public Scene getScene() {
    return viewerTitleField.getScene();
  }

  @FXML
  public void initialize() {
    nodeListControl = new DepanFxNodeListChooser.NodeListControl(
        workspace, dialogRunner, nodeListField);
  }

  public Optional<DepanFxNodeListViewerData> getViewerData() {
    return viewerData;
  }

  public void setViewerData(DepanFxNodeListViewerData viewerData) {
    viewerTitleField.setText(viewerData.getViewerTitle());
    setNodeListResource(viewerData.getNodeListRsrc());
    setTableViewResource(viewerData.getTableViewRsrc());
    this.viewerData = Optional.of(viewerData);
  }

  @FXML
  public void onSelectNodeList() {
    DepanFxNodeListChooser.runNodeListChooser(workspace, dialogRunner, getScene())
        .ifPresent(this::setNodeListResource);
  }

  @FXML
  public void onSaveNodeList() {
    Optional.ofNullable(nodeListControl.getNodeListResource())
        .flatMap(r -> DepanFxSaveNodeListDialog.runSaveNodeList(dialogRunner, r))
        .ifPresent(this::setNodeListResource);
  }

  @FXML
  public void onSelectTableView() {
    DepanFxNodeListTableViewSaveDialog.runTableViewChooser(
        workspace, dialogRunner, getScene())
        .ifPresent(this::setTableViewResource);
  }

  @FXML
  public void onCancel() {
    viewerData = Optional.empty();
    close();
  }

  @FXML
  public void onConfirm() {
    if (hasSelections()) {
      viewerData = Optional.of(new DepanFxNodeListViewerData(
          viewerTitleField.getText(),
          nodeListControl.getNodeListResource(),
          tableViewRsrc));
    } else {
      viewerData = Optional.empty();
    }
    close();
  }

  private void close() {
    viewerTitleField.getScene().getWindow().hide();
  }

  private boolean hasSelections() {
    return nodeListControl.getNodeListResource() != null && tableViewRsrc != null;
  }

  private void setNodeListResource(
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {
    nodeListControl.setNodeListResource(nodeListRsrc);
    if (viewerTitleField.getText().isBlank()) {
      viewerTitleField.setText(
          DepanFxWorkspaceFactory.buildDocTitle(nodeListRsrc.getDocument()));
    }
  }

  private void setTableViewResource(
      DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc) {
    this.tableViewRsrc = tableViewRsrc;
    tableViewField.setText(
        DepanFxProjects.asSaveLabel(workspace, tableViewRsrc));
  }
}
