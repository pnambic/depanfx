package com.pnambic.depanfx.nodeview.persistence;

import com.pnambic.depanfx.graph.info.GraphNodeInfo;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.docdata.NodeInfoBlock;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodeview.gui.DepanFxNodeViewLinkDisplayDataBuiltIns;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxJoglColor;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeLocationData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewCameraData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewSceneData;
import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistMarshalContext;
import com.pnambic.depanfx.persistence.PersistTagDataLoader;
import com.pnambic.depanfx.persistence.PersistUnmarshalContext;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

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

  public static final String GRAPH_DOC = "graph-doc";

  public static final String LINK_DISPLAY_DOC = "link-display-doc";

  public static final String NODE_VIEW_NAME = "node-view-name";

  public static final String NODE_VIEW_DESCR = "node-view-descr";

  public static final String SCENE_DATA = "scene-data";

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
          new PersistTagDataLoader.TagDescriptor(
              GRAPH_DOC, DepanFxWorkspaceResource.class),
          new PersistTagDataLoader.TagDescriptor(
              LINK_DISPLAY_DOC, DepanFxWorkspaceResource.class),
          new PersistTagDataLoader.TagDescriptor(NODE_VIEW_NAME, String.class),
          new PersistTagDataLoader.TagDescriptor(NODE_VIEW_DESCR, String.class),
          new PersistTagDataLoader.TagDescriptor(
              SCENE_DATA, DepanFxNodeViewSceneData.class)
      };

  private static final Map<String, String> TAGS_ALIAS = Collections.emptyMap();

  private static final PersistTagDataLoader TAG_LOADER =
      new PersistTagDataLoader(TAG_DATA_DESCR, TAGS_ALIAS);

  private static final String[] META_TAGS = new String[] {
      GRAPH_DOC, LINK_DISPLAY_DOC, NODE_VIEW_NAME, NODE_VIEW_DESCR, SCENE_DATA
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
  public void marshal(PersistMarshalContext dstContext, Object source) {
    DepanFxNodeViewData viewData = (DepanFxNodeViewData) source;

    marshalObject(dstContext,
        GRAPH_DOC, viewData.getGraphDocRsrc());
    marshalObject(dstContext,
        LINK_DISPLAY_DOC, viewData.getLinkDisplayDocRsrc());
    marshalObject(dstContext,
        NODE_VIEW_NAME, viewData.getToolName());
    marshalObject(dstContext,
        NODE_VIEW_DESCR, viewData.getToolDescription());
    marshalObject(dstContext,
        SCENE_DATA, viewData.getSceneData());

    viewData.getViewNodes().stream()
        .forEach(n -> marshalNodeInfo(dstContext, n, viewData));
  }

  @Override
  public DepanFxNodeViewData unmarshal(PersistUnmarshalContext srcContext) {

    Map<String, Object> metaData = TAG_LOADER.loadData(META_TAGS, srcContext);

    String toolName = (String) metaData.get(NODE_VIEW_NAME);
    String toolDescr = (String) metaData.get(NODE_VIEW_DESCR);
    DepanFxWorkspaceResource graphDocRsrc =
        (DepanFxWorkspaceResource) metaData.get(GRAPH_DOC);
    DepanFxWorkspaceResource linkDisplayDocRsrc =
        (DepanFxWorkspaceResource) metaData.get(LINK_DISPLAY_DOC);
    DepanFxNodeViewSceneData sceneData =
        (DepanFxNodeViewSceneData) metaData.get(SCENE_DATA);

    // Extract the basis for model mapping.
    GraphDocument graphDoc = (GraphDocument) graphDocRsrc.getResource();
    GraphModel graphModel = graphDoc.getGraph();
    srcContext.putContextValue(GraphModel.class, graphModel );

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

    Collection<GraphNode> viewNodes = nodeBuilder.getViewNodes();
    Map<GraphNode, DepanFxNodeLocationData> nodeLocations =
        nodeBuilder.getNodeLocations();
    Map<GraphNode, DepanFxNodeDisplayData> nodeDisplay =
        nodeBuilder.getNodeDisplay();

    if (linkDisplayDocRsrc == null) {
      DepanFxWorkspace workspace =
          (DepanFxWorkspace) srcContext.getContextValue(DepanFxWorkspace.class);
      linkDisplayDocRsrc =
          DepanFxProjects.getBuiltIn(workspace,
              DepanFxNodeViewLinkDisplayData.class,
              DepanFxNodeViewLinkDisplayDataBuiltIns.ALL_EDGES_DOC_PATH)
          .get();
    }
    return new DepanFxNodeViewData(toolName, toolDescr, sceneData,
        graphDocRsrc, linkDisplayDocRsrc,
        viewNodes, nodeLocations, nodeDisplay, Collections.emptyMap());
  }

  private void marshalNodeInfo(
      PersistMarshalContext dstContext,
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
