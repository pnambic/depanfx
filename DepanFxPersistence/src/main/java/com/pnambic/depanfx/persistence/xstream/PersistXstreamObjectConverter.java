package com.pnambic.depanfx.persistence.xstream;

import com.pnambic.depanfx.persistence.PersistMarshalContext;
import com.pnambic.depanfx.persistence.PersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistUnmarshalContext;
import com.pnambic.depanfx.xstream.XstreamMarshalContext;
import com.pnambic.depanfx.xstream.XstreamObjectConverter;
import com.pnambic.depanfx.xstream.XstreamUnmarshalContext;

public class PersistXstreamObjectConverter<T> implements XstreamObjectConverter<T> {

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
    PersistMarshalContext persistContext =
        new PersistMarshalWrapper(dstContext);
    contrib.marshal(persistContext, source);
  }

  @Override
  public Object unmarshal(XstreamUnmarshalContext srcContext) {
    PersistUnmarshalContext persistContext =
        new PersistUnmarshalWrapper(srcContext);
    return contrib.unmarshal(persistContext);
  }

  @Override
  public String getTag() {
    return contrib.getTag();
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return contrib.getAllowTypes();
  }

  private static class PersistMarshalWrapper implements PersistMarshalContext {

    private final XstreamMarshalContext xstreamMarshal;

    public PersistMarshalWrapper(XstreamMarshalContext xstreamMarshal) {
      this.xstreamMarshal = xstreamMarshal;
    }

    @Override
    public String serializedClass(Class<?> type) {
      return xstreamMarshal.serializedClass(type);
    }

    @Override
    public void startNode(String tag) {
      xstreamMarshal.startNode(tag);
    }

    @Override
    public void endNode() {
      xstreamMarshal.endNode();
    }

    @Override
    public void convertAnother(Object value) {
      xstreamMarshal.convertAnother(value);
    }

    @Override
    public void putContextValue(Object key, Object value) {
      xstreamMarshal.putContextValue(key, value);
    }

    @Override
    public Object getContextValue(Object key) {
      return xstreamMarshal.getContextValue(key);
    }
  }

  /**
   * Exposed so {@link com.pnambic.depanfx.persistence.PersistTagDataLoader}
   * can use peek-ahead via
   * {@link PersistUnmarshalWrapper#getXstreamUnmarshal()}.
   */
  public static class PersistUnmarshalWrapper
      implements PersistUnmarshalContext {

    private final XstreamUnmarshalContext xstreamUnmarshal;

    public PersistUnmarshalWrapper(XstreamUnmarshalContext xstreamUnmarshal) {
      this.xstreamUnmarshal = xstreamUnmarshal;
    }

    @Override
    public void moveDown() {
      xstreamUnmarshal.moveDown();
    }

    @Override
    public void moveUp() {
      xstreamUnmarshal.moveUp();
    }

    @Override
    public String getNodeName() {
      return xstreamUnmarshal.getNodeName();
    }

    @Override
    public Object convertAnother(Object object, Class<?> childClass) {
      return xstreamUnmarshal.convertAnother(object, childClass);
    }

    @Override
    public String getValue() {
      return xstreamUnmarshal.getValue();
    }

    @Override
    public Class<?> realClass(String childName) {
      return xstreamUnmarshal.realClass(childName);
    }

    @Override
    public Object getContextValue(Object key) {
      return xstreamUnmarshal.getContextValue(key);
    }

    @Override
    public void putContextValue(Object key, Object value) {
      xstreamUnmarshal.putContextValue(key, value);
    }

    @Override
    public boolean hasMoreChildren() {
      return xstreamUnmarshal.hasMoreChildren();
    }

    /**
     * Let callbacks use the underlying context.
     */
    public XstreamUnmarshalContext getXstreamUnmarshal() {
      return xstreamUnmarshal;
    }
  }
}
