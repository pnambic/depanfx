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
package com.pnambic.depanfx.nodelist.gui.sections.folds;

import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.text.MessageFormat;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@DepanFxFxmlDialog
@FxmlView("node-fold-tool-dialog.fxml")
public class DepanFxNodeFoldToolDialog
    extends DepanFxBaseToolDialog<DepanFxNodeFoldData>{

  public static final ExtensionFilter NODE_FOLD_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Node Folding", DepanFxNodeFoldData.NODE_FOLD_TOOL_EXT);

  @SuppressWarnings("unused")
  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private Label nodeFoldDetailsLabel;

  @Autowired
  public DepanFxNodeFoldToolDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, DepanFxNodeFoldData.class);
    this.dialogRunner = dialogRunner;
  }

  public static Dialog<DepanFxNodeFoldToolDialog> runCreateDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxWorkspaceResource<DepanFxNodeFoldData> storeRsrc) {
    Dialog<DepanFxNodeFoldToolDialog> result =
        DepanFxResourcePerspectives.prepareDialog(
            storeRsrc, dialogRunner,
            DepanFxNodeFoldToolDialog.class);
    result.getController().setToolResource(storeRsrc);
    result.runDialog(null);
    return result;
  }

  @FXML
  public void initialize() {
  }

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxNodeFoldData> toolRsrc) {
    super.setToolResource(toolRsrc);

    DepanFxNodeFoldData foldInfo = toolRsrc.getResource();
    GraphDocument graphDoc = foldInfo.getGraphDocResource().getResource();
    nodeFoldDetailsLabel.setText(
        MessageFormat.format(
            "Node folding for graph {0}. {1}",
            graphDoc.getGraphName(), graphDoc.getGraphDescription()));
  }

  @Override
  protected DepanFxNodeFoldData prepareResult() {
    return getToolResource()
        .map(r -> r.getResource())
        .map(d -> d.forUpdate(getToolName(), getToolDescription()))
        .orElse(null);
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(NODE_FOLD_FILTER);
    result.setSelectedExtensionFilter(NODE_FOLD_FILTER);
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxNodeFoldData.NODE_FOLD_TOOL_EXT,
        DepanFxProjects.ANALYZES_PATH);
  }

  @Override
  protected String getInputCheckFailureText() {
    return "Node folding Save Confirmation Error";
  }
}
