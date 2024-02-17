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
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcher;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectResource;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.io.File;
import java.nio.file.Path;

/**
 * Persisted data for tree sections.
 */
public class DepanFxTreeSectionData extends DepanFxBaseToolData {

  public static final String TREE_SECTION_TOOL_EXT = "dtsti";

  public static final String BASE_SECTION_LABEL = "Tree";

  public enum ContainerOrder { FIRST, MIXED, LAST };

  private final String sectionLabel; // 1

  private final boolean displayNodeCount; // 3

  private final DepanFxProjectResource linkMatcherRsrc; // 2

  private final boolean inferMissingParents; // 4

  private final OrderBy orderBy;

  private final ContainerOrder containerOrder;

  private final OrderDirection orderDirection;

  public DepanFxTreeSectionData(String toolName, String toolDescription,
      String sectionLabel, boolean displayNodeCount,
      DepanFxProjectResource linkMatcherRsrc, boolean inferMissingParents,
      OrderBy orderBy, ContainerOrder containerOrder,
      OrderDirection orderDirection) {
    super(toolName, toolDescription);
    // Column header
    this.sectionLabel = sectionLabel;
    this.displayNodeCount = displayNodeCount;
    // Tree construction
    this.linkMatcherRsrc = linkMatcherRsrc;
    this.inferMissingParents = inferMissingParents;
    // Collation criteria
    this.orderBy = orderBy;
    this.containerOrder = containerOrder;
    this.orderDirection = orderDirection;
  }

  public DepanFxWorkspaceResource getLinkMatcherRsrc(
      DepanFxWorkspace workspace) {
    return linkMatcherRsrc.getResource(workspace, DepanFxTreeSectionData.class).get();
  }

  public DepanFxLinkMatcher getLinkMatcher(DepanFxWorkspace workspace) {
    return ((DepanFxLinkMatcherDocument) getLinkMatcherRsrc(workspace)
        .getResource()).getMatcher();
  }

  public OrderBy getOrderBy() {
    return orderBy;
  }

  public OrderDirection getOrderDirection() {
    return orderDirection;
  }

  public ContainerOrder getContainerOrder() {
    return containerOrder;
  }

  public String getSectionLabel() {
    return sectionLabel;
  }

  public boolean inferMissingParents() {
    return inferMissingParents;
  }

  public boolean displayNodeCount() {
    return displayNodeCount;
  }

  public static File buildCurrentToolFile(
      Path parentPath, String dataLabel) {
    String toolName = DepanFxWorkspaceFactory.buildDocumentTimestampName(
        dataLabel, TREE_SECTION_TOOL_EXT);
    return parentPath.resolve(toolName).toFile();
  }
}
