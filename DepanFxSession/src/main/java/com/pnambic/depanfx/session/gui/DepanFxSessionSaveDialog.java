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
import java.nio.file.Path;
import java.text.MessageFormat;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

@Component
@FxmlView("session-save-dialog.fxml")
/**
 * A small cheat:  Since their is only one session and it its controlled by
 * dependency injection, use the injected value.  The managed session is never
 * passed to the dialog controller via a setter.
 */
public class DepanFxSessionSaveDialog {

  private static final String BLANK_DESTINATION = "";

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
  public void initialize() {
    sessionDetailsLabel.setText(buildSessionDetails());
    destinationField.setText(buildDestinationPath());
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
    Path dstPath = dstFile.toPath();
    transport.saveSession(dstPath, session);
    session.setSessionPath(dstPath);
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
                    baseName, DepanFxSessionDataTransport.XML_EXT)));
    result.getExtensionFilters().add(DepanFxSessionDataTransport.XML_FILTER);
    result.setSelectedExtensionFilter(DepanFxSessionDataTransport.XML_FILTER);

    return result;
  }

  private String buildSessionDetails() {
    StringBuilder result = new StringBuilder();

    int sceneCount = session.getScenes().size();
    if (sceneCount == 1) {
      result.append("The active session has 1 scene");
    } else {
      result.append(MessageFormat.format(
          "The active session has {0} scenes", sceneCount));
    }

    // Don't include the built-in project.
    int projectCount = session.getWorkspace().getProjectList().size() - 1;
    if (projectCount == 1) {
      result.append(" and the workspace has a single project.");
    } else {
      result.append(MessageFormat.format(
          " and the workspace has {0} projects.", projectCount));
    }

    return result.toString();
  }

  private String buildDestinationPath() {
    Path sessionPath = session.getSessionPath();
    if (sessionPath != null) {
      return sessionPath.toAbsolutePath().toString();
    }
    return BLANK_DESTINATION;
  }

  private String buildTimestampName(String prefix, String ext) {
    return DepanFxWorkspaceFactory.buildDocumentTimestampName(prefix, ext);
  }
}
