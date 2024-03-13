package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DepanFxNodeViewData extends DepanFxBaseToolData {

  public static final String NODE_VIEW_TOOL_EXT = "dnvi";

  public static final String NODE_VIEW_DIR = "Node Views";

  public static final String SIMPLE_VIEW_NAME = "Simple View";

  public static final Path NODE_VIEW_TOOL_PATH =
      DepanFxProjects.TOOLS_PATH.resolve(NODE_VIEW_DIR);

  private final DepanFxNodeViewSceneData sceneData;

  private final DepanFxWorkspaceResource graphDocRsrc;

  /**
   * Source of default rendering data for edges.
   *
   * Edges are matched to a link, then rendered with that link's selections.
   * Provides a {@link DepanFxNodeViewLinkDisplayData},
   * with a stream of {@code LinkDisplayEnty} values.
   */
  private final DepanFxWorkspaceResource linkDisplayDocRsrc;

  private final Collection<GraphNode> viewNodes;

  private final Map<GraphNode, DepanFxNodeLocationData> nodeLocations;

  private final Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay;

  private final Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay;

  public DepanFxNodeViewData(
      String toolName, String toolDescription,
      DepanFxNodeViewSceneData sceneData,
      DepanFxWorkspaceResource graphDocRsrc,
      DepanFxWorkspaceResource linkDisplayDocRsrc,
      Collection<GraphNode> viewNodes,
      Map<GraphNode, DepanFxNodeLocationData> nodeLocations,
      Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay,
      Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay) {
    super(toolName, toolDescription);
    this.sceneData = sceneData;
    this.graphDocRsrc = graphDocRsrc;
    this.linkDisplayDocRsrc = linkDisplayDocRsrc;
    this.viewNodes = viewNodes;
    this.nodeLocations = nodeLocations;
    this.nodeDisplay = nodeDisplay;
    this.edgeDisplay = edgeDisplay;
  }

  public DepanFxNodeViewData(
      String toolName, String toolDescription,
      DepanFxNodeViewSceneData sceneData,
      DepanFxWorkspaceResource graphDocRsrc,
      DepanFxWorkspaceResource linkDisplayDocRsrc,
      Collection<GraphNode> viewNodes) {
    this(toolName, toolDescription, sceneData,
        graphDocRsrc, linkDisplayDocRsrc,
        viewNodes, new HashMap<>(), new HashMap<>(), new HashMap<>());
  }

  public DepanFxNodeViewSceneData getSceneData() {
    return sceneData;
  }

  public DepanFxWorkspaceResource getGraphDocRsrc() {
    return graphDocRsrc;
  }

  public DepanFxWorkspaceResource getLinkDisplayDocRsrc() {
    return linkDisplayDocRsrc;
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

  /** Provide defensive copy. */
  public DepanFxNodeLocationData getNodeLocation(GraphNode node) {
    return nodeLocations.get(node);
  }

  /** Provide defensive copy. */
  public DepanFxNodeDisplayData getNodeDisplay(GraphNode node) {
    return nodeDisplay.get(node);
  }

  /** Provide defensive copy. */
  public DepanFxLineDisplayData getLineDisplay(GraphEdge edge) {
    return edgeDisplay.get(edge);
  }
}
