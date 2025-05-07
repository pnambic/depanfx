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
package com.pnambic.depanfx.nodelist.gui.columns.annos;

import com.pnambic.depanfx.graph.nodeanno.DepanFxAnnotationIndexData;
import com.pnambic.depanfx.nodelist.gui.columns.annos.DepanFxAnnotationIndexChooser.AnnotationIndexControl;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxAnnotationStoreData;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
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

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@DepanFxFxmlDialog
@FxmlView("annotation-store-tool-dialog.fxml")
public class DepanFxAnnotationStoreToolDialog
    extends DepanFxBaseToolDialog<DepanFxAnnotationStoreData>{

  public static final ExtensionFilter ANNOTATION_STORE_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Annotation Store",
          DepanFxAnnotationStoreData.ANNOTATION_STORE_TOOL_EXT);

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private Label annotationStoreDetailsLabel;

  @FXML
  private TextField annotationIndexRsrcField;

  private AnnotationIndexControl annotationIndexControl;

  @Autowired
  public DepanFxAnnotationStoreToolDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, DepanFxAnnotationStoreData.class);
    this.dialogRunner = dialogRunner;
  }

  public static Dialog<DepanFxAnnotationStoreToolDialog> runCreateDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxWorkspaceResource<DepanFxAnnotationStoreData> storeRsrc) {
    Dialog<DepanFxAnnotationStoreToolDialog> result =
        DepanFxResourcePerspectives.prepareDialog(
            storeRsrc, dialogRunner,
            DepanFxAnnotationStoreToolDialog.class);
    result.getController().setToolResource(storeRsrc);
    result.runDialog(null);
    return result;
  }

  @FXML
  public void initialize() {
    annotationIndexControl =
        new DepanFxAnnotationIndexChooser.AnnotationIndexControl(
            workspace, dialogRunner, annotationIndexRsrcField);
  }

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxAnnotationStoreData> toolRsrc) {
    super.setToolResource(toolRsrc);

    annotationStoreDetailsLabel.setText(
        MessageFormat.format(
            "Node info for graph {0}.",
            toolRsrc.getDocument().getMemberName()));
  }

  @FXML
  private void handleBrowseAnnotationIndex() {
    annotationIndexControl.runAnnotationIndexFinder();
  }

  @Override
  protected DepanFxAnnotationStoreData prepareResult() {
    DepanFxWorkspaceResource<DepanFxAnnotationIndexData> annoIndexRsrc =
        getAnnoIndexRsrc();
    return getToolResource().get().getResource().buildUpdate(
        getToolName(), getToolDescription(), annoIndexRsrc );
  }

  private DepanFxWorkspaceResource<DepanFxAnnotationIndexData> getAnnoIndexRsrc() {
    return annotationIndexControl.getAnnotationIndexResource();
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(ANNOTATION_STORE_FILTER);
    result.setSelectedExtensionFilter(ANNOTATION_STORE_FILTER);
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxAnnotationStoreData.ANNOTATION_STORE_TOOL_EXT,
        DepanFxAnnotationIndexToolDialog.ANNOTATION_TOOL_PATH);
  }

  @Override
  protected String getInputCheckFailureText() {
    return "Annotation Store Save Confirmation Error";
  }
}
