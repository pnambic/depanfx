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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class DepanFxNodeFiltersReferencedItem extends DepanFxNodeFiltersTableItem {

  private boolean childrenLoaded = false;

  public DepanFxNodeFiltersReferencedItem(
      DepanFxNodeFiltersReferencedMember refMember) {
    super(refMember);
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  @Override
  public ObservableList<TreeItem<DepanFxNodeFiltersTableMember>> getChildren() {
    if (!childrenLoaded ) {
      childrenLoaded = true;
      super.getChildren().setAll(buildChildren());
    }

    return super.getChildren();
  }

  private ObservableList<TreeItem<DepanFxNodeFiltersTableMember>> buildChildren() {

    DepanFxNodeFiltersReferencedMember refMember =
        (DepanFxNodeFiltersReferencedMember) getValue();

    ObservableList<TreeItem<DepanFxNodeFiltersTableMember>> result =
        FXCollections.observableArrayList();

    DepanFxBaseFilterData refFilter = refMember.getFilter();
    result.add(buildTableItem(refFilter));
    return result;
  }

  private TreeItem<DepanFxNodeFiltersTableMember> buildTableItem(
      DepanFxBaseFilterData filter) {
    return DepanFxNodeFiltersTableItemFactory.buildTableItem(filter);
  }
}
