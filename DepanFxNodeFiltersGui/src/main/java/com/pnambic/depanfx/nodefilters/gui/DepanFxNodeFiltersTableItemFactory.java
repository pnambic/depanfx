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
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxMatcherFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxListFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxReferencedFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxSequenceFilterData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.TreeItem;

public class DepanFxNodeFiltersTableItemFactory {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeFiltersTableItemFactory.class);

  private DepanFxNodeFiltersTableItemFactory() {
    // Prevent instantiation.
  }

  public static TreeItem<DepanFxNodeFiltersTableMember> buildTableItem(
      DepanFxBaseFilterData filter) {

    switch (filter) {
    case DepanFxListFilterData list:
      return new DepanFxNodeFiltersListItem(
          new DepanFxNodeFiltersListMember(list));

    case DepanFxMatcherFilterData link:
      return new DepanFxNodeFiltersMatcherItem(
          new DepanFxNodeFiltersMatcherMember(link));

    case DepanFxReferencedFilterData ref:
      return new DepanFxNodeFiltersReferencedItem(
          new DepanFxNodeFiltersReferencedMember(ref));

    case DepanFxSequenceFilterData seq:
      return new DepanFxNodeFiltersSequenceItem(
          new DepanFxNodeFiltersSequenceMember(seq));

    default:
      // fall through and out to failure
    }
    LOG.warn("Unexpected filter {}", filter.getClass().getName());
    throw new IllegalArgumentException(
        "Unexpected filter data " + filter.getClass().getName());
  }
}
