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

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;

import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.util.StringConverter;

public class DepanFxEditInfoColumnCell
    extends TextFieldTreeTableCell<DepanFxNodeListMember, String> {

  private final DepanFxInfoColumn infoColumn;

  private boolean editing;

  public DepanFxEditInfoColumnCell(DepanFxInfoColumn infoColumn) {
    super();
    this.setConverter(new Converter());
    this.infoColumn = infoColumn;
    infoColumn.prepareCell(this);
  }

  @Override
  public void updateItem(String member, boolean empty) {
    super.updateItem(member, empty);

    // Visual space reserved for future use.
    if (empty) {
      return;
    }
    // The normal case.
    if (member != null) {
      stylizeCell();
      return;
    }
  }

  @Override
  public void startEdit() {
    super.startEdit();
    editing = true;
  }

  @Override
  public void cancelEdit() {
    super.cancelEdit();
    editing = false;
  }

  @Override
  public void commitEdit(String editValue) {
    super.commitEdit(editValue);
    if (editing) {
      infoColumn.commitEdit(editValue);
    }
    editing = false;
  }

  /**
   * The value (a string) is rarely interesting for styling.
   * Content specific rendering should probably access the underlying data
   * with {@code getTableRow().getItem()}.
   */
  protected void stylizeCell() {
  }

  private class Converter extends StringConverter<String> {

    @Override
    public String toString(String value) {
      return value;
    }

    @Override
    public String fromString(String value) {
      return infoColumn.cleanInput(value);
    }
  }
}
