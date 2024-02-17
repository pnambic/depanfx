package com.pnambic.depanfx.persistence;

/**
 * Useful protected methods for implementing persistent.
 */
public abstract class BasePersistObjectConverter<T>
    implements PersistObjectConverter<T> {

  protected void marshalObject(PersistMarshalContext dstContext, Object source) {
    String tag = dstContext.serializedClass(source.getClass());
    marshalObject(dstContext, tag, source);
  }

  protected void marshalObject(
      PersistMarshalContext dstContext, String tag, Object source) {
    dstContext.startNode(tag);
    marshalValue(dstContext, source);
    dstContext.endNode();
  }

  protected void marshalProperty(
      PersistMarshalContext dstContext, String propertyTag, Object source) {
    dstContext.startNode(propertyTag);
    marshalObject(dstContext, source);
    dstContext.endNode();
  }

  protected void marshalValue(PersistMarshalContext dstContext, Object value) {
    dstContext.convertAnother(value);
  }

  protected Object unmarshalOne(PersistUnmarshalContext srcContext) {
    srcContext.moveDown();

    String childName = srcContext.getNodeName();
    Class<?> childClass = mapChildName(childName, srcContext);
    Object result = unmarshalValue(srcContext, childClass);

    srcContext.moveUp();

    return result;
  }

  protected Object unmarshalValue(
      PersistUnmarshalContext context, Class<?> childClass) {
    return childClass.cast(context.convertAnother(null, childClass));
  }

  protected Class<?> mapChildName(String childName, PersistUnmarshalContext srcContext) {
    Class<?> childClass = srcContext.realClass(childName);
    return childClass;
  }
}
