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

import com.pnambic.depanfx.nodefilters.model.DepanFxClosableFilter;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javafx.scene.Scene;
import javafx.scene.control.TreeItem;

/**
 * Encapsulate common behaviors for node filters.
 */
@Component
public class DepanFxNodeFiltersRegistry {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeFiltersRegistry.class);

  private final List<DepanFxNodeFiltersContribution> contribs;

  public DepanFxNodeFiltersRegistry(List<DepanFxNodeFiltersContribution> contribs) {
    this.contribs = contribs;
  }

  public Optional<Boolean> getClosure(DepanFxBaseFilterData filter) {
    return DepanFxClosableFilter.getClosure(filter);
  }

  public TreeItem<DepanFxNodeFiltersTableMember> buildTableItem(
      DepanFxNodeFiltersTableMember parentMember,
      DepanFxBaseFilterData filter) {
    return lookupContrib(filter, "buildTableItem")
        .map(c -> c.buildTableMember(parentMember, filter, this))
        .orElseThrow(() -> new IllegalArgumentException(
            "Unexpected filter data " + filter.getClass().getName()));
  }

  public void runSaveFilters(
      DepanFxDialogRunner dialogRunner, DepanFxBaseFilterData saveFilter) {
    lookupContrib(saveFilter, "runSaveFilters")
        .ifPresent( c -> c.runSaveFilter(dialogRunner, saveFilter));
  }

  public Optional<DepanFxBaseFilterData> runUpdateFilters(
      DepanFxDialogRunner dialogRunner, DepanFxBaseFilterData updateFilter) {

     return lookupContrib(updateFilter, "runUpdateFilters")
        .flatMap(c -> c.runUpdateFilter(dialogRunner, updateFilter));
  }

  public void appendAddFilters(
      DepanFxContextMenuBuilder builder,
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      Scene scene,
      Consumer<DepanFxBaseFilterData> onAddFilter) {
    orderedContribs()
        .forEach(c -> c.appendCreateActionItem(
            builder, workspace, dialogRunner, scene, onAddFilter));
  }

  private Stream<DepanFxNodeFiltersContribution> orderedContribs() {
    return contribs.stream()
        .sorted((a, b) -> a.getOrderKey().compareTo(b.getOrderKey()));
  }

  private Optional<DepanFxNodeFiltersContribution> lookupContrib(
      DepanFxBaseFilterData filter, String caller) {
    Optional<DepanFxNodeFiltersContribution> result = contribs.stream()
        .filter(c -> c.accepts(filter))
        .findFirst();
    if (result.isEmpty()) {
      LOG.warn("Unexpected filter {} for {}",
          filter.getClass().getName(), caller);
    }
    return result;
  }
}
