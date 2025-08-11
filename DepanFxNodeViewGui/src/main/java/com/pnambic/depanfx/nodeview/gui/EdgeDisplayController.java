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
package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLink;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcherSequenceDocument;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodeview.jogl.JoglLines;
import com.pnambic.depanfx.nodeview.jogl.JoglPane;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData.LinkDisplayEntry;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EdgeDisplayController {

  private static final Logger LOG =
      LoggerFactory.getLogger(EdgeDisplayController.class);

  private final JoglPane joglPane;

  /**
   * Some edges are individually styled.
   */
  private final Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay;

  private DepanFxLineDisplayData remainderDisplay;

  private String remainderLabel;

  private boolean remainderVisible;

  ///////////////////////////////////
  // Track edges in each group

  private Collection<GraphEdge> directEdges = new ArrayList<>();

  private Collection<GraphEdge> remainderEdges = new ArrayList<>();

  ///////////////////////////////////
  // Edge Visibility

  /**
   * Number of showing visibility matchers for each edges.  Transitions to
   * and from zero cause a change in the edge's display status.
   */
  private final Map<GraphEdge, Integer> edgeVisibleMatchers = new HashMap<>();

  /**
   * Track each edge that is matched by any visibility matcher.
   */
  private final Map<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>, Collection<GraphEdge>>
      edgeVisibleGroup = new HashMap<>();

  /**
   * The matchers that are currently visible.
   * Not the complete inventory of matchers for visibility.
   */
  private final Set<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
      visibleMatcherRsrcs = new HashSet<>();

  private DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> availableMatchersRsrc;

  private DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> visibleMatchersRsrc;

  /**
   * Filter resources that determine which edges are visible.
   * These matchers are independent of the matchers associated with edge display.
   */
  private DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> edgeFiltersRsrc;

  ///////////////////////////////////
  // Edge Display

  /**
   * Track which edges's displays are handled by the display matcher.
   */
  private final Map<
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>,
      Collection<GraphEdge>>
      edgeDisplayGroup = new HashMap<>();

  private DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> displayRsrc;

  public EdgeDisplayController(
      JoglPane joglPane,
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> availableMatchersRsrc,
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> visibleMatchersRsrc,
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> edgeFiltersRsrc,
      DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> displayRsrc,
      Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay,
      boolean remainderVisible, String remainderLabel,
      DepanFxLineDisplayData remainderDisplay) {
    this.joglPane = joglPane;
    this.availableMatchersRsrc = availableMatchersRsrc;
    this.visibleMatchersRsrc = visibleMatchersRsrc;
    this.edgeFiltersRsrc = edgeFiltersRsrc;
    this.displayRsrc = displayRsrc;
    this.edgeDisplay = edgeDisplay;
    this.remainderVisible = remainderVisible;
    this.remainderLabel = remainderLabel;
    this.remainderDisplay = remainderDisplay;

    // Initialize from provided set, so all matchers are initially known.
    availableMatchersRsrc.getResource().streamMatchers()
        .forEach(m -> edgeVisibleGroup.put(m, new ArrayList<>()));
    visibleMatchersRsrc.getResource().streamMatchers()
        .forEach(r -> setMatcherVisibility(r, true));
  }

  public static EdgeDisplayController of(
      JoglPane joglPane, DepanFxNodeViewData viewData) {

    return new EdgeDisplayController(
        joglPane,
        viewData.getAvailableEdgeResource(),
        viewData.getVisibleEdgeResource(),
        viewData.getEdgeFiltersResource(),
        viewData.getLinkDisplayResource(),
        viewData.getEdgeDisplay(),
        viewData.getRemainderEdgesVisible(),
        viewData.getRemainderEdgesLabel(),
        viewData.getRemainderEdgeDisplay());
  }

  public DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData>
  getLinkDisplayResource() {
    return displayRsrc;
  }

  public void revertLinkDisplay() {
    setLinkDisplay();
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

  public boolean getRemainderVisibility() {
    return remainderVisible;
  }

  public void setRemainderVisibility(boolean isVisible) {
    this.remainderVisible = isVisible;
    remainderEdges
        .forEach(e -> setEdgeVisible(e, isVisible));
  }

  public void forEachAvailableMatchers(
      Consumer<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>> filterSrvc) {
    streamAvailableMatchers().forEach(filterSrvc);
  }

  /**
   * Provides an alphabetically ordered sequence of matcher resources,
   * based on the tool name of each matcher.
   *
   * This ensure that consumers always see the same order,
   * regardless of set construction.
   */
  public Stream<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
  streamAvailableMatchers() {
    return edgeVisibleGroup.keySet().stream()
        .sorted(DepanFxWorkspaceResource.BY_RESOURCE_NAME);
  }

  /**
   * Provides an alphabetically ordered sequence of matcher resources,
   * based on the tool name of each matcher.
   *
   * This ensure that consumers always see the same order,
   * regardless of set construction.
   */
  public Stream<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
  streamVisibilityMatchers() {
    return visibleMatcherRsrcs.stream()
        .sorted(DepanFxWorkspaceResource.BY_RESOURCE_NAME);
  }

  public Stream<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
  streamEdgeFilters() {
    return edgeFiltersRsrc.getResource().streamMatchers();
  }

  public void setLinkDisplayResource(
      DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> displayRsrc) {
    this.displayRsrc = displayRsrc;
    setLinkDisplay();
  }

  public int getVisiblityMatcherEdgeCount(
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc) {
    return edgeVisibleGroup
        .getOrDefault(matcherRsrc, Collections.emptyList()).size();
  }

  public int getDisplayMatcherEdgeCount(
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc) {
    return edgeDisplayGroup
        .getOrDefault(matcherRsrc, Collections.emptyList()).size();
  }

  public boolean getMatcherVisibility(
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc) {
    return visibleMatcherRsrcs.contains(matcherRsrc);
  }

  public void clearMatcherVisibility() {
    // Skip over any increments, and just make all edges hidden.
    edgeVisibleMatchers.forEach((e, c) -> {
      edgeVisibleMatchers.put(e, Integer.valueOf(0));
      setEdgeVisible(e, false);
    });
    visibleMatcherRsrcs.clear();
  }

  public void setMatcherVisibility(
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc,
      boolean isVisible) {
    checkKnownMatcher(matcherRsrc);
    boolean currVisible = visibleMatcherRsrcs.contains(matcherRsrc);
    // Nothing to change.
    if (currVisible == isVisible) {
      return;
    }

    Collection<GraphEdge> updateEdges = edgeVisibleGroup.get(matcherRsrc);
    if (isVisible) {
      visibleMatcherRsrcs.add(matcherRsrc);
      updateEdges.forEach(e -> increaseVisible(e));
      return;
    }

    visibleMatcherRsrcs.remove(matcherRsrc);
    updateEdges.forEach(e -> decreaseVisible(e));
  }

  public void setVisibiltyResource(
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> visibleMatchersRsrc) {
    this.visibleMatchersRsrc = visibleMatchersRsrc;

    clearMatcherVisibility();
    visibleMatchersRsrc.getResource().streamMatchers()
        .forEach(r -> setMatcherVisibility(r, true));
  }

  /**
   * Add the matcher document to the set of matchers that track edge
   * visibility. Because the added matcher is not added to the set of visible
   * matchers, the added matcher has a not visible status.
   */
  public void addAvailableMatcher(
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc) {
    List<GraphEdge> matcherEdges = edgeVisibleMatchers.keySet().stream()
        .filter(e ->
            matcherRsrc.getResource().getMatcher().match(e).isPresent())
        .collect(Collectors.toList());
    edgeVisibleGroup.put(matcherRsrc, matcherEdges);
  }

  public void updateAvailableMatchers(
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> availableMatchersRsrc) {

    clearMatcherVisibility();
    edgeVisibleGroup.isEmpty();

    this.availableMatchersRsrc = availableMatchersRsrc;
    availableMatchersRsrc.getResource().streamMatchers()
        .forEach(m -> edgeVisibleGroup.put(m, new ArrayList<>()));
    edgeVisibleMatchers.keySet().stream()
        .forEach(e -> installEdgeVisible(e));
  }

  public void updateEdgeDisplayByMatcher(
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc,
      LinkDisplayEntry displayEntry) {
    Collection<GraphEdge> updateEdges = edgeDisplayGroup.get(matcherRsrc);
    if (updateEdges != null) {
      updateEdges.forEach(e -> updateMatchedEdge(e, matcherRsrc, displayEntry));
    }
  }

  public void installEdge(GraphEdge edge) {
    int visibleCount = installEdgeVisible(edge);
    installEdgeDisplay(edge, visibleCount > 0);
  }

  public void setEdgeFilterResource(
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> edgeFilterRsrc) {
    this.edgeFiltersRsrc = edgeFilterRsrc;
  }

  private boolean checkEdgeFilter(GraphEdge edge) {
    return edgeFiltersRsrc.getResource().streamMatchers()
        .anyMatch(m -> m.getResource().getMatcher().match(edge).isPresent());
  }

  public DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>
  forUpdateAvailableMatcherSequenceDoc() {

    List<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>> matcherRefs =
        streamAvailableMatchers().collect(Collectors.toList());

    DepanFxLinkMatcherSequenceDocument availableEdgeInfo =
        availableMatchersRsrc.getResource();
    DepanFxLinkMatcherSequenceDocument matcherInfo =
        new DepanFxLinkMatcherSequenceDocument(
            availableEdgeInfo.getToolName(),
            availableEdgeInfo.getToolDescription(),
            matcherRefs);

    return DepanFxWorkspaceResource.forUpdate(availableMatchersRsrc, matcherInfo);
  }

  public DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>
  forUpdateVisibleMatcherSequenceDoc() {

    List<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>> vizMatcherRsrcs =
        streamVisibilityMatchers().toList();

    DepanFxLinkMatcherSequenceDocument visibleMatchersInfo =
        visibleMatchersRsrc.getResource();
    DepanFxLinkMatcherSequenceDocument matcherInfo =
        new DepanFxLinkMatcherSequenceDocument(
            visibleMatchersInfo.getToolName(),
            visibleMatchersInfo.getToolDescription(),
            vizMatcherRsrcs);

    return DepanFxWorkspaceResource.forUpdate(visibleMatchersRsrc, matcherInfo);
  }

  public DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>
  forSaveEdgeFilterSequenceDoc() {

    if (edgeFiltersRsrc == null) {
      return null;
    }

    List<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>> edgeFiltersRsrcs =
        streamEdgeFilters().toList();

    DepanFxLinkMatcherSequenceDocument edgeFiltersInfo =
        edgeFiltersRsrc.getResource();
    DepanFxLinkMatcherSequenceDocument matcherInfo =
        new DepanFxLinkMatcherSequenceDocument(
            edgeFiltersInfo.getToolName(),
            edgeFiltersInfo.getToolDescription(),
            edgeFiltersRsrcs);

    return DepanFxWorkspaceResource.forUpdate(edgeFiltersRsrc, matcherInfo);
  }

  public DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>
  forEditEdgeFilterSequenceDoc(
      DepanFxWorkspace workspace, GraphDocument graphDoc) {

    if (edgeFiltersRsrc == null) {
      DepanFxLinkMatcherSequenceDocument filters =
          new DepanFxLinkMatcherSequenceDocument(
              "Edge Filters", "Edge visibility filters",
              Collections.emptyList());
      return workspace.addScratchResource(filters);
    }

    List<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>> edgeFiltersRsrcs =
        streamEdgeFilters().toList();

    DepanFxLinkMatcherSequenceDocument edgeFiltersInfo =
        edgeFiltersRsrc.getResource();
    DepanFxLinkMatcherSequenceDocument matcherInfo =
        new DepanFxLinkMatcherSequenceDocument(
            edgeFiltersInfo.getToolName(),
            edgeFiltersInfo.getToolDescription(),
            edgeFiltersRsrcs);

    return DepanFxWorkspaceResource.forUpdate(edgeFiltersRsrc, matcherInfo);
  }

  private void increaseVisible(GraphEdge edge) {
    int matcherCnt = incrementCnt(edge);
    edgeVisibleMatchers.put(edge, matcherCnt);

    // If it was the first matcher, start showing the edge
    if (matcherCnt == 1) {
      setEdgeVisible(edge, true);
    }
  }

  private void decreaseVisible(GraphEdge edge) {
    int matcherCnt = decrementCnt(edge);
    edgeVisibleMatchers.put(edge, matcherCnt);

    // If this was the last matcher, stop showing the edge
    if (matcherCnt == 0) {
      setEdgeVisible(edge, false);
    }
  }

  /** Avoid any "impossible" increment. */
  private int incrementCnt(GraphEdge edge) {
    int matcherCnt = edgeVisibleMatchers.get(edge).intValue();
    int maxCnt = edgeVisibleGroup.keySet().size() - 1;
    if (matcherCnt < maxCnt) {
      return matcherCnt + 1;
    }
    LOG.error("Incrementing maximum matcher count {}", edge.toString());
    return maxCnt;
  }

  /** Avoid any "impossible" decrement. */
  private int decrementCnt(GraphEdge edge) {
    int matcherCnt = edgeVisibleMatchers.get(edge).intValue();
    if (matcherCnt > 0) {
      return matcherCnt - 1;
    }
    LOG.error("Decrementing zero matcher count {}", edge.toString());
    return 0;
  }

  private void checkKnownMatcher(
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc) {
    if (edgeVisibleGroup.keySet().contains(matcherRsrc)) {
      return;
    }
    LOG.error("Unexpected matcher {}.\nAdding matcher to available",
        matcherRsrc.getResource().getToolName());
    addAvailableMatcher(matcherRsrc);
  }

  private void addMatcherEdge(
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc,
      GraphEdge edge) {
    Collection<GraphEdge> currEdges = edgeVisibleGroup.get(matcherRsrc);
    currEdges.add(edge);
  }

  private int installEdgeVisible(GraphEdge edge) {
    List<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>> edgeMatchers =
        edgeVisibleGroup.keySet().stream()
            .filter(m -> m.getResource().getMatcher().match(edge).isPresent())
            .collect(Collectors.toList());

    edgeMatchers.stream()
        .forEach(m -> addMatcherEdge(m, edge));

    int visibleCount = (int) edgeMatchers.stream()
        .filter(m -> visibleMatcherRsrcs.contains(m))
        .count();

    edgeVisibleMatchers.put(edge, visibleCount);
    return visibleCount;
  }

  private void installEdgeDisplay(GraphEdge edge, boolean isVisible) {

    // Prefer direct edge display
    DepanFxLineDisplayData edgeDirect = edgeDisplay.get(edge);
    if (edgeDirect != null) {
      addDirectEdge(edge, edgeDirect, isVisible);
      return;
    }

    // Mostly, edges display per matcher
    Optional<LinkDisplayEntry> entryMatch =
        displayRsrc.getResource().getLinkDisplayEntry(edge);
    if (entryMatch.isPresent()) {
      addMatchedEdge(edge, entryMatch.get(), isVisible);
      return;
    }

    // Fall through for any missed edges
    addRemainderEdge(edge, isVisible);
  }

  private void setLinkDisplay() {
    // Capture the edges before we zap the current assignments
    Set<GraphEdge> updateEdges = new HashSet<>();
    edgeDisplayGroup.values().stream()
        .flatMap(s -> s.stream())
        .forEach(updateEdges::add);
    remainderEdges.forEach(updateEdges::add);

    // Rebuild the edge group info.
    edgeDisplayGroup.clear();
    remainderEdges.clear();
    updateEdges.forEach(this::installEdge);
  }

  private void addDirectEdge(
      GraphEdge edge, DepanFxLineDisplayData edgeDisplay, boolean isVisible) {
    directEdges.add(edge);
    JoglLines.installEdge(joglPane, edge, "Direct", edgeDisplay, isVisible);
  }

  private void addMatchedEdge(
      GraphEdge edge, LinkDisplayEntry lineDisplay, boolean isVisible) {

    // Record edge with the matcher.
    DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc =
        lineDisplay.getLinkRsrc();
    edgeDisplayGroup
        .computeIfAbsent(matcherRsrc, m -> new ArrayList<>())
        .add(edge);

    installMatchedEdge(edge, matcherRsrc.getResource(), lineDisplay, isVisible);
  }

  private void setEdgeVisible(GraphEdge edge, boolean isVisible) {
    JoglLines.setEdgeVisible(joglPane, edge, isVisible);
  }

  private void installMatchedEdge(
      GraphEdge edge,
      DepanFxLinkMatcherDocument matcher,
      LinkDisplayEntry lineDisplay, boolean isVisible) {

    DepanFxLink link = matcher.getMatcher().match(edge).get();
    JoglLines.installLine(joglPane, edge, link, lineDisplay, isVisible);
  }

  private void updateMatchedEdge(
      GraphEdge edge,
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc,
      LinkDisplayEntry lineDisplay) {

    DepanFxLink link = matcherRsrc.getResource().getMatcher().match(edge).get();
    JoglLines.updateLine(joglPane, edge, link, lineDisplay);
  }

  private void addRemainderEdge(GraphEdge edge, boolean isVisible) {
    remainderEdges.add(edge);
    JoglLines.installEdge(
        joglPane, edge, remainderLabel, remainderDisplay, isVisible);
  }
}
