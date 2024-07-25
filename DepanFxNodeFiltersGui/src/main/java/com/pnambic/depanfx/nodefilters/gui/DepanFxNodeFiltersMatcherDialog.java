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

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxMatcherFilterData;
import com.pnambic.depanfx.nodelist.gui.link.DepanFxLinkMatcherChooser;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherDocument;
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
import javafx.scene.control.CheckBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
@FxmlView("matcher-filter-dialog.fxml")
public class DepanFxNodeFiltersMatcherDialog
    extends DepanFxNodeFiltersBaseDialog<DepanFxMatcherFilterData> {

  private static final String EXT =
      DepanFxMatcherFilterData.MATCHER_FILTER_TOOL_EXT;

  public static final ExtensionFilter EXT_FILTER =
      DepanFxSceneControls.buildExtFilter("Link Matcher Filter", EXT);

  @FXML
  private CheckBox useInverseCheckBox;

  @Autowired
  public DepanFxNodeFiltersMatcherDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
      DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry) {
    super(workspace, dialogRunner, nodeFiltersDialogRegistry,
        DepanFxMatcherFilterData.class);
  }

  public static Optional<DepanFxWorkspaceResource<DepanFxMatcherFilterData>>
      runEditDialog(
          DepanFxDialogRunner dialogRunner,
          DepanFxWorkspaceResource<DepanFxMatcherFilterData> filterRsrc) {

    Dialog<DepanFxNodeFiltersMatcherDialog> saveDlg =
        dialogRunner.createDialogAndParent(
            DepanFxNodeFiltersMatcherDialog.class);
    saveDlg.getController().setFilter(filterRsrc.getResource());
    saveDlg.getController().setDestination(filterRsrc.getDocument());
    saveDlg.getController().setForSave();
    saveDlg.runDialog("Edit link matcher filter");
    return saveDlg.getController().getSavedResource();
  }

  public static Optional<DepanFxWorkspaceResource<DepanFxMatcherFilterData>>
      runSaveFilter(
          DepanFxDialogRunner dialogRunner,
          DepanFxMatcherFilterData matcherFilter) {

    Dialog<DepanFxNodeFiltersMatcherDialog> saveDlg =
        dialogRunner.createDialogAndParent(
            DepanFxNodeFiltersMatcherDialog.class);
    saveDlg.getController().setFilter(matcherFilter);
    saveDlg.getController().setForSave();
    saveDlg.runDialog("Save link matcher filter");
    return saveDlg.getController().getSavedResource();
  }

  public static Optional<DepanFxMatcherFilterData> runUpdateFilter(
      DepanFxDialogRunner dialogRunner,
      DepanFxMatcherFilterData updateFilter) {

    Dialog<DepanFxNodeFiltersMatcherDialog> saveDlg =
        dialogRunner.createDialogAndParent(
            DepanFxNodeFiltersMatcherDialog.class);
    saveDlg.getController().setFilter(updateFilter);
    saveDlg.getController().setForUpdate();
    saveDlg.runDialog("Update link matcher filter");
    return saveDlg.getController().getUpdateFilterData();
  }

  @Override
  public void setFilter(DepanFxMatcherFilterData matcherFilter) {
    super.setFilter(matcherFilter);
    setFilterResource(matcherFilter.getMatcherResource());
    useInverseCheckBox.setSelected(matcherFilter.useInverse());
  }

  @FXML
  protected void handleBrowseLinkMatcher() {
    DepanFxLinkMatcherChooser.runLinkMatcherFinder(
            getWorkspace(), getDialogRunner(), getScene())
        .ifPresent(this::setFilterResource);
  }

  /////////////////////////////////////
  // Base Document Dialog protected overrides

  @Override
  protected DepanFxMatcherFilterData prepareResult() {
    return new DepanFxMatcherFilterData(
        getToolName(), getToolDescription(),
        getMergeMode(), getFilterResource(DepanFxLinkMatcherDocument.class),
        useInverseCheckBox.isSelected(),
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
    return "Link Matcher Filter Save Confirmation Error";
  }
}
