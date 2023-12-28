package com.pnambic.depanfx.nodelist.xstream;

import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphModels;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeLists;
import com.pnambic.depanfx.persistence.AbstractObjectXmlConverter;
import com.pnambic.depanfx.persistence.TagDataLoader;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NodeListConverter
    extends AbstractObjectXmlConverter<DepanFxNodeList> {

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxNodeList.class
  };

  public static final String NODE_LIST_TAG = "node-list";

  public static final String NODE_LIST_NAME = "node-list-name";

  public static final String NODE_LIST_DESCR = "node-list-descr";

  public static final String GRAPH_DOC = "graph-doc";

  // Legacy type alias for graphDoc.
  public static final String WORSPACE_RSRC = "workspace-resource";

  private static final TagDataLoader.DataDescriptor[] TAG_DATA_DESCR =
      new TagDataLoader.DataDescriptor[] {
          new TagDataLoader.DataDescriptor(
              GRAPH_DOC, DepanFxWorkspaceResource.class),
          new TagDataLoader.DataDescriptor(NODE_LIST_NAME, String.class),
          new TagDataLoader.DataDescriptor(NODE_LIST_DESCR, String.class)
      };

  private static final Map<String, String> TAGS_ALIAS =
      new HashMap<>();
  static {
    TAGS_ALIAS.put(WORSPACE_RSRC, GRAPH_DOC);
  }

  private static final TagDataLoader TAG_LOADER =
      new TagDataLoader(TAG_DATA_DESCR, TAGS_ALIAS);

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
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context, Mapper mapper) {
    DepanFxNodeList nodeList = (DepanFxNodeList) source;

    marshalObject(GRAPH_DOC, nodeList.getGraphDocResource(), writer, context);
    marshalObject(NODE_LIST_NAME, nodeList.getNodeListName(), writer, context);
    marshalObject(
        NODE_LIST_DESCR, nodeList.getNodeListDescription(), writer, context);

    for (GraphNode node : nodeList.getNodes()) {
      marshalObject(node, writer, context, mapper);
    }
  }

  @Override
  public DepanFxNodeList unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context, Mapper mapper) {

    Map<String, Object> metaData = TAG_LOADER.loadData(
        META_TAGS, reader, context, mapper);

    DepanFxWorkspaceResource graphDocRsrc =
        (DepanFxWorkspaceResource) metaData.get(GRAPH_DOC);
    GraphDocument graphDoc = (GraphDocument) graphDocRsrc.getResource();
    GraphModel graphModel = graphDoc.getGraph();

    Collection<GraphNode> nodes = new ArrayList<>();
    while (reader.hasMoreChildren()) {
      GraphNode baseNode = (GraphNode) unmarshalOne(reader, context, mapper);
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
