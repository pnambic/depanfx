package com.pnambic.depanfx.graph_doc.xstream;

import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.graph.model.GraphRelation;
import com.pnambic.depanfx.persistence.AbstractObjectXmlConverter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class GraphRelationConverter
    extends AbstractObjectXmlConverter<GraphRelation> {

  public static final String GRAPH_RELATION_TAG = "relation";

  @Override
  public Class<?> forType() {
    return GraphRelation.class;
  }
  @Override
  public String getTag() {
    return GRAPH_RELATION_TAG;
  }

  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context, Mapper mapper) {
    GraphRelation relation = (GraphRelation) source;
    marshalObject(
      GRAPH_RELATION_TAG, GraphContextKeys.toRelationKey(relation.getId()),
      writer, context);
  }

  @Override
  public GraphRelation unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context, Mapper mapper) {
    return (GraphRelation) unmarshalOne(reader, context, mapper);
  }
}
