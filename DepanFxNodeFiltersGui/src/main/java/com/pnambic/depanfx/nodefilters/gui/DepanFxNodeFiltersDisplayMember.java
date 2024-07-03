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
import com.pnambic.depanfx.nodefilters.tooldata.FilterMergeMode;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Derived classes must implement {@link #prepareFilterData()}.
 */
public abstract class DepanFxNodeFiltersDisplayMember<T extends DepanFxBaseFilterData>
    implements DepanFxNodeFiltersTableMember,
        DepanFxNodeFiltersTableColumns, DepanFxNodeFiltersDataProvider {

  private final DepanFxNodeFiltersTableMember parentMember;

  private T baseFilter;

  private final StringProperty toolNameProperty;

  private final StringProperty toolDescriptionProperty;

  private final BooleanProperty useClosureProperty;

  private final ObjectProperty<FilterMergeMode> mergeModeProperty;

  public DepanFxNodeFiltersDisplayMember(
      DepanFxNodeFiltersTableMember parentMember, T baseFilter) {
    this.parentMember = parentMember;
    this.baseFilter = baseFilter;

    this.toolNameProperty =
        new SimpleStringProperty(baseFilter.getToolName());
    this.toolDescriptionProperty =
        new SimpleStringProperty(baseFilter.getToolDescription());
    this.mergeModeProperty =
        new SimpleObjectProperty<>(baseFilter.getMergeMode());
    this.useClosureProperty =
        new SimpleBooleanProperty(calcFilterClosureProperty(baseFilter));
  }

  @SuppressWarnings("unchecked")
  public void updateFilter(DepanFxBaseFilterData filterData) {
    baseFilter = (T) filterData;
    toolNameProperty.setValue(filterData.getToolName());
    toolDescriptionProperty.setValue(filterData.getToolDescription());
    mergeModeProperty.setValue(filterData.getMergeMode());
    useClosureProperty.setValue(calcFilterClosureProperty(baseFilter));
  }

  @Override
  public DepanFxNodeFiltersTableMember getParent() {
    return parentMember;
  }

  @Override
  public String getDisplayName() {
    return toolNameProperty.getValue();
  }

  public StringProperty getDisplayNameProperty() {
    return toolNameProperty;
  }

  @Override
  public StringProperty getToolNameProperty() {
    return toolNameProperty;
  }

  @Override
  public StringProperty getToolDescriptionProperty() {
    return toolDescriptionProperty;
  }

  @Override
  public BooleanProperty getUseClosureProperty() {
    return useClosureProperty;
  }

  @Override
  public ObjectProperty<FilterMergeMode> getMergeModeProperty() {
    return mergeModeProperty;
  }

  protected T getFilterData() {
    return baseFilter;
  }

  /**
   * 'cuz sometimes it's immutable (e.g. don't use closure).
   */
  private boolean calcFilterClosureProperty(DepanFxBaseFilterData baseFilter) {
    return DepanFxNodeFiltersRegistry.getClosure(baseFilter).orElse(false);
  }
}
