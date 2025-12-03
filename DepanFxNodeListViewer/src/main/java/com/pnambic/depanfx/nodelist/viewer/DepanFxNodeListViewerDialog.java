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
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableViewChooser;
import com.pnambic.depanfx.nodelist.gui.DepanFxSaveNodeListDialog;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.viewdata.DepanFxNodeListViewerData;
import com.pnambic.depanfx.perspective.DepanFxBaseDialog;
import com.pnambic.depanfx.perspective.DepanFxProctor;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * The data type manipulated by this dialog ({@link DepanFxNodeListViewerData})
 * is not a document.  It is only a set of state elements as part of a scene.
 * The scene is saved as part of a session document (with at least a
 * destination, a name, and a description).
 *
 * A {@link DepanFxNodeListViewerData} is never persisted as an independent entity.
 */
@DepanFxFxmlDialog
@FxmlView("node-list-viewer-dialog.fxml")
public class DepanFxNodeListViewerDialog extends DepanFxBaseDialog {

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private TextField viewerTitleField;

  @FXML
  private TextField nodeListRsrcField;

  @FXML
  private TextField tableViewRsrcField;

  @FXML
  private Label saveNodeListLabel;

  private DepanFxNodeListChooser.NodeListControl nodeListControl;

  private DepanFxNodeListTableViewChooser.TableViewControl tableViewControl;

  private Optional<DepanFxNodeListViewerData> viewerInfo = Optional.empty();

  @Autowired
  public DepanFxNodeListViewerDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner) {
    super(workspace);
    this.dialogRunner = dialogRunner;
  }

  public static Optional<DepanFxNodeListViewerData> runEditDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeListViewerData viewerInfo) {

    Dialog<DepanFxNodeListViewerDialog> editDlg =
        dialogRunner.createDialogAndParent(DepanFxNodeListViewerDialog.class);
    editDlg.getController().setViewerData(viewerInfo);
    editDlg.runDialog("Node List Viewer");
    return editDlg.getController().getViewerData();
  }

  @Override // DepanFxWorkspaceDialog
  public Scene getScene() {
    return viewerTitleField.getScene();
  }

  @FXML
  public void initialize() {
    nodeListControl = new DepanFxNodeListChooser.NodeListControl(
        workspace, dialogRunner, nodeListRsrcField);
    tableViewControl = new DepanFxNodeListTableViewChooser.TableViewControl(
        workspace, dialogRunner, tableViewRsrcField);
    saveNodeListLabel.setVisible(false);
  }

  public Optional<DepanFxNodeListViewerData> getViewerData() {
    return viewerInfo;
  }

  public void setViewerData(DepanFxNodeListViewerData viewerData) {
    viewerTitleField.setText(viewerData.getViewerTitle());
    setNodeListResource(viewerData.getNodeListRsrc());
    tableViewControl.setTableViewResource(viewerData.getTableViewRsrc());
    this.viewerInfo = Optional.of(viewerData);
  }

  @FXML
  public void openNodeListChooser() {
    DepanFxNodeListChooser.runNodeListChooser(
        workspace, dialogRunner, getScene())
        .ifPresent(this::setNodeListResource);
  }

  @FXML
  public void openTableViewChooser() {
    DepanFxNodeListTableViewChooser.runTableViewChooser(
        workspace, dialogRunner, getScene())
        .ifPresent(tableViewControl::setTableViewResource);
  }

  @FXML
  public void handleNewNodeList() {
    DepanFxSaveNodeListDialog.runSaveNodeList(
        dialogRunner, viewerInfo.get().getNodeListRsrc())
        .ifPresent(this::setNodeListResource);
  }

  @FXML
  public void handleCancel() {
    this.viewerInfo = Optional.empty();
    closeDialog();
  }

  @FXML
  protected void handleOpen() {
    if (hasInputErrors()) {
      return;
    }
    closeDialog();

    viewerInfo = Optional.of(prepareResult());
  }

  @Override
  protected String getInputCheckFailureText() {
    return "Node List Viewer Confirmation Error";
  }

  @Override
  protected void checkInput(DepanFxProctor proctor) {
    if (viewerTitleField.getText().isBlank()) {
      proctor.addError("Blank viewer title",
          "The title of the viewer should not be blank.");
    }
    if (nodeListControl.getNodeListResource() == null) {
      proctor.addError("Blank node list",
          "The node list for the viewer is not valid.");
    }
    if (DepanFxWorkspaceResource.isUnsavedResource(
        nodeListControl.getNodeListResource(), workspace)) {
      proctor.addError("Unsaved node list",
          "The node list for the viewer must be saved.");
    }
    if (tableViewControl.getTableViewResource() == null) {
      proctor.addError("Blank table view",
          "The table for the view viewer is not valid.");
    }
  }

  private void setNodeListResource(
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {
    saveNodeListLabel.setVisible(
        DepanFxWorkspaceResource.isUnsavedResource(nodeListRsrc, workspace));
    nodeListControl.setNodeListResource(nodeListRsrc);
  }

  private DepanFxNodeListViewerData prepareResult() {
    return new DepanFxNodeListViewerData(
        viewerTitleField.getText(),
        nodeListControl.getNodeListResource(),
        tableViewControl.getTableViewResource());
  }
}
