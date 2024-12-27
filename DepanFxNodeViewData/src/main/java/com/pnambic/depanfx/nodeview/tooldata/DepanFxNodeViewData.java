package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxNodeFilterSequenceData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherSequenceDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.paint.Color;

public class DepanFxNodeViewData extends DepanFxBaseToolData {

  public static final DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData>
      X_EMPTY_AVAILABLE_NODES = null;

  public static final DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData>
      X_EMPTY_VISIBLE_NODES = null;

  public static final boolean DEFAULT_REMAINDER_NODES_VISIBLE = true;

  public static final DepanFxNodeDisplayData DEFAULT_REMAINDER_NODE_DISPLAY =
      buildRemainerNodeDisplay();

  public static final boolean DEFAULT_REMAINDER_EDGES_VISIBLE = true;

  public static final String DEFAULT_REMAINDER_EDGES_LABEL = "Remainder";

  public static final DepanFxLineDisplayData DEFAULT_REMAINDER_EDGE_DISPLAY =
      buildRemainerEdgeDisplay();

  public static final String NODE_VIEW_TOOL_EXT = "dnvi";

  public static final String NODE_VIEW_DIR = "Node Views";

  public static final String SIMPLE_VIEW_NAME = "Simple View";

  public static final Path NODE_VIEW_TOOL_PATH =
      DepanFxProjects.TOOLS_PATH.resolve(NODE_VIEW_DIR);

  private final DepanFxWorkspaceResource<GraphDocument> graphDocRsrc;

  private final Collection<GraphNode> viewNodes;

  private final Map<GraphNode, DepanFxNodeLocationData> nodeLocations;

  private final Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay;

  private final Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay;

  /**
   * How the graph is observed.
   */
  private final DepanFxNodeViewSceneData sceneData;

  /**
   * The set of nodes shown as selectable for display.
   */
  private DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData>
      availableNodeRsrc;

  /**
   * The set of nodes that are visible in the render.
   *
   * This is typically a subset of the nodes in {@link #availableNodeRsrc},
   * but it may be independent.
   */
  private DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData>
      visibleNodeRsrc;

  /**
   * Source of default rendering data for nodes.
   *
   * Nodes are matched to a filter, then rendered with that filters's selections.
   * Provides a {@link DepanFxNodeViewNodeDisplayData},
   * with a stream of {@code NodeDisplayEnity} values.
   */
  private final DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData>
      nodeDisplayDocRsrc;

  private boolean remainderNodesVisible;

  private DepanFxNodeDisplayData remainderNodesDisplay;

  /**
   * The set of edges shown as selectable for display.
   *
   * This set is often initialized from {@link #linkDisplayDocRsrc},
   * but may be independent.
   */
  private DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>
      availableEdgeRsrc;

  /**
   * The set of edges that are visible in the render.
   *
   * This set is often initialized from {@link #linkDisplayDocRsrc},
   * and is typically a subset of the edges in {@link #availableEdgeRsrc},
   * but it may be independent from either.
   */
  private DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>
      visibleEdgeRsrc;

  /**
   * Source of default rendering data for edges.
   *
   * Edges are matched to a link, then rendered with that link's selections.
   * Provides a {@link DepanFxNodeViewLinkDisplayData},
   * with a stream of {@code LinkDisplayEnty} values.
   */
  private final DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData>
      linkDisplayDocRsrc;

  private boolean remainerEdgesVisible;

  private String remainderEdgesLabel;

  private DepanFxLineDisplayData remainderEdgesDisplay;

  public DepanFxNodeViewData(
      String toolName, String toolDescription,

      DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
      Collection<GraphNode> viewNodes,
      Map<GraphNode, DepanFxNodeLocationData> nodeLocations,
      Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay,
      Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay,

      DepanFxNodeViewSceneData sceneData,

      DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> availableNodeRsrc,
      DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> visibleNodeRsrc,
      DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData> nodeDisplayDocRsrc,
      boolean remainerNodesVisible,
      DepanFxNodeDisplayData remainderNodesDisplay,

      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> availableEdgeRsrc,
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> visibleEdgeRsrc,
      DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> linkDisplayDocRsrc,
      boolean remainerEdgesVisible,
      String remainderEdgesLabel,
      DepanFxLineDisplayData remainderEdgesDisplay) {

    super(toolName, toolDescription);
    this.graphDocRsrc = graphDocRsrc;
    this.viewNodes = viewNodes;
    this.nodeLocations = nodeLocations;
    this.nodeDisplay = nodeDisplay;
    this.edgeDisplay = edgeDisplay;

    this.sceneData = sceneData;

    this.availableNodeRsrc = availableNodeRsrc;
    this.visibleNodeRsrc = visibleNodeRsrc;
    this.nodeDisplayDocRsrc = nodeDisplayDocRsrc;
    this.remainderNodesVisible = remainerNodesVisible;
    this.remainderNodesDisplay = remainderNodesDisplay;

    this.availableEdgeRsrc = availableEdgeRsrc;
    this.visibleEdgeRsrc = visibleEdgeRsrc;
    this.linkDisplayDocRsrc = linkDisplayDocRsrc;
    this.remainerEdgesVisible = remainerEdgesVisible;
    this.remainderEdgesLabel = remainderEdgesLabel;
    this.remainderEdgesDisplay = remainderEdgesDisplay;
  }

  /**
   * Provide a minimal available edge resource, in case one is missing.
   */
  public static DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>
  buildAvailableEdgeResource(
      DepanFxWorkspace workspace,
      DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> linkDisplayRsrc) {

    return workspace.addScratchResource(
        linkDisplayRsrc.getResource().asLinkMatcherSequenceDoc());
  }

  /**
   * Provide a minimal available node resource, in case one is missing.
   */
  public static DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData>
  buildAvailableNodeResource(
      DepanFxWorkspace workspace,
      DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData> nodeDisplayRsrc) {
    return workspace.addScratchResource(
        nodeDisplayRsrc.getResource().asNodeFilterSequenceDoc());
  }

  public DepanFxWorkspaceResource<GraphDocument> getGraphDocRsrc() {
    return graphDocRsrc;
  }

  /** Provide defensive copy. */
  public Collection<GraphNode> getViewNodes() {
    return new ArrayList<>(viewNodes);
  }

  /** Provide defensive copy. */
  public Map<GraphNode, DepanFxNodeLocationData> getNodeLocations() {
    return new HashMap<>(nodeLocations);
  }

  /** Provide defensive copy. */
  public Map<GraphNode, DepanFxNodeDisplayData> getNodeDisplay() {
    return new HashMap<>(nodeDisplay);
  }

  /** Provide defensive copy. */
  public Map<GraphEdge, DepanFxLineDisplayData> getEdgeDisplay() {
    return new HashMap<>(edgeDisplay);
  }

  public DepanFxNodeLocationData getNodeLocation(GraphNode node) {
    return nodeLocations.get(node);
  }

  public DepanFxNodeDisplayData getNodeDisplay(GraphNode node) {
    return nodeDisplay.get(node);
  }

  public DepanFxLineDisplayData getLineDisplay(GraphEdge edge) {
    return edgeDisplay.get(edge);
  }

  public DepanFxNodeViewSceneData getSceneData() {
    return sceneData;
  }

  /////////////////////////////////////
  // Nodes and Filters

  public DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData>
      getAvailableNodeResource() {
    return availableNodeRsrc;
  }

  public void setAvailableNodeResource(
      DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> availableNodeRsrc) {
    this.availableNodeRsrc = availableNodeRsrc;
  }

  public DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData>
      getVisibleNodeResource() {
    return visibleNodeRsrc;
  }

  public void setVisibleNodeRsrc(
      DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> visibleNodeRsrc) {
    this.visibleNodeRsrc = visibleNodeRsrc;
  }

  public DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData>
      getNodeDisplayDocRsrc() {
    return nodeDisplayDocRsrc;
  }

  public boolean getRemainderNodesVisible() {
    return remainderNodesVisible;
  }

  public DepanFxNodeDisplayData getRemainderNodesDisplay() {
    return remainderNodesDisplay;
  }

  /////////////////////////////////////
  // Links, Edges, and Matchers

  public DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>
      getAvailableEdgeResource() {
    return availableEdgeRsrc;
  }

  public void setAvailableEdgeRsrc(
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> availEdgeRsrc) {
    this.availableEdgeRsrc = availEdgeRsrc;
  }

  public DepanFxLinkMatcherSequenceDocument getAvailableEdgesDoc() {
    return availableEdgeRsrc.getResource();
  }

  public DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>
      getVisibleEdgeResource() {
    return visibleEdgeRsrc;
  }

  public DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>
      getVisibleEdgeRsrc(DepanFxWorkspace workspace) {
    return visibleEdgeRsrc;
  }

  public void setVisibleEdgeRsrc(
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> visibleEdgeRsrc) {
    this.visibleEdgeRsrc = visibleEdgeRsrc;
  }

  public DepanFxLinkMatcherSequenceDocument getVisibleEdgesDoc() {
    return visibleEdgeRsrc.getResource();
  }

  public DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData>
      getLinkDisplayDocRsrc() {
    return linkDisplayDocRsrc;
  }

  public boolean getRemainderEdgesVisible() {
    return remainerEdgesVisible;
  }

  public String getRemainderEdgesLabel() {
    return remainderEdgesLabel;
  }

  public DepanFxLineDisplayData getRemainderEdgeDisplay() {
    return remainderEdgesDisplay;
  }

  /////////////////////////////////////
  // For constructor

  private static DepanFxNodeDisplayData buildRemainerNodeDisplay() {
    DepanFxNodeDisplayData result =
        DepanFxNodeDisplayData.buildSimpleNodeDisplayData();
    return result;
  }

  private static DepanFxLineDisplayData buildRemainerEdgeDisplay() {
    DepanFxLineDisplayData result =
        DepanFxLineDisplayData.buildSimpleLineDisplayData();
    result.lineColor = DepanFxJoglColor.of(Color.GRAY);
    return result;
  }
}
