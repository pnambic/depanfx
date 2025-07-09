/*
 * Copyright 2023 The Depan Project Authors
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

import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.util.StringConverter;

public class DepanFxNodeListCell
    extends CheckBoxTreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember> {

  // Allow cells to act on viewer (e.g. change sections, etc.)
  private final DepanFxNodeListTableAdapter tableAdapter;

  public DepanFxNodeListCell(DepanFxNodeListTableAdapter tableAdapter) {
    this.tableAdapter = tableAdapter;
    setConverter(new NameConverter());
    setSelectedStateCallback(
        p -> tableAdapter.getCheckBoxObservable(p.intValue()));
  }

  @Override
  public void updateItem(DepanFxNodeListMember member, boolean empty) {
    super.updateItem(member, empty);

    // Visual space reserved for future use.
    if (empty) {
      return;
    }
    // The normal case.
    if (member != null) {
      stylizeCell(member);
      return;
    }
    // Something unexpected.
    setText("<null>");
    setGraphic(null);
  }

  private void stylizeCell(DepanFxNodeListMember member) {

    setContextMenu(getLiveContextMenu());
  }

  private ContextMenu getLiveContextMenu() {
    ContextMenu result = new DepanFxContextMenuBuilder().build();
    fillContextMenu(result);
    result.setOnShowing(e -> fillContextMenu(result));
    return result;
  }

  private void fillContextMenu(ContextMenu contextMenu) {
    contextMenu.getItems().clear();
    DepanFxNodeListItem cellItem =
        (DepanFxNodeListItem) getTableRow().getTreeItem();
    TreeTableViewSelectionModel<DepanFxNodeListMember> selected =
        getTreeTableView().getSelectionModel();

    if (selected.isEmpty()) {
      cellItem.fillNodeContextMenu(
          contextMenu, getScene(), tableAdapter, null);
      return;
    }

    ObservableList<TreeItem<DepanFxNodeListMember>> choices =
        selected.getSelectedItems();
    if (choices.size() == 1) {
      DepanFxNodeListMember node = choices.get(0).getValue();
      if (node instanceof DepanFxNodeListGraphNode graphNode) {
        cellItem.fillNodeContextMenu(
            contextMenu, getScene(), tableAdapter, graphNode.getGraphNode());
        return;
      } 
    }
    cellItem.fillMultiContextMenu(
        contextMenu, getScene(), tableAdapter, choices);
  }

  private static class NameConverter
      extends StringConverter<DepanFxNodeListMember> {

    @Override
    public String toString(DepanFxNodeListMember member) {
      if (member != null) {
        return member.getDisplayName();
      }
      return "<empty>";
    }

    @Override
    public DepanFxNodeListMember fromString(String string) {
      throw new UnsupportedOperationException();
    }
  }
}
