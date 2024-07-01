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
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxReferencedFilterData;

public class DepanFxNodeFiltersReferencedMember
    extends DepanFxNodeFiltersDisplayMember<DepanFxReferencedFilterData> {

  public DepanFxNodeFiltersReferencedMember(
      DepanFxNodeFiltersTableMember parentMember,
      DepanFxReferencedFilterData referencedFilter) {
    super(parentMember, referencedFilter);
  }

  public DepanFxBaseFilterData getFilter() {
    return getFilterData().getFilter();
  }

  @Override
  public DepanFxBaseFilterData prepareFilterData() {
    return new DepanFxReferencedFilterData(
        getToolNameProperty().get(), getToolDescriptionProperty().get(),
        getMergeModeProperty().get(), getFilterData().getFilterResource(),
        getUseClosureProperty().get());
  }
}
