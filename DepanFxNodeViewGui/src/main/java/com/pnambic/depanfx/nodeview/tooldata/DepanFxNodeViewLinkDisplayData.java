package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class DepanFxNodeViewLinkDisplayData extends DepanFxBaseToolData {

  public static class LinkDisplayEntry {

    private final String linkLabel;

    private final DepanFxWorkspaceResource linkRsrc;

    private final DepanFxLineDisplayData lineDisplay;

    public LinkDisplayEntry(
        String linkLabel,
        DepanFxWorkspaceResource linkRsrc,
        DepanFxLineDisplayData lineDisplay) {
      this.linkLabel = linkLabel;
      this.linkRsrc = linkRsrc;
      this.lineDisplay = lineDisplay;
    }

    public String getLinkLabel() {
      return linkLabel;
    }

    public DepanFxWorkspaceResource getLinkRsrc() {
      return linkRsrc;
    }

    public DepanFxLineDisplayData getLineDisplay() {
      return lineDisplay;
    }
  }

  public static final String LINK_DISPLAY_DIR = "Link Display";

  public static final Path LINK_DISPLAY_PATH =
      DepanFxNodeViewData.NODE_VIEW_TOOL_PATH.resolve(LINK_DISPLAY_DIR);

  public static final String NODE_VIEW_LINK_DISPLAY_EXT = "dnvedi";

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

  public Optional<DepanFxLineDisplayData> getEdgeDisplayData(GraphEdge edge) {
    return linkDisplayEntries.stream()
        .filter(d -> handlesEdge(d, edge))
        .map(d -> d.getLineDisplay())
        .findFirst();
  }

  public Stream<LinkDisplayEntry> streamLinkDisplay() {
    return linkDisplayEntries.stream();
  }

  private boolean handlesEdge(LinkDisplayEntry displayInfo, GraphEdge edge) {
    DepanFxLinkMatcherDocument matcherDoc =
        (DepanFxLinkMatcherDocument) displayInfo.getLinkRsrc().getResource();
    return matcherDoc.getMatcher().match(edge).isPresent();
  }
}
