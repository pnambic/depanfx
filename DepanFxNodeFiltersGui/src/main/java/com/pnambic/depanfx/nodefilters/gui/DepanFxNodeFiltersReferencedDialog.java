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
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
@FxmlView("referenced-filter-dialog.fxml")
public class DepanFxNodeFiltersReferencedDialog
    extends DepanFxNodeFiltersBaseDialog<DepanFxReferencedFilterData> {

  private static final String EXT =
      DepanFxReferencedFilterData.REFERENCED_FILTER_TOOL_EXT;

  public static final ExtensionFilter EXT_FILTER =
      DepanFxSceneControls.buildExtFilter("Referenced Filter", EXT);

  @Autowired
  public DepanFxNodeFiltersReferencedDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, dialogRunner, DepanFxReferencedFilterData.class);
  }

  public static Optional<DepanFxWorkspaceResource<DepanFxReferencedFilterData>>
      runEditFilter(
          DepanFxDialogRunner dialogRunner,
          DepanFxWorkspaceResource<DepanFxReferencedFilterData> filterRsrc) {

    Dialog<DepanFxNodeFiltersReferencedDialog> saveDlg =
        dialogRunner.createDialogAndParent(
            DepanFxNodeFiltersReferencedDialog.class);
    saveDlg.getController().setFilter(filterRsrc.getResource());
    saveDlg.getController().setDestination(filterRsrc.getDocument());
    saveDlg.getController().setForSave();
    saveDlg.runDialog("Edit referenced filter");
    return saveDlg.getController().getSavedResource();
  }

  public static Optional<DepanFxWorkspaceResource<DepanFxReferencedFilterData>>
      runSaveFilter(
          DepanFxDialogRunner dialogRunner,
          DepanFxReferencedFilterData referencedFilter) {

    Dialog<DepanFxNodeFiltersReferencedDialog> saveDlg =
        dialogRunner.createDialogAndParent(
            DepanFxNodeFiltersReferencedDialog.class);
    saveDlg.getController().setFilter(referencedFilter);
    saveDlg.getController().setForSave();
    saveDlg.runDialog("Save referenced filter");
    return saveDlg.getController().getSavedResource();
  }

  public static Optional<DepanFxReferencedFilterData>
      runUpdateFilter(
          DepanFxDialogRunner dialogRunner,
          DepanFxReferencedFilterData referencedFilter) {

    Dialog<DepanFxNodeFiltersReferencedDialog> saveDlg =
        dialogRunner.createDialogAndParent(
            DepanFxNodeFiltersReferencedDialog.class);
    saveDlg.getController().setFilter(referencedFilter);
    saveDlg.getController().setForUpdate();
    saveDlg.runDialog("Update referenced filter");
    return saveDlg.getController().getUpdateFilterData();
  }

  @Override
  public void setFilter(DepanFxReferencedFilterData referencedFilter) {
    super.setFilter(referencedFilter);
    setFilterResource(referencedFilter.getFilterResource());
  }

  @FXML
  protected void handleBrowseRefFilter() {
    DepanFxNodeFiltersChooser.runNodeFiltersFinder(
            getWorkspace(), getDialogRunner(), getScene())
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
    return buildAnalysisInitialDestination(EXT);
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
