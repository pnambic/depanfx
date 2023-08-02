package com.pnambic.depanfx.graph_doc.xstream;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.persistence.AbstractObjectXmlConverter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class GraphDocumentConverter
    extends AbstractObjectXmlConverter<GraphDocument> {

  public static final String GRAPH_DOC_TAG = "graph-doc";

  @Override
  public Class<?> forType() {
    return GraphDocument.class;
  }

  @Override
  public String getTag() {
    return GRAPH_DOC_TAG;
  }

  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context, Mapper mapper) {
    GraphDocument doc = (GraphDocument) source;

    marshalObject(doc.getContextModelId(), writer, context, mapper);
    marshalObject(doc.getGraph(), writer, context, mapper);
  }

  @Override
  public GraphDocument unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context, Mapper mapper) {

        ContextModelId contextModelKey =
       (ContextModelId) unmarshalOne(reader, context, mapper);

    GraphModel graph =
        (GraphModel) unmarshalOne(reader, context, mapper);
    return new GraphDocument(contextModelKey, graph);
  }
}
