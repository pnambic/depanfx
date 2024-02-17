package com.pnambic.depanfx.persistence;

public interface PersistUnmarshalContext {

  void moveDown();

  void moveUp();

  boolean hasMoreChildren();

  String getValue();

  String getNodeName();

  Object convertAnother(Object object, Class<?> childClass);

  Class<?> realClass(String childName);

  /**
   * In addition to context value provided during data transfer, some initial
   * context data (e.g. the worksace) can be installed by
   * {@code com.pnambic.depanfx.persistence.PersistDocumentTransport#addContextValue(Object, Object)}.
   */
  Object getContextValue(Object key);

  void putContextValue(Object key, Object value);
}
