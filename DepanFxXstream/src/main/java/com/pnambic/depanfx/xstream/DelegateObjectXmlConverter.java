package com.pnambic.depanfx.xstream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class DelegateObjectXmlConverter implements Converter {

  private final XstreamObjectConverter<?> delegate;

  private final Mapper mapper;

  public DelegateObjectXmlConverter(
      XstreamObjectConverter<?> delegate, Mapper mapper) {
    this.delegate = delegate;
    this.mapper = mapper;
  }

  // The raw Class type is required to match the API defined in the base types.
  @SuppressWarnings("rawtypes")
  @Override
  public boolean canConvert(Class type) {
    return delegate.forType().isAssignableFrom(type);
  }

  @Override
  public void marshal(Object source,
      HierarchicalStreamWriter writer, MarshallingContext context) {
    XstreamMarshalContext dstContext =
        new XstreamMarshalContext(writer, context, mapper);
    delegate.marshal(dstContext, source);
  }

  @Override
  public Object unmarshal(
      HierarchicalStreamReader reader, UnmarshallingContext context) {
    XstreamUnmarshalContext srcContext =
        new XstreamUnmarshalContext(reader, context, mapper);
    return delegate.unmarshal(srcContext);
  }
}
