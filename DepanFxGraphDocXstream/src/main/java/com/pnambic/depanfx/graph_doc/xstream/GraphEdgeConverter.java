package com.pnambic.depanfx.graph_doc.xstream;

import com.pnambic.depanfx.graph.context.plugins.ContextModelRegistry;
import com.pnambic.depanfx.graph.model.GraphEdge;
import com.pnambic.depanfx.graph.model.GraphNode;
import com.pnambic.depanfx.graph.model.GraphRelation;
import com.pnambic.depanfx.graph_doc.builder.DepanFxGraphModelBuilder;
import com.pnambic.depanfx.persistence.AbstractObjectXmlConverter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class GraphEdgeConverter
    extends AbstractObjectXmlConverter<GraphEdge> {

  private static final Class[] ALLOW_TYPES = new Class[] {
    GraphEdge.class
  };

  public static final String GRAPH_EDGE_TAG = "graph-edge";

  public static final String RELATION_TAG = "relation";

  private static final String HEAD_TAG = "head";

  private static final String TAIL_TAG = "tail";

  private static DepanFxGraphModelBuilder builder;

  private ContextModelRegistry modelRegistry;

  public GraphEdgeConverter(ContextModelRegistry modelRegistry) {
    this.modelRegistry = modelRegistry;
  }

  @Override
  public Class<?> forType() {
    return GraphEdge.class;
  }

  @Override
  public Class[] getAllowTypes() {
    return ALLOW_TYPES;
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
    marshalProperty(HEAD_TAG, edge.getHead(), writer, context, mapper);
    marshalProperty(TAIL_TAG, edge.getTail(), writer, context, mapper);
  }

  @Override
  public GraphEdge unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context, Mapper mapper) {
    DepanFxGraphModelBuilder modelBuilder =
       (DepanFxGraphModelBuilder) context.get(DepanFxGraphModelBuilder.class);

    GraphRelation relation = (GraphRelation) unmarshalOne(reader, context, mapper);

    GraphEdgeBuilder edgeBuilder = new GraphEdgeBuilder(modelBuilder);
    while (reader.hasMoreChildren()) {
      edgeBuilder.addNode(reader, context, mapper);
    }

    GraphEdge edge = edgeBuilder.newGraphEdge(relation);
    modelBuilder.addEdge(edge);
    return edge;
  }

  private class GraphEdgeBuilder {

    private final DepanFxGraphModelBuilder modelBuilder;

    private GraphNode headNode;

    private GraphNode tailNode;

    public GraphEdgeBuilder(DepanFxGraphModelBuilder modelBuilder) {
      this.modelBuilder = modelBuilder;
    }

    public void addNode(HierarchicalStreamReader reader,
      UnmarshallingContext context, Mapper mapper) {

      reader.moveDown();
      GraphNode node = (GraphNode) unmarshalOne(reader, context, mapper);
      GraphNode mappedNode = modelBuilder.mapNode(node);
      switch (reader.getNodeName()) {
        case HEAD_TAG:
          headNode = mappedNode;
          break;
        case TAIL_TAG:
          tailNode = mappedNode;
          break;
      }
      reader.moveUp();
    }

    public GraphEdge newGraphEdge(GraphRelation relation) {
      return new GraphEdge(headNode, tailNode, relation);
    }
  }
}
