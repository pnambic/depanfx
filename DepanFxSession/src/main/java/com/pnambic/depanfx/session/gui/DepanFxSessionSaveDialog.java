/*
 * Copyright 2024 The Depan Project Authors
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
package com.pnambic.depanfx.session.gui;

import com.pnambic.depanfx.perspective.DepanFxDialogChecks;
import com.pnambic.depanfx.perspective.DepanFxProctor;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.session.core.DepanFxSession;
import com.pnambic.depanfx.session.core.DepanFxSessionDataTransport;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

@Component
@FxmlView("session-save-dialog.fxml")
public class DepanFxSessionSaveDialog {

  private final DepanFxSession session;

  private final DepanFxSessionDataTransport transport;

  @FXML
  private Label sessionDetailsLabel;

  @FXML
  private TextField destinationField;

  @Autowired
  public DepanFxSessionSaveDialog(
      DepanFxSession session,
      DepanFxSessionDataTransport transport) {
    this.session = session;
    this.transport = transport;
    }

  public static Dialog<DepanFxSessionSaveDialog> runSaveSessionDialog(
      DepanFxDialogRunner dialogRunner) {
    Dialog<DepanFxSessionSaveDialog> dlg =
        dialogRunner.createDialogAndParent(DepanFxSessionSaveDialog.class);
    dlg.runDialog("Save Session");
    return dlg;
  }

  @FXML
  private void openFileChooser() {
    FileChooser fileChooser = prepareFileChooser();
    File selectedFile =
        fileChooser.showSaveDialog(destinationField.getScene().getWindow());
    if (selectedFile != null) {
      destinationField.setText(selectedFile.getAbsolutePath());
    }
  }

  @FXML
  private void handleCancel() {
    closeDialog();
  }

  @FXML
  private void handleConfirm() {
    DepanFxProctor proctor = new DepanFxProctor.Simple();
    DepanFxDialogChecks.checkDestinationFile(
        proctor, destinationField.getText());
    if (DepanFxResourcePerspectives.errorAlert(
        proctor, "Session Save Confirmation Error")) {
      return;
    }

    closeDialog();

    File dstFile = new File(destinationField.getText());
    transport.saveSession(dstFile.toPath(), session);
  }

  private void closeDialog() {
    ((Stage) destinationField.getScene().getWindow()).close();
  }

  private FileChooser prepareFileChooser() {
    String baseName = DepanFxSessionDataTransport.DEPAN_FX_SESSION_LABEL;
    FileChooser result =
        DepanFxSceneControls.prepareFileChooser(
            destinationField,
            () -> new File(
                buildTimestampName(
                    baseName, DepanFxSessionDataTransport.YAML_EXT)));
    result.getExtensionFilters().add(DepanFxSessionDataTransport.YAML_FILTER);
    result.setSelectedExtensionFilter(DepanFxSessionDataTransport.YAML_FILTER);

    return result;
  }

  private String buildTimestampName(String prefix, String ext) {
    return DepanFxWorkspaceFactory.buildDocumentTimestampName(prefix, ext);
  }
}
