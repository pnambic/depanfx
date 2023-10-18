package com.pnambic.depanfx.graph_doc.xstream;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.context.plugins.ContextModelRegistry;
import com.pnambic.depanfx.persistence.AbstractObjectXmlConverter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class ContextModelIdConverter
    extends AbstractObjectXmlConverter<ContextModelId> {

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
    ContextModelId.class
  };

  public static final String CONTEXT_KEY_TAG = "context-key";

  private final ContextModelRegistry modelRegistry;

  public ContextModelIdConverter(ContextModelRegistry modelRegistry) {
    this.modelRegistry = modelRegistry;
  }

  @Override
  public Class<?> forType() {
    return ContextModelId.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOW_TYPES;
  }

  @Override
  public String getTag() {
    return CONTEXT_KEY_TAG;
  }

  @Override
  public void marshal(Object source,
      HierarchicalStreamWriter writer, MarshallingContext context, Mapper mapper) {

    marshalObject(CONTEXT_KEY_TAG, ((ContextModelId) source).getContextModelKey(),
        writer, context);
  }

  @Override
  public ContextModelId unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context, Mapper mapper) {

    String modelKey = reader.getValue();
    return modelRegistry.getContextModel(modelKey).getId();
  }
}
