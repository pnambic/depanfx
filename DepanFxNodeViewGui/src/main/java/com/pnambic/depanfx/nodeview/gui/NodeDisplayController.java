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

public class NodeDisplayController {

  private static final Logger LOG =
      LoggerFactory.getLogger(NodeDisplayController.class);

  private final JoglPane joglPane;

  /**
   * Some nodes are individually styled.
   */
  private final Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay;

  /**
   * Transforms filter data into a usable filter.
   */
  private final DepanFxNodeFiltersRegistry.NodeFilterFactory filterFactory;

  private DepanFxNodeDisplayData remainderDisplay;

  private boolean remainderVisible;

  ///////////////////////////////////
  // Track nodes in each group

  private Collection<GraphNode> directNodes = new ArrayList<>();

  private Collection<GraphNode> remainderNodes = new ArrayList<>();

  /**
   * Number of showing visibility filters for each node.  Transitions to
   * and from zero cause a change in the node's display status.
   */
  private final Map<GraphNode, Integer> nodeVisibleFilters = new HashMap<>();

  /**
   * Track each nodes that is matched by any visibility filter.
   */
  private FilterControl visibleGroup;

  /**
   * Track which nodes's displays are handled by the display filter.
   */
  private FilterControl displayGroup;

  private Map<DepanFxBaseFilterData, DepanFxNodeDisplayData> displayByFilter;

  /**
   * The filters that are currently visible.
   * Not the complete inventory of filters for visibility.
   */
  private Set<DepanFxBaseFilterData> visibleFilters;

  /**
   * Resource for displayData.
   */
  private DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData> displayRsrc;

  public NodeDisplayController(
      JoglPane joglPane,
      Set<DepanFxBaseFilterData> availableFilters,
      Set<DepanFxBaseFilterData> visibleFilters,
      DepanFxNodeFiltersRegistry.NodeFilterFactory filterFactory,
      DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData> displayRsrc,
      Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay,
      boolean remainderVisible,
      DepanFxNodeDisplayData remainderDisplay) {
    this.joglPane = joglPane;
    this.visibleFilters = visibleFilters;
    this.filterFactory = filterFactory;
    this.displayRsrc = displayRsrc;
    this.nodeDisplay = nodeDisplay;
    this.remainderVisible = remainderVisible;
    this.remainderDisplay = remainderDisplay;

    // Initialize from provided set, so all filters are initially known.
    visibleGroup = new FilterControl(filterFactory, availableFilters.size());
    availableFilters.forEach(visibleGroup::installFilter);

    refreshDisplayGroup();
  }

  public static NodeDisplayController of(
      JoglPane joglPane, DepanFxNodeViewData viewData,
      DepanFxNodeFiltersRegistry.NodeFilterFactory filterFactory) {
    Set<DepanFxBaseFilterData> availableFilters =
        viewData.getAvailableNodeResource().getResource().streamFilterRefs()
            .map(r -> r.getResource())
            .collect(Collectors.toSet());

    Set<DepanFxBaseFilterData> visibleFilters =
        viewData.getVisibleNodeResource().getResource().streamFilterRefs()
            .map(r -> r.getResource())
            .collect(Collectors.toSet());

    return new NodeDisplayController(
        joglPane,
        availableFilters, visibleFilters, filterFactory,
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

  public Map<GraphNode, DepanFxNodeDisplayData> getNodeDisplay() {
    return nodeDisplay;
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
    remainderNodes
        .forEach(n -> setNodeVisible(n, isVisible));
  }

  public Stream<DepanFxBaseFilterData> streamDisplayFilters() {
    return displayGroup.streamFilters();
  }

  public Stream<DepanFxBaseFilterData> streamVisibilityFilters() {
    return visibleGroup.streamFilters();
  }

  public void setNodeDisplayResource(
      DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData> displayRsrc) {
    this.displayRsrc = displayRsrc;
    setNodeDisplay();
  }

  public int getVisiblityFilterNodeCount(DepanFxBaseFilterData filter) {
    return visibleGroup.getNodeCount(filter);
  }

  public int getDisplayFilterNodeCount(DepanFxBaseFilterData filter) {
    return displayGroup.getNodeCount(filter);
  }

  public boolean getFilterVisibility(DepanFxBaseFilterData filter) {
    return visibleFilters.contains(filter);
  }

  public void clearFilterVisibility() {
    // Skip over any increments, and just make all nodes hidden.
    nodeVisibleFilters.forEach((n, c) -> {
      nodeVisibleFilters.put(n, Integer.valueOf(0));
      setNodeVisible(n, false);
    });
    visibleFilters.clear();
  }

  public void setFilterVisibility(
      DepanFxBaseFilterData filter, boolean isVisible) {
    checkKnownFilter(filter);
    boolean currVisible = visibleFilters.contains(filter);
    // Nothing to change.
    if (currVisible == isVisible) {
      return;
    }

    if (isVisible) {
      visibleFilters.add(filter);
      visibleGroup.forEach(filter, n -> increaseVisible(n));
      return;
    }

    visibleFilters.remove(filter);
    visibleGroup.forEach(filter, n -> decreaseVisible(n));
  }

  /**
   * Add the filter document to the set of filters that track node
   * visibility. Because the added filter is not added to the set of visible
   * filters, the added filter has a not visible status.
   */
  public void addAvailableFilter(DepanFxBaseFilterData filter) {
    visibleGroup.installFilter(filter, nodeVisibleFilters.keySet().stream());
  }

  public void updateAvailableFilters(
      Set<DepanFxBaseFilterData> availableFilters) {
    clearFilterVisibility();

    visibleGroup = new FilterControl(filterFactory, availableFilters.size());
    availableFilters.forEach(visibleGroup::installFilter);

    nodeVisibleFilters.keySet().stream()
        .forEach(n -> installNodeVisible(n));
  }

  public void updateNodeDisplayByFilter(
      DepanFxBaseFilterData filter,
      NodeDisplayEntry displayEntry) {
    displayGroup.forEach(filter,
        n -> setNodeDisplay(n, displayEntry.getNodeDisplay()));

    // TODO: Update displayInfo.
  }

  public void installNode(GraphNode node, DepanFxNodeLocationData location) {
    int visibleCount = installNodeVisible(node);
    installNodeDisplay(node, location, visibleCount > 0);
  }

  public DepanFxNodeFilterSequenceData buildAvailableFilterSequenceDoc(
      ContextModelId modelId,
      List<DepanFxWorkspaceResource<DepanFxBaseFilterData>> filterRsrcs) {
    // Ensure serializable ArrayList.
    List<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
    availFilterRsrc = new ArrayList<>();

    filterRsrcs.stream()
        .filter(r -> visibleGroup.hasFilter(r.getResource()))
        .forEach(availFilterRsrc::add);

    return new DepanFxNodeFilterSequenceData(
        "Available Nodes",
        "Available nodes from ", modelId, availFilterRsrc);
  }

  public DepanFxNodeFilterSequenceData buildVisibleFilterSequenceDoc(
      ContextModelId modelId,
      List<DepanFxWorkspaceResource<DepanFxBaseFilterData>> filterRsrcs) {
    // Ensure serializable ArrayList.
    List<DepanFxWorkspaceResource<DepanFxBaseFilterData>>
    visibleFilterRsrcs = new ArrayList<>();
    filterRsrcs.stream()
        .filter(r -> visibleFilters.contains(r.getResource()))
        .forEach(visibleFilterRsrcs::add);

    return new DepanFxNodeFilterSequenceData(
        " Visible Nodes",
        "Visible nodes from ",
        modelId, visibleFilterRsrcs);
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

  private void checkKnownFilter(DepanFxBaseFilterData filter) {
    if (visibleGroup.hasFilter(filter)) {
      return;
    }
    LOG.error("Unexpected filter {}.\nAdding filter to available",
        filter.getToolName());
    addAvailableFilter(filter);
  }

  /**
   * Install the node on every filter in the visible group,
   * and return the count of filters that are currently visible.
   */
  private int installNodeVisible(GraphNode node) {
    int visibleCount = (int) visibleGroup.installOnEvery(node).stream()
        .filter(visibleFilters::contains)
        .count();
    nodeVisibleFilters.put(node, visibleCount);
    return visibleCount;
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
    Optional<DepanFxBaseFilterData> optFilter =
        displayGroup.installOnFirst(node);
    if (optFilter.isPresent()) {
        DepanFxNodeDisplayData nodeDisplay = displayByFilter.get(optFilter.get());
        installNode(node, location, nodeDisplay, isVisible);
        return;
    }

    // Fall through for any missed nodes
    addRemainderNode(node, location, isVisible);
  }

  /**
   * Update display for nodes without changing visibility/
   */
  private void updateNodeDisplay(GraphNode node) {

    // Prefer direct node display
    DepanFxNodeDisplayData nodeDirect = nodeDisplay.get(node);
    if (nodeDirect != null) {
      setNodeDisplay(node, nodeDirect);
      return;
    }

    // Mostly, nodes display per filter
    Optional<DepanFxBaseFilterData> optFilter =
        displayGroup.installOnFirst(node);
    if (optFilter.isPresent()) {
        setNodeDisplay(node, displayByFilter.get(optFilter.get()));
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
    DepanFxBaseFilterData filter =
        displayEntry.getFilterResource().getResource();
    displayByFilter.put(filter, displayEntry.getNodeDisplay());
    displayGroup.installFilter(filter);
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

    private final Map<DepanFxBaseFilterData, FilterInfo> filterInfos;

    public FilterControl(NodeFilterFactory filterFactory, int size) {
      this.filterFactory = filterFactory;
      filterInfos = new HashMap<>(size);
    }

    public boolean hasFilter(DepanFxBaseFilterData filter) {
      return filterInfos.containsKey(filter);
    }

    public int filterCount() {
      return filterInfos.size();
    }

    public int getNodeCount(DepanFxBaseFilterData filter) {
      return filterInfos.get(filter).getNodeCount();
    }

    public void installFilter(DepanFxBaseFilterData filterInfo) {
      DepanFxBaseFilter<?> filter = filterFactory.buildFilter(filterInfo);
      filterInfos.put(filterInfo, new FilterInfo(filter));
    }

    public void installFilter(
        DepanFxBaseFilterData filter, Stream<GraphNode> stream) {
      installFilter(filter);
      FilterInfo info = filterInfos.get(filter);
      stream.forEach(info::installNode);
    }

    public void forEach(
        DepanFxBaseFilterData filter, Consumer<GraphNode> onEach) {
      filterInfos.get(filter).forEach(onEach);
    }

    public Stream<DepanFxBaseFilterData> streamFilters() {
      return filterInfos.keySet().stream();
    }

    public Optional<DepanFxBaseFilterData> installOnFirst(GraphNode node) {
      return filterInfos.entrySet().stream()
          .filter(e -> e.getValue().installNode(node))
          .findFirst()
          .map(e -> e.getKey());
    }

    @SuppressWarnings("unused")
    public Optional<DepanFxBaseFilterData> lookupFirst(GraphNode node) {
      return filterInfos.entrySet().stream()
          .filter(e -> e.getValue().inFilter(node))
          .findFirst()
          .map(e -> e.getKey());
    }

    public List<DepanFxBaseFilterData> installOnEvery(GraphNode node) {
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
