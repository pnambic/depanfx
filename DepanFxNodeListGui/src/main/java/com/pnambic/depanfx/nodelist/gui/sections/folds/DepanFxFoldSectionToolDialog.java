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
import com.pnambic.depanfx.nodelist.gui.sections.DepanFxBaseSectionToolDialog;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxContainerOrder;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxFoldSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@DepanFxFxmlDialog
@FxmlView("fold-section-tool-dialog.fxml")
public class DepanFxFoldSectionToolDialog
    extends DepanFxBaseSectionToolDialog<DepanFxFoldSectionData> {

  public static final ExtensionFilter FOLD_SECTION_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Fold Sections", DepanFxFoldSectionData.FOLD_SECTION_TOOL_EXT);

  public static final DepanFxResourceFilter FOLD_SECTION_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Fold Sections",
          DepanFxFoldSectionData.FOLD_SECTION_TOOL_EXT,
          DepanFxFoldSectionData.class);

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private TextField nodeFoldingResourceField;

  private DepanFxNodeFoldChooser.NodeFoldingControl foldNestControl;

  @FXML
  private CheckBox inferMissingParentsField;

  @FXML
  private ComboBox<OrderBy> orderByField;

  @FXML
  private ComboBox<DepanFxContainerOrder> containerOrderField;

  @Autowired
  public DepanFxFoldSectionToolDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, DepanFxFoldSectionData.class);
    this.dialogRunner = dialogRunner;
  }

  public static Dialog<DepanFxFoldSectionToolDialog> runEditDialog(
      DepanFxWorkspaceResource<DepanFxFoldSectionData> sectionRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runEditDialog(
        sectionRsrc, dialogRunner,
        DepanFxFoldSectionToolDialog.class,
        DepanFxFoldSection.EDIT_FOLD_SECTION_DATA);
  }

  @FXML
  @Override // DepanFxBaseSectionToolDialog
  public void initialize() {
    super.initialize();

    foldNestControl = new DepanFxNodeFoldChooser.NodeFoldingControl(
        getWorkspace(), dialogRunner, nodeFoldingResourceField);

    populateOrderBy(orderByField);

    containerOrderField.getItems().add(DepanFxContainerOrder.FIRST);
    containerOrderField.getItems().add(DepanFxContainerOrder.LAST);
    containerOrderField.getItems().add(DepanFxContainerOrder.MIXED);
  }

  @FXML
  public void onOpenNodeFoldingChooser() {
    foldNestControl.runNodeFoldingFinder();
  }

  @FXML
  public void onNewNodeFolding() {
    DepanFxWorkspaceResource<GraphDocument> graphRsrc =
        getToolResource().get()
            .getResource().getNodeFoldResource()
            .getResource().getGraphDocResource();
    DepanFxWorkspaceResource<DepanFxNodeFoldData> foldInfo =
        workspace.addScratchResource(
            DepanFxNodeFoldData.emptyNodeFoldData(graphRsrc));

    DepanFxNodeFoldToolDialog.runCreateDialog(
        workspace, dialogRunner, foldInfo)
        .getController()
        .getToolResource()
        .ifPresent(r -> foldNestControl.setNodeFoldResource(r));
  }

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxFoldSectionData> sectionRsrc) {
    super.setToolResource(sectionRsrc);

    DepanFxFoldSectionData sectionData = sectionRsrc.getResource();
    foldNestControl.setNodeFoldResource(sectionData.getNodeFoldResource());

    // orderDirectionField is set by super.setToolResource()
    orderByField.setValue(sectionData.getOrderBy());
    containerOrderField.setValue(sectionData.getContainerOrder());
  }

  @Override
  protected DepanFxFoldSectionData prepareResult() {
    return new DepanFxFoldSectionData(
        getToolName(), getToolDescription(),
        getSectionLabel(), displayNodeCount(),
        orderByField.getValue(), getOrderDirection(),
        containerOrderField.getValue(),
        foldNestControl.getNodeFoldingResource());
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(FOLD_SECTION_FILTER);
    result.setSelectedExtensionFilter(FOLD_SECTION_FILTER);
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxFoldSectionData.FOLD_SECTION_TOOL_EXT,
        DepanFxNodeListSectionData.SECTIONS_TOOL_PATH);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Fold Section Save Confirmation Error";
  }
}
