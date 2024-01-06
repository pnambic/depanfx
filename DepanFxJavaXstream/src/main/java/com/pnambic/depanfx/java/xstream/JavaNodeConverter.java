package com.pnambic.depanfx.java.xstream;

import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.java.context.JavaNodeKindId;
import com.pnambic.depanfx.java.graph.JavaNode;
import com.pnambic.depanfx.persistence.AbstractObjectXmlConverter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public abstract class JavaNodeConverter<T extends JavaNode>
    extends AbstractObjectXmlConverter<T> {

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
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context, Mapper mapper) {
    JavaNode node = (JavaNode) source;
    marshalValue(node.getId().getNodeKey(), context);
  }

  @Override
  public T unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context, Mapper mapper) {
    String nodeKey = reader.getValue();
    return createNode(nodeKey);
  }

  protected abstract T createNode(String nodeKey);
}
