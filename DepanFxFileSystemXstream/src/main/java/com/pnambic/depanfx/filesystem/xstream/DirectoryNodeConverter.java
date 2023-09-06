package com.pnambic.depanfx.filesystem.xstream;

import java.nio.file.Path;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.filesystem.graph.DirectoryNode;
import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.persistence.AbstractObjectXmlConverter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class DirectoryNodeConverter
    extends AbstractObjectXmlConverter<DirectoryNode> {

  private static final Class[] ALLOW_TYPES = new Class[] {
    DirectoryNode.class
  };

  public static final String DIRECTORY_NODE_TAG =
      GraphContextKeys.toNodeKindKey(FileSystemContextDefinition.DIRECTORY_NKID);

  @Override
  public Class<?> forType() {
    return DirectoryNode.class;
  }

  @Override
  public Class[] getAllowTypes() {
    return ALLOW_TYPES;
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
    String pathText = reader.getValue();
    Path nodePath = Path.of(pathText);
    return new DirectoryNode(nodePath);
  }
}
