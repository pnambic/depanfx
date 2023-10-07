package com.pnambic.depanfx.graph_doc.xstream;

import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.persistence.AbstractObjectXmlConverter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class GraphNodeConverter
    extends AbstractObjectXmlConverter<GraphNode> {

  private static final Class[] ALLOW_TYPES = new Class[] {
    GraphNode.class
  };

  public static final String GRAPH_NODE_TAG = "graph-node";

  @Override
  public Class<?> forType() {
    return GraphNode.class;
  }

  @Override
  public String getTag() {
    return GRAPH_NODE_TAG;
  }

  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context, Mapper mapper) {
    GraphNode node = (GraphNode) source;
    marshalObject(GRAPH_NODE_TAG, getNodeKey(node), writer, context);
  }

  @Override
  public GraphNode unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context, Mapper mapper) {
    return (GraphNode) unmarshalOne(reader, context, mapper);
  }

  private String getNodeKey(GraphNode node) {
    return GraphContextKeys.toNodeKey(node.getId());
  }

  @Override
  public Class[] getAllowTypes() {
    return ALLOW_TYPES;
  }
}
