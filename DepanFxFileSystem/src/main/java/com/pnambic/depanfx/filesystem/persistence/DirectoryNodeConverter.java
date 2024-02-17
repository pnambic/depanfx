package com.pnambic.depanfx.filesystem.persistence;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.filesystem.graph.DirectoryNode;
import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistMarshalContext;
import com.pnambic.depanfx.persistence.PersistUnmarshalContext;

import java.nio.file.Path;

public class DirectoryNodeConverter
    extends BasePersistObjectConverter<DirectoryNode> {

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
    DirectoryNode.class
  };

  public static final String DIRECTORY_NODE_TAG =
      GraphContextKeys.toNodeKindKey(FileSystemContextDefinition.DIRECTORY_NKID);

  @Override
  public Class<?> forType() {
    return DirectoryNode.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOW_TYPES;
  }

  @Override
  public String getTag() {
    return DIRECTORY_NODE_TAG;
  }

  @Override
  public void marshal(PersistMarshalContext dstContext, Object source) {
    DirectoryNode node = (DirectoryNode) source;
    String value = node.getPath().toString();
    marshalValue(dstContext, value);
  }

  @Override
  public DirectoryNode unmarshal(PersistUnmarshalContext context) {
    String pathText = context.getValue();
    Path nodePath = Path.of(pathText);
    return new DirectoryNode(nodePath);
  }
}
