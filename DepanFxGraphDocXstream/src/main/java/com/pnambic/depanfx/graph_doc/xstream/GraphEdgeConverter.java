package com.pnambic.depanfx.graph_doc.xstream;

import com.pnambic.depanfx.graph.api.Relation;
import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.persistence.AbstractObjectXmlConverter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class GraphEdgeConverter
    extends AbstractObjectXmlConverter<GraphEdge> {

  public static final String GRAPH_EDGE_TAG = "graph-edge";

  public static final String RELATION_TAG = "relation";

  private static final String HEAD_TAG = "head";

  private static final String TAIL_TAG = "tail";

  @Override
  public Class<?> forType() {
    return GraphEdge.class;
  }
  @Override
  public String getTag() {
    return GRAPH_EDGE_TAG;
  }

  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context, Mapper mapper) {
    GraphEdge edge = (GraphEdge) source;

    marshalObject(edge.getRelation(), writer, context, mapper);
    marshalObject(HEAD_TAG, getNodeKey(edge.getHead()), writer, context);
    marshalObject(TAIL_TAG, getNodeKey(edge.getTail()), writer, context);
  }

  @Override
  public GraphEdge unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context, Mapper mapper) {
    return (GraphEdge) unmarshalOne(reader, context, mapper);
  }

  private String getNodeKey(GraphNode node) {
    return GraphContextKeys.toNodeKey(node.getId());
  }
}
