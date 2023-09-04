package com.pnambic.depanfx.filesystem.xstream;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.filesystem.graph.DirectoryNode;
import com.pnambic.depanfx.graph.context.ContextModelTools;
import com.pnambic.depanfx.persistence.AbstractObjectXmlConverter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class DirectoryNodeConverter
    extends AbstractObjectXmlConverter<DirectoryNode> {

  public static final String DIRECTORY_NODE_TAG =
      ContextModelTools.buildNodeKindTag(FileSystemContextDefinition.DIRECTORY_NKID);

  @Override
  public Class<?> forType() {
    return DirectoryNode.class;
  }

  @Override
  public String getTag() {
    return DIRECTORY_NODE_TAG;
  }

  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context, Mapper mapper) {
    DirectoryNode node = (DirectoryNode) source;
    String value = node.getPath().toString();
    marshalValue(value, context);
  }

  @Override
  public DirectoryNode unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context, Mapper mapper) {
    return (DirectoryNode) unmarshalOne(reader, context, mapper);
  }
}
