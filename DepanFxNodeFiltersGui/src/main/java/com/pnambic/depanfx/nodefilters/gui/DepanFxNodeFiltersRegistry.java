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
import com.pnambic.depanfx.scene.DepanFxDialogRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import javafx.scene.control.TreeItem;

/**
 * Encapsulate common behaviors for node filters.
 */
public class DepanFxNodeFiltersRegistry {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeFiltersRegistry.class);

  public static TreeItem<DepanFxNodeFiltersTableMember> buildTableItem(
      DepanFxNodeFiltersTableMember parentMember,
      DepanFxBaseFilterData filter) {

    switch (filter) {
    case DepanFxListFilterData list:
      return new DepanFxNodeFiltersListItem(
          new DepanFxNodeFiltersListMember(parentMember, list));

    case DepanFxMatcherFilterData link:
      return new DepanFxNodeFiltersMatcherItem(
          new DepanFxNodeFiltersMatcherMember(parentMember, link));

    case DepanFxReferencedFilterData ref:
      return new DepanFxNodeFiltersReferencedItem(
          new DepanFxNodeFiltersReferencedMember(parentMember, ref));

    case DepanFxSequenceFilterData seq:
      return new DepanFxNodeFiltersSequenceItem(
          new DepanFxNodeFiltersSequenceMember(parentMember, seq));

    default:
      // fall through and out to failure
    }
    LOG.warn("Unexpected filter {}", filter.getClass().getName());
    throw new IllegalArgumentException(
        "Unexpected filter data " + filter.getClass().getName());
  }

  public static Optional<Boolean> getClosure(DepanFxBaseFilterData filter) {
    switch (filter) {
    case DepanFxListFilterData listData:
      return Optional.empty();
    case DepanFxMatcherFilterData matcherData:
      return Optional.of(matcherData.useClosure());
    case DepanFxReferencedFilterData refData:
      return Optional.of(refData.useClosure());
    case DepanFxSequenceFilterData seqData:
      return Optional.of(seqData.useClosure());
    default:
        // Fall-through any unhandled results
    }
    LOG.warn("Unsupported filter type {} for closure",
        filter.getClass().getName());
    return Optional.empty();
  }

  public static void runSaveFilters(
      DepanFxDialogRunner dialogRunner, DepanFxBaseFilterData saveFilter) {
    switch (saveFilter) {
    case DepanFxListFilterData listData:
      DepanFxNodeFiltersListDialog.runSaveFilter(dialogRunner, listData);
      return;
    case DepanFxMatcherFilterData matcherData:
      DepanFxNodeFiltersMatcherDialog.runSaveFilter(dialogRunner, matcherData);
      return;
    case DepanFxReferencedFilterData refData:
      DepanFxNodeFiltersReferencedDialog.runSaveFilter(dialogRunner, refData);
      return;
    case DepanFxSequenceFilterData seqData:
      DepanFxNodeFiltersSequenceDialog.runSaveFilter(dialogRunner, seqData);
      return;
    default:
        // Fall-through any unhandled results
    }
    LOG.warn("Unsupported filter type {} to save",
        saveFilter.getClass().getName());
  }

  @SuppressWarnings("unchecked")
  public static <T extends DepanFxBaseFilterData> Optional<T> runUpdateFilters(
      DepanFxDialogRunner dialogRunner,
      T updateFilter) {
    // Consumer<DepanFxBaseFilterData> onUpdate = null;
    switch (updateFilter) {
    case DepanFxListFilterData listData:
      return (Optional<T>) DepanFxNodeFiltersListDialog.runUpdateFilter(
          dialogRunner, listData);
    case DepanFxMatcherFilterData matcherData:
      return (Optional<T>) DepanFxNodeFiltersMatcherDialog.runUpdateFilter(
          dialogRunner, matcherData);
    case DepanFxReferencedFilterData refData:
      return (Optional<T>) DepanFxNodeFiltersReferencedDialog.runUpdateFilter(
          dialogRunner, refData);
    case DepanFxSequenceFilterData seqData:
      return (Optional<T>) DepanFxNodeFiltersSequenceDialog.runUpdateFilter(
          dialogRunner, seqData);
    default:
        // Fall-through any unhandled results
    }
    LOG.warn("Unsupported filter type {} to update",
        updateFilter.getClass().getName());
    return Optional.empty();
  }
}
