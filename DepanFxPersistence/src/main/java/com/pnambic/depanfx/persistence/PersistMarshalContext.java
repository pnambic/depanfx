package com.pnambic.depanfx.persistence;

public interface PersistMarshalContext {

  String serializedClass(Class<?> type);

  void startNode(String tag);

  void endNode();

  void convertAnother(Object value);

  void putContextValue(Object key, Object value);

  Object getContextValue(Object key);
}
