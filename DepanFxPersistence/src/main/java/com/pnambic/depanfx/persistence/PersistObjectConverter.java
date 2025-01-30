package com.pnambic.depanfx.persistence;

import com.pnambic.modxstream.XstreamMarshalContext;
import com.pnambic.modxstream.XstreamUnmarshalContext;

/**
 * Fit into XStream converters via simple String conversions.
 */
public interface PersistObjectConverter<T> {

  Class<?> forType();

  Class<?>[] getAllowTypes();

  String getTag();

  void marshal(XstreamMarshalContext dstContext, Object source);

  T unmarshal(XstreamUnmarshalContext srcContext);
}

