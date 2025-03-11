package com.pnambic.depanfx.nodeview.persistence;

import com.pnambic.depanfx.graph.info.GraphNodeInfo;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.docdata.NodeInfoBlock;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.graph_doc.model.GraphModel;
import com.pnambic.depanfx.nodefilters.tooldata.DepanFxNodeFilterSequenceData;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherSequenceDocument;
import com.pnambic.depanfx.nodeview.builtins.DepanFxGraphLinkViewBuiltIns;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxJoglColor;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewCameraData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewSceneData;
import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistTagDataLoader;
import com.pnambic.depanfx.persistence.PersistTagDataResult;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;
import com.pnambic.modxstream.XstreamMarshalContext;
import com.pnambic.modxstream.XstreamUnmarshalContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepanFxNodeViewDataConverter
    extends BasePersistObjectConverter<DepanFxNodeViewData> {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeViewDataConverter.class);

  public static final String NODE_VIEW_TAG = "node-view";

  public static final String NODE_VIEW_NAME = "node-view-name";

  public static final String NODE_VIEW_DESCR = "node-view-descr";

  public static final String GRAPH_DOC = "graph-doc";

  public static final String SCENE_DATA = "scene-data";

  private static final String AVAILABLE_NODE_RSRC = "avail-nodes-rsrc";

  private static final String VISIBLE_NODE_RSRC = "visible-nodes-rsrc";

  public static final String NODE_DISPLAY_DOC = "node-display-doc";

  private static final String REMAINDER_NODES_VISIBLE = "remainder-nodes-visible";

  private static final String REMAINDER_NODES_DISPLAY = "remainder-nodes-display";

  private static final String AVAILABLE_EDGE_RSRC = "avail-edges-rsrc";

  private static final String VISIBLE_EDGE_RSRC = "visible-edges-rsrc";

  public static final String LINK_DISPLAY_DOC = "link-display-doc";

  private static final String REMAINDER_EDGES_VISIBLE = "remainder-edges-visible";

  private static final String REMAINDER_EDGES_LABEL = "remainder-edges-label";

  private static final String REMAINDER_EDGES_DISPLAY = "remainder-edges-display";

  private static final Class<?>[] ALLOWED_TYPES = new Class[] {
      DepanFxNodeViewData.class,
      DepanFxNodeViewSceneData.class,
          DepanFxJoglColor.class, DepanFxNodeViewCameraData.class,
      NodeInfoBlock.class,
          DepanFxNodeLocationData.class, DepanFxNodeDisplayData.class,
      DepanFxLineDisplayData.class
  };

  private static final PersistTagDataLoader.TagDescriptor[] TAG_DATA_DESCR =
      new PersistTagDataLoader.TagDescriptor[] {
          new PersistTagDataLoader.TagDescriptor(NODE_VIEW_NAME, String.class),
          new PersistTagDataLoader.TagDescriptor(NODE_VIEW_DESCR, String.class),
          new PersistTagDataLoader.TagDescriptor(
              GRAPH_DOC, DepanFxWorkspaceResource.class),
          new PersistTagDataLoader.TagDescriptor(
              SCENE_DATA, DepanFxNodeViewSceneData.class),
          new PersistTagDataLoader.TagDescriptor(
              AVAILABLE_NODE_RSRC, DepanFxWorkspaceResource.class),
          new PersistTagDataLoader.TagDescriptor(
              VISIBLE_NODE_RSRC, DepanFxWorkspaceResource.class),
          new PersistTagDataLoader.TagDescriptor(
              NODE_DISPLAY_DOC, DepanFxWorkspaceResource.class),
          new PersistTagDataLoader.TagDescriptor(
              REMAINDER_NODES_VISIBLE, Boolean.class),
          new PersistTagDataLoader.TagDescriptor(
              REMAINDER_NODES_DISPLAY, DepanFxNodeDisplayData.class),
          new PersistTagDataLoader.TagDescriptor(
              AVAILABLE_EDGE_RSRC, DepanFxWorkspaceResource.class),
          new PersistTagDataLoader.TagDescriptor(
              VISIBLE_EDGE_RSRC, DepanFxWorkspaceResource.class),
          new PersistTagDataLoader.TagDescriptor(
              LINK_DISPLAY_DOC, DepanFxWorkspaceResource.class),
          new PersistTagDataLoader.TagDescriptor(
              REMAINDER_EDGES_VISIBLE, Boolean.class),
          new PersistTagDataLoader.TagDescriptor(
              REMAINDER_EDGES_LABEL, String.class),
          new PersistTagDataLoader.TagDescriptor(
              REMAINDER_EDGES_DISPLAY, DepanFxLineDisplayData.class)
      };

  private static final Map<String, String> TAGS_ALIAS = Collections.emptyMap();

  private static final PersistTagDataLoader TAG_LOADER =
      new PersistTagDataLoader(TAG_DATA_DESCR, TAGS_ALIAS);

  private static final String[] META_TAGS = new String[] {
      NODE_VIEW_NAME, NODE_VIEW_DESCR,
      GRAPH_DOC,
      SCENE_DATA,
      AVAILABLE_NODE_RSRC, VISIBLE_NODE_RSRC, NODE_DISPLAY_DOC,
      REMAINDER_NODES_VISIBLE, REMAINDER_NODES_DISPLAY,
      AVAILABLE_EDGE_RSRC, VISIBLE_EDGE_RSRC, LINK_DISPLAY_DOC,
      REMAINDER_EDGES_VISIBLE, REMAINDER_EDGES_LABEL, REMAINDER_EDGES_DISPLAY
  };

  @Override
  public Class<?> forType() {
    return DepanFxNodeViewData.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOWED_TYPES;
  }

  @Override
  public String getTag() {
    return NODE_VIEW_TAG;
  }

  @Override
  public void marshal(XstreamMarshalContext dstContext, Object source) {
    DepanFxNodeViewData viewData = (DepanFxNodeViewData) source;

    DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> availableNodeRsrc =
        viewData.getAvailableNodeResource();
    DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> visibleNodeRsrc =
        viewData.getVisibleNodeResource();

    DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> availableEdgeRsrc =
        viewData.getAvailableEdgeResource();
    DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> visibleEdgeRsrc =
        viewData.getVisibleEdgeResource();

    marshalObject(dstContext,
        NODE_VIEW_NAME, viewData.getToolName());
    marshalObject(dstContext,
        NODE_VIEW_DESCR, viewData.getToolDescription());

    marshalObject(dstContext,
        GRAPH_DOC, viewData.getGraphDocRsrc());

    marshalObject(dstContext,
        SCENE_DATA, viewData.getSceneData());

    if (availableNodeRsrc != null) {
      marshalObject(dstContext, AVAILABLE_NODE_RSRC, availableNodeRsrc);
    }
    if (visibleNodeRsrc != null) {
      marshalObject(dstContext, VISIBLE_NODE_RSRC, visibleNodeRsrc);
    }
    marshalObject(dstContext,
        NODE_DISPLAY_DOC, viewData.getNodeDisplayDocRsrc());
    marshalObject(dstContext,
        REMAINDER_NODES_VISIBLE, viewData.getRemainderNodesVisible());
    marshalObject(dstContext,
        REMAINDER_NODES_DISPLAY, viewData.getRemainderNodesDisplay());

    if (availableEdgeRsrc != null) {
      marshalObject(dstContext, AVAILABLE_EDGE_RSRC, availableEdgeRsrc);
    }
    if (visibleEdgeRsrc != null) {
      marshalObject(dstContext, VISIBLE_EDGE_RSRC, visibleEdgeRsrc);
    }

    marshalObject(dstContext,
        LINK_DISPLAY_DOC, viewData.getLinkDisplayDocRsrc());
    marshalObject(dstContext,
        REMAINDER_EDGES_VISIBLE, viewData.getRemainderEdgesVisible());
    marshalObject(dstContext,
        REMAINDER_EDGES_LABEL, viewData.getRemainderEdgesLabel());
    marshalObject(dstContext,
        REMAINDER_EDGES_DISPLAY, viewData.getRemainderEdgeDisplay());

    viewData.getViewNodes().stream()
        .forEach(n -> marshalNodeInfo(dstContext, n, viewData));
  }

  @Override
  public DepanFxNodeViewData unmarshal(XstreamUnmarshalContext srcContext) {

    DepanFxWorkspace workspace =
        (DepanFxWorkspace) srcContext.getContextValue(DepanFxWorkspace.class);

    PersistTagDataResult metaData =
        new PersistTagDataResult(TAG_LOADER.loadData(META_TAGS, srcContext));

    String toolName = metaData.getString(NODE_VIEW_NAME);
    String toolDescr = metaData.getString(NODE_VIEW_DESCR);
    DepanFxNodeViewSceneData sceneData =
        metaData.getObject(SCENE_DATA, DepanFxNodeViewSceneData.class);

    @SuppressWarnings("unchecked")
    DepanFxWorkspaceResource<GraphDocument> graphDocRsrc =
        metaData.getObject(GRAPH_DOC, DepanFxWorkspaceResource.class);

    @SuppressWarnings("unchecked")
    DepanFxWorkspaceResource<DepanFxNodeViewNodeDisplayData> nodeDisplayDocRsrc =
        metaData.getObject(NODE_DISPLAY_DOC, DepanFxWorkspaceResource.class);

    boolean remainderNodesVisible = metaData.getBoolean(
        REMAINDER_NODES_VISIBLE,
        DepanFxNodeViewData.DEFAULT_REMAINDER_NODES_VISIBLE);
    DepanFxNodeDisplayData remainderNodeDisplay =
        metaData.getObject(
            REMAINDER_NODES_DISPLAY, DepanFxNodeDisplayData.class,
            DepanFxNodeViewData.DEFAULT_REMAINDER_NODE_DISPLAY);

    @SuppressWarnings("unchecked")
    DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> availableNodeRsrc =
        metaData.getObject(AVAILABLE_NODE_RSRC, DepanFxWorkspaceResource.class);

    @SuppressWarnings("unchecked")
    DepanFxWorkspaceResource<DepanFxNodeFilterSequenceData> visibleNodeRsrc =
        metaData.getObject(VISIBLE_NODE_RSRC, DepanFxWorkspaceResource.class);

    @SuppressWarnings("unchecked")
    DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> availableEdgeRsrc =
        metaData.getObject(AVAILABLE_EDGE_RSRC, DepanFxWorkspaceResource.class);

    @SuppressWarnings("unchecked")
    DepanFxWorkspaceResource<DepanFxLinkMatcherSequenceDocument> visibleEdgeRsrc =
        metaData.getObject(VISIBLE_EDGE_RSRC, DepanFxWorkspaceResource.class);

    @SuppressWarnings("unchecked")
    DepanFxWorkspaceResource<DepanFxNodeViewLinkDisplayData> linkDisplayDocRsrc =
        metaData.getObject(LINK_DISPLAY_DOC, DepanFxWorkspaceResource.class);

    boolean remainderEdgesVisible = metaData.getBoolean(
        REMAINDER_EDGES_VISIBLE,
        DepanFxNodeViewData.DEFAULT_REMAINDER_NODES_VISIBLE);
    String remainderEdgesLabel = metaData.getString(
        REMAINDER_EDGES_LABEL,
        DepanFxNodeViewData.DEFAULT_REMAINDER_EDGES_LABEL);
    DepanFxLineDisplayData remainderEdgeDisplay =
        metaData.getObject(
            REMAINDER_EDGES_DISPLAY, DepanFxLineDisplayData.class,
            DepanFxNodeViewData.DEFAULT_REMAINDER_EDGE_DISPLAY);

    // Extract the basis for model mapping.
    GraphModel graphModel = graphDocRsrc.getResource().getGraph();
    srcContext.putContextValue(GraphModel.class, graphModel);

    // Load the nodes and their view data
    NodeInfoBuilder nodeBuilder = new NodeInfoBuilder();

    while (srcContext.hasMoreChildren()) {

      Object element = unmarshalOne(srcContext);
      if (element instanceof NodeInfoBlock) {
        NodeInfoBlock nodeInfo = (NodeInfoBlock) element;
        GraphNode node = nodeInfo.mapNode(graphModel);
        nodeBuilder.addViewNode(node);
        nodeInfo.streamInfos()
            .forEach(i -> nodeBuilder.addInfo(node, i));
      } else {
        LOG.warn("Unrecognized node view element {}", element.getClass());
      }
    }

    // [Apr-2024] Not yet persisted.
    Map<GraphEdge, DepanFxLineDisplayData> edgeDisplay =
        Collections.emptyMap();

    Collection<GraphNode> viewNodes = nodeBuilder.getViewNodes();
    Map<GraphNode, DepanFxNodeLocationData> nodeLocations =
        nodeBuilder.getNodeLocations();
    Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay =
        nodeBuilder.getNodeDisplay();

    if (linkDisplayDocRsrc == null) {
      linkDisplayDocRsrc =
          DepanFxProjects.getBuiltIn(workspace,
              DepanFxNodeViewLinkDisplayData.class,
              DepanFxGraphLinkViewBuiltIns.ALL_EDGES_DISPLAY_DOC_PATH)
          .get();
    }

    // When scratch resources are used.
    if (availableEdgeRsrc == null) {
      availableEdgeRsrc = DepanFxNodeViewData.buildAvailableEdgeResource(
          workspace, linkDisplayDocRsrc);
    }
    if (visibleEdgeRsrc == null) {
      visibleEdgeRsrc = availableEdgeRsrc;
    }

    if (availableNodeRsrc == null) {
      availableNodeRsrc = DepanFxNodeViewData.buildAvailableNodeResource(
          workspace, nodeDisplayDocRsrc);
    }
    if (visibleNodeRsrc == null) {
      visibleNodeRsrc = availableNodeRsrc;
    }

    return new DepanFxNodeViewData(toolName, toolDescr,
        graphDocRsrc, viewNodes, nodeLocations, nodeDisplay, edgeDisplay,
        sceneData,
        availableNodeRsrc, visibleNodeRsrc, nodeDisplayDocRsrc,
        remainderNodesVisible, remainderNodeDisplay,
        availableEdgeRsrc, visibleEdgeRsrc, linkDisplayDocRsrc,
        remainderEdgesVisible, remainderEdgesLabel, remainderEdgeDisplay);
  }

  private void marshalNodeInfo(
      XstreamMarshalContext dstContext,
      GraphNode node,
      DepanFxNodeViewData viewData) {

    GraphNodeInfo[] viewInfo = new GraphNodeInfo[] {
        viewData.getNodeLocation(node),
        viewData.getNodeDisplay(node)};

    marshalObject(dstContext, new NodeInfoBlock(node, Arrays.asList(viewInfo)));
  }

  private static class NodeInfoBuilder {

    private final List<GraphNode> viewNodes = new ArrayList<>();

    private final Map<GraphNode, DepanFxNodeLocationData> nodeLocations =
        new HashMap<>();

    private final Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay =
        new HashMap<>();

    public void addViewNode(GraphNode viewNode) {
      viewNodes.add(viewNode);
    }

    public void addInfo(GraphNode node, GraphNodeInfo info) {
      if (info instanceof DepanFxNodeLocationData) {
        nodeLocations.put(node, (DepanFxNodeLocationData) info);
      }
      if (info instanceof DepanFxNodeDisplayData) {
        nodeDisplay.put(node, (DepanFxNodeDisplayData) info);
      }
    }

    public Map<GraphNode, DepanFxNodeDisplayData> getNodeDisplay() {
      return nodeDisplay;
    }

    public Map<GraphNode, DepanFxNodeLocationData> getNodeLocations() {
      return nodeLocations;
    }

    public Collection<GraphNode> getViewNodes() {
      return viewNodes;
    }
  }
}
