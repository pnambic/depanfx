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

import com.pnambic.depanfx.edgematchers.gui.DepanFxEdgeMatcherDialogRegistry;
import com.pnambic.depanfx.edgematchers.gui.DepanFxLinkMatcherChooser;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.StringConverter;

@DepanFxFxmlDialog
@FxmlView("tree-layout-tool-dialog.fxml")
public class DepanFxTreeLayoutToolDialog
    extends DepanFxBaseToolDialog<DepanFxTreeLayoutData> {

  // Translatable labels for directions in the UX.
  public static final String DIRECTION_DOWN_LABEL = "Down";

  public static final String DIRECTION_UP_LABEL = "Up";

  public static final String DIRECTION_LEFT_LABEL = "Left";

  public static final String DIRECTION_RIGHT_LABEL = "Right";

  public static final ExtensionFilter TREE_LAYOUT_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Tree Layout", DepanFxTreeLayoutData.TREE_LAYOUT_TOOL_EXT);

  public static final DepanFxResourceFilter TREE_LAYOUT_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Tree Layout", DepanFxTreeLayoutData.TREE_LAYOUT_TOOL_EXT,
          DepanFxTreeLayoutData.class);

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry;

  @FXML
  private TextField hierarchyMatcherRsrcField;

  private DepanFxLinkMatcherChooser.LinkMatcherControl hierarchyMatcherControl;

  @FXML
  private ChoiceBox<DepanFxTreeLayoutData.Direction> treeDirectionChoiceBox;

  @Autowired
  public DepanFxTreeLayoutToolDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxEdgeMatcherDialogRegistry matcherDialogRegistry) {
    super(workspace, DepanFxTreeLayoutData.class);
    this.dialogRunner = dialogRunner;
    this.matcherDialogRegistry = matcherDialogRegistry;
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
    hierarchyMatcherControl =
        new DepanFxLinkMatcherChooser.LinkMatcherControl(
            getWorkspace(), dialogRunner,
            matcherDialogRegistry, hierarchyMatcherRsrcField);

    treeDirectionChoiceBox.getItems().addAll(
        DepanFxTreeLayoutData.Direction.values());
    treeDirectionChoiceBox.setConverter(new DirectionConverter());
  }

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxTreeLayoutData> toolRsrc) {
    super.setToolResource(toolRsrc);

    treeDirectionChoiceBox.setValue(toolRsrc.getResource().getDirection());
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
        treeDirectionChoiceBox.getValue(),
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

  private static class DirectionConverter
      extends StringConverter<DepanFxTreeLayoutData.Direction> {

    @Override
    public String toString(DepanFxTreeLayoutData.Direction direction) {
      switch (direction) {
        case RIGHT: return DIRECTION_RIGHT_LABEL;
        case LEFT: return DIRECTION_LEFT_LABEL;
        case UP: return DIRECTION_UP_LABEL;
        case DOWN: return DIRECTION_DOWN_LABEL;
      }
      return null;
    }

    @Override
    public DepanFxTreeLayoutData.Direction fromString(String text) {
      return DepanFxTreeLayoutData.Direction.valueOf(text.toUpperCase());
    }
  }
}
