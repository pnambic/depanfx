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
package com.pnambic.depanfx.nodelist.tooldata;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

/**
 * Persisted data for sections.
 */
public class DepanFxBaseSectionData extends DepanFxBaseToolData {

  private final String sectionLabel;

  private final boolean displayNodeCount;

  private final OrderDirection orderDirection;

  public DepanFxBaseSectionData(String toolName, String toolDescription,
      String sectionLabel, boolean displayNodeCount,
      OrderDirection orderDirection) {
    super(toolName, toolDescription);
    // Column header
    this.sectionLabel = sectionLabel;
    this.displayNodeCount = displayNodeCount;
    // Collation criteria
    this.orderDirection = orderDirection;
  }

  public OrderDirection getOrderDirection() {
    return orderDirection;
  }

  public String getSectionLabel() {
    return sectionLabel;
  }

  public boolean displayNodeCount() {
    return displayNodeCount;
  }
}
