package com.pnambic.depanfx.xstream;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class XstreamMarshalContext {

  private final HierarchicalStreamWriter writer;

  private final MarshallingContext context;

  private final Mapper mapper;

  public XstreamMarshalContext(HierarchicalStreamWriter writer,
      MarshallingContext context, Mapper mapper) {
    this.writer = writer;
    this.context = context;
    this.mapper = mapper;
  }

  public String serializedClass(Class<?> type) {
    return mapper.serializedClass(type);
  }

  public void startNode(String tag) {
    writer.startNode(tag);
  }

  public void endNode() {
    writer.endNode();
  }

  public void convertAnother(Object value) {
    context.convertAnother(value);
  }

  public void putContextValue(Object key, Object value) {
    context.put(key, value);
  }

  public Object getContextValue(Object key) {
    return context.get(key);
  }
}
