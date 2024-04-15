package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
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

  public static final boolean DEFAULT_REMAINDER_VISIBLE = true;

  public static final String DEFAULT_REMAINDER_LABEL = "Remainder";

  public static final DepanFxLineDisplayData DEFAULT_REMAINDER_DISPLAY =
      buildRemainerDisplay();

  public static final String NODE_VIEW_TOOL_EXT = "dnvi";

  public static final String NODE_VIEW_DIR = "Node Views";

  public static final String SIMPLE_VIEW_NAME = "Simple View";

  public static final Path NODE_VIEW_TOOL_PATH =
      DepanFxProjects.TOOLS_PATH.resolve(NODE_VIEW_DIR);

  private final DepanFxNodeViewSceneData sceneData;

  private final DepanFxWorkspaceResource<GraphDocument> graphDocRsrc;

  /**
   * Source of default rendering data for edges.
   *
   * Edges are matched to a link, then rendered with that link's selections.
   * Provides a {@link DepanFxNodeViewLinkDisplayData},
   * with a stream of {@code LinkDisplayEnty} values.
   */
  private final DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData>
      linkDisplayDocRsrc;

  private final Collection<GraphNode> viewNodes;

  private final Map<GraphNode, DepanFxNodeLocationData> nodeLocations;

  private final Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay;

  private final Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay;

  private boolean remainerVisible;

  private String remainderLabel;

  private DepanFxLineDisplayData remainderDisplay;

  public DepanFxNodeViewData(
      String toolName, String toolDescription,
      DepanFxNodeViewSceneData sceneData,
      DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
      DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> linkDisplayDocRsrc,
      Collection<GraphNode> viewNodes,
      Map<GraphNode, DepanFxNodeLocationData> nodeLocations,
      Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay,
      Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay,
      boolean remainerVisible,
      String remainderLabel,
      DepanFxLineDisplayData remainderDisplay) {
    super(toolName, toolDescription);
    this.sceneData = sceneData;
    this.graphDocRsrc = graphDocRsrc;
    this.linkDisplayDocRsrc = linkDisplayDocRsrc;
    this.viewNodes = viewNodes;
    this.nodeLocations = nodeLocations;
    this.nodeDisplay = nodeDisplay;
    this.edgeDisplay = edgeDisplay;
    this.remainerVisible = remainerVisible;
    this.remainderLabel = remainderLabel;
    this.remainderDisplay = remainderDisplay;
  }

  public DepanFxNodeViewSceneData getSceneData() {
    return sceneData;
  }

  public DepanFxWorkspaceResource<GraphDocument> getGraphDocRsrc() {
    return graphDocRsrc;
  }

  public DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> getLinkDisplayDocRsrc() {
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

  public boolean getRemainerVisible() {
    return remainerVisible;
  }

  public String getRemainderLabel() {
    return remainderLabel;
  }

  public DepanFxLineDisplayData getRemainerDisplay() {
    return remainderDisplay;
  }

  private static DepanFxLineDisplayData buildRemainerDisplay() {
    DepanFxLineDisplayData result =
        DepanFxLineDisplayData.buildSimpleLineDisplayData();
    result.lineColor = DepanFxJoglColor.of(Color.GRAY);
    return result ;
  }
}
