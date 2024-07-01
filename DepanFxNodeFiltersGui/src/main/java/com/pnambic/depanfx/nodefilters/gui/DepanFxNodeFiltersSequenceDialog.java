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

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxSequenceFilterData;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
@FxmlView("sequence-filter-dialog.fxml")
public class DepanFxNodeFiltersSequenceDialog
    extends DepanFxNodeFiltersBaseDialog<DepanFxSequenceFilterData> {

  private static final String EXT =
      DepanFxSequenceFilterData.SEQUENCE_FILTER_TOOL_EXT;

  public static final ExtensionFilter EXT_FILTER =
      DepanFxSceneControls.buildExtFilter("Filter Sequence", EXT);

  @FXML
  private Label seqFilterDetailsLabel;

  private DepanFxSequenceFilterData seqFilter;

  @Autowired
  public DepanFxNodeFiltersSequenceDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, dialogRunner, DepanFxSequenceFilterData.class);
  }

  public static Optional<DepanFxWorkspaceResource<DepanFxSequenceFilterData>>
      runSaveFilter(
          DepanFxDialogRunner dialogRunner,
          DepanFxSequenceFilterData seqFilter) {

    Dialog<DepanFxNodeFiltersSequenceDialog> saveDlg =
        dialogRunner.createDialogAndParent(
            DepanFxNodeFiltersSequenceDialog.class);
    saveDlg.getController().setFilter(seqFilter);
    saveDlg.getController().setForSave();
    saveDlg.runDialog("Save sequence filter");
    return saveDlg.getController().getSavedResource();
  }

  public static Optional<DepanFxSequenceFilterData> runUpdateFilter(
      DepanFxDialogRunner dialogRunner,
      DepanFxSequenceFilterData seqFilter) {

    Dialog<DepanFxNodeFiltersSequenceDialog> saveDlg =
        dialogRunner.createDialogAndParent(
            DepanFxNodeFiltersSequenceDialog.class);
    saveDlg.getController().setFilter(seqFilter);
    saveDlg.getController().setForUpdate();
    saveDlg.runDialog("Update sequence seqFilter");
    return saveDlg.getController().getUpdateFilterData();
  }

  @Override
  public void setFilter(DepanFxSequenceFilterData seqFilter) {
    this.seqFilter = seqFilter;
    super.setFilter(seqFilter);

    seqFilterDetailsLabel.setText(buildDetailsLabel(seqFilter));
  }

  private String buildDetailsLabel(DepanFxSequenceFilterData seqFilter) {
    long filterCount = seqFilter.streamFilters().count();
    return MessageFormat.format(
        "Sequence filter with {0} elements.", filterCount);
  }

  /////////////////////////////////////
  // Base Document Dialog protected overrides

  @Override
  protected DepanFxSequenceFilterData prepareResult() {
    List<? extends DepanFxBaseFilterData> filters =
        seqFilter.streamFilters().collect(Collectors.toList());
    return new DepanFxSequenceFilterData(
        getToolName(), getToolDescription(),
        getMergeMode(), filters, useClosure());
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
    return "Filter Sequence Save Confirmation Error";
  }
}
