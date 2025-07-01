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

import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.nodefilters.model.DepanFxBaseFilter;
import com.pnambic.depanfx.nodefilters.model.DepanFxNodeFiltersRegistry;
import com.pnambic.depanfx.nodefilters.model.DepanFxNodeFiltersRegistry.NodeFilterFactory;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxNodeFilterSequenceData;
import com.pnambic.depanfx.nodeview.jogl.JoglPane;
import com.pnambic.depanfx.nodeview.jogl.JoglShapes;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewNodeDisplayData.NodeDisplayEntry;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Controller for node display, including visibility and display data.
 *
 * This controller is responsible for managing the visibility of nodes
 * based on filters, as well as their display properties.
 */
public class NodeDisplayController {

  private static final Logger LOG =
      LoggerFactory.getLogger(NodeDisplayController.class);

  /**
   * Destination for live changes.
   */
  private final JoglPane joglPane;

  /**
   * Transforms filter data into a usable filter.
   */
  private final DepanFxNodeFiltersRegistry.NodeFilterFactory filterFactory;

  /**
   * Some nodes are individually styled.
   */
  private final Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay;

  private DepanFxNodeDisplayData remainderDisplay;

  private boolean remainderVisible;

  ///////////////////////////////////
  // Track nodes in each group

  private Collection<GraphNode> directNodes = new ArrayList<>();

  private Collection<GraphNode> remainderNodes = new ArrayList<>();

  ///////////////////////////////////
  // Node Visibility

  /**
   * Number of showing visibility filters for each node.  Transitions to
   * and from zero cause a change in the node's display status.
   */
  private final Map<GraphNode, Integer> nodeVisibleFilters = new HashMap<>();

  /**
   * The filters for choosing which nodes are visible.
   */
  private DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> availableFilterRsrc;

  /**
   * Track each nodes that is matched by any visibility filter.
   * A mutable record of the available visibility filters
   */
  private FilterControl visibleGroup;

  /**
   * The filters that are currently visible.
   * This should be a subset of the {@code availableFilterRsrc}.
   */
  private DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> visibleNodeRsrc;

  /**
   * Mutable record of active visibility filters.
   */
  private Set<DepanFxWorkspaceResource<DepanFxBaseFilterData>> visibleFilterRsrcs;

  ///////////////////////////////////
  // Node Display

  private Map<DepanFxBaseFilterData, DepanFxNodeDisplayData> displayByFilter;

  /**
   * Track which nodes's displays are handled by the display filter.
   */
  private FilterControl displayGroup;

  /**
   * Resource for displayData.
   */
  private DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData> displayRsrc;

  public NodeDisplayController(
      JoglPane joglPane,
      DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> availableFilterRsrc,
      DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> visibleNodeRsrc,
      DepanFxNodeFiltersRegistry.NodeFilterFactory filterFactory,
      DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData> displayRsrc,
      Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay,
      boolean remainderVisible,
      DepanFxNodeDisplayData remainderDisplay) {
    this.joglPane = joglPane;
    this.availableFilterRsrc = availableFilterRsrc;
    this.visibleNodeRsrc = visibleNodeRsrc;
    this.filterFactory = filterFactory;
    this.displayRsrc = displayRsrc;
    this.nodeDisplay = nodeDisplay;
    this.remainderVisible = remainderVisible;
    this.remainderDisplay = remainderDisplay;

    // Initialize visibility choices from provided available filter resource,
    // so all of the filter choices are initially known.
    visibleGroup =
        FilterControl.of(availableFilterRsrc.getResource(), filterFactory);

    visibleFilterRsrcs = visibleNodeRsrc.getResource().streamFilterRefs()
        .collect(Collectors.toSet());
    refreshDisplayGroup();
  }

  public static NodeDisplayController of(
      JoglPane joglPane, DepanFxNodeViewData viewData,
      DepanFxNodeFiltersRegistry.NodeFilterFactory filterFactory) {

    return new NodeDisplayController(
        joglPane,
        viewData.getAvailableNodeResource(),
        viewData.getVisibleNodeResource(),
        filterFactory,
        viewData.getNodeDisplayDocRsrc(),
        viewData.getNodeDisplay(),
        viewData.getRemainderNodesVisible(),
        viewData.getRemainderNodesDisplay());
  }

  public DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData>
      getNodeDisplayResource() {
    return displayRsrc;
  }

  public void revertNodeDisplay() {
    setNodeDisplay();
  }

  public boolean getRemainderVisible() {
    return remainderVisible;
  }

  public int getRemainderCount() {
    return remainderNodes.size();
  }

  public DepanFxNodeDisplayData getRemainderDisplay() {
    return remainderDisplay;
  }

  public boolean getRemainderVisibility() {
    return remainderVisible;
  }

  public void setRemainderVisibility(boolean isVisible) {
    this.remainderVisible = isVisible;
    remainderNodes.forEach(n -> setNodeVisible(n, isVisible));
  }

  /**
   * Provides an alphabetically ordered sequence of filter resources,
   * based on the tool name of each resource.
   *
   * This ensure that consumes always see the same order,
   * regardless of set construction.
   */
  public Stream<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
  streamVisibilityResource() {

    return visibleFilterRsrcs.stream()
        .sorted(DepanFxWorkspaceResource.BY_RESOURCE_NAME);
  }

  public void forEachAvailablityFilter(
      Consumer<DepanFxWorkspaceResource<DepanFxBaseFilterData>> filterUpdate) {
    visibleGroup.streamAvailableFilters().forEach(filterUpdate);
  }

  public void setNodeDisplayResource(
      DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData> displayRsrc) {
    this.displayRsrc = displayRsrc;
    setNodeDisplay();
  }

  public int getVisiblityFilterNodeCount(
      DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc) {
    return visibleGroup.getNodeCount(filterRsrc);
  }

  public int getDisplayFilterNodeCount(
      DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc) {
    return displayGroup.getNodeCount(filterRsrc);
  }

  public boolean getFilterVisibility(
      DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc) {
    return visibleFilterRsrcs.contains(filterRsrc);
  }

  public void clearFilterVisibility() {
    // Skip over any increments, and just make all nodes hidden.
    nodeVisibleFilters.forEach((n, c) -> {
      nodeVisibleFilters.put(n, Integer.valueOf(0));
      setNodeVisible(n, false);
    });
    visibleFilterRsrcs.clear();
  }

  public void setFilterVisibility(
      DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc,
      boolean isVisible) {
    checkKnownFilter(filterRsrc);
    boolean currVisible = visibleFilterRsrcs.contains(filterRsrc);
    // Nothing to change.
    if (currVisible == isVisible) {
      return;
    }

    if (isVisible) {
      visibleFilterRsrcs.add(filterRsrc);
      visibleGroup.forEach(filterRsrc, n -> increaseVisible(n));
      return;
    }

    visibleFilterRsrcs.remove(filterRsrc);
    visibleGroup.forEach(filterRsrc, n -> decreaseVisible(n));
  }

  public void setVisiblityResource(
      DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> visibleNodeRsrc) {
    this.visibleNodeRsrc = visibleNodeRsrc;

    clearFilterVisibility();
    visibleNodeRsrc.getResource().streamFilterRefs()
        .forEach(r -> setFilterVisibility(r, true));
  }

  /**
   * Add the filter document to the set of filters that track node
   * visibility. Because the added filter is not added to the set of visible
   * filters, the added filter has a not visible status.
   */
  public void addAvailableFilter(
      DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc) {
    visibleGroup.installFilter(filterRsrc, nodeVisibleFilters.keySet().stream());
  }

  public void setAvailableResource(
      DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> availableFilterRsrc) {
    this.availableFilterRsrc = availableFilterRsrc;

    visibleGroup =
        FilterControl.of(availableFilterRsrc.getResource(), filterFactory);
    refreshDisplayGroup();
  }

  public void updateNodeDisplayByFilter(
      DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc,
      NodeDisplayEntry displayEntry) {
    displayGroup.forEach(filterRsrc,
        n -> setNodeDisplay(n, displayEntry.getNodeDisplay()));
  }

  /**
   * Provides an alphabetically ordered sequence of filter resources,
   * based on the tool name of each resource.
   *
   * This ensure that consumers always see the same order,
   * regardless of set construction.
   */
  public Stream<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
  streamAvailableResources() {
    return visibleGroup.streamAvailableFilters();
  }

  public void installNode(GraphNode node, DepanFxNodeLocationData location) {
    int visibleCount = installNodeVisible(node);
    installNodeDisplay(node, location, visibleCount > 0);
  }

  public DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData>
  forUpdateAvailableFilterResource() {

    List<DepanFxWorkspaceResource<DepanFxBaseFilterData>> availFilterRsrcs =
        streamAvailableResources().collect(Collectors.toList());

    DepanFxNodeFilterSequenceData availableNodeInfo =
        availableFilterRsrc.getResource();
    DepanFxNodeFilterSequenceData filterInfo =
        new DepanFxNodeFilterSequenceData(
            availableNodeInfo.getToolName(),
            availableNodeInfo.getToolDescription(),
            availableNodeInfo.getContextModelId(),
            availFilterRsrcs);

    return DepanFxWorkspaceResource.forUpdate(availableFilterRsrc, filterInfo);
  }

  public DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData>
  forUpdateVisibleFilterResource() {

    List<DepanFxWorkspaceResource<DepanFxBaseFilterData>> visibleFilterRsrcs =
        streamVisibilityResource().collect(Collectors.toList());

    DepanFxNodeFilterSequenceData visibleNodeInfo =
        visibleNodeRsrc.getResource();
    DepanFxNodeFilterSequenceData filterInfo =
        new DepanFxNodeFilterSequenceData(
            visibleNodeInfo.getToolName(),
            visibleNodeInfo.getToolDescription(),
            visibleNodeInfo.getContextModelId(),
            visibleFilterRsrcs);

    return DepanFxWorkspaceResource.forUpdate(visibleNodeRsrc, filterInfo);
  }

  private void increaseVisible(GraphNode node) {
    int filterCnt = incrementCnt(node);
    nodeVisibleFilters.put(node, filterCnt);

    // If it was the first filter, start showing the node
    if (filterCnt == 1) {
      setNodeVisible(node, true);
    }
  }

  private void decreaseVisible(GraphNode node) {
    int filterCnt = decrementCnt(node);
    nodeVisibleFilters.put(node, filterCnt);

    // If this was the last filter, stop showing the node
    if (filterCnt == 0) {
      setNodeVisible(node, false);
    }
  }

  /** Avoid any "impossible" increment. */
  private int incrementCnt(GraphNode node) {
    int filterCnt = nodeVisibleFilters.get(node).intValue();
    int maxCnt = visibleGroup.filterCount();
    if (filterCnt < maxCnt) {
      return filterCnt + 1;
    }
    LOG.error("Incrementing maximum filter count {}", node.toString());
    return maxCnt;
  }

  /** Avoid any "impossible" decrement. */
  private int decrementCnt(GraphNode node) {
    int filterCnt = nodeVisibleFilters.get(node).intValue();
    if (filterCnt > 0) {
      return filterCnt - 1;
    }
    LOG.error("Decrementing zero filter count {}", node.toString());
    return 0;
  }

  private void checkKnownFilter(
      DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc) {
    if (visibleGroup.hasFilter(filterRsrc)) {
      return;
    }
    LOG.error("Unexpected filter {}.\nAdding filter to available",
        filterRsrc.getResource().getToolName());
    addAvailableFilter(filterRsrc);
  }

  /**
   * Install the node on every filter in the visible group,
   * and return the count of filters that are currently visible.
   */
  private int installNodeVisible(GraphNode node) {
    int visibleCount = (int) visibleGroup.installOnEvery(node).stream()
        .filter(r -> filterVisible(r))
        .count();
    nodeVisibleFilters.put(node, visibleCount);
    return visibleCount;
  }

  private boolean filterVisible(
      DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc) {
    boolean result = visibleFilterRsrcs.contains(filterRsrc);
    return result;
  }

  private void installNodeDisplay(
      GraphNode node, DepanFxNodeLocationData location, boolean isVisible) {

    // Prefer direct node display
    DepanFxNodeDisplayData nodeDirect = nodeDisplay.get(node);
    if (nodeDirect != null) {
      addDirectNode(node, location, nodeDirect, isVisible);
      return;
    }

    // Mostly, nodes display per filter
    Optional<DepanFxWorkspaceResource<DepanFxBaseFilterData>> optFilterRsrc =
        displayGroup.installOnFirst(node);
    if (optFilterRsrc.isPresent()) {
        DepanFxNodeDisplayData nodeDisplay =
            displayByFilter.get(optFilterRsrc.get().getResource());
        installNode(node, location, nodeDisplay, isVisible);
        return;
    }

    // Fall through for any missed nodes
    addRemainderNode(node, location, isVisible);
  }

  /**
   * Update display for nodes without changing visibility.
   */
  private void updateNodeDisplay(GraphNode node) {

    // Prefer direct node display
    DepanFxNodeDisplayData nodeDirect = nodeDisplay.get(node);
    if (nodeDirect != null) {
      setNodeDisplay(node, nodeDirect);
      return;
    }

    // Mostly, nodes display per filter
    Optional<DepanFxWorkspaceResource<DepanFxBaseFilterData>> optFilterRsrc =
        displayGroup.installOnFirst(node);
    if (optFilterRsrc.isPresent()) {
        setNodeDisplay(
            node, displayByFilter.get(optFilterRsrc.get().getResource()));
        return;
    }

    // Fall through for any missed nodes
    setNodeDisplay(node, remainderDisplay);
  }

  private void setNodeDisplay() {
    refreshDisplayGroup();

    nodeVisibleFilters.keySet().stream()
        .forEach(this::updateNodeDisplay);
  }

  /**
   * Refresh the display group after the display info changes.
   */
  private void refreshDisplayGroup() {
    DepanFxNodeViewNodeDisplayData displayInfo = displayRsrc.getResource();
    displayGroup =
        new FilterControl(filterFactory, displayInfo.countFilters());
    displayByFilter = new HashMap<>(displayInfo.countFilters());
    displayInfo.streamNodeDisplay()
        .forEach(this::installDisplay);
  }

  private void installDisplay(NodeDisplayEntry displayEntry) {
    DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc =
        displayEntry.getFilterResource();
    displayByFilter.put(
        filterRsrc.getResource(), displayEntry.getNodeDisplay());
    displayGroup.installFilter(filterRsrc);
  }

  private void addDirectNode(
      GraphNode node, DepanFxNodeLocationData location,
      DepanFxNodeDisplayData nodeDisplay, boolean isVisible) {
    directNodes.add(node);
    installNode(node, location, nodeDisplay, isVisible);
  }

  private void addRemainderNode(
      GraphNode node, DepanFxNodeLocationData location, boolean isVisible) {
    remainderNodes.add(node);
    installNode(node, location, remainderDisplay, isVisible);
  }

  private void installNode(
      GraphNode node, DepanFxNodeLocationData location,
      DepanFxNodeDisplayData nodeDisplay, boolean isVisible) {

    JoglShapes.installShape(joglPane, node, location, nodeDisplay, isVisible);
  }

  private void setNodeVisible(GraphNode node, boolean isVisible) {
    JoglShapes.updateVisibility(joglPane, node, isVisible);
  }

  private void setNodeDisplay(
      GraphNode node, DepanFxNodeDisplayData nodeDisplay) {

    JoglShapes.updateDisplay(joglPane, node, nodeDisplay);
  }

  private static class FilterControl {

    /**
     * Transforms filter data into a usable filter.
     */
    private final DepanFxNodeFiltersRegistry.NodeFilterFactory filterFactory;

    private final Map<
        DepanFxWorkspaceResource<DepanFxBaseFilterData>,
        FilterInfo> filterInfos;

    public FilterControl(NodeFilterFactory filterFactory, int size) {
      this.filterFactory = filterFactory;
      filterInfos = new HashMap<>(size);
    }

    public static FilterControl of(
        DepanFxNodeFilterSequenceData filterInfo,
        NodeFilterFactory filterFactory) {

      // Initialize from provided set, so all filters are initially known.
      Set<DepanFxWorkspaceResource<DepanFxBaseFilterData>> availableFilterRsrcs =
          filterInfo.streamFilterRefs().collect(Collectors.toSet());

      FilterControl result = new FilterControl(
          filterFactory, availableFilterRsrcs.size());
      availableFilterRsrcs.forEach(r -> result.installFilter(r));
      return result;
    }

    public boolean hasFilter(
        DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc) {
      return filterInfos.containsKey(filterRsrc);
    }

    public int filterCount() {
      return filterInfos.size();
    }

    public int getNodeCount(
        DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc) {
      return filterInfos.get(filterRsrc).getNodeCount();
    }

    public void installFilter(
        DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc) {
      DepanFxBaseFilter<?> filter =
          filterFactory.buildFilter(filterRsrc.getResource());
      filterInfos.put(filterRsrc, new FilterInfo(filter));
    }

    public void installFilter(
        DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc,
        Stream<GraphNode> nodeStream) {
      installFilter(filterRsrc);
      FilterInfo info = filterInfos.get(filterRsrc);
      nodeStream.forEach(info::installNode);
    }

    public void forEach(
        DepanFxWorkspaceResource<DepanFxBaseFilterData> filterRsrc,
        Consumer<GraphNode> onEach) {
      filterInfos.get(filterRsrc).forEach(onEach);
    }

    /**
     * Provides an alphabetically ordered sequence of filter resources,
     * based on the tool name of each resource.
     *
     * This ensure that consumes always see the same order,
     * regardless of set construction.
     */
    public Stream<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
    streamAvailableFilters() {
      return filterInfos.keySet().stream()
          .sorted(DepanFxWorkspaceResource.BY_RESOURCE_NAME);
    }

    public Optional<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
    installOnFirst(
        GraphNode node) {
      return filterInfos.entrySet().stream()
          .filter(e -> e.getValue().installNode(node))
          .findFirst()
          .map(e -> e.getKey());
    }

    @SuppressWarnings("unused")
    public Optional<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
    lookupFirst(
        GraphNode node) {
      return filterInfos.entrySet().stream()
          .filter(e -> e.getValue().inFilter(node))
          .findFirst()
          .map(e -> e.getKey());
    }

    public List<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
    installOnEvery(
        GraphNode node) {
      return filterInfos.entrySet().stream()
          .filter(e -> e.getValue().installNode(node))
          .map(e -> e.getKey())
          .collect(Collectors.toList());
    }
  }

  private static class FilterInfo {

    private final DepanFxBaseFilter<?> filter;

    private final Collection<GraphNode> filterNodes;

    public FilterInfo(DepanFxBaseFilter<?> filter) {
      this.filter = filter;
      filterNodes = new ArrayList<>();
    }

    public int getNodeCount() {
      return filterNodes.size();
    }

    public void forEach(Consumer<GraphNode> onEach) {
      filterNodes.forEach(onEach);
    }

    public boolean inFilter(GraphNode node) {
      return filter.computeNodes(Collections.singletonList(node)).stream()
          .filter(n -> node.equals(n))
          .findFirst()
          .isPresent();
    }

    public boolean installNode(GraphNode node) {
      if (inFilter(node)) {
        filterNodes.add(node);
        return true;
      }
      return false;
    }
  }
}
