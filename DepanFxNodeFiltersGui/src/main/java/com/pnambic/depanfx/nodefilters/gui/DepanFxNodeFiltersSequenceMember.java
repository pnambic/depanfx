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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class DepanFxNodeFiltersSequenceMember
    extends DepanFxNodeFiltersDisplayMember<DepanFxSequenceFilterData>
    implements DepanFxNodeFiltersTableContainer {

  private final ObservableList<DepanFxBaseFilterData> filterItems;

  public DepanFxNodeFiltersSequenceMember(
      DepanFxNodeFiltersTableMember parentMember,
      DepanFxSequenceFilterData sequenceFilter) {
    super(parentMember, sequenceFilter);
    List<? extends DepanFxBaseFilterData> sourceFilters =
        sequenceFilter.streamFilters().collect(Collectors.toList());
    filterItems = FXCollections.observableArrayList(sourceFilters);
  }

  public Stream<? extends DepanFxBaseFilterData> streamMembers() {
    return getFilterData().streamFilters();
  }

  public void addMemberListener(
      ListChangeListener<DepanFxBaseFilterData> chgListener) {
    filterItems.addListener(chgListener);
  }

  @Override
  public DepanFxBaseFilterData prepareFilterData() {
    List<? extends DepanFxBaseFilterData> filters =
        filterItems.stream().collect(Collectors.toList());
    return new DepanFxSequenceFilterData(
        getToolNameProperty().get(), getToolDescriptionProperty().get(),
        getMergeModeProperty().get(), filters,
        getUseClosureProperty().get());
  }

  @Override
  public void addFilter(DepanFxBaseFilterData filterData) {
    filterItems.add(filterData);
  }

  @Override
  public void deleteFilter(DepanFxBaseFilterData filterData) {
    filterItems.remove(filterData);
  }

  @Override
  public void updateFilter(DepanFxBaseFilterData filterData) {
    super.updateFilter(filterData);

    DepanFxSequenceFilterData sequenceFilter =
        (DepanFxSequenceFilterData) filterData;
    List<? extends DepanFxBaseFilterData> updateFilters =
        sequenceFilter .streamFilters().collect(Collectors.toList());

    filterItems.clear();
    filterItems.addAll(updateFilters);
  }
}
