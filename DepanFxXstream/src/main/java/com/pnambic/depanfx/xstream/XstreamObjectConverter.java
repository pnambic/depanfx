package com.pnambic.depanfx.xstream;

public interface XstreamObjectConverter<T> {

  Class<?> forType();

  void marshal(XstreamMarshalContext dstContext, Object source);

  Object unmarshal(XstreamUnmarshalContext srcContext);

  String getTag();

  Class<?>[] getAllowTypes();
}
