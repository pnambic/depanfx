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
package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.nodeview.layouts.DepanFxLayoutsChooser;
import com.pnambic.depanfx.nodeview.layouts.DepanFxNodeLayoutRegistry;
import com.pnambic.depanfx.nodeview.viewdata.DepanFxNodeViewPanelInitData;
import com.pnambic.depanfx.perspective.DepanFxBaseDialog;
import com.pnambic.depanfx.perspective.DepanFxProctor;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import net.rgielen.fxweaver.core.FxmlView;

import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;

@DepanFxFxmlDialog
@FxmlView("node-view-init-dialog.fxml")
public class DepanFxNodeViewInitDialog extends DepanFxBaseDialog {

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxNodeLayoutRegistry layoutRegistry;

  @FXML
  private TextField nodeLayoutRsrcField;

  private DepanFxLayoutsChooser.LayoutControl layoutControl;

  private Optional<DepanFxNodeViewPanelInitData> initInfo =
      Optional.empty();

  public DepanFxNodeViewInitDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeLayoutRegistry layoutRegistry) {
    super(workspace);
    this.dialogRunner = dialogRunner;
    this.layoutRegistry = layoutRegistry;
  }

  public static Optional<DepanFxNodeViewPanelInitData> runEditDialog(
      DepanFxDialogRunner dialogRunner, DepanFxNodeViewPanelInitData initInfo) {
    Dialog<DepanFxNodeViewInitDialog> editDlg =
        dialogRunner.createDialogAndParent(DepanFxNodeViewInitDialog.class);
    editDlg.getController().setViewPanelInitData(initInfo);
    editDlg.runDialog("Node View Panel");
    return editDlg.getController().getViewPanelInitData();
  }

  @FXML
  public void initialize() {
    layoutControl = new DepanFxLayoutsChooser.LayoutControl(
        workspace, dialogRunner, nodeLayoutRsrcField, layoutRegistry);
  }

  public void setViewPanelInitData(DepanFxNodeViewPanelInitData initInfo) {
    this.initInfo = Optional.of(initInfo);
    layoutControl.setLayoutResource(initInfo.getLayoutRsrc());
  }

  public Optional<DepanFxNodeViewPanelInitData> getViewPanelInitData() {
    return initInfo;
  }

  @FXML
  public void openNodeLayoutChooser() {
    DepanFxLayoutsChooser.runLayoutFinder(
        workspace, dialogRunner, getScene(), layoutRegistry)
        .ifPresent(layoutControl::setLayoutResource);
  }

  @FXML
  public void handleCancel() {
    this.initInfo = Optional.empty();
    closeDialog();
  }

  @FXML
  protected void handleOpen() {
    if (hasInputErrors()) {
      return;
    }
    closeDialog();

    initInfo = Optional.of(prepareResult());
  }

  @Override
  protected String getInputCheckFailureText() {
    return "Node View Panel Confirmation Error";
  }

  @Override
  protected void checkInput(DepanFxProctor proctor) {
    if (layoutControl.getLayoutResource() == null) {
      proctor.addError("Invalid layout", "Initial layout is invalid.");
    }
  }

  @Override
  public Scene getScene() {
    return nodeLayoutRsrcField.getScene();
  }

  private DepanFxNodeViewPanelInitData prepareResult() {
    return new DepanFxNodeViewPanelInitData(
        layoutControl.getLayoutResource());
  }
}
