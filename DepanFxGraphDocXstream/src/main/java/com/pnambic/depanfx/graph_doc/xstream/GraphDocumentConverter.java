package com.pnambic.depanfx.graph_doc.xstream;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.persistence.AbstractObjectXmlConverter;
import com.pnambic.depanfx.persistence.TagDataLoader;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.HashMap;
import java.util.Map;

public class GraphDocumentConverter
    extends AbstractObjectXmlConverter<GraphDocument> {

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
    GraphDocument.class
  };

  public static final String GRAPH_DOC_TAG = "graph-doc";

  public static final String GRAPH_NAME_TAG = "graph-name";

  public static final String GRAPH_DESCR_TAG = "graph-descr";

  private static final String CONTEXT_KEY_TAG = "context-key";

  public static final String GRAPH_MODEL_TAG = "graph-model";

  private static final TagDataLoader.DataDescriptor[] TAG_DATA_DESCR =
      new TagDataLoader.DataDescriptor[] {
          new TagDataLoader.DataDescriptor(GRAPH_NAME_TAG, String.class),
          new TagDataLoader.DataDescriptor(GRAPH_DESCR_TAG, String.class),
          new TagDataLoader.DataDescriptor(
              CONTEXT_KEY_TAG, ContextModelId.class),
          new TagDataLoader.DataDescriptor(
              GRAPH_MODEL_TAG, GraphModel.class)
      };

  private static final Map<String, String> TAGS_ALIAS =
      new HashMap<>();
  static {
    TAGS_ALIAS.put(ContextModelIdConverter.CONTEXT_KEY_TAG, CONTEXT_KEY_TAG);
    TAGS_ALIAS.put(GraphModelConverter.GRAPH_MODEL_TAG, GRAPH_MODEL_TAG);
  }

  private static final TagDataLoader TAG_LOADER =
      new TagDataLoader(TAG_DATA_DESCR, TAGS_ALIAS);

  private static final String[] META_TAGS = new String[] {
      GRAPH_NAME_TAG, GRAPH_DESCR_TAG, CONTEXT_KEY_TAG
  };

  @Override
  public Class<?> forType() {
    return GraphDocument.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOW_TYPES;
  }

  @Override
  public String getTag() {
    return GRAPH_DOC_TAG;
  }

  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context, Mapper mapper) {
    GraphDocument doc = (GraphDocument) source;

    marshalObject(GRAPH_NAME_TAG, doc.getGraphName(), writer, context);
    marshalObject(GRAPH_DESCR_TAG, doc.getGraphDescription(), writer, context);
    marshalObject(CONTEXT_KEY_TAG, doc.getContextModelId(), writer, context);
    marshalObject(GRAPH_MODEL_TAG, doc.getGraph(), writer, context);
  }

  @Override
  public GraphDocument unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context, Mapper mapper) {

    Map<String, Object> metaData = TAG_LOADER.loadData(
        META_TAGS, reader, context, mapper);

    String graphName = guessName((String) metaData.get(GRAPH_NAME_TAG));
    String graphDescr = 
        guessDescr((String) metaData.get(GRAPH_DESCR_TAG), graphName);
    ContextModelId contextModelId =
        (ContextModelId) metaData.get(CONTEXT_KEY_TAG);
    GraphModel graph = (GraphModel) metaData.get(GRAPH_MODEL_TAG);

    return new GraphDocument(graphName, graphDescr, contextModelId, graph);
  }

  private String guessName(String loadName) {
    if (loadName != null) {
      return loadName;
    }
    return "Graph";
  }

  private String guessDescr(String loadDescr, String graphName) {
    if (loadDescr != null) {
      return loadDescr;
    }
    return graphName;
  }
}
