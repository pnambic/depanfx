package com.pnambic.depanfx.persistence.builder;

import com.pnambic.depanfx.persistence.ObjectXmlConverter;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class DelegateObjectXmlConverter implements Converter {

  private final ObjectXmlConverter<?> delegate;

  private final Mapper mapper;

  public DelegateObjectXmlConverter(
      ObjectXmlConverter<?> delegate, Mapper mapper) {
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
    delegate.marshal(source, writer, context, mapper);
  }

  @Override
  public Object unmarshal(
      HierarchicalStreamReader reader, UnmarshallingContext context) {
    return delegate.unmarshal(reader, context, mapper);
  }
}
