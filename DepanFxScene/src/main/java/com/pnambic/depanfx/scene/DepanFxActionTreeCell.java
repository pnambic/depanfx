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
package com.pnambic.depanfx.scene;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;

/**
 * A cell intended to handle a context menu for a tree row.  The delete action
 * is built in as the final action, but each derived type is invited to add
 * their own actions to the context menu.
 *
 * @param <T> element type for the rendered {@link TreeTableView}.
 */
public abstract class DepanFxActionTreeCell<T> extends
    TreeTableCell<T, String> {

  private static final String HAMBURGER_MENU = "\u2261";

  private static final Logger LOG = LoggerFactory.getLogger(DepanFxActionTreeCell.class);

  private final TreeTableView<T> tableView;
  // private final ObservableList<T> tableData;

  public static interface ItemContainer<C> {
    void deleteItem(C item);
    void addItem(C item);
  }

  public static <T> void styleColumn(TreeTableColumn<T, String> rowActionColumn) {
    rowActionColumn.setStyle("-fx-alignment: BASELINE-CENTER;");
  }

  public static <T> void prepareColumn(
      TreeTableColumn<T, String> rowActionColumn,
      Callback<TreeTableColumn<T, String>,
      TreeTableCell<T, String>> cellFactory) {
    styleColumn(rowActionColumn);

    rowActionColumn.setCellFactory(cellFactory);
  }

  public DepanFxActionTreeCell(TreeTableView<T> tableView) {
    this.tableView = tableView;
  }

  // Hook method for derived classes.
  protected abstract void populateContextMenu(
      DepanFxContextMenuBuilder builder);

  // Available for derived classes
  protected T getRowData(int cellIndex) {
    return tableView.getTreeItem(cellIndex).getValue();
  }

  // Available for derived classes
  protected void setRow(int index, T data) {
    TreeItem<T> item = tableView.getTreeItem(index);
    item.setValue(data);
  }

  // Available for derived classes
  protected void appendMoveOps(DepanFxContextMenuBuilder builder) {
    int index = getIndex();
    boolean hasUp = index > 0;
    boolean hasDown = false; // index < tableData.size() - 1;
    if (!hasUp && !hasDown ) {
      return;
    }
    builder.appendConditionalSeparator();
    if (hasUp) {
      builder.appendActionItem("Up", e -> moveRows(getIndex(), -1));
    }
    if (hasDown) {
      builder.appendActionItem("Down", e -> moveRows(getIndex(), 1));
    }
  }

  @Override // TableCell
  protected void updateItem(String item, boolean empty) {
    super.updateItem(item, empty);

    if (!empty) {
      setText(HAMBURGER_MENU);
      setGraphic(null);
      stylizeCell();
      return;
    }
    setText(null);
    setGraphic(null);
  }

  private void stylizeCell() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    populateContextMenu(builder);
    builder.appendConditionalSeparator();
    builder.appendActionItem("Delete", e -> deleteRow(getIndex()));
    setContextMenu(builder.build());
  }

  private void deleteRow(int index) {
    TreeItem<T> item = tableView.getTreeItem(index);
    T parentInfo = item.getParent().getValue();
    // By construction, every shown item has a parent.
    // Note that the root item is not shown.
    @SuppressWarnings("unchecked")
    ItemContainer<T> container = (ItemContainer<T>) parentInfo;
    container.deleteItem(item.getValue());
  }

  private void moveRows(int srcIndex, int moveBy) {
    int dstIndex = srcIndex + moveBy;
    if (dstIndex < 0 || dstIndex >= 1) { //tableData.size()) {
      LOG.error("Bad filter move to {} from {} by {}",
          dstIndex, srcIndex, moveBy);
      return;
    }
    // T moveItem = tableData.remove(srcIndex);
    // tableData.add(dstIndex, moveItem);
  }
}
