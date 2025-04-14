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

import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListMember;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxBaseColumnData;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

/**
 * A column for the common case that the rendering values can be extracted
 * from the list member. This allows for simple read-only columns with only
 * value extractors.
 *
 * This works less well for editable columns where the column type kinda
 * needs to match the rendered value (e.g. {@code String}).
 *
 * The infos columns ({@code DepanFxInfoColumn} take a different approach.
 */
public abstract class DepanFxMemberColumn<T extends DepanFxBaseColumnData>
    extends DepanFxAbstractColumn<T> {

  public DepanFxMemberColumn(
      DepanFxNodeListTableAdapter tableAdapter,
      DepanFxWorkspaceResource<T> columnDataRsrc) {
    super(tableAdapter, columnDataRsrc);
  }

  /**
   * Build a column that extracts its rendering values from the list member.
   * This allows for simple read-only columns with only value extractors.
   * This works less well for editable columns where the column type kinda
   * needs to match the rendered value (e.g. {@code String}).
   *
   * Columns that need type based control of the cell should override
   * this method to provide those capabilities.
   */
  @Override
  protected TreeTableColumn<DepanFxNodeListMember, ?> buildColumn() {
    TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember> result =
        new TreeTableColumn<>(getColumnLabel());
    result.setCellFactory(buildCellFactory());
    result.setCellValueFactory(p ->
        new ReadOnlyObjectWrapper<>(p.getValue().getValue()));
    return result;
  }

  protected abstract
      Callback<TreeTableColumn<DepanFxNodeListMember, DepanFxNodeListMember>,
          TreeTableCell<DepanFxNodeListMember, DepanFxNodeListMember>>
      buildCellFactory();
}
