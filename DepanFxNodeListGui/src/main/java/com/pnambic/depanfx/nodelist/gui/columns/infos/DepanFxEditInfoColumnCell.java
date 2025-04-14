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
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;

import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.util.StringConverter;

public class DepanFxEditInfoColumnCell
    extends TextFieldTreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember> {

  private final DepanFxNodeListColumn nodeListColumn;

  public DepanFxEditInfoColumnCell(DepanFxNodeListColumn nodeListColumn) {
    super();
    this.setConverter(new Converter());
    this.nodeListColumn = nodeListColumn;
    nodeListColumn.prepareCell(this);
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

  protected void stylizeCell(DepanFxNodeListMember member) {
  }

  private class Converter extends StringConverter<DepanFxNodeListMember> {

    @Override
    public String toString(DepanFxNodeListMember member) {
      if (member instanceof DepanFxNodeListGraphNode node) {
        return nodeListColumn.toString(node);
      }

      return null;
    }

    @Override
    public DepanFxNodeListMember fromString(String string) {
      throw new UnsupportedOperationException(
          "Unable to transform trait value to graph node");
    }
  }
}
