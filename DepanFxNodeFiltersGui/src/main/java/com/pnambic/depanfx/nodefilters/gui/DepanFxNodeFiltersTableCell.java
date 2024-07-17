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
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxListFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxMatcherFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxReferencedFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxSequenceFilterData;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListChooser;
import com.pnambic.depanfx.nodelist.gui.link.DepanFxLinkMatcherChooser;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Optional;

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

  public static final String ADD_MATCHER_FILTER = "Add Link Matcher Filter...";

  public static final String ADD_LIST_FILTER = "Add List Node Filter...";

  public static final String ADD_REFERENCE_FILTER = "Add Reference Filter...";

  public static final String ADD_SEQUENCE_FILTER = "Add Sequence Filter";

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  public DepanFxNodeFiltersTableCell(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(new DefaultStringConverter());
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
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
      builder.appendActionItem(ADD_LIST_FILTER,
          e -> addListFilterRow());
      builder.appendActionItem(ADD_MATCHER_FILTER,
          e -> addMatcherFilterRow());
      builder.appendActionItem(ADD_REFERENCE_FILTER,
          e -> addReferencedFilterRow());
      builder.appendActionItem(ADD_SEQUENCE_FILTER,
          e -> addSequenceFilterRow());
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
    DepanFxNodeFiltersRegistry.runUpdateFilters(dialogRunner, filterData)
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

  private void addMatcherFilterRow() {
    matcherFilterChooser()
        .map(DepanFxMatcherFilterData::createMatcherFilterData)
        .ifPresent(this::addFilter);
  }

  private void addListFilterRow() {
    listFilterChooser()
        .map(DepanFxListFilterData::createListFilterData)
        .ifPresent(this::addFilter);
  }

  private void addReferencedFilterRow() {
    nodeFilterChooser()
        .map(DepanFxReferencedFilterData::createReferenceFilterData)
        .ifPresent(this::addFilter);
  }

  private void addSequenceFilterRow() {
    DepanFxSequenceFilterData seqFilter =
        DepanFxSequenceFilterData.createSequenceFilterData();
    addFilter(seqFilter);
  }

  private Optional<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
      matcherFilterChooser() {

    return DepanFxLinkMatcherChooser.runLinkMatcherFinder(
        workspace, dialogRunner, getScene());
  }

  private Optional<DepanFxWorkspaceResource<DepanFxNodeList>>
      listFilterChooser() {

    return DepanFxNodeListChooser.runNodeListChooser(
        workspace, dialogRunner, getScene());
  }

  private Optional<DepanFxWorkspaceResource<? extends DepanFxBaseFilterData>>
      nodeFilterChooser() {

    return DepanFxNodeFiltersChooser.runNodeFiltersFinder(
        workspace, dialogRunner, getScene());
  }
}
