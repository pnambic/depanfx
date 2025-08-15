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

import com.pnambic.depanfx.edgematchers.link.DepanFxLinkMatchersRegistry;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxBaseMatcherDocument;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLink;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcher;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.edgematchers.tooldata.DepanFxLinkMatcherSequenceDocument;
import com.pnambic.depanfx.graph.model.GraphEdge;
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

  private final DepanFxLinkMatchersRegistry matcherRegistry;

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
  private final Map<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>, Collection<GraphEdge>>
      edgeVisibleGroup = new HashMap<>();

  /**
   * The matchers that are currently visible.
   * Not the complete inventory of matchers for visibility.
   */
  private final Set<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>>
      visibleMatcherRsrcs = new HashSet<>();

  private DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>
      availableMatchersRsrc;

  private DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>
      visibleMatchersRsrc;

  /**
   * Edge matcher resources that determine which edges are visible.
   * These matchers are independent of the matchers associated with edge display.
   *
   * Any filtered edge is still installed with potential matchers (or remainder),
   * so no independent tracking is needed.
   */
  private DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>
      edgeFiltersRsrc;

  private List<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>>
      edgeFilterDataRsrcs;

  private List<DepanFxLinkMatcher>
      edgeFilters;

  // private Set<GraphEdge> edgeFiltered = new HashSet<>();

  ///////////////////////////////////
  // Edge Display

  /**
   * Track which edges's displays are handled by the display matcher.
   */
  private final Map<
      DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>,
      Collection<GraphEdge>>
      edgeDisplayGroup = new HashMap<>();

  private DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> displayRsrc;

  public EdgeDisplayController(
      JoglPane joglPane,
      DepanFxLinkMatchersRegistry matcherRegistry,
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> availableMatchersRsrc,
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> visibleMatchersRsrc,
      DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> edgeFiltersRsrc,
      DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> displayRsrc,
      Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay,
      boolean remainderVisible, String remainderLabel,
      DepanFxLineDisplayData remainderDisplay) {
    this.joglPane = joglPane;
    this.matcherRegistry = matcherRegistry;
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

    edgeFilterDataRsrcs = buildFilterResources();
    populateEdgeFilters();
  }

  public static EdgeDisplayController of(
      JoglPane joglPane,
      DepanFxLinkMatchersRegistry matcherRegistry,
      DepanFxNodeViewData viewData) {

    return new EdgeDisplayController(
        joglPane, matcherRegistry,
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
    updateAllEdgeDisplay();
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
      Consumer<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>> filterSrvc) {
    streamAvailableMatchers().forEach(filterSrvc);
  }

  /**
   * Provides an alphabetically ordered sequence of matcher resources,
   * based on the tool name of each matcher.
   *
   * This ensure that consumers always see the same order,
   * regardless of set construction.
   */
  public Stream<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>>
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
  public Stream<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>>
  streamVisibilityMatchers() {
    return visibleMatcherRsrcs.stream()
        .sorted(DepanFxWorkspaceResource.BY_RESOURCE_NAME);
  }

  public Stream<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>>
  streamEdgeFilters() {
    if (edgeFilterDataRsrcs != null) {
      return edgeFilterDataRsrcs.stream();
    }
    return Stream.empty();
  }

  public void setLinkDisplayResource(
      DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> displayRsrc) {
    this.displayRsrc = displayRsrc;
    updateAllEdgeDisplay();
  }

  public int getVisiblityMatcherEdgeCount(
      DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> matcherRsrc) {
    return edgeVisibleGroup
        .getOrDefault(matcherRsrc, Collections.emptyList()).size();
  }

  public int getDisplayMatcherEdgeCount(
      DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> matcherRsrc) {
    return edgeDisplayGroup
        .getOrDefault(matcherRsrc, Collections.emptyList()).size();
  }

  public boolean getMatcherVisibility(
      DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> matcherRsrc) {
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
      DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> matcherRsrc,
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
      DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> matcherRsrc) {
    DepanFxLinkMatcher matcher =
        getMatcherByResource(matcherRsrc.getResource());
    List<GraphEdge> matcherEdges = edgeVisibleMatchers.keySet().stream()
        .filter(e -> matcher.match(e).isPresent())
        .collect(Collectors.toList());
    edgeVisibleGroup.put(matcherRsrc, matcherEdges);
  }

  public void updateAvailableMatchers(
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> availableMatchersRsrc) {

    clearMatcherVisibility();

    this.availableMatchersRsrc = availableMatchersRsrc;
    availableMatchersRsrc.getResource().streamMatchers()
        .forEach(m -> edgeVisibleGroup.put(m, new ArrayList<>()));
    edgeVisibleMatchers.keySet().stream()
        .forEach(e -> installEdgeVisible(e));
  }

  public void updateEdgeDisplayByMatcher(
      DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> matcherRsrc,
      LinkDisplayEntry displayEntry) {
    Collection<GraphEdge> updateEdges = edgeDisplayGroup.get(matcherRsrc);
    if (updateEdges != null) {
      DepanFxLinkMatcher matcher =
          getMatcherByResource(matcherRsrc.getResource());
      updateEdges.forEach(e -> updateMatchedEdge(e, matcher, displayEntry));
    }
  }

  public void installEdge(GraphEdge edge) {
    if (checkEdgeFilter(edge)) {
      int visibleCount = installEdgeVisible(edge);
      installEdgeDisplay(edge, visibleCount > 0);
      return;
    }

    // Install the filtered edge, but do not show it.
    installEdgeDisplay(edge, false);
  }

  public void setEdgeFilterResource(
      DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> edgeFilterRsrc) {
    this.edgeFiltersRsrc = edgeFilterRsrc;
    edgeFilterDataRsrcs = buildFilterResources();
    populateEdgeFilters();

    // Trigger update of all edges.
    updateAllEdgeDisplay();
  }

  public void addEdgeMatcherResource(
      DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> matcherRsrc) {
    edgeFilterDataRsrcs.add(matcherRsrc);
    populateEdgeFilters();

    // Trigger update of all edges.
    updateAllEdgeDisplay();
  }

  public DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>
  forUpdateAvailableMatcherSequenceDoc() {

    List<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>> matcherRefs =
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

    List<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>> vizMatcherRsrcs =
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

  /**
   * When saving the view panel state, the edge filter must be a
   * persistent resource.  Scratch resources are not saved or referenced.
   */
  public DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>
  forSaveEdgeFilterSequenceDoc() {
    if (edgeFiltersRsrc == null) {
      return null;
    }

    // Just the one matcher, as itself.
    // For update, but unchanged.
    if (edgeFilterDataRsrcs.size() == 1 &&
        edgeFilterDataRsrcs.get(0).equals(edgeFiltersRsrc)) {
      return edgeFiltersRsrc;
    }

    // Update the current resource if it is a sequence document.
    if (edgeFiltersRsrc.getResource() instanceof
        DepanFxLinkMatcherSequenceDocument seqnInfo) {
      DepanFxBaseMatcherDocument matcherInfo = buildEdgeFilterData(
          seqnInfo.getToolName(), seqnInfo.getToolDescription());
      return DepanFxWorkspaceResource.forUpdate(
          edgeFiltersRsrc, matcherInfo);
    }
    return null;
  }

  /**
   * Provide a resource that represents the current edge filter data.
   * If the current resource is not a matcher sequence, provide one as a
   * scratch resource.
   */
  public DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>
  forEditEdgeFilterSequenceDoc(DepanFxWorkspace workspace) {
    if (edgeFiltersRsrc == null) {
      return buildEdgeFilterScratchResource(workspace);
    }

    // Update the current resource if it is a sequence document.
    if (edgeFiltersRsrc.getResource() instanceof
        DepanFxLinkMatcherSequenceDocument seqnInfo) {
      DepanFxLinkMatcherSequenceDocument matcherInfo = buildEdgeFilterData(
          seqnInfo.getToolName(), seqnInfo.getToolDescription());
      return DepanFxWorkspaceResource.forSource(
          edgeFiltersRsrc.getDocument(), matcherInfo);
    }
    return buildEdgeFilterScratchResource(workspace);
  }

  private DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>
  buildEdgeFilterScratchResource(
      DepanFxWorkspace workspace) {
    return workspace.addScratchResource(
        buildEdgeFilterData("Edge Filters", "Edge visibility filters"));
  }

  private DepanFxLinkMatcherSequenceDocument buildEdgeFilterData(
      String toolName, String toolDescr) {

    List<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>> edgeFiltersRsrcs =
        streamEdgeFilters().collect(Collectors.toList());

    DepanFxLinkMatcherSequenceDocument matcherInfo =
        new DepanFxLinkMatcherSequenceDocument(
            toolName, toolDescr, edgeFiltersRsrcs);

    return matcherInfo;
  }

  private void populateEdgeFilters() {

    edgeFilters = edgeFilterDataRsrcs.stream()
        .map(r -> buildMatcher(r))
        .collect(Collectors.toList());

    // Avoid tests at all for empty case
    if (edgeFilters.isEmpty()) {
      edgeFilters = null;
    }
  }

  private List<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>> buildFilterResources() {

    if (edgeFiltersRsrc == null) {
      return new ArrayList<>();
    }

    DepanFxBaseMatcherDocument matcherInfo = edgeFiltersRsrc.getResource();
    if (matcherInfo instanceof DepanFxLinkMatcherSequenceDocument seqnInfo) {
      // Use the sequence document to get the matchers
      return seqnInfo.streamMatchers()
          .collect(Collectors.toList());
    }
    List<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>> result =
        new ArrayList<>();
    result.add(edgeFiltersRsrc);
    return result;
  }

  private DepanFxLinkMatcher buildMatcher(
      DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> matcherRsrc) {
    return matcherRegistry.buildMatcher(matcherRsrc.getResource());
  }

  /**
   * Returns {@code true} if the edge matches any of the edge filters
   * or there are no the edge matcher.
   *
   * This supports the semantics of "should this edge be shown".
   */
  private boolean checkEdgeFilter(GraphEdge edge) {
    if (edgeFilters != null) {
      return edgeFilters.stream()
          .anyMatch(m -> m.match(edge).isPresent());
    }
    return true;
  }

  private DepanFxLinkMatcher getMatcherByResource(
      DepanFxBaseMatcherDocument resource) {
    // should use matcher registry
    if (resource instanceof DepanFxLinkMatcherDocument linkInfo) {
      return linkInfo.getMatcher();
    }

    return null;
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
      DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> matcherRsrc) {
    if (edgeVisibleGroup.keySet().contains(matcherRsrc)) {
      return;
    }
    LOG.error("Unexpected matcher {}.\nAdding matcher to available",
        matcherRsrc.getResource().getToolName());
    addAvailableMatcher(matcherRsrc);
  }

  private void addMatcherEdge(
      DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> matcherRsrc,
      GraphEdge edge) {
    Collection<GraphEdge> currEdges = edgeVisibleGroup.get(matcherRsrc);
    currEdges.add(edge);
  }

  /**
   * Install the edge into the set of edges that are visible by any matcher,
   * recording and returning that count.
   *
   * @return The number of matchers that show this edge.
   */
  private int installEdgeVisible(GraphEdge edge) {
    List<DepanFxWorkspaceResource<DepanFxBaseMatcherDocument>> edgeMatchers =
        edgeVisibleGroup.keySet().stream()
            .filter(m -> getMatcherByResource(m.getResource())
                .match(edge).isPresent())
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

  private void updateAllEdgeDisplay() {
    // Capture the edges before we zap the current assignments
    Set<GraphEdge> updateEdges = new HashSet<>();
    updateEdges.addAll(directEdges);
    edgeDisplayGroup.values().stream()
        .flatMap(s -> s.stream())
        .forEach(updateEdges::add);
    updateEdges.addAll(remainderEdges);

    // Rebuild the edge group info.
    directEdges.clear();
    edgeDisplayGroup.clear();
    remainderEdges.clear();
    updateEdges.forEach(this::installEdge);
  }

  /////////////////////////////////////
  // JOGL Operations

  private void setEdgeVisible(GraphEdge edge, boolean isVisible) {
    JoglLines.setEdgeVisible(joglPane, edge, isVisible);
  }

  private void updateMatchedEdge(
      GraphEdge edge,
      DepanFxLinkMatcher matcher,
      LinkDisplayEntry lineDisplay) {

    DepanFxLink link = matcher.match(edge).get();
    JoglLines.updateLine(joglPane, edge, link, lineDisplay);
  }

  private void addDirectEdge(
      GraphEdge edge, DepanFxLineDisplayData edgeDisplay, boolean isVisible) {
    directEdges.add(edge);
    JoglLines.installEdge(joglPane, edge, "Direct", edgeDisplay, isVisible);
  }

  private void addMatchedEdge(
      GraphEdge edge, LinkDisplayEntry lineDisplay, boolean isVisible) {

    // Record edge with the matcher.
    DepanFxWorkspaceResource<DepanFxBaseMatcherDocument> matcherRsrc =
        lineDisplay.getLinkRsrc();
    edgeDisplayGroup
        .computeIfAbsent(matcherRsrc, m -> new ArrayList<>())
        .add(edge);

    DepanFxLinkMatcher matcher =
        getMatcherByResource(matcherRsrc.getResource());

    DepanFxLink link = matcher.match(edge).get();
    JoglLines.installLine(joglPane, edge, link, lineDisplay, isVisible);
  }

  private void addRemainderEdge(GraphEdge edge, boolean isVisible) {
    remainderEdges.add(edge);
    JoglLines.installEdge(
        joglPane, edge, remainderLabel, remainderDisplay, isVisible);
  }
}
