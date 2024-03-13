package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodeview.layouts.GridLayoutRunner;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxJoglColor;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewCameraData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewSceneData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxSizerModel;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.scene.paint.Color;

public class DepanFxNodeViews {

  public static final Color DEFAULT_BACKGROUND_COLOR =
      Color.rgb(240, 240, 240);     // cream

  public static final Color DEFAULT_NODE_COLOR =
      Color.rgb(40, 40, 40);        // dark

  public static final Color[] NODE_COLOR_CHOICES =
      new Color[] {
          Color.rgb(40, 40, 40),    // dark
          Color.rgb(140, 40, 40),   // burgundy
          Color.rgb(40, 140, 40),   // green
          Color.rgb(40, 40, 140),   // navy blue
          Color.rgb(140, 140, 40),  // khaki
          Color.rgb(40, 140, 140),  // teal
          Color.rgb(255, 255, 255)  // white (over cream background)
      };

  private static int colorChoice = 0;

  private DepanFxNodeViews() {
    // Prevent instantiation.
  }

  public static DepanFxNodeViewData fromNodeList(
      DepanFxWorkspaceResource nodeListRsrc,
      DepanFxWorkspaceResource linkDisplayDocRsrc) {

    DepanFxNodeList nodeList = (DepanFxNodeList) nodeListRsrc.getResource();
    Collection<GraphNode> nodes = nodeList.getNodes().stream()
        .map(GraphNode.class::cast)
        .collect(Collectors.toList());

    String baseName =
        DepanFxWorkspaceFactory.buildDocTitle(nodeListRsrc.getDocument());
    String resultName = MessageFormat.format("{0} view", baseName);
    String resultDescr = MessageFormat.format(
        "From node list {0} ({1} nodes).", baseName, nodes.size());
    return buildNodeView(
        resultName, resultDescr,
        nodeList.getGraphDocResource(), linkDisplayDocRsrc,
        nodes, null);
  }

  public static DepanFxNodeViewData fromGraphDocument(
      DepanFxWorkspaceResource graphDocRsrc,
      DepanFxWorkspaceResource linkDisplayDocRsrc) {

    GraphDocument graphDoc = (GraphDocument) graphDocRsrc.getResource();

    Collection<GraphNode> nodes = graphDoc.getGraph().getNodes().stream()
        .map(GraphNode.class::cast)
        .collect(Collectors.toList());

    String baseName =
        DepanFxWorkspaceFactory.buildDocTitle(graphDocRsrc.getDocument());
    String resultName = MessageFormat.format("{0} view", baseName);
    String resultDescr = MessageFormat.format(
        "From graph {0} ({1} nodes).", baseName, nodes.size());

    return buildNodeView(
        resultName, resultDescr,
        graphDocRsrc, linkDisplayDocRsrc,
        nodes, null);
  }

  public static DepanFxNodeViewData updateNameDescr(
      DepanFxNodeViewData viewDoc, String nameText, String descrText) {
    return new DepanFxNodeViewData(
        nameText, descrText, viewDoc.getSceneData(),
        viewDoc.getGraphDocRsrc(), viewDoc.getLinkDisplayDocRsrc(),
        viewDoc.getViewNodes(),
        viewDoc.getNodeLocations(), viewDoc.getNodeDisplay(),
        viewDoc.getEdgeDisplay());
  }

  private static DepanFxNodeViewData buildNodeView(
      String viewName, String viewDescr,
      DepanFxWorkspaceResource graphDocRsrc,
      DepanFxWorkspaceResource linkViewDocRsrc,
      Collection<GraphNode> nodes,
      DepanFxWorkspace workspace) {
    DepanFxNodeViewCameraData cameraData = DepanFxNodeViewCameraData.getHome();
    DepanFxNodeViewSceneData sceneData =
        new DepanFxNodeViewSceneData(
            DepanFxJoglColor.of(DEFAULT_BACKGROUND_COLOR), cameraData);

    Map<GraphNode, DepanFxNodeLocationData> locations =
        buildNodeLocations(nodes);
    Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay =
        buildNodeDisplay(nodes);
    Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay =
        buildEdgeDisplay();
    return new DepanFxNodeViewData(viewName, viewDescr, sceneData,
        graphDocRsrc, linkViewDocRsrc, nodes,
        locations, nodeDisplay, edgeDisplay);
  }

  private static Map<GraphEdge, DepanFxLineDisplayData> buildEdgeDisplay() {
    return Collections.emptyMap();
  }

  private static Map<GraphNode, DepanFxNodeDisplayData> buildNodeDisplay(
      Collection<GraphNode> nodes) {

    Map<GraphNode, DepanFxNodeDisplayData> result =
        new HashMap<>(nodes.size());
    nodes.stream()
        .forEach(n -> result.put(n, buildNodeDisplay(n)));
    return result;
  }

  private static DepanFxNodeDisplayData buildNodeDisplay(GraphNode n) {
    Color color = NODE_COLOR_CHOICES[colorChoice++];
    if (colorChoice >= NODE_COLOR_CHOICES.length) {
      colorChoice = 0;
    }
    DepanFxJoglColor blix = DepanFxJoglColor.of(color);
    return new DepanFxNodeDisplayData(
        true, DepanFxSizerModel.FIXED, blix );
  }

  private static Map<GraphNode, DepanFxNodeLocationData> buildNodeLocations(
      Collection<GraphNode> nodes) {
    int size = nodes.size();
    int width = (int) Math.ceil(Math.sqrt(size));
    int breadth = (size + width - 1) / width;
    GridLayoutRunner layout = new GridLayoutRunner(
        width, breadth, GridLayoutRunner.LayoutDirection.HORIZONTAL);
    layout.layoutNodes(nodes);
    return layout.getPositions(nodes);
  }
}
