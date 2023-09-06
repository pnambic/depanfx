package com.pnambic.depanfx.persistence;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Fit into XStream converters via simple String conversions.
 */
public interface ObjectXmlConverter<T> {

  Class<?> forType();

  Class[] getAllowTypes();

  String getTag();

  void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context, Mapper mapper);

  T unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context, Mapper mapper);
}
