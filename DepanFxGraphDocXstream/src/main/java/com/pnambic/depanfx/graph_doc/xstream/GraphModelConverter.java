package com.pnambic.depanfx.graph_doc.xstream;

import com.pnambic.depanfx.graph.api.Edge;
import com.pnambic.depanfx.graph.api.Node;
import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.context.ContextRelationId;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.persistence.AbstractObjectXmlConverter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class GraphModelConverter
    extends AbstractObjectXmlConverter<GraphModel> {

  public static final String GRAPH_MODEL_TAG = "graph-model";

  @Override
  public Class<?> forType() {
    return GraphModel.class;
  }

  @Override
  public String getTag() {
    return GRAPH_MODEL_TAG;
  }

  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context, Mapper mapper) {
    GraphModel graph = (GraphModel) source;

    // Save all nodes.
    for (Node<? extends ContextNodeId> node : graph.getNodes()) {
      marshalObject(node, writer, context, mapper);
    }

    // Save all edges.
    for (Edge<? extends ContextNodeId, ? extends ContextRelationId> edge : graph.getEdges()) {
      marshalObject(edge, writer, context, mapper);
    }
  }

  @Override
  public GraphModel unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context, Mapper mapper) {
    return (GraphModel) unmarshalOne(reader, context, mapper);
  }
}
