package com.pnambic.depanfx.nodelist.xstream;

import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.persistence.AbstractObjectXmlConverter;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.ArrayList;
import java.util.Collection;

public class NodeListConverter
    extends AbstractObjectXmlConverter<DepanFxNodeList> {

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
      DepanFxNodeList.class
  };

  public static final String NODE_LIST_TAG = "node-list";

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

    // Should use field (e.g. graphDocRef) as tag, not object type.
    String classTag = mapper.serializedClass(DepanFxWorkspaceResource.class);
    marshalObject(classTag, nodeList.getWorkspaceResource(), writer, context);

    for (GraphNode node : nodeList.getNodes()) {
      marshalObject(node, writer, context, mapper);
    }
  }

  @Override
  public DepanFxNodeList unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context, Mapper mapper) {

    // Should validate expected tag.
    DepanFxWorkspaceResource graphRef =
        (DepanFxWorkspaceResource) unmarshalOne(reader, context, mapper);

    GraphDocument graphDoc = (GraphDocument) graphRef.getResource();
    GraphModel model = graphDoc.getGraph();
    Collection<GraphNode> nodes = new ArrayList<>();
    while (reader.hasMoreChildren()) {
      GraphNode node = (GraphNode) unmarshalOne(reader, context, mapper);
      GraphNode mapped = (GraphNode) model.findNode(node.getId());
      nodes.add(mapped);
    }
    return new DepanFxNodeList(graphRef, nodes);
  }
}
