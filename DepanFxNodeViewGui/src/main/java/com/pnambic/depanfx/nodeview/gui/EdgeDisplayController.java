package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.nodelist.link.DepanFxLink;
import com.pnambic.depanfx.nodelist.link.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodeview.jogl.JoglLines;
import com.pnambic.depanfx.nodeview.jogl.JoglPane;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData.LinkDisplayEntry;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class EdgeDisplayController {

  private final JoglPane joglPane;

  /**
   * Some edges are individually styled.
   */
  private final Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay;

  private DepanFxNodeViewLinkDisplayData displayInfo;

  private DepanFxLineDisplayData remainderDisplay;

  private String remainderLabel;

  private boolean remainderVisible;

  ///////////////////////////////////
  // Track edges in each group

  private Collection<GraphEdge> directEdges = new ArrayList<>();

  private Collection<GraphEdge> remainderEdges = new ArrayList<>();

  private final Map<DepanFxLinkMatcherDocument, Collection<GraphEdge>>
      edgeDisplayGroup = new HashMap<>();

  /**
   * The matchers that are currently visible.
   * Not the complete inventory of matchers for visibility.
   */
  private final Set<DepanFxLinkMatcherDocument> visiblityMatchers =
      new HashSet<>();

  public EdgeDisplayController(
      JoglPane joglPane,
      DepanFxNodeViewLinkDisplayData displayInfo,
      Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay,
      boolean remainderVisible, String remainderLabel,
      DepanFxLineDisplayData remainderDisplay) {
    this.joglPane = joglPane;
    this.displayInfo = displayInfo;
    this.edgeDisplay = edgeDisplay;
    this.remainderVisible = remainderVisible;
    this.remainderLabel = remainderLabel;
    this.remainderDisplay = remainderDisplay;
    resetVisiblityMatchers();
  }

  public static EdgeDisplayController of(
      JoglPane joglPane, DepanFxNodeViewData viewData) {
    return new EdgeDisplayController(
        joglPane,
        viewData.getLinkDisplayDocRsrc().getResource(),
        viewData.getEdgeDisplay(),
        viewData.getRemainerVisible(),
        viewData.getRemainderLabel(),
        viewData.getRemainerDisplay());
  }

  public DepanFxNodeViewLinkDisplayData getLinkDisplayInfo() {
    return displayInfo;
  }

  public void setLinkDisplayInfo(DepanFxNodeViewLinkDisplayData displayInfo) {
    this.displayInfo = displayInfo;
    setLinkDisplay();
  }

  public void revertLinkDisplay() {
    setLinkDisplay();
  }

  public Stream<DepanFxLinkMatcherDocument> streamDisplayMatchers() {
    return edgeDisplayGroup.keySet().stream();
  }

  public Stream<DepanFxLinkMatcherDocument> streamVisibilityMatchers() {
    // TODO: A separate visibility matcher group.
    // It would start from the display matcher group.
    return edgeDisplayGroup.keySet().stream();
  }

  public void setLinkDisplayRsrc(
      DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> displayRsrc) {
    setLinkDisplayInfo(displayRsrc.getResource());
  }

  public int getMatcherEdgeCount(DepanFxLinkMatcherDocument matcher) {
    return edgeDisplayGroup
        .getOrDefault(matcher, Collections.emptyList()).size();
  }

  public boolean getMatcherVisibility(DepanFxLinkMatcherDocument matcher) {
    return visiblityMatchers.contains(matcher);
  }

  public void setMatcherVisibility(
      DepanFxLinkMatcherDocument matcher, boolean isVisible) {
    if (isVisible) {
      visiblityMatchers.add(matcher);
    }
    else {
      visiblityMatchers.remove(matcher);
    }
    edgeDisplayGroup
        .getOrDefault(matcher, Collections.emptyList())
        .forEach(e -> setEdgeVisible(e, isVisible));
  }

  public boolean getRemainderVisibility() {
    return remainderVisible;
  }

  public void setRemainderVisibility(boolean isVisible) {
    this.remainderVisible = isVisible;
    remainderEdges
        .forEach(e -> setEdgeVisible(e, isVisible));
  }

  public void updateEdgeDisplayByMatcher(
      DepanFxLinkMatcherDocument matcher,
      LinkDisplayEntry displayEntry) {
    Collection<GraphEdge> updateEdges = edgeDisplayGroup.get(matcher);
    if (updateEdges != null) {
      updateEdges.forEach(e -> updateMatchedEdge(e, matcher, displayEntry));
    }
    // TODO: Update displayInfo.
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
        getDisplayData().getLinkDisplayEntry(edge);
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

  public int getRemainderCount() {
    return remainderEdges.size();
  }

  public DepanFxLineDisplayData getRemainderDisplay() {
    return remainderDisplay;
  }

  private DepanFxNodeViewLinkDisplayData getDisplayData() {
    return displayInfo;
  }

  private void setLinkDisplay() {
    // Capture the edges before we zap the current assignments
    List<GraphEdge> updateEdges = new ArrayList<>();
    edgeDisplayGroup.values().stream()
        .flatMap(s -> s.stream())
        .forEach(updateEdges::add);
    remainderEdges.forEach(updateEdges::add);

    // Rebuild with new display info.
    resetVisiblityMatchers();

    // Rebuild the edge group info.
    edgeDisplayGroup.clear();
    remainderEdges.clear();
    updateEdges.forEach(this::installEdge);
  }

  private void resetVisiblityMatchers() {

    // Start with the display matchers
    // TODO: Keep a separate set of visibility matchers,
    // somewhat in synchronization with the display matchers.
    visiblityMatchers.clear();
    getDisplayData().streamLinkDisplay()
        .map(d -> d.getLinkRsrc().getResource())
        .forEach(visiblityMatchers::add);
  }

  private void addDirectEdge(GraphEdge edge, DepanFxLineDisplayData edgeDisplay) {
    directEdges .add(edge);
    JoglLines.installEdge(joglPane, edge, "Direct", edgeDisplay);
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

  private void setEdgeVisible(GraphEdge edge, boolean isVisible) {
    JoglLines.setEdgeVisible(joglPane, edge, isVisible);
  }

  private void updateMatchedEdge(
      GraphEdge edge,
      DepanFxLinkMatcherDocument matcher,
      LinkDisplayEntry lineDisplay) {

    DepanFxLink link = matcher.getMatcher().match(edge).get();
    JoglLines.installLine(joglPane, edge, link, lineDisplay);
  }

  private void addRemainderEdge(GraphEdge edge) {
    remainderEdges.add(edge);
    JoglLines.installEdge(joglPane, edge, remainderLabel, remainderDisplay);
  }
}
