package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.nodelist.link.DepanFxLink;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodeview.jogl.JoglLines;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData.LinkDisplayEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EdgeDisplayController {

  private final DepanFxJoglView joglView;

  /**
   * Some edges are individually styled.
   */
  private final Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay;

  private DepanFxNodeViewLinkDisplayData displayInfo;

  private DepanFxLineDisplayData remainderDisplay;

  private String remainderLabel;

  private boolean remainderVisible;

  private Collection<GraphEdge> directEdges = new ArrayList<>();

  private Collection<GraphEdge> remainderEdges = new ArrayList<>();

  private final Map<DepanFxLinkMatcherDocument, Collection<GraphEdge>>
      edgeDisplayGroup = new HashMap<>();

  public EdgeDisplayController(
      DepanFxJoglView joglView,
      DepanFxNodeViewLinkDisplayData displayInfo,
      Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay,
      boolean remainderVisible, String remainderLabel,
      DepanFxLineDisplayData remainderDisplay) {
    this.joglView = joglView;
    this.displayInfo = displayInfo;
    this.edgeDisplay = edgeDisplay;
    this.remainderVisible = remainderVisible;
    this.remainderLabel = remainderLabel;
    this.remainderDisplay = remainderDisplay;
  }

  public static EdgeDisplayController of(
      DepanFxJoglView joglView,
      DepanFxNodeViewData viewData) {
    return new EdgeDisplayController(
        joglView,
        viewData.getLinkDisplayDocRsrc().getResource(),
        viewData.getEdgeDisplay(),
        viewData.getRemainerVisible(),
        viewData.getRemainderLabel(),
        viewData.getRemainerDisplay());
  }

  public void setLinkDisplay(DepanFxNodeViewLinkDisplayData displayInfo) {
    this.displayInfo = displayInfo;

    // Capture the edges before we zap the current assignments
    List<GraphEdge> updateEdges = new ArrayList<>();
    edgeDisplayGroup.values().stream()
        .flatMap(s -> s.stream())
        .forEach(updateEdges::add);
    remainderEdges.forEach(updateEdges::add);

    edgeDisplayGroup.clear();
    remainderEdges.clear();

    // Rebuild with new display info.
    updateEdges.forEach(this::installEdge);
  }

  public void updateEdgeDisplayByMatcher(
      DepanFxLinkMatcherDocument matcher,
      LinkDisplayEntry displayEntry) {
    Collection<GraphEdge> updateEdges = edgeDisplayGroup.get(matcher);
    if (updateEdges != null) {
      updateEdges.forEach(e -> updateMatchedEdge(e, matcher, displayEntry));
    }
  }

  public void installEdge(GraphEdge edge) {
    // Prefer direct edge display
    DepanFxLineDisplayData edgeDirect = edgeDisplay.get(edge);
    if (edgeDirect != null) {
      addDirectEdge(edge, edgeDirect);
      return;
    }

    // Mostly, edges display per matcher
    Optional<LinkDisplayEntry> entryMatch =
        displayInfo.getLinkDisplayEntry(edge);
    if (entryMatch.isPresent()) {
      addMatchedEdge(edge, entryMatch.get());
      return;
    }

    // Fall through for any missed edges
    addRemainderEdge(edge);
  }

  public Map<GraphEdge, DepanFxLineDisplayData> getEdgeDisplay() {
    return edgeDisplay;
  }

  public boolean getRemainderVisible() {
    return remainderVisible;
  }

  public String getRemainderLabel() {
    return remainderLabel;
  }

  public DepanFxLineDisplayData getRemainderDisplay() {
    return remainderDisplay;
  }

  private void addDirectEdge(GraphEdge edge, DepanFxLineDisplayData edgeDisplay) {
    directEdges .add(edge);
    JoglLines.installEdge(joglView, edge, "Direct", edgeDisplay);
  }

  private void addMatchedEdge(GraphEdge edge, LinkDisplayEntry lineDisplay) {

    // Record edge with the matcher,
    DepanFxLinkMatcherDocument matcher =
        lineDisplay.getLinkRsrc().getResource();
    edgeDisplayGroup
        .computeIfAbsent(matcher, m -> new ArrayList<>())
        .add(edge);

    updateMatchedEdge(edge, matcher, lineDisplay);
  }

  private void updateMatchedEdge(
      GraphEdge edge,
      DepanFxLinkMatcherDocument matcher,
      LinkDisplayEntry lineDisplay) {

    DepanFxLink link = matcher.getMatcher().match(edge).get();
    JoglLines.installLine(joglView, edge, link, lineDisplay);
  }

  private void addRemainderEdge(GraphEdge edge) {
    remainderEdges.add(edge);
    JoglLines.installEdge(joglView, edge, remainderLabel, remainderDisplay);
  }
}
