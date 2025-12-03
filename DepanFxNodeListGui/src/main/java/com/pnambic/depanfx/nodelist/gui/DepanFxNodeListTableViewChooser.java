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
package com.pnambic.depanfx.nodelist.gui;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListTableViewData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.util.Optional;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;

public class DepanFxNodeListTableViewChooser {

  public static final String SELECT_TABLE_VIEW =
      "Select Table view...";

  public static final DepanFxResourceFilter TABLE_VIEW_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Table View", DepanFxNodeListTableViewData.TABLE_VIEW_TOOL_EXT,
          DepanFxNodeListTableViewData.class);

  /**
   * Obtain an existing node list with a file chooser.
   */
  public static Optional<DepanFxWorkspaceResource<DepanFxNodeListTableViewData>>
  runTableViewChooser(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        Scene scene) {

    DepanFxResourceChooser chooser = prepareChooser(workspace, dialogRunner);
    return chooser.showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(p -> workspace.getWorkspaceResource(
            p, DepanFxNodeListTableViewData.class));
  }

  /**
   * Bind a pop-up to text field for the resource name.
   */
  public static class TableViewControl {

    private final DepanFxWorkspace workspace;

    private final DepanFxDialogRunner dialogRunner;

    private DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc;

    private final TextField tableViewField;

    public TableViewControl(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        TextField tableViewField) {
      this.workspace = workspace;
      this.dialogRunner = dialogRunner;
      this.tableViewField = tableViewField;
      tableViewField.setContextMenu(buildContextMenu());
    }

    public void runNodeListFinder() {
      runTableViewChooser(workspace, dialogRunner, tableViewField.getScene())
          .ifPresent(this::setTableViewResource);
    }

    public void setTableViewResource(
        DepanFxWorkspaceResource<DepanFxNodeListTableViewData> tableViewRsrc) {
      this.tableViewRsrc = tableViewRsrc;
      tableViewField.setText(
          DepanFxProjects.asReferenceLabel(workspace, tableViewRsrc));
    }

    public DepanFxWorkspaceResource<DepanFxNodeListTableViewData>
    getTableViewResource() {
      return tableViewRsrc;
    }

    private ContextMenu buildContextMenu() {
      DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
      builder.appendActionItem(SELECT_TABLE_VIEW,
          e -> runNodeListFinder());
      return builder.build();
    }
  }

  /////////////////////////////////////

  private static DepanFxResourceChooser prepareChooser(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    DepanFxResourceChooser result =
        new DepanFxResourceChooser(workspace, dialogRunner);
    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxProjects.TOOLS_PATH);
    result.getExtensionFilters().add(TABLE_VIEW_RSRC_FILTER);
    result.setSelectedExtensionFilter(TABLE_VIEW_RSRC_FILTER);
    return result;
  }
}
