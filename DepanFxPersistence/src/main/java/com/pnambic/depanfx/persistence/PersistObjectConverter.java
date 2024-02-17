package com.pnambic.depanfx.persistence;

/**
 * Fit into XStream converters via simple String conversions.
 */
public interface PersistObjectConverter<T> {

  Class<?> forType();

  Class<?>[] getAllowTypes();

  String getTag();

  void marshal(PersistMarshalContext dstContext, Object source);

  T unmarshal(PersistUnmarshalContext srcContext);
}

