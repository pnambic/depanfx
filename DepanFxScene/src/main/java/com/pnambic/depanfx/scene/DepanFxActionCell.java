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
package com.pnambic.depanfx.scene;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * A cell intended to handle a context menu for a table row.  The delete action
 * is built in as the final action, but each derived type is invited to add
 * their own actions to the context menu.
 *
 * @param <T> element type for the {@link ObservableList} rendered in
 * the table.
 */
public abstract class DepanFxActionCell<T> extends
    TableCell<T, String> {

  private static final String HAMBURGER_MENU = "\u2261";

  private static final Logger LOG = LoggerFactory.getLogger(DepanFxActionCell.class);

  private final ObservableList<T> tableData;

  public static <T> void styleColumn(TableColumn<T, String> rowActionColumn) {
    rowActionColumn.setStyle("-fx-alignment: BASELINE-CENTER;");
  }

  public static <T> void prepareColumn(
      TableColumn<T, String> rowActionColumn,
      Callback<TableColumn<T, String>, TableCell<T, String>> cellFactory) {
    styleColumn(rowActionColumn);

    rowActionColumn.setCellFactory(cellFactory);
  }

  public DepanFxActionCell(ObservableList<T> tableData) {
    this.tableData = tableData;
  }

  // Hook method for derived classes.
  protected abstract void populateContextMenu(
      DepanFxContextMenuBuilder builder);

  // Available for derived classes
  protected T getRowData(int cellIndex) {
    return tableData.get(cellIndex);
  }

  // Available for derived classes
  protected void setRow(int index, T data) {
    tableData.set(index, data);
  }

  // Available for derived classes
  protected void appendMoveOps(DepanFxContextMenuBuilder builder) {
    int index = getIndex();
    boolean hasUp = index > 0;
    boolean hasDown = index < tableData.size() - 1;
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
    tableData.remove(index);
  }

  private void moveRows(int srcIndex, int moveBy) {
    int dstIndex = srcIndex + moveBy;
    if (dstIndex < 0 || dstIndex >= tableData.size()) {
      LOG.error("Bad filter move to {} from {} by {}",
          dstIndex, srcIndex, moveBy);
      return;
    }
    T moveItem = tableData.remove(srcIndex);
    tableData.add(dstIndex, moveItem);
  }
}
