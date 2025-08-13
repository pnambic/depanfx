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
package com.pnambic.depanfx.nodeview.layouts;

import com.pnambic.depanfx.edgematchers.gui.DepanFxLinkMatcherChooser;
import com.pnambic.depanfx.edgematchers.gui.DepanFxLinkMatcherChooser.LinkMatcherControl;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLayoutData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxTreeLayoutData;
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

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@DepanFxFxmlDialog
@FxmlView("tree-layout-tool-dialog.fxml")
public class DepanFxTreeLayoutToolDialog
    extends DepanFxBaseToolDialog<DepanFxTreeLayoutData> {

  public static final ExtensionFilter TREE_LAYOUT_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Tree Layout", DepanFxTreeLayoutData.TREE_LAYOUT_TOOL_EXT);

  public static final DepanFxResourceFilter TREE_LAYOUT_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Tree Layout", DepanFxTreeLayoutData.TREE_LAYOUT_TOOL_EXT,
          DepanFxTreeLayoutData.class);

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private TextField hierarchyMatcherRsrcField;

  private LinkMatcherControl hierarchyMatcherControl;

  @Autowired
  public DepanFxTreeLayoutToolDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, DepanFxTreeLayoutData.class);
    this.dialogRunner = dialogRunner;
  }

  public static Dialog<DepanFxTreeLayoutToolDialog> runEditDialog(
      DepanFxWorkspaceResource<DepanFxTreeLayoutData> treeLayoutRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runEditDialog(
        treeLayoutRsrc, dialogRunner,
        DepanFxTreeLayoutToolDialog.class,
        "Edit Tree Layout");
  }

  public static Dialog<DepanFxTreeLayoutToolDialog> runCreateDialog(
      DepanFxWorkspaceResource<DepanFxTreeLayoutData> treeLayoutRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runCreateDialog(
        treeLayoutRsrc, dialogRunner,
        DepanFxTreeLayoutToolDialog.class,
        "New Tree Layout");
  }

  @FXML
  public void initialize() {
    hierarchyMatcherControl = new DepanFxLinkMatcherChooser.LinkMatcherControl(
        getWorkspace(), dialogRunner, hierarchyMatcherRsrcField);
  }

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxTreeLayoutData> toolRsrc) {
    super.setToolResource(toolRsrc);

    hierarchyMatcherControl.setLinkMatcherResource(
        toolRsrc.getResource().getHierarchyMatcherRsrc());
  }

  @FXML
  private void handleBrowseLinkMatcher() {
    hierarchyMatcherControl.runLinkMatcherFinder();
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxTreeLayoutData prepareResult() {
    return new DepanFxTreeLayoutData(
        getToolName(), getToolDescription(),
        hierarchyMatcherControl.getLinkMatcherResource());
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxTreeLayoutData.TREE_LAYOUT_TOOL_EXT,
        DepanFxNodeViewLayoutData.LAYOUT_TOOL_PATH);
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(TREE_LAYOUT_FILTER);
    result.setSelectedExtensionFilter(TREE_LAYOUT_FILTER);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Tree Layout Save Confirmation Error";
  }
}
