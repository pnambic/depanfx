package com.pnambic.depanfx.java.persistence;

import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.java.context.JavaNodeKindId;
import com.pnambic.depanfx.java.graph.JavaNode;
import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.modxstream.XstreamMarshalContext;
import com.pnambic.modxstream.XstreamUnmarshalContext;

public abstract class JavaNodeConverter<T extends JavaNode>
    extends BasePersistObjectConverter<T> {

  private final Class<?> targetType;

  public final String nodeTag;

  private final Class<?>[] allowTypes;

  public JavaNodeConverter(Class<?> targetType, JavaNodeKindId nodeKindId) {
    this.targetType = targetType;
    this.nodeTag = GraphContextKeys.toNodeKindKey(nodeKindId);
    this.allowTypes = new Class[] { targetType };
  }

  @Override
  public Class<?> forType() {
    return targetType;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return allowTypes;
  }

  @Override
  public String getTag() {
    return nodeTag;
  }

  @Override
  public void marshal(XstreamMarshalContext dstContext, Object source) {
    JavaNode node = (JavaNode) source;
    marshalValue(dstContext, node.getId().getNodeKey());
  }

  @Override
  public T unmarshal(XstreamUnmarshalContext srcContext) {
    String nodeKey = srcContext.getValue();
    return createNode(nodeKey);
  }

  protected abstract T createNode(String nodeKey);
}
