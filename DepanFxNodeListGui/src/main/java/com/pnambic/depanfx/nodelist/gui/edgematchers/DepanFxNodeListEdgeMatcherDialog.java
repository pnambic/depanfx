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

package com.pnambic.depanfx.nodelist.gui.edgematchers;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListChooser.NodeListControl;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListEdgeMatcherData;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.perspective.DepanFxProctor;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import net.rgielen.fxweaver.core.FxmlView;

import java.io.File;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Create and edit node list link matchers.
 */
@DepanFxFxmlDialog
@FxmlView("node-list-edge-matcher-dialog.fxml")
public class DepanFxNodeListEdgeMatcherDialog
    extends DepanFxBaseToolDialog<DepanFxNodeListEdgeMatcherData> {

  private static final String NODE_LIST_EDGE_MATCHER_RESOURCE_FILTER =
      "Node List Edge Matcher";

  public static final ExtensionFilter NODE_LIST_EDGE_FILTER =
      DepanFxSceneControls.buildExtFilter(
          NODE_LIST_EDGE_MATCHER_RESOURCE_FILTER,
          DepanFxNodeListEdgeMatcherData.NODE_LIST_EDGE_MATCHER_TOOL_EXT);

  public static final DepanFxResourceFilter NODE_LIST_EDGE_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          NODE_LIST_EDGE_MATCHER_RESOURCE_FILTER,
          DepanFxNodeListEdgeMatcherData.NODE_LIST_EDGE_MATCHER_TOOL_EXT,
          DepanFxNodeListEdgeMatcherData.class);

  public static final String NEW_NODE_LIST_LINK_MATCHER_TITLE =
      "Create Node List Link Matcher";

  public static final String EDIT_NODE_LIST_LINK_MATCHER_TITLE =
      "Edit Node List Link Matcher";

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private TextField headNodeListField;

  private NodeListControl headNodeListControl;

  @FXML
  private TextField tailNodeListField;

  private NodeListControl tailNodeListControl;

  /**
   * Run dialog for creating a new node list link matcher.
   */
  public static Optional<DepanFxWorkspaceResource<DepanFxNodeListEdgeMatcherData>>
  runCreateDialog(
      DepanFxDialogRunner dialogRunner) {
    Dialog<DepanFxNodeListEdgeMatcherDialog> dlg =
        dialogRunner.createDialogAndParent(DepanFxNodeListEdgeMatcherDialog.class);
    dlg.runDialog(NEW_NODE_LIST_LINK_MATCHER_TITLE);
    return dlg.getController().getToolResource();
  }

  /**
   * Run dialog for editing an existing node list link matcher.
   */
  public static Optional<DepanFxWorkspaceResource<DepanFxNodeListEdgeMatcherData>>
  runEditDialog(
      DepanFxDialogRunner dialogRunner,
      DepanFxWorkspaceResource<DepanFxNodeListEdgeMatcherData> matcherRsrc) {
    Dialog<DepanFxNodeListEdgeMatcherDialog> dlg =
        dialogRunner.createDialogAndParent(DepanFxNodeListEdgeMatcherDialog.class);
    dlg.getController().setToolResource(matcherRsrc);
    dlg.runDialog(EDIT_NODE_LIST_LINK_MATCHER_TITLE);
    return dlg.getController().getToolResource();
  }

  public DepanFxNodeListEdgeMatcherDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner) {
    super(workspace, DepanFxNodeListEdgeMatcherData.class);
    this.dialogRunner = dialogRunner;
  }

  @FXML
  public void initialize() {
    headNodeListControl = new NodeListControl(
        getWorkspace(), dialogRunner, headNodeListField);
    tailNodeListControl = new NodeListControl(
        getWorkspace(), dialogRunner, tailNodeListField);
  }

  @Override
  public Scene getScene() {
    return headNodeListField.getScene();
  }

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxNodeListEdgeMatcherData> toolRsrc) {
    super.setToolResource(toolRsrc);
    setMatcherData(toolRsrc.getResource());
  }

  @FXML
  private void handleHeadBrowse() {
    headNodeListControl.runNodeListFinder();
  }

  @FXML
  private void handleTailBrowse() {
    tailNodeListControl.runNodeListFinder();
  }

  private void setMatcherData(DepanFxNodeListEdgeMatcherData matcherInfo) {
    DepanFxWorkspaceResource<DepanFxNodeList> headRsrc =
        matcherInfo.getHeadNodesResource();
    if (headRsrc != null) {
      headNodeListControl.setNodeListResource(headRsrc);
    }

    DepanFxWorkspaceResource<DepanFxNodeList> tailRsrc =
        matcherInfo.getTailNodesResource();
    if (tailRsrc != null) {
      headNodeListControl.setNodeListResource(headRsrc);
    }
  }

  @Override
  protected void checkInput(DepanFxProctor proctor) {
    // Either or both node lists may be empty, so little to check.
  }

  @Override
  protected String getInputCheckFailureText() {
    return "Node List Link Matcher Confirmation Error";
  }

  @Override
  protected DepanFxNodeListEdgeMatcherData prepareResult() {
    return new DepanFxNodeListEdgeMatcherData(
        getToolName(), getToolDescription(),
        headNodeListControl.getNodeListResource(),
        tailNodeListControl.getNodeListResource());
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(NODE_LIST_EDGE_FILTER);
    result.setSelectedExtensionFilter(NODE_LIST_EDGE_FILTER);
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxNodeListEdgeMatcherData.NODE_LIST_EDGE_MATCHER_TOOL_EXT,
        DepanFxProjects.TOOLS_PATH);
  }
}
