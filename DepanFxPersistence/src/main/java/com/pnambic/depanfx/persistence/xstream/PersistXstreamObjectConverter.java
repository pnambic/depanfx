package com.pnambic.depanfx.persistence.xstream;

import com.pnambic.depanfx.persistence.PersistObjectConverter;
import com.pnambic.modxstream.XstreamMarshalContext;
import com.pnambic.modxstream.XstreamObjectConverter;
import com.pnambic.modxstream.XstreamUnmarshalContext;

public class PersistXstreamObjectConverter<T>
    implements XstreamObjectConverter {

  private final PersistObjectConverter<T> contrib;

  public PersistXstreamObjectConverter(PersistObjectConverter<T> contrib) {
    this.contrib = contrib;
  }

  @Override
  public Class<?> forType() {
    return contrib.forType();
  }

  @Override
  public void marshal(XstreamMarshalContext dstContext, Object source) {
    contrib.marshal(dstContext, source);
  }

  @Override
  public Object unmarshal(XstreamUnmarshalContext srcContext) {
    return contrib.unmarshal(srcContext);
  }

  @Override
  public String getTag() {
    return contrib.getTag();
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return contrib.getAllowTypes();
  }
}
