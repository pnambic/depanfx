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

import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxNodeFiltersRootItem extends DepanFxNodeFiltersTableItem {

  private final DepanFxNodeFiltersRegistry nodeFiltersRegistry;

  private boolean childrenLoaded = false;

  public DepanFxNodeFiltersRootItem(
      DepanFxNodeFiltersRootMember rootInfo,
      DepanFxNodeFiltersRegistry nodeFiltersRegistry) {
    super(rootInfo);
    this.nodeFiltersRegistry = nodeFiltersRegistry;
    rootInfo.addMemberListener(this::onMembersChanged);
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  @Override
  public ObservableList<TreeItem<DepanFxNodeFiltersTableMember>> getChildren() {
    if (!childrenLoaded) {
      childrenLoaded = true;
      super.getChildren().setAll(buildChildren());
    }

    return super.getChildren();
  }

  private ObservableList<TreeItem<DepanFxNodeFiltersTableMember>> buildChildren() {

    DepanFxNodeFiltersRootMember root = (DepanFxNodeFiltersRootMember) getValue();

    ObservableList<TreeItem<DepanFxNodeFiltersTableMember>> result =
        FXCollections.observableArrayList();

    root.streamMembers()
        .map(this::buildTableItem)
        .forEach(result::add);
    return result;
  }

  private TreeItem<DepanFxNodeFiltersTableMember> buildTableItem(
      DepanFxBaseFilterData filter) {
    return nodeFiltersRegistry.buildTableItem(this.getValue(), filter);
  }

  private void onMembersChanged(
      Change<? extends DepanFxBaseFilterData> memberChange) {
    while (memberChange.next()) {
      if (memberChange.wasAdded()) {
        List<TreeItem<DepanFxNodeFiltersTableMember>> insertItems =
            memberChange.getAddedSubList().stream()
                .map(this::buildTableItem)
                .collect(Collectors.toList());
        getChildren().addAll(memberChange.getFrom(), insertItems);
      }
      if (memberChange.wasRemoved()) {
        int fromIndex = memberChange.getFrom();
        int toIndex = fromIndex + memberChange.getRemovedSize();
        getChildren().remove(fromIndex, toIndex);
      }
    }
  }
}
