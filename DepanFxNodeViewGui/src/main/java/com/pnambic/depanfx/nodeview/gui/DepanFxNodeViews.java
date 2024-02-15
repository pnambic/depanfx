package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodeview.layouts.GridLayoutRunner;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxEdgeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxJoglColor;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewCameraData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewSceneData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxSizerModel;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DepanFxNodeViews {

  public static final DepanFxJoglColor DEFAULT_BACKGROUND_COLOR =
      DepanFxJoglColor.ofRGB(240, 240, 240);

  public static final DepanFxJoglColor DEFAULT_NODE_COLOR =
      DepanFxJoglColor.ofRGB(40, 40, 40);

  public static final DepanFxJoglColor[] NODE_COLOR_CHOICES =
      new DepanFxJoglColor[] {
          DepanFxJoglColor.ofRGB(40, 40, 40),   // dark
          DepanFxJoglColor.ofRGB(140, 40, 40),  // burgundy
          DepanFxJoglColor.ofRGB(40, 140, 40),  // green
          DepanFxJoglColor.ofRGB(40, 40, 140),  // navy blue
          DepanFxJoglColor.ofRGB(140, 140, 40), // khaki
          DepanFxJoglColor.ofRGB(40, 140, 140), // teal
          DepanFxJoglColor.ofRGB(240, 240, 240)
      };

  private static int colorChoice = 0;

  private DepanFxNodeViews() {
    // Prevent instantiation.
  }

  public static DepanFxNodeViewData fromNodeList(
      DepanFxWorkspaceResource nodeListRsrc) {

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
        resultName, resultDescr, nodeList.getGraphDocResource(), nodes);
  }

  public static DepanFxNodeViewData fromGraphDocument(
      DepanFxWorkspaceResource graphDocRsrc) {

    GraphDocument graphDoc = (GraphDocument) graphDocRsrc.getResource();

    Collection<GraphNode> nodes = graphDoc.getGraph().getNodes().stream()
        .map(GraphNode.class::cast)
        .collect(Collectors.toList());

    String baseName =
        DepanFxWorkspaceFactory.buildDocTitle(graphDocRsrc.getDocument());
    String resultName = MessageFormat.format("{0} view", baseName);
    String resultDescr = MessageFormat.format(
        "From graph {0} ({1} nodes).", baseName, nodes.size());
    return buildNodeView(resultName, resultDescr, graphDocRsrc, nodes);
  }

  public static DepanFxNodeViewData updateNameDescr(
      DepanFxNodeViewData viewDoc, String nameText, String descrText) {
    return new DepanFxNodeViewData(
        nameText, descrText, viewDoc.getSceneData(),
        viewDoc.getGraphDocRsrc(), viewDoc.getViewNodes(),
        viewDoc.getNodeLocations(), viewDoc.getNodeDisplay(),
        viewDoc.getEdgeDisplay());
  }

  private static DepanFxNodeViewData buildNodeView(
      String viewName, String viewDescr,
      DepanFxWorkspaceResource graphDocRsrc, Collection<GraphNode> nodes) {
    DepanFxNodeViewCameraData cameraData = DepanFxNodeViewCameraData.getHome();
    DepanFxNodeViewSceneData sceneData =
        new DepanFxNodeViewSceneData(DEFAULT_BACKGROUND_COLOR, cameraData);
    Map<GraphNode, DepanFxNodeLocationData> locations =
        buildNodeLocations(nodes);
    Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay =
        buildNodeDisplay(nodes);
    Map<GraphEdge, DepanFxEdgeDisplayData> edgeDisplay =
        buildEdgeDisplay();
    return new DepanFxNodeViewData(viewName, viewDescr, sceneData,
        graphDocRsrc, nodes,
        locations, nodeDisplay, edgeDisplay);
  }

  private static Map<GraphEdge, DepanFxEdgeDisplayData> buildEdgeDisplay() {
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
    DepanFxJoglColor color = NODE_COLOR_CHOICES[colorChoice++];
    if (colorChoice >= NODE_COLOR_CHOICES.length) {
      colorChoice = 0;
    }
    return new DepanFxNodeDisplayData(
        true, DepanFxSizerModel.FIXED, color);
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
