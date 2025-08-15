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

import com.pnambic.depanfx.edgematchers.tooldata.DepanFxBaseMatcherDocument;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.io.File;
import java.nio.file.Path;

/**
 * Persisted data for tree sections.
 */
public class DepanFxTreeSectionData extends DepanFxBaseSectionData {

  public static final String TREE_SECTION_TOOL_EXT = "dtsti";

  public static final String BASE_SECTION_LABEL = "Tree";

  private final DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> linkMatcherRsrc;

  private final boolean inferMissingParents;

  private final DepanFxContainerOrder containerOrder;

  public DepanFxTreeSectionData(
      String toolName, String toolDescription,
      String sectionLabel, boolean displayNodeCount,
      DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> linkMatcherRsrc,
      boolean inferMissingParents,
      OrderBy orderBy,
      DepanFxContainerOrder containerOrder,
      OrderDirection orderDirection) {
    super(toolName, toolDescription,
        sectionLabel, displayNodeCount, orderBy, orderDirection);

    // Tree construction
    this.linkMatcherRsrc = linkMatcherRsrc;
    this.inferMissingParents = inferMissingParents;

    // Collation criteria
    this.containerOrder = containerOrder;
  }

  public DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> getLinkMatcherRsrc() {
    return linkMatcherRsrc;
  }

  public DepanFxContainerOrder getContainerOrder() {
    return containerOrder;
  }

  public boolean inferMissingParents() {
    return inferMissingParents;
  }

  public static File buildCurrentToolFile(
      Path parentPath, String dataLabel) {
    String toolName = DepanFxWorkspaceFactory.buildDocumentTimestampName(
        dataLabel, TREE_SECTION_TOOL_EXT);
    return parentPath.resolve(toolName).toFile();
  }
}
