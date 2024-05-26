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
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class DepanFxNodeFiltersRootMember
    implements DepanFxNodeFiltersTableMember {

  private final DepanFxWorkspace workspace;

  private final ObservableList<DepanFxBaseFilterData> filterItems;

  public DepanFxNodeFiltersRootMember(
      DepanFxWorkspace workspace,
      List<DepanFxBaseFilterData> filterItems) {
    this.workspace = workspace;
    this.filterItems = FXCollections.observableArrayList(filterItems);
  }

  public DepanFxNodeFiltersRootMember(
      DepanFxWorkspace workspace) {
    this(workspace, Collections.emptyList());
  }

  public DepanFxWorkspace getWorkspace() {
    return workspace;
  }

  public void addMemberListener(
      ListChangeListener<DepanFxBaseFilterData> chgListener) {
    filterItems.addListener(chgListener);
  }

  @Override
  public String getDisplayName() {
    return "<root>";
  }

  public Stream<DepanFxBaseFilterData> streamMembers() {
    return filterItems.stream();
  }

  public void add(DepanFxBaseFilterData filterMember) {
    filterItems.add(filterMember);
  }

  public void set(DepanFxBaseFilterData filterMember) {
    filterItems.clear();
    filterItems.add(filterMember);
  }

  public void setAll(List<? extends DepanFxBaseFilterData> seqMemebers) {
    filterItems.setAll(seqMemebers);
  }
}
