/*
 * Copyright 2023 The Depan Project Authors
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

package com.pnambic.depanfx.nodelist.gui.tooldata;

import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData.OrderDirection;

/**
 * Persisted data for flat sections.
 */
public class DepanFxFlatSectionData extends DepanFxBaseSectionData {

  public static final String FLAT_SECTION_TOOL_EXT = "dfsti";

  public static final String BASE_SECTION_LABEL = "Section";

  private final OrderBy orderBy;

  public DepanFxFlatSectionData(
      String toolName, String toolDescription,
      String sectionLabel, boolean displayNodeCount,
      OrderBy orderBy, OrderDirection orderDirection) {
    super(toolName, toolDescription,
        sectionLabel, displayNodeCount, orderDirection);

    // Collation criteria
    this.orderBy = orderBy;
  }

  public OrderBy getOrderBy() {
    return orderBy;
  }
}
