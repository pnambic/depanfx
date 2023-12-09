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

package com.pnambic.depanfx.nodelist.tooldata;

import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.io.File;
import java.nio.file.Path;

/**
 * Persisted data for flat sections.
 */
public class DepanFxFlatSectionData extends DepanFxBaseToolData {

  public static final String FLAT_SECTION_TOOL_EXT = "dfsti";

  public static final String BASE_SECTION_LABEL = "Section";

  private final String sectionLabel; // 1

  private final boolean displayNodeCount; // 3

  private final OrderBy orderBy;

  private final OrderDirection orderDirection;

  public DepanFxFlatSectionData(String toolName, String toolDescription,
      String sectionLabel, boolean displayNodeCount,
      OrderBy orderBy, OrderDirection orderDirection) {
    super(toolName, toolDescription);
    // Column header
    this.sectionLabel = sectionLabel;
    this.displayNodeCount = displayNodeCount;
    // Collation criteria
    this.orderBy = orderBy;
    this.orderDirection = orderDirection;
  }

  public OrderBy getOrderBy() {
    return orderBy;
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

  public static File buildCurrentToolFile(
      Path parentPath, String dataLabel) {
    String toolName = DepanFxWorkspaceFactory.buildDocumentTimestampName(
        dataLabel, FLAT_SECTION_TOOL_EXT);
    return parentPath.resolve(toolName).toFile();
  }
}
