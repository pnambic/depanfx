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

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxSequenceFilterData;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.util.converter.DefaultStringConverter;

/**
 * Provide a context menu for filter rows.
 */
public class DepanFxNodeFiltersTableCell
    extends TextFieldTreeTableCell<
        DepanFxNodeFiltersTableMember, String> {

  public static final String DELETE_FILTER = "Delete Filter";

  public static final String EDIT_FILTER = "Edit Filter...";

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry;

  public DepanFxNodeFiltersTableCell(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner,
      DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry) {
    super(new DefaultStringConverter());
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.nodeFiltersDialogRegistry = nodeFiltersDialogRegistry;
  }

  @Override
  public void updateItem(String filterName, boolean empty) {
    super.updateItem(filterName, empty);

    // Visual space reserved for future use.
    if (empty) {
      return;
    }
    // The normal case.
    if (filterName != null) {
      stylizeCell(filterName);
      return;
    }
    // Something unexpected.
    setText("<null>");
    setGraphic(null);
  }

  private void stylizeCell(String filterName) {
    DepanFxNodeFiltersTableMember filterItem = getTableRow().getItem();
    if (filterItem instanceof DepanFxNodeFiltersDisplayMember<?> displayItem) {
      setContextMenu(buildFilterMenu(displayItem));
    }
  }

  private ContextMenu buildFilterMenu(
      DepanFxNodeFiltersDisplayMember<?> filterMember) {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    appendEditMenu(builder, filterMember);
    appendFilterMenu(builder, filterMember);
    appendDeleteMenu(builder, filterMember);
    return builder.build();
  }

  private void appendEditMenu(
      DepanFxContextMenuBuilder builder,
      DepanFxNodeFiltersDisplayMember<?> filterMember) {
    builder.appendActionItem(EDIT_FILTER,
        e -> runUpdateFilter(filterMember));
  }


  private void appendFilterMenu(DepanFxContextMenuBuilder builder,
      DepanFxNodeFiltersDisplayMember<?> filterMember) {
    DepanFxBaseFilterData filterData = filterMember.getFilterData();
    if (filterData instanceof DepanFxSequenceFilterData seqData) {
      builder.appendConditionalSeparator();
      nodeFiltersDialogRegistry.appendAddFilters(
          builder, workspace, dialogRunner, getScene(),
          this::addFilter);
    }
  }

  private void appendDeleteMenu(
      DepanFxContextMenuBuilder builder,
      DepanFxNodeFiltersDisplayMember<?> filterMember) {
    builder.appendConditionalSeparator();
    builder.appendActionItem(DELETE_FILTER,
        e -> runDeleteFilter(filterMember));
  }

  private void runUpdateFilter(
      DepanFxNodeFiltersDisplayMember<?> filterMember) {
    DepanFxBaseFilterData filterData = filterMember.prepareFilterData();
    nodeFiltersDialogRegistry.runUpdateFilters(dialogRunner, filterData)
        .ifPresent(filterMember::updateFilter);
  }

  private void runDeleteFilter(
      DepanFxNodeFiltersDisplayMember<?> filterMember) {
    DepanFxNodeFiltersTableMember parent = filterMember.getParent();
    if (parent instanceof DepanFxNodeFiltersTableContainer container) {
      container.deleteFilter(filterMember.getFilterData());
    }
  }

  private void addFilter(DepanFxBaseFilterData filter) {
    DepanFxNodeFiltersTableMember filterItem = getTableRow().getItem();
    if (filterItem instanceof DepanFxNodeFiltersTableContainer container) {
      container.addFilter(filter);
    }
  }
}
