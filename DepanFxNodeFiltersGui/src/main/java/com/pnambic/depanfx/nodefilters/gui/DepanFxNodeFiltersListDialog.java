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

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxListFilterData;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListChooser;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
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
@FxmlView("list-filter-dialog.fxml")
public class DepanFxNodeFiltersListDialog
    extends DepanFxNodeFiltersBaseDialog<DepanFxListFilterData> {

  private static final String EXT =
      DepanFxListFilterData.LIST_FILTER_TOOL_EXT;

  public static final ExtensionFilter EXT_FILTER =
      DepanFxSceneControls.buildExtFilter("List Filter", EXT);

  @Autowired
  public DepanFxNodeFiltersListDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
      DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry) {
    super(workspace, dialogRunner, nodeFiltersDialogRegistry,
        DepanFxListFilterData.class);
  }

  public static Optional<DepanFxWorkspaceResource<DepanFxListFilterData>>
      runEditDialog(
          DepanFxDialogRunner dialogRunner,
          DepanFxWorkspaceResource<DepanFxListFilterData> filterRsrc) {

    Dialog<DepanFxNodeFiltersListDialog> saveDlg =
        DepanFxResourcePerspectives.prepareDialog(
            filterRsrc, dialogRunner,
            DepanFxNodeFiltersListDialog.class);
    saveDlg.getController().setForSave();
    saveDlg.runDialog("Edit list filter");
    return saveDlg.getController().getToolResource();
  }

  public static Optional<DepanFxWorkspaceResource<DepanFxListFilterData>>
      runSaveFilter(
          DepanFxDialogRunner dialogRunner,
          DepanFxWorkspaceResource<DepanFxListFilterData> filterRsrc) {

    Dialog<DepanFxNodeFiltersListDialog> saveDlg =
        DepanFxResourcePerspectives.prepareDialog(
            filterRsrc, dialogRunner,
            DepanFxNodeFiltersListDialog.class);
    saveDlg.getController().setForSave();
    saveDlg.runDialog("Save list filter");
    return saveDlg.getController().getToolResource();
  }

  public static Optional<DepanFxListFilterData>
      runUpdateFilter(
          DepanFxWorkspace workspace,
          DepanFxDialogRunner dialogRunner,
          DepanFxListFilterData updateFilter) {

    DepanFxWorkspaceResource<DepanFxListFilterData> listFilterRsrc =
        workspace.addScratchResource(updateFilter);

    Dialog<DepanFxNodeFiltersListDialog> saveDlg =
        DepanFxResourcePerspectives.prepareDialog(
            listFilterRsrc, dialogRunner,
            DepanFxNodeFiltersListDialog.class);
    saveDlg.getController().setForUpdate();
    saveDlg.runDialog("Update list filter");
    return saveDlg.getController().getToolResource().map(null);
  }

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxListFilterData> listFilterRsrc) {
    super.setToolResource(listFilterRsrc);
    setFilterResource(listFilterRsrc.getResource().getNodeListResource());
  }

  @FXML
  protected void handleBrowseNodeList() {
    DepanFxNodeListChooser.runNodeListChooser(
            getWorkspace(), getDialogRunner(), getScene())
        .ifPresent(this::setFilterResource);
  }

  /////////////////////////////////////
  // Base Document Dialog protected overrides

  @Override
  protected DepanFxListFilterData prepareResult() {
    return new DepanFxListFilterData(
        getToolName(), getToolDescription(),
        getMergeMode(), getFilterResource(DepanFxNodeList.class));
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
    return "List Filter Save Confirmation Error";
  }
}
