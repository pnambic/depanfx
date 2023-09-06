package com.pnambic.depanfx.persistence;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public abstract class AbstractObjectXmlConverter<T>
    implements ObjectXmlConverter<T> {

  protected void marshalObject(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context, Mapper mapper) {
    String tag = mapper.serializedClass(source.getClass());
    marshalObject(tag, source, writer, context);
  }

  protected void marshalObject(String tag, Object source,
      HierarchicalStreamWriter writer, MarshallingContext context) {
    writer.startNode(tag);
    marshalValue(source, context);
    writer.endNode();
  }

  protected void marshalProperty(String propertyTag, Object source,
      HierarchicalStreamWriter writer, MarshallingContext context, Mapper mapper) {
    writer.startNode(propertyTag);
    marshalObject(source, writer, context, mapper);
    writer.endNode();
  }

  protected void marshalValue(Object value, MarshallingContext context) {
    context.convertAnother(value);
  }

  protected Object unmarshalOne(HierarchicalStreamReader reader,
      UnmarshallingContext context, Mapper mapper) {
    reader.moveDown();

    String childName = reader.getNodeName();
    Class<?> childClass = mapChildName(childName, mapper);
    Object result = context.convertAnother(null, childClass);

    reader.moveUp();

    return result;
  }

  protected Class<?> mapChildName(String childName, Mapper mapper) {
    Class<?> childClass = mapper.realClass(childName);
    return childClass;
  }
}
