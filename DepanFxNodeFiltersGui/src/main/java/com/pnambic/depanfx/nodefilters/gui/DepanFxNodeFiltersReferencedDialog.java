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
package com.pnambic.depanfx.nodefilters.gui;

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxReferencedFilterData;
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
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@DepanFxFxmlDialog
@FxmlView("referenced-filter-dialog.fxml")
public class DepanFxNodeFiltersReferencedDialog
    extends DepanFxNodeFiltersBaseDialog<DepanFxReferencedFilterData> {

  private static final String EXT =
      DepanFxReferencedFilterData.REFERENCED_FILTER_TOOL_EXT;

  public static final ExtensionFilter EXT_FILTER =
      DepanFxSceneControls.buildExtFilter("Referenced Filter", EXT);

  @Autowired
  public DepanFxNodeFiltersReferencedDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
      DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry) {
    super(workspace, dialogRunner, nodeFiltersDialogRegistry,
        DepanFxReferencedFilterData.class);
  }

  public static Optional<DepanFxWorkspaceResource<DepanFxReferencedFilterData>>
      runEditFilter(
          DepanFxDialogRunner dialogRunner,
          DepanFxWorkspaceResource<DepanFxReferencedFilterData> filterRsrc) {

    Dialog<DepanFxNodeFiltersReferencedDialog> saveDlg =
        DepanFxResourcePerspectives.prepareDialog(
            filterRsrc, dialogRunner, DepanFxNodeFiltersReferencedDialog.class);
    saveDlg.getController().setForSave();
    saveDlg.runDialog("Edit referenced filter");
    return saveDlg.getController().getToolResource();
  }

  public static Optional<DepanFxWorkspaceResource<DepanFxReferencedFilterData>>
      runSaveFilter(
          DepanFxDialogRunner dialogRunner,
          DepanFxWorkspaceResource<DepanFxReferencedFilterData> filterRsrc) {

    Dialog<DepanFxNodeFiltersReferencedDialog> saveDlg =
        DepanFxResourcePerspectives.prepareDialog(
            filterRsrc, dialogRunner, DepanFxNodeFiltersReferencedDialog.class);
    saveDlg.getController().setForSave();
    saveDlg.runDialog("Save referenced filter");
    return saveDlg.getController().getToolResource();
  }

  /**
   * Update a filter without requiring a resource save.
   */
  public static Optional<DepanFxReferencedFilterData>
      runUpdateFilter(
          DepanFxWorkspace workspace,
          DepanFxDialogRunner dialogRunner,
          DepanFxReferencedFilterData filterInfo) {

    DepanFxWorkspaceResource<DepanFxReferencedFilterData> refFilterRsrc =
        workspace.addScratchResource(filterInfo);
    Dialog<DepanFxNodeFiltersReferencedDialog> saveDlg =
        DepanFxResourcePerspectives.prepareDialog(
            refFilterRsrc, dialogRunner,
            DepanFxNodeFiltersReferencedDialog.class);
    saveDlg.getController().setForUpdate();
    saveDlg.runDialog("Update referenced filter");
    return saveDlg.getController().getToolResource()
        .map(DepanFxWorkspaceResource::getResource);
  }

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxReferencedFilterData> refFilterRsrc) {
    super.setToolResource(refFilterRsrc);

    setFilterResource(refFilterRsrc.getResource().getFilterResource());
  }

  @FXML
  protected void handleBrowseRefFilter() {
    DepanFxNodeFiltersChooser.runNodeFiltersFinder(
            getWorkspace(), getDialogRunner(), getScene(),
            getNodeFiltersDialogRegistry())
        .ifPresent(this::setFilterResource);
  }

  /////////////////////////////////////
  // Base Document Dialog protected overrides

  @Override
  protected DepanFxReferencedFilterData prepareResult() {
    return new DepanFxReferencedFilterData(
        getToolName(), getToolDescription(),
        getMergeMode(), getFilterResource(DepanFxReferencedFilterData.class),
        useClosure());
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildFilterInitialDestinationFile(EXT);
  }

  @Override
  protected void setTooldataFilters(FileChooser chooser) {
    chooser.getExtensionFilters().add(EXT_FILTER);
    chooser.setSelectedExtensionFilter(EXT_FILTER);
  }

  @Override
  protected String getInputCheckFailureText() {
    return "Reference Filter Save Confirmation Error";
  }
}
