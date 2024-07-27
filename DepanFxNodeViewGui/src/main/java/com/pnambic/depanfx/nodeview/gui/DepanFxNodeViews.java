package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodeview.builtins.DepanFxGraphLinkViewBuiltIns;
import com.pnambic.depanfx.nodeview.builtins.DepanFxGraphNodeViewBuiltIns;
import com.pnambic.depanfx.nodeview.jogl.JoglCameras;
import com.pnambic.depanfx.nodeview.jogl.JoglColors;
import com.pnambic.depanfx.nodeview.layouts.GridLayoutRunner;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewCameraData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLayoutData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewSceneData;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
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

  private DepanFxNodeViews() {
    // Prevent instantiation.
  }

  public static DepanFxNodeViewData fromNodeList(
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc,
      DepanFxWorkspace workspace) {

    DepanFxNodeList nodeList = nodeListRsrc.getResource();
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
        nodeList.getGraphDocResource(), nodes, workspace);
  }

  public static DepanFxNodeViewData fromGraphDocument(
      DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
      DepanFxWorkspace workspace) {

    GraphDocument graphDoc = graphDocRsrc.getResource();

    Collection<GraphNode> nodes = graphDoc.getGraph().getGraphNodes();

    String baseName =
        DepanFxWorkspaceFactory.buildDocTitle(graphDocRsrc.getDocument());
    String resultName = MessageFormat.format("{0} view", baseName);
    String resultDescr = MessageFormat.format(
        "From graph {0} ({1} nodes).", baseName, nodes.size());

    return buildNodeView(
        resultName, resultDescr,
        graphDocRsrc, nodes, workspace);
  }

  public static DepanFxNodeViewData updateNameDescr(
      DepanFxNodeViewData viewDoc, String nameText, String descrText) {
    return new DepanFxNodeViewData(
        nameText, descrText,
        viewDoc.getGraphDocRsrc(),
        viewDoc.getViewNodes(),
        viewDoc.getNodeLocations(),
        viewDoc.getNodeDisplay(),
        viewDoc.getEdgeDisplay(),

        viewDoc.getSceneData(),

        viewDoc.getAvailableNodeRsrc(),
        viewDoc.getVisibleNodeRsrc(),
        viewDoc.getNodeDisplayDocRsrc(),
        viewDoc.getRemainderNodesVisible(),
        viewDoc.getRemainderNodesDisplay(),

        viewDoc.getAvailableEdgeRsrc(),
        viewDoc.getVisibleEdgeRsrc(),
        viewDoc.getLinkDisplayDocRsrc(),
        viewDoc.getRemainderEdgesVisible(),
        viewDoc.getRemainderEdgesLabel(),
        viewDoc.getRemainderEdgeDisplay());
  }

  private static DepanFxNodeViewData buildNodeView(
      String viewName, String viewDescr,
      DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
      Collection<GraphNode> nodes, DepanFxWorkspace workspace) {
    DepanFxNodeViewCameraData cameraData = JoglCameras.getHome();
    DepanFxNodeViewSceneData sceneData =
        new DepanFxNodeViewSceneData(
            JoglColors.of(DEFAULT_BACKGROUND_COLOR), cameraData);

      ContextModelId modelId = graphDocRsrc.getResource().getContextModelId();
      DepanFxWorkspaceResource<DepanFxNodeViewLayoutData> layoutRsrc =
          getContextLayout(workspace, modelId );
      DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> linkDisplayRsrc =
          getContextLinkDisplay(workspace, modelId);
      DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData> nodeDisplayRsrc =
          getContextNodeDisplay(workspace, modelId);

    Map<GraphNode, DepanFxNodeLocationData> locations =
        buildNodeLocations(nodes, layoutRsrc);
    Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay =
        buildNodeDisplay(nodes);
    Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay =
        buildEdgeDisplay();

    return new DepanFxNodeViewData(viewName, viewDescr,
        graphDocRsrc, nodes, locations, nodeDisplay, edgeDisplay,

        sceneData,
        DepanFxNodeViewData.EMPTY_AVAILABLE_NODES,
        DepanFxNodeViewData.EMPTY_VISIBLE_NODES,
        nodeDisplayRsrc,
        DepanFxNodeViewData.DEFAULT_REMAINDER_NODES_VISIBLE,
        DepanFxNodeViewData.DEFAULT_REMAINDER_NODE_DISPLAY,

        DepanFxNodeViewData.EMPTY_AVAILABLE_EDGES,
        DepanFxNodeViewData.EMPTY_VISIBLE_EDGES,
        linkDisplayRsrc,
        DepanFxNodeViewData.DEFAULT_REMAINDER_EDGES_VISIBLE,
        DepanFxNodeViewData.DEFAULT_REMAINDER_EDGES_LABEL,
        DepanFxNodeViewData.DEFAULT_REMAINDER_EDGE_DISPLAY);
  }

  private static Map<GraphEdge, DepanFxLineDisplayData> buildEdgeDisplay() {
    return Collections.emptyMap();
  }

  private static Map<GraphNode, DepanFxNodeDisplayData> buildNodeDisplay(
      Collection<GraphNode> nodes) {
    return Collections.emptyMap();
  }

  private static Map<GraphNode, DepanFxNodeLocationData> buildNodeLocations(
      Collection<GraphNode> nodes,
      DepanFxWorkspaceResource<DepanFxNodeViewLayoutData> layoutRsrc) {

    // TODO: Pick from alternatives for a better initial layout choice.
    return GridLayoutRunner.buildNodeLocations(nodes);
  }

  private static DepanFxWorkspaceResource<DepanFxNodeViewLayoutData>
  getContextLayout(
      DepanFxWorkspace workspace, ContextModelId contextModelId) {

    return null;
  }

  private static DepanFxWorkspaceResource<DepanFxNodeViewLayoutData>
  TODOgetContextLayout(
      DepanFxWorkspace workspace, ContextModelId contextModelId) {

    return getContextBuiltIn(workspace, contextModelId,
        DepanFxNodeViewLayoutData.class,
        d -> null,
        null); // DepanFxNodeLayoutConfiguration.ALL_EDGES_DOC_PATH);
  }

  private static DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData>
  getContextNodeDisplay(
        DepanFxWorkspace workspace, ContextModelId contextModelId) {

    return getContextBuiltIn(workspace, contextModelId,
        DepanFxNodeViewNodeDisplayData.class,
        DepanFxNodeViewData.NODE_VIEW_TOOL_PATH, " Node Display",
        DepanFxGraphNodeViewBuiltIns.ALL_NODES_DISPLAY_DOC_PATH);
  }

  private static DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData>
  getContextLinkDisplay(
      DepanFxWorkspace workspace, ContextModelId contextModelId) {

    return getContextBuiltIn(workspace, contextModelId,
        DepanFxNodeViewLinkDisplayData.class,
        d -> d.getContextModelId(),
        DepanFxGraphLinkViewBuiltIns.ALL_EDGES_DISPLAY_DOC_PATH);
  }

  private static <T> DepanFxWorkspaceResource<T>
  getContextBuiltIn(
        DepanFxWorkspace workspace, ContextModelId contextModelId,
        Class<T> resourceType, Function<T, ContextModelId> getModel,
        Path fallbackPath) {

    return DepanFxProjects.getBuiltIn(
            workspace, resourceType,
            c -> byContextModel(c, contextModelId, getModel))
        .orElseGet(() ->
            DepanFxProjects.getBuiltIn(workspace, resourceType, fallbackPath)
                .get());
  }

  private static <T> DepanFxWorkspaceResource<T>
  getContextBuiltIn(
        DepanFxWorkspace workspace, ContextModelId contextModelId,
        Class<T> resourceType, Path contextBasePath, String suffix,
        Path fallbackPath) {

    String resourceName = contextModelId.getContextModelKey() + suffix;
    Path resourcePath = contextBasePath.resolve(resourceName);

    return DepanFxProjects.getBuiltIn(workspace, resourceType, resourcePath)
        .orElseGet(() ->
            DepanFxProjects.getBuiltIn(workspace, resourceType, fallbackPath)
                .get());
  }

  private static <T> boolean byContextModel(
      DepanFxBuiltInContribution<T> contrib, ContextModelId contextModelId,
      Function<T, ContextModelId> getModel) {

    return contextModelId.equals(getModel.apply(contrib.getDocument()));
  }
}
