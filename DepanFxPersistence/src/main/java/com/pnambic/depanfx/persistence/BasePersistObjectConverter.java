package com.pnambic.depanfx.persistence;

import com.pnambic.modxstream.XstreamMarshalContext;
import com.pnambic.modxstream.XstreamUnmarshalContext;

/**
 * Useful protected methods for implementing persistent.
 */
public abstract class BasePersistObjectConverter<T>
    implements PersistObjectConverter<T> {

  protected void marshalObject(
      XstreamMarshalContext dstContext, Object source) {
    String tag = dstContext.serializedClass(source.getClass());
    marshalObject(dstContext, tag, source);
  }

  protected void marshalObject(
      XstreamMarshalContext dstContext, String tag, Object source) {
    dstContext.startNode(tag);
    marshalValue(dstContext, source);
    dstContext.endNode();
  }

  protected void marshalOptionalValue(
      XstreamMarshalContext dstContext, String tag, Object source) {
    if (source != null) {
      dstContext.startNode(tag);
      marshalValue(dstContext, source);
      dstContext.endNode();
    }
  }

  protected void marshalProperty(
      XstreamMarshalContext dstContext, String propertyTag, Object source) {
    dstContext.startNode(propertyTag);
    marshalObject(dstContext, source);
    dstContext.endNode();
  }

  protected void marshalValue(XstreamMarshalContext dstContext, Object value) {
    dstContext.convertAnother(value);
  }

  protected Object unmarshalOne(XstreamUnmarshalContext srcContext) {
    srcContext.moveDown();

    String childName = srcContext.getNodeName();
    Class<?> childClass = mapChildName(childName, srcContext);
    Object result = unmarshalValue(srcContext, childClass);

    srcContext.moveUp();

    return result;
  }

  protected Object unmarshalValue(
      XstreamUnmarshalContext context, Class<?> childClass) {
    return childClass.cast(context.convertAnother(null, childClass));
  }

  protected Class<?> mapChildName(
      String childName, XstreamUnmarshalContext srcContext) {
    Class<?> childClass = srcContext.realClass(childName);
    return childClass;
  }
}
