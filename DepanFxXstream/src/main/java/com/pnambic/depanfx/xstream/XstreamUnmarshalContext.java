package com.pnambic.depanfx.xstream;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.AbstractPullReader;
import com.thoughtworks.xstream.mapper.Mapper;

public class XstreamUnmarshalContext {
  
  public static class PeekableReader {

    private final AbstractPullReader peekable; // = srcContext.getPeekableReader();

    public PeekableReader(AbstractPullReader peekable) {
      this.peekable = peekable;
    }

    public boolean hasMoreChildren() {
      return peekable.hasMoreChildren();
    }

    public String peekNextChild() {
      return peekable.peekNextChild();
    }
  }

  private final HierarchicalStreamReader reader;

  private final UnmarshallingContext context;

  private final Mapper mapper;

  public XstreamUnmarshalContext(HierarchicalStreamReader reader,
      UnmarshallingContext context, Mapper mapper) {
    this.reader = reader;
    this.context = context;
    this.mapper = mapper;
  }

  public void moveDown() {
    reader.moveDown();
  }

  public void moveUp() {
    reader.moveUp();
  }

  public String getNodeName() {
    return reader.getNodeName();
  }

  public String getValue() {
    return reader.getValue();
  }

  public boolean hasMoreChildren() {
    return reader.hasMoreChildren();
  }

  public Object convertAnother(Object object, Class<?> childClass) {
    return context.convertAnother(object, childClass);
  }

  public Class<?> realClass(String childName) {
    return mapper.realClass(childName);
  }

  public void putContextValue(Object key, Object value) {
    context.put(key, value);
  }

  public Object getContextValue(Object key) {
    return context.get(key);
  }

  public PeekableReader getPeekableReader() {
    return new PeekableReader((AbstractPullReader) reader.underlyingReader());
  }
}
