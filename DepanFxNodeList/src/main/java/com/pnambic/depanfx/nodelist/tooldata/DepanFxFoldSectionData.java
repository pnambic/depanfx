/*
 * Copyright 2025 The Depan Project Authors
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

import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Persisted data for fold sections.
 */
public class DepanFxFoldSectionData extends DepanFxBaseSectionData {

  public static final String FOLD_SECTION_TOOL_EXT = "dxsti";

  public static final String FOLD_SECTION_LABEL = "Fold";

  private final DepanFxContainerOrder containerOrder;

  private final DepanFxWorkspaceResource<DepanFxNodeFoldData> foldRsrc;

  public DepanFxFoldSectionData(
      String toolName, String toolDescription,
      String sectionLabel, boolean displayNodeCount,
      OrderBy orderBy,
      OrderDirection orderDirection,
      DepanFxContainerOrder containerOrder,
      DepanFxWorkspaceResource<DepanFxNodeFoldData> foldRsrc) {
    super(toolName, toolDescription,
        sectionLabel, displayNodeCount, orderBy, orderDirection);

    // Collation criteria
    this.containerOrder = containerOrder;

    this.foldRsrc = foldRsrc;
  }

  public static DepanFxFoldSectionData emptyFoldSectionData(
      DepanFxWorkspace workspace, DepanFxWorkspaceResource<GraphDocument> graphRsrc) {

    return new DepanFxFoldSectionData(
        "Empty Fold Section", "Empty fold section.",
        FOLD_SECTION_LABEL, true,
        OrderBy.NODE_KEY, OrderDirection.FORWARD, DepanFxContainerOrder.FIRST,
        workspace.addScratchResource(
            DepanFxNodeFoldData.emptyNodeFoldData(graphRsrc)));
  }

  public DepanFxContainerOrder getContainerOrder() {
    return containerOrder;
  }

  public DepanFxWorkspaceResource<DepanFxNodeFoldData> getNodeFoldResource() {
    return foldRsrc;
  }

  public Stream<DepanFxNodeFoldData.NodeNest> streamNodeNests() {
    return foldRsrc.getResource().streamNodeNests();
  }

  public static File buildCurrentToolFile(
      Path parentPath, String dataLabel) {
    String toolName = DepanFxWorkspaceFactory.buildDocumentTimestampName(
        dataLabel, FOLD_SECTION_TOOL_EXT);
    return parentPath.resolve(toolName).toFile();
  }
}
