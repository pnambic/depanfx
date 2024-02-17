package com.pnambic.depanfx.graph_doc.persistence;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.model.GraphModel;
import com.pnambic.depanfx.graph_doc.model.GraphDocument;
import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistMarshalContext;
import com.pnambic.depanfx.persistence.PersistTagDataLoader;
import com.pnambic.depanfx.persistence.PersistUnmarshalContext;

import java.util.HashMap;
import java.util.Map;

public class GraphDocumentConverter
    extends BasePersistObjectConverter<GraphDocument> {

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
    GraphDocument.class
  };

  public static final String GRAPH_DOC_TAG = "graph-doc";

  public static final String GRAPH_NAME_TAG = "graph-name";

  public static final String GRAPH_DESCR_TAG = "graph-descr";

  private static final String CONTEXT_KEY_TAG = "context-key";

  public static final String GRAPH_MODEL_TAG = "graph-model";

  private static final PersistTagDataLoader.TagDescriptor[] TAG_DATA_DESCR =
      new PersistTagDataLoader.TagDescriptor[] {
          new PersistTagDataLoader.TagDescriptor(GRAPH_NAME_TAG, String.class),
          new PersistTagDataLoader.TagDescriptor(GRAPH_DESCR_TAG, String.class),
          new PersistTagDataLoader.TagDescriptor(
              CONTEXT_KEY_TAG, ContextModelId.class),
          new PersistTagDataLoader.TagDescriptor(
              GRAPH_MODEL_TAG, GraphModel.class)
      };

  private static final Map<String, String> TAGS_ALIAS =
      new HashMap<>();
  static {
    TAGS_ALIAS.put(ContextModelIdConverter.CONTEXT_KEY_TAG, CONTEXT_KEY_TAG);
    TAGS_ALIAS.put(GraphModelConverter.GRAPH_MODEL_TAG, GRAPH_MODEL_TAG);
  }

  private static final PersistTagDataLoader TAG_LOADER =
      new PersistTagDataLoader(TAG_DATA_DESCR, TAGS_ALIAS);

  private static final String[] META_TAGS = new String[] {
      GRAPH_NAME_TAG, GRAPH_DESCR_TAG, CONTEXT_KEY_TAG, GRAPH_MODEL_TAG
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
  public void marshal(PersistMarshalContext dstContext, Object source) {
    GraphDocument doc = (GraphDocument) source;

    marshalObject(dstContext, GRAPH_NAME_TAG, doc.getGraphName());
    marshalObject(dstContext, GRAPH_DESCR_TAG, doc.getGraphDescription());
    marshalObject(dstContext, CONTEXT_KEY_TAG, doc.getContextModelId());
    marshalObject(dstContext, GRAPH_MODEL_TAG, doc.getGraph());
  }

  @Override
  public GraphDocument unmarshal(PersistUnmarshalContext srcContext) {

    Map<String, Object> metaData =
        TAG_LOADER.loadData(META_TAGS, srcContext);

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
