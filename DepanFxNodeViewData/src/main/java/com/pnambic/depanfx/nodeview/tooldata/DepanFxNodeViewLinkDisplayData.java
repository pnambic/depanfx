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
package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.base.tooldata.DepanFxBaseToolData;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxBaseMatcherDocument;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcherSequenceDocument;
import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class DepanFxNodeViewLinkDisplayData extends DepanFxBaseToolData {

  public static class LinkDisplayEntry {

    private final String linkLabel;

    private final DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> linkRsrc;

    private final DepanFxLineDisplayData lineDisplay;

    public LinkDisplayEntry(
        String linkLabel,
        DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> linkRsrc,
        DepanFxLineDisplayData lineDisplay) {
      this.linkLabel = linkLabel;
      this.linkRsrc = linkRsrc;
      this.lineDisplay = lineDisplay;
    }

    public String getLinkLabel() {
      return linkLabel;
    }

    public DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> getLinkRsrc() {
      return linkRsrc;
    }

    public DepanFxLineDisplayData getLineDisplay() {
      return lineDisplay;
    }
  }

  public static final String NODE_VIEW_LINK_DISPLAY_EXT = "dnvedi";

  public static final String EDGE_DISPLAY_CONTEXT_RESOURCE_NAME = "Edge Display";

  public static final String EDGE_DISPLAY_DIR = "Edge Display";

  public static final Path EDGE_DISPLAY_TOOL_PATH =
      DepanFxNodeViewData.NODE_VIEW_TOOL_PATH.resolve(EDGE_DISPLAY_DIR);

  private final ContextModelId contextModelId;

  private final List<LinkDisplayEntry> linkDisplayEntries;

  public DepanFxNodeViewLinkDisplayData(
      String toolName, String toolDescription,
      ContextModelId contextModelId,
      List<LinkDisplayEntry> linkDisplayEntries) {
    super(toolName, toolDescription);
    this.contextModelId = contextModelId;
    this.linkDisplayEntries = linkDisplayEntries;
  }

  public ContextModelId getContextModelId() {
    return contextModelId;
  }

  public boolean isFor(ContextModelId modelId) {
    return this.contextModelId.equals(modelId);
  }

  public Optional<LinkDisplayEntry> getLinkDisplayEntry(GraphEdge edge) {
    return linkDisplayEntries.stream()
        .filter(d -> handlesEdge(d, edge))
        .findFirst();
  }

  public Optional<DepanFxLineDisplayData> getEdgeDisplayData(GraphEdge edge) {
    return linkDisplayEntries.stream()
        .filter(d -> handlesEdge(d, edge))
        .map(d -> d.getLineDisplay())
        .findFirst();
  }

  public DepanFxLinkMatcherSequenceDocument asLinkMatcherSequenceDoc() {
    List<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>> matcherSeq =
        new ArrayList<>(linkDisplayEntries.size());
    streamLinkDisplay()
        .map(d -> d.getLinkRsrc())
        .forEach(matcherSeq::add);

    return new DepanFxLinkMatcherSequenceDocument(
        getToolName() + " Sequence",
        "Edge sequence from " + getToolDescription(),
        matcherSeq);
  }

  public Stream<LinkDisplayEntry> streamLinkDisplay() {
    return linkDisplayEntries.stream();
  }

  private boolean handlesEdge(LinkDisplayEntry displayInfo, GraphEdge edge) {
    DepanFxBaseMatcherDocument matcherDoc =
        displayInfo.getLinkRsrc().getResource();
    if (matcherDoc instanceof DepanFxLinkMatcherDocument linkInfo) {
      return linkInfo.getMatcher().match(edge).isPresent();
    }

    return false;
  }
}
