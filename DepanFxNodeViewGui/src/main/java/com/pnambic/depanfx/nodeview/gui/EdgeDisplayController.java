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

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLink;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherSequenceDocument;
import com.pnambic.depanfx.nodeview.jogl.JoglLines;
import com.pnambic.depanfx.nodeview.jogl.JoglPane;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData.LinkDisplayEntry;
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

  private DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> displayRsrc;

  private DepanFxLineDisplayData remainderDisplay;

  private String remainderLabel;

  private boolean remainderVisible;

  ///////////////////////////////////
  // Track edges in each group

  private Collection<GraphEdge> directEdges = new ArrayList<>();

  private Collection<GraphEdge> remainderEdges = new ArrayList<>();

  /**
   * Number of showing visibility matchers for each edges.  Transitions to
   * and from zero cause a change in the edge's display status.
   */
  private final Map<GraphEdge, Integer> edgeVisibleMatchers = new HashMap<>();

  /**
   * Track each edge that is matched by any visibility matcher.
   */
  private final Map<DepanFxLinkMatcherDocument, Collection<GraphEdge>>
      edgeVisibleGroup = new HashMap<>();

  /**
   * Track which edges's displays are handled by the display matcher.
   */
  private final Map<DepanFxLinkMatcherDocument, Collection<GraphEdge>>
      edgeDisplayGroup = new HashMap<>();

  /**
   * The matchers that are currently visible.
   * Not the complete inventory of matchers for visibility.
   */
  private Set<DepanFxLinkMatcherDocument> visibleMatchers;

  public EdgeDisplayController(
      JoglPane joglPane,
      Set<DepanFxLinkMatcherDocument> availableMatchers,
      Set<DepanFxLinkMatcherDocument> visibleMatchers,
      DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> displayRsrc,
      Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay,
      boolean remainderVisible, String remainderLabel,
      DepanFxLineDisplayData remainderDisplay) {
    this.joglPane = joglPane;
    this.visibleMatchers = visibleMatchers;
    this.displayRsrc = displayRsrc;
    this.edgeDisplay = edgeDisplay;
    this.remainderVisible = remainderVisible;
    this.remainderLabel = remainderLabel;
    this.remainderDisplay = remainderDisplay;

    // Initialize from provided set, so all matchers are initially known.
    availableMatchers.forEach(m -> edgeVisibleGroup.put(m, new ArrayList<>()));
  }

  public static EdgeDisplayController of(
      JoglPane joglPane, DepanFxNodeViewData viewData) {
    Set<DepanFxLinkMatcherDocument> trackingMatchers =
        viewData.getAvailableEdgesDoc().streamMatchers()
            .map(r -> r.getResource())
            .collect(Collectors.toSet());

    Set<DepanFxLinkMatcherDocument> visibleMatchers =
        viewData.getVisibleEdgesDoc().streamMatchers()
            .map(r -> r.getResource())
            .collect(Collectors.toSet());

    return new EdgeDisplayController(
        joglPane,
        trackingMatchers, visibleMatchers,
        viewData.getLinkDisplayDocRsrc(),
        viewData.getEdgeDisplay(),
        viewData.getRemainderEdgesVisible(),
        viewData.getRemainderEdgesLabel(),
        viewData.getRemainderEdgeDisplay());
  }

  public DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> getLinkDisplayResource() {
    return displayRsrc;
  }

  public void revertLinkDisplay() {
    setLinkDisplay();
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

  public boolean getRemainderVisibility() {
    return remainderVisible;
  }

  public void setRemainderVisibility(boolean isVisible) {
    this.remainderVisible = isVisible;
    remainderEdges
        .forEach(e -> setEdgeVisible(e, isVisible));
  }

  public Stream<DepanFxLinkMatcherDocument> streamDisplayMatchers() {
    return edgeDisplayGroup.keySet().stream();
  }

  public Stream<DepanFxLinkMatcherDocument> streamVisibilityMatchers() {
    return edgeVisibleGroup.keySet().stream();
  }

  public void setLinkDisplayResource(
      DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> displayRsrc) {
    this.displayRsrc = displayRsrc;
    setLinkDisplay();
  }

  public int getVisiblityMatcherEdgeCount(DepanFxLinkMatcherDocument matcher) {
    return edgeVisibleGroup
        .getOrDefault(matcher, Collections.emptyList()).size();
  }

  public int getDisplayMatcherEdgeCount(DepanFxLinkMatcherDocument matcher) {
    return edgeDisplayGroup
        .getOrDefault(matcher, Collections.emptyList()).size();
  }

  public boolean getMatcherVisibility(DepanFxLinkMatcherDocument matcher) {
    return visibleMatchers.contains(matcher);
  }

  public void clearMatcherVisibility() {
    // Skip over any increments, and just make all edges hidden.
    edgeVisibleMatchers.forEach((e, c) -> {
      edgeVisibleMatchers.put(e, Integer.valueOf(0));
      setEdgeVisible(e, false);
    });
    visibleMatchers.clear();
  }

  public void setMatcherVisibility(
      DepanFxLinkMatcherDocument matcher, boolean isVisible) {
    checkKnownMatcher(matcher);
    boolean currVisible = visibleMatchers.contains(matcher);
    // Nothing to change.
    if (currVisible == isVisible) {
      return;
    }

    Collection<GraphEdge> updateEdges = edgeVisibleGroup.get(matcher);
    if (isVisible) {
      visibleMatchers.add(matcher);
      updateEdges.forEach(e -> increaseVisible(e));
      return;
    }

    visibleMatchers.remove(matcher);
    updateEdges.forEach(e -> decreaseVisible(e));
  }

  /**
   * Add the matcher document to the set of matchers that track edge
   * visibility. Because the added matcher is not added to the set of visible
   * matchers, the added matcher has a not visible status.
   */
  public void addAvailableMatcher(DepanFxLinkMatcherDocument matcher) {
    List<GraphEdge> matcherEdges = edgeVisibleMatchers.keySet().stream()
        .filter(e -> matcher.getMatcher().match(e).isPresent())
        .collect(Collectors.toList());
    edgeVisibleGroup.put(matcher, matcherEdges);
  }

  public void updateAvailableMatchers(
      Set<DepanFxLinkMatcherDocument> availableMatchers) {
    clearMatcherVisibility();
    edgeVisibleGroup.isEmpty();

    // Initialize from provided set, so all matchers are initially known.
    availableMatchers.forEach(m -> edgeVisibleGroup.put(m, new ArrayList<>()));
    edgeVisibleMatchers.keySet().stream()
        .forEach(e -> installEdgeVisible(e));
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
    int visibleCount = installEdgeVisible(edge);
    installEdgeDisplay(edge, visibleCount > 0);
  }

  public DepanFxLinkMatcherSequenceDocument buildAvailableMatcherSequenceDoc(
      ContextModelId modelId,
      List<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>> matcherRsrcs) {
    // Ensure serializable ArrayList.
    List<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
    availMatcherRsrc = new ArrayList<>();
    Set<DepanFxLinkMatcherDocument> availMatchers =
        new HashSet<>(edgeVisibleGroup.keySet());

    matcherRsrcs.stream()
        .filter(r -> availMatchers.contains(r.getResource()))
        .forEach(availMatcherRsrc::add);

    return new DepanFxLinkMatcherSequenceDocument(
        "Available Edges",
        "Available edges from ", modelId, availMatcherRsrc);
  }

  public DepanFxLinkMatcherSequenceDocument buildVisibleMatcherSequenceDoc(
      ContextModelId modelId,
      List<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>> matcherRsrcs) {
    // Ensure serializable ArrayList.
    List<DepanFxWorkspaceResource<DepanFxLinkMatcherDocument>>
    visibleMatcherRsrcs = new ArrayList<>();
    matcherRsrcs.stream()
        .filter(r -> visibleMatchers.contains(r.getResource()))
        .forEach(visibleMatcherRsrcs::add);

    return new DepanFxLinkMatcherSequenceDocument(
        " Visible Edges",
        "Visible edges from ",
        modelId, visibleMatcherRsrcs);
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

  private void checkKnownMatcher(DepanFxLinkMatcherDocument matcher) {
    if (edgeVisibleGroup.keySet().contains(matcher)) {
      return;
    }
    LOG.error("Unexpected matcher {}.\nAdding matcher to available",
        matcher.getToolName());
    addAvailableMatcher(matcher);
  }

  private void addMatcherEdge(
      DepanFxLinkMatcherDocument matcherDoc, GraphEdge edge) {
    Collection<GraphEdge> currEdges = edgeVisibleGroup.get(matcherDoc);
    currEdges.add(edge);
  }

  private int installEdgeVisible(GraphEdge edge) {
    List<DepanFxLinkMatcherDocument> edgeMatchers =
        edgeVisibleGroup.keySet().stream()
            .filter(m -> m.getMatcher().match(edge).isPresent())
            .collect(Collectors.toList());

    edgeMatchers.stream()
        .forEach(m -> addMatcherEdge(m, edge));

    int visibleCount = (int) edgeMatchers.stream()
        .filter(m -> visibleMatchers.contains(m))
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
    List<GraphEdge> updateEdges = new ArrayList<>();
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

    // Record edge with the matcher,
    DepanFxLinkMatcherDocument matcher =
        lineDisplay.getLinkRsrc().getResource();
    edgeDisplayGroup
        .computeIfAbsent(matcher, m -> new ArrayList<>())
        .add(edge);

    installMatchedEdge(edge, matcher, lineDisplay, isVisible);
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
      DepanFxLinkMatcherDocument matcher,
      LinkDisplayEntry lineDisplay) {

    DepanFxLink link = matcher.getMatcher().match(edge).get();
    JoglLines.updateLine(joglPane, edge, link, lineDisplay);
  }

  private void addRemainderEdge(GraphEdge edge, boolean isVisible) {
    remainderEdges.add(edge);
    JoglLines.installEdge(
        joglPane, edge, remainderLabel, remainderDisplay, isVisible);
  }
}
