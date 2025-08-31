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
package com.pnambic.depanfx.nodelist.gui.columns;

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableController;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceChooser;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilterModel;
import com.pnambic.depanfx.scene.DepanFxMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxMenuItemFactory;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class DepanFxNodeListColumns {

  public static final String ADD_COLUMN = "Add Column";

  public static final String SELECT_COLUMN = "Select Column...";

  public static final String NODE_LIST_COLUMNS = "Node List Columns";

  public static Menu newColumnMenu(
      DepanFxNodeListColumn after,
      DepanFxNodeListTableAdapter tableAdapter) {

    DepanFxMenuBuilder menuBuilder = new DepanFxMenuBuilder(ADD_COLUMN);
    populateNewColumnMenu(menuBuilder, after, tableAdapter);
    return menuBuilder.build();
  }

  public static void updateNewColumnMenu(
      Menu newColumnMenu,
      DepanFxNodeListColumn after,
      DepanFxNodeListTableAdapter tableAdapter) {
    newColumnMenu.getItems().clear();

    DepanFxMenuBuilder menuBuilder = new DepanFxMenuBuilder(newColumnMenu);
    populateNewColumnMenu(menuBuilder, after, tableAdapter);
  }

  private static void populateNewColumnMenu(
      DepanFxMenuBuilder menuBuilder,
      DepanFxNodeListColumn after,
      DepanFxNodeListTableAdapter tableAdapter) {
    menuBuilder.appendActionItem(
        SELECT_COLUMN, e -> doSelectColumnAction(after, tableAdapter));
    menuBuilder.appendSeparator();

    tableAdapter.streamColumnChoices()
        .map(c -> buildColumnItem(after, tableAdapter, c))
        .forEach(menuBuilder::appendMenuItem);
  }

  private static void doSelectColumnAction(
      DepanFxNodeListColumn after,
      DepanFxNodeListTableAdapter tableAdapter) {
    // Probably should expose getScene() from
    // either DepanFxNodeListColumn or DepanFxNodeListTableAdapter.
    Scene scene = ((DepanFxNodeListTableController) tableAdapter).getScene();

    prepareColumnChooser(tableAdapter).showOpenDialog(scene)
        .map(DepanFxProjectDocument.class::cast)
        .flatMap(m ->
            tableAdapter.getWorkspace().getWorkspaceResource(
                m, DepanFxBaseColumnData.class))
        .ifPresent(r -> tableAdapter.addColumn(after, r));
  }

  private static DepanFxResourceChooser prepareColumnChooser(
      DepanFxNodeListTableAdapter tableAdapter) {

    DepanFxResourceFilterModel rsrcFilter =
        new DepanFxResourceFilterModel.Composite(
            NODE_LIST_COLUMNS,
            prepareColumnFilters(tableAdapter));
    return prepareColumnChooser(tableAdapter, rsrcFilter);
  }

  private static List<DepanFxResourceFilterModel> prepareColumnFilters(
      DepanFxNodeListTableAdapter tableAdapter) {
    return tableAdapter.streamColumnChoices()
        .map(c -> c.getColumnFilter())
        .collect(Collectors.toList());
  }

  private static DepanFxResourceChooser prepareColumnChooser(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxResourceFilterModel rsrcFilter) {

    DepanFxResourceChooser result = new DepanFxResourceChooser(
        tableAdapter.getWorkspace(), tableAdapter.getDialogRunner());

    DepanFxResourcePerspectives.prepareResourceFinder(
        result, DepanFxNodeListColumnData.COLUMNS_TOOL_PATH);

    result.getExtensionFilters().add(rsrcFilter);
    result.setSelectedExtensionFilter(rsrcFilter);
    return result;
  }

  private static MenuItem buildColumnItem(
      DepanFxNodeListColumn after,
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxColumnRegistry.Contribution contrib) {
    String fmtLabel = MessageFormat.format(
        "New {0} Column...", contrib.getColumnLabel());
    return DepanFxMenuItemFactory.createActionItem(
        fmtLabel,
        e -> contrib.getNewColumn(
            e, tableAdapter.getWorkspace(),
            tableAdapter.getDialogRunner(),
            tableAdapter)
        .ifPresent(r -> tableAdapter.addColumn(after, r))
        );
  }
}
