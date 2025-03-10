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

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListGraphNode;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxAbstractColumn;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import javafx.scene.control.ContextMenu;

public class DepanFxInfoColumn
    extends DepanFxAbstractColumn<DepanFxNodeInfoColumnData> {

  public static final String EDIT_INFO_COLUMN = "Edit Info Column...";

  public static final String NEW_INFO_COLUMN = "New Info Column...";

  public static final String SELECT_INFO_COLUMN = "Select Info Column...";

  public DepanFxInfoColumn(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxWorkspaceResource<DepanFxNodeInfoColumnData> columnDataRsrc) {
    super(tableAdapter, columnDataRsrc);
  }

  @Override
  public ContextMenu buildColumnContextMenu(DepanFxDialogRunner dialogRunner) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem(SELECT_INFO_COLUMN,
        e -> openColumnChooser(dialogRunner));
    builder.appendActionItem(EDIT_INFO_COLUMN,
        e -> openColumnEditor(dialogRunner));
    return builder.build();
  }

  @Override
  public String toString(DepanFxNodeListGraphNode member) {
    return "<unknown info>";
  }

  public static void addNewColumnAction(
      DepanFxContextMenuBuilder builder,
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner) {
    builder.appendActionItem(NEW_INFO_COLUMN,
        e -> openColumnCreate(workspace, dialogRunner));
  }

  private static void openColumnCreate(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxNodeInfoColumnData initialData =
        DepanFxNodeInfoColumnData.buildInitialColumnData();
    DepanFxWorkspaceResource<DepanFxNodeInfoColumnData> columnRsrc =
        workspace.addScratchResource(initialData);
    DepanFxInfoColumnToolDialog.runCreateDialog(columnRsrc, dialogRunner);
  }

  private void openColumnEditor(DepanFxDialogRunner dialogRunner) {
    Dialog<DepanFxInfoColumnToolDialog> nodeKeyColumnEditor =
          DepanFxInfoColumnToolDialog.runEditDialog(
              forUpdate(buildEditData()), dialogRunner);

    nodeKeyColumnEditor.getController().getToolResource()
        .ifPresent(this::updateColumnDataRsrc);
  }

  private DepanFxNodeInfoColumnData buildEditData() {
    DepanFxNodeInfoColumnData columnData = getColumnData();

    int widthMs = (int) Math.round(
        column.getWidth() / DepanFxSceneControls.layoutWidthMs(1));
    return new DepanFxNodeInfoColumnData(
        columnData.getToolName(), columnData.getToolDescription(),
        columnData.getColumnLabel(), widthMs,
        columnData.getInfoContribution(), columnData.getInfoProperty());
  }

  private void openColumnChooser(DepanFxDialogRunner dialogRunner) {
    DepanFxWorkspace workspace = tableAdapter.getWorkspace();
    DepanFxResourceChooser columnChooser =
        prepareChooser(workspace, dialogRunner);
    columnChooser.showOpenDialog(getScene())
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
              p, DepanFxNodeInfoColumnData.class))
        .ifPresent(this::updateColumnDataRsrc);
  }

  private static DepanFxResourceChooser prepareChooser(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxResourceChooser result =
        new DepanFxResourceChooser(workspace, dialogRunner);
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxNodeListColumnData.COLUMNS_TOOL_PATH);

    result.getExtensionFilters().add(
        DepanFxInfoColumnToolDialog.INFO_COLUMN_RSRC_FILTER);
    result.setSelectedExtensionFilter(
        DepanFxInfoColumnToolDialog.INFO_COLUMN_RSRC_FILTER);
    return result;
  }
}
