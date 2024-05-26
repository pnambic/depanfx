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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class DepanFxNodeFiltersSequenceMember
    extends DepanFxNodeFiltersDisplayMember<DepanFxSequenceFilterData> {

  public DepanFxNodeFiltersSequenceMember(
      DepanFxSequenceFilterData sequenceFilter) {
    super(sequenceFilter);
  }

  public Stream<? extends DepanFxBaseFilterData> streamMembers() {
    return getFilterData().streamFilters();
  }

  @Override
  public DepanFxBaseFilterData prepareFilterData() {
    List<? extends DepanFxBaseFilterData> filters =
        getFilterData().streamFilters().collect(Collectors.toList());
    return new DepanFxSequenceFilterData(
        getToolNameProperty().get(), getToolDescriptionProperty().get(),
        getMergeModeProperty().get(), filters,
        getUseClosureProperty().get());
  }

  @Override
  protected BooleanProperty buildClosureProperty(
      DepanFxSequenceFilterData baseFilter) {
    return new SimpleBooleanProperty(baseFilter.useClosure());
  }
}
