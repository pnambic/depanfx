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
package com.pnambic.depanfx.nodefilters.gui;

import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.util.StringConverter;

public class DepanFxNodeFiltersTableCell
    extends CheckBoxTreeTableCell<DepanFxNodeFiltersTableMember, DepanFxNodeFiltersTableMember> {

  private static final String SELECT_FLAT_SECTION = "Select Flat Section...";

  private static final String EDIT_FLAT_SECTION = "Edit Flat Section...";

  // Tree section actions
  private static final String SELECT_TREE_SECTION = "Select Tree Section...";

  private static final String EDIT_TREE_SECTION = "Edit Tree Section...";

  private static final String EXPORT_TO_CSV = "Export to CSV...";

  private static final String INSERT_ABOVE_MEMBER_TREE_SECTION =
      "Insert Member Tree Section";

  // Fork/Directory actions
  private static final String SELECT_RECURSIVE = "Select Recursive";

  private static final String CLEAR_RECURSIVE = "Clear Recursive";

  private static final String EXPAND_CHILDREN = "Expand Children";

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeFiltersTableCell.class);

  public DepanFxNodeFiltersTableCell() {
    setConverter(new NameConverter());
  }

  @Override
  public void updateItem(DepanFxNodeFiltersTableMember member, boolean empty) {
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

  private void stylizeCell(DepanFxNodeFiltersTableMember tableMember) {
    switch (tableMember) {
    case DepanFxNodeFiltersMatcherMember link:
      setContextMenu(linkFilterMenu(link));
      return;

    case DepanFxNodeFiltersSequenceMember seq:
      setContextMenu(sequenceFilterMenu(seq));
      return;

    default:
      LOG.warn("Unexpected filter member  {}", tableMember.getClass().getName());
    }

    // Otherwise clear the context menu
    setContextMenu(null);
  }

  private ContextMenu linkFilterMenu(DepanFxNodeFiltersMatcherMember linkMember) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    return builder.build();
  }

  private ContextMenu sequenceFilterMenu(
      DepanFxNodeFiltersSequenceMember seqMember) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    return builder.build();
  }

  private static class NameConverter
      extends StringConverter<DepanFxNodeFiltersTableMember> {

    @Override
    public String toString(DepanFxNodeFiltersTableMember member) {
      if (member != null) {
        return member.getDisplayName();
      }
      return "<empty>";
    }

    @Override
    public DepanFxNodeFiltersTableMember fromString(String string) {
      throw new UnsupportedOperationException();
    }
  }
}
