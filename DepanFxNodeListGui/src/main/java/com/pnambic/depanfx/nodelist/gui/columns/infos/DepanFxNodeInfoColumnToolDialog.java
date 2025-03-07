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
package com.pnambic.depanfx.nodelist.gui.columns.infos;

import com.pnambic.depanfx.nodelist.gui.columns.DepanFxBaseColumnToolDialog;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeInfoColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
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
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@DepanFxFxmlDialog
@FxmlView("info-column-tool-dialog.fxml")
public class DepanFxNodeInfoColumnToolDialog
    extends DepanFxBaseColumnToolDialog<DepanFxNodeInfoColumnData> {

  public static final ExtensionFilter INFO_COLUMN_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Info Columns", DepanFxNodeInfoColumnData.NODE_INFO_COLUMN_TOOL_EXT);

  public static final DepanFxResourceFilter INFO_COLUMN_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Info Columns", DepanFxNodeInfoColumnData.NODE_INFO_COLUMN_TOOL_EXT,
          DepanFxNodeInfoColumnData.class);

  private static final String EDIT_INFO_COLUMN_TITLE =
      "Edit InfoColumn";

  private static final String NEW_INFO_COLUMN_TITLE =
      "New Info Column";

  @FXML
  private ComboBox<?> keyChoiceField;

  @Autowired
  public DepanFxNodeInfoColumnToolDialog(DepanFxWorkspace workspace) {
    super(workspace, DepanFxNodeInfoColumnData.class);
  }

  public static Dialog<DepanFxNodeInfoColumnToolDialog> runEditDialog(
      DepanFxWorkspaceResource<DepanFxNodeInfoColumnData> columnRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runEditDialog(
        columnRsrc, dialogRunner,
        DepanFxNodeInfoColumnToolDialog.class,
        EDIT_INFO_COLUMN_TITLE);
  }

  public static Dialog<DepanFxNodeInfoColumnToolDialog> runCreateDialog(
      DepanFxWorkspaceResource<DepanFxNodeInfoColumnData> columnRsrc,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runCreateDialog(
        columnRsrc, dialogRunner,
        DepanFxNodeInfoColumnToolDialog.class,
        NEW_INFO_COLUMN_TITLE);
  }

  public static void setNodeKeyColumnTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(INFO_COLUMN_FILTER);
    result.setSelectedExtensionFilter(INFO_COLUMN_FILTER);
  }

  @FXML
  public void initialize() {
  }

  @Override
  public void setToolResource(DepanFxWorkspaceResource<DepanFxNodeInfoColumnData> columnRsrc) {
    super.setToolResource(columnRsrc);

    // keyChoiceField.setValue(columnRsrc.getResource().getKeyChoice());
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxNodeInfoColumnData prepareResult() {

    return new DepanFxNodeInfoColumnData(
        getToolName(), getToolDescription(),
        getColumnLabel(), getColumnWidthMs());
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxNodeInfoColumnData.NODE_INFO_COLUMN_TOOL_EXT,
        DepanFxNodeListColumnData.COLUMNS_TOOL_PATH);
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    DepanFxNodeInfoColumnToolDialog.setNodeKeyColumnTooldataFilters(result);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Infos Column Save Confirmation Error";
  }
}
