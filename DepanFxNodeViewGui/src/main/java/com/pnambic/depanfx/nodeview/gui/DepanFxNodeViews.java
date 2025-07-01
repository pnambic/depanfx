package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxNodeFilterSequenceData;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherSequenceDocument;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxNodeFoldData;
import com.pnambic.depanfx.nodeview.builtins.DepanFxGraphLinkViewBuiltIns;
import com.pnambic.depanfx.nodeview.builtins.DepanFxGraphNodeViewBuiltIns;
import com.pnambic.depanfx.nodeview.jogl.JoglCameras;
import com.pnambic.depanfx.nodeview.jogl.JoglColors;
import com.pnambic.depanfx.nodeview.layouts.DepanFxNodeLayoutRegistry;
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
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.scene.paint.Color;

public class DepanFxNodeViews {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeViews.class);

  public static final DepanFxResourceFilter NODE_VIEW_RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Node View",
          DepanFxNodeViewData.NODE_VIEW_TOOL_EXT,
          DepanFxNodeViewData.class);

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
      DepanFxWorkspace workspace, DepanFxNodeLayoutRegistry layoutRegistry) {

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
        nodeList.getGraphDocResource(), nodes, workspace, layoutRegistry);
  }

  public static DepanFxNodeViewData fromGraphDocument(
      DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
      DepanFxWorkspace workspace, DepanFxNodeLayoutRegistry layoutRegistry) {

    GraphDocument graphDoc = graphDocRsrc.getResource();

    Collection<GraphNode> nodes = graphDoc.getGraph().getGraphNodes();

    String baseName =
        DepanFxWorkspaceFactory.buildDocTitle(graphDocRsrc.getDocument());
    String resultName = MessageFormat.format("{0} view", baseName);
    String resultDescr = MessageFormat.format(
        "From graph {0} ({1} nodes).", baseName, nodes.size());

    return buildNodeView(
        resultName, resultDescr,
        graphDocRsrc, nodes, workspace, layoutRegistry);
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

        viewDoc.getAvailableNodeResource(),
        viewDoc.getVisibleNodeResource(),
        viewDoc.getNodeFoldResource(),
        viewDoc.getNodeDisplayDocRsrc(),
        viewDoc.getRemainderNodesVisible(),
        viewDoc.getRemainderNodesDisplay(),

        viewDoc.getAvailableEdgeResource(),
        viewDoc.getVisibleEdgeResource(),
        viewDoc.getLinkDisplayDocRsrc(),
        viewDoc.getRemainderEdgesVisible(),
        viewDoc.getRemainderEdgesLabel(),
        viewDoc.getRemainderEdgeDisplay());
  }

  private static DepanFxNodeViewData buildNodeView(
      String viewName, String viewDescr,
      DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
      Collection<GraphNode> nodes, DepanFxWorkspace workspace,
      DepanFxNodeLayoutRegistry layoutRegistry) {
    DepanFxNodeViewCameraData cameraData = JoglCameras.getHome();
    DepanFxNodeViewSceneData sceneData =
        new DepanFxNodeViewSceneData(
            JoglColors.of(DEFAULT_BACKGROUND_COLOR), cameraData);

      ContextModelId modelId = graphDocRsrc.getResource().getContextModelId();
      DepanFxWorkspaceResource<DepanFxNodeViewLayoutData> layoutRsrc =
          getContextLayout(workspace, modelId).orElse(null);

      // Nodes
      DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData> nodeDisplayRsrc =
          getContextNodeDisplay(workspace, modelId);
      DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> availableNodeRsrc =
          getContextNodeAvailable(workspace, nodeDisplayRsrc);
      DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> visibleNodeRsrc =
          availableNodeRsrc;
      // Start with no node folding.
      DepanFxWorkspaceResource<DepanFxNodeFoldData> nodeFoldRsrc = null;

      // Edges
      DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> linkDisplayRsrc =
          getContextLinkDisplay(workspace, modelId);
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> availableEdgeRsrc =
          getContextEdgeAvailable(workspace, linkDisplayRsrc);
      DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> visibleEdgeRsrc =
          availableEdgeRsrc;

    Map<GraphNode, DepanFxNodeLocationData> locations =
        buildNodeLocations(layoutRegistry, graphDocRsrc, nodes, layoutRsrc);
    Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay =
        buildNodeDisplay(nodes);
    Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay =
        buildEdgeDisplay();
    return new DepanFxNodeViewData(viewName, viewDescr,
        graphDocRsrc, nodes, locations, nodeDisplay, edgeDisplay,

        sceneData,

        availableNodeRsrc, visibleNodeRsrc, nodeFoldRsrc, nodeDisplayRsrc,
        DepanFxNodeViewData.DEFAULT_REMAINDER_NODES_VISIBLE,
        DepanFxNodeViewData.DEFAULT_REMAINDER_NODE_DISPLAY,

        availableEdgeRsrc, visibleEdgeRsrc, linkDisplayRsrc,
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
      DepanFxNodeLayoutRegistry layoutRegistry,
      DepanFxWorkspaceResource<GraphDocument> graphDocRsrc,
      Collection<GraphNode> nodes,
      DepanFxWorkspaceResource<DepanFxNodeViewLayoutData> layoutRsrc) {

    Map<GraphNode, DepanFxNodeLocationData> result =
        layoutRegistry.layoutNodes(graphDocRsrc, layoutRsrc, nodes);

    // Prepare a grid placement for any nodes not covered by the chosen layout.
    Set<GraphNode> gridNodes = new HashSet<>(nodes);
    gridNodes.removeAll(result.keySet());
    Map<GraphNode, DepanFxNodeLocationData> gridLayouts =
        GridLayoutRunner.buildNodeLocations(gridNodes);
    result.putAll(gridLayouts);
    return result;

  }

  private static Optional<DepanFxWorkspaceResource<DepanFxNodeViewLayoutData>>
  getContextLayout(
      DepanFxWorkspace workspace, ContextModelId contextModelId) {
    return getContextBuiltIn(workspace, contextModelId,
        DepanFxNodeViewLayoutData.class,
        DepanFxNodeViewLayoutData.LAYOUT_TOOL_PATH,
        DepanFxNodeViewLayoutData.MEMBER_LAYOUT_RESOURCE_NAME);
  }

  private static DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData>
  getContextNodeDisplay(
        DepanFxWorkspace workspace, ContextModelId contextModelId) {

    return getContextBuiltIn(workspace, contextModelId,
        DepanFxNodeViewNodeDisplayData.class,
        DepanFxNodeViewNodeDisplayData.NODE_DISPLAY_TOOL_PATH,
        DepanFxNodeViewNodeDisplayData.NODE_DISPLAY_CONTEXT_RESOURCE_NAME,
        DepanFxGraphNodeViewBuiltIns.ALL_NODES_DISPLAY_DOC_PATH);
  }

  private static DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData>
  getContextNodeAvailable(
      DepanFxWorkspace workspace,
      DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData> nodeDisplayRsrc) {
    return getContextBuiltIn(
            workspace,
            nodeDisplayRsrc.getResource().getContextModelId(),
            DepanFxNodeFilterSequenceData.class,
            DepanFxBaseFilterData.NODE_FILTERS_TOOL_PATH,
            DepanFxNodeFilterSequenceData.NODE_VISIBILITY_CONTEXT_RESOURCE_NAME)
        .orElseGet(() -> {
          LOG.info("Building available nodes for context {}",
              nodeDisplayRsrc.getResource()
              .getContextModelId().getContextModelKey());
          return DepanFxNodeViewData.buildAvailableNodeResource(
              workspace, nodeDisplayRsrc);
        });
  }

  private static DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData>
  getContextLinkDisplay(
      DepanFxWorkspace workspace, ContextModelId contextModelId) {

    return getContextBuiltIn(workspace, contextModelId,
        DepanFxNodeViewLinkDisplayData.class,
        DepanFxNodeViewLinkDisplayData.EDGE_DISPLAY_TOOL_PATH,
        DepanFxNodeViewLinkDisplayData.EDGE_DISPLAY_CONTEXT_RESOURCE_NAME,
        DepanFxGraphLinkViewBuiltIns.ALL_EDGES_DISPLAY_DOC_PATH);
  }

  private static DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument>
  getContextEdgeAvailable(
      DepanFxWorkspace workspace,
      DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> linkDisplayRsrc) {
    return getContextBuiltIn(
            workspace,
            linkDisplayRsrc.getResource().getContextModelId(),
            DepanFxLinkMatcherSequenceDocument.class,
            DepanFxLinkMatcherSequenceDocument.LINK_MATCHER_SEQUENCE_TOOL_PATH,
            DepanFxLinkMatcherSequenceDocument.EDGE_VISIBILITY_CONTEXT_RESOURCE_NAME)
        .orElseGet(() -> {
          LOG.info("Building available edges for context {}",
              linkDisplayRsrc.getResource()
              .getContextModelId().getContextModelKey());
          return DepanFxNodeViewData.buildAvailableEdgeResource(
              workspace, linkDisplayRsrc);
        });
  }

  private static <T> Optional<DepanFxWorkspaceResource<T>>
  getContextBuiltIn(
        DepanFxWorkspace workspace, ContextModelId contextModelId,
        Class<T> resourceType, Path contextBasePath, String rsrcName) {

    Path contextPath = contextBasePath
        .resolve(contextModelId.getContextModelKey())
        .resolve(rsrcName);

    return DepanFxProjects.getBuiltIn(workspace, resourceType, contextPath);
  }

  private static <T> DepanFxWorkspaceResource<T>
  getContextBuiltIn(
        DepanFxWorkspace workspace, ContextModelId contextModelId,
        Class<T> resourceType, Path contextBasePath, String rsrcName,
        Path fallbackPath) {

    return getContextBuiltIn(
        workspace, contextModelId, resourceType, contextBasePath, rsrcName)
        .orElseGet(() -> {
          LOG.info("Using fallback resource {} for {}({})",
              fallbackPath, rsrcName, resourceType.getSimpleName());
          return DepanFxProjects.getBuiltIn(
              workspace, resourceType, fallbackPath).get();
        });
  }
}
