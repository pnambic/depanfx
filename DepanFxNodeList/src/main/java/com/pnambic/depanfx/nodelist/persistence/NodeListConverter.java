package com.pnambic.depanfx.nodelist.persistence;

import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphModels;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistMarshalContext;
import com.pnambic.depanfx.persistence.PersistTagDataLoader;
import com.pnambic.depanfx.persistence.PersistUnmarshalContext;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NodeListConverter
    extends BasePersistObjectConverter<DepanFxNodeList> {

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxNodeList.class
  };

  public static final String NODE_LIST_TAG = "node-list";

  public static final String NODE_LIST_NAME = "node-list-name";

  public static final String NODE_LIST_DESCR = "node-list-descr";

  public static final String GRAPH_DOC = "graph-doc";

  // Legacy type alias for graphDoc.
  public static final String WORSPACE_RSRC = "workspace-resource";

  private static final PersistTagDataLoader.TagDescriptor[] TAG_DATA_DESCR =
      new PersistTagDataLoader.TagDescriptor[] {
          new PersistTagDataLoader.TagDescriptor(
              GRAPH_DOC, DepanFxWorkspaceResource.class),
          new PersistTagDataLoader.TagDescriptor(NODE_LIST_NAME, String.class),
          new PersistTagDataLoader.TagDescriptor(NODE_LIST_DESCR, String.class)
      };

  private static final Map<String, String> TAGS_ALIAS =
      new HashMap<>();
  static {
    TAGS_ALIAS.put(WORSPACE_RSRC, GRAPH_DOC);
  }

  private static final PersistTagDataLoader TAG_LOADER =
      new PersistTagDataLoader(TAG_DATA_DESCR, TAGS_ALIAS);

  private static final String[] META_TAGS = new String[] {
      GRAPH_DOC, NODE_LIST_NAME, NODE_LIST_DESCR
  };

  public NodeListConverter() {
  }

  @Override
  public Class<?> forType() {
    return DepanFxNodeList.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOW_TYPES;
  }

  @Override
  public String getTag() {
    return NODE_LIST_TAG;
  }

  @Override
  public void marshal(PersistMarshalContext dstContext, Object source) {
    DepanFxNodeList nodeList = (DepanFxNodeList) source;

    marshalObject(dstContext, GRAPH_DOC, nodeList.getGraphDocResource());
    marshalObject(dstContext, NODE_LIST_NAME, nodeList.getNodeListName());
    marshalObject(dstContext,
        NODE_LIST_DESCR, nodeList.getNodeListDescription());

    for (GraphNode node : nodeList.getNodes()) {
      marshalObject(dstContext, node);
    }
  }

  @Override
  public DepanFxNodeList unmarshal(
      PersistUnmarshalContext srcContext) {

    Map<String, Object> metaData = TAG_LOADER.loadData(META_TAGS, srcContext);

    DepanFxWorkspaceResource graphDocRsrc =
        (DepanFxWorkspaceResource) metaData.get(GRAPH_DOC);
    GraphDocument graphDoc = (GraphDocument) graphDocRsrc.getResource();
    GraphModel graphModel = graphDoc.getGraph();

    Collection<GraphNode> nodes = new ArrayList<>();
    while (srcContext.hasMoreChildren()) {
      GraphNode baseNode = (GraphNode) unmarshalOne(srcContext);
      GraphNode mapped = GraphModels.mapGraphNode(baseNode, graphModel);

      nodes.add(mapped);
    }
    String nodeListName = guessName(
        (String) metaData.get(NODE_LIST_NAME), graphDocRsrc);
    String nodeListDescr = guessDescr(
        (String) metaData.get(NODE_LIST_DESCR), nodeListName);
    return new DepanFxNodeList(nodeListName, nodeListDescr, graphDocRsrc, nodes);
  }

  private String guessName(
      String loadName, DepanFxWorkspaceResource graphDocRsrc) {
    if (loadName != null) {
      return loadName;
    }
    return DepanFxNodeLists.getNameFromGraphDoc(graphDocRsrc);
  }

  private String guessDescr(String loadDescr, String nodeListName) {
    if (loadDescr != null) {
      return loadDescr;
    }
    return nodeListName;
  }
}
