package com.pnambic.depanfx.filesystem.persistence;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.filesystem.graph.DocumentNode;
import com.pnambic.depanfx.graph.context.GraphContextKeys;
import com.pnambic.depanfx.persistence.BasePersistObjectConverter;
import com.pnambic.depanfx.persistence.PersistMarshalContext;
import com.pnambic.depanfx.persistence.PersistUnmarshalContext;

import java.nio.file.Path;

public class DocumentNodeConverter
    extends BasePersistObjectConverter<DocumentNode> {

  private static final Class<?>[] ALLOW_TYPES = new Class[] {
    DocumentNode.class
  };

  public static final String DOCUMENT_NODE_TAG =
      GraphContextKeys.toNodeKindKey(FileSystemContextDefinition.DOCUMENT_NKID);

  @Override
  public Class<?> forType() {
    return DocumentNode.class;
  }

  @Override
  public Class<?>[] getAllowTypes() {
    return ALLOW_TYPES;
  }

  @Override
  public String getTag() {
    return DOCUMENT_NODE_TAG;
  }

  @Override
  public void marshal(PersistMarshalContext dstContext, Object source) {
    DocumentNode node = (DocumentNode) source;
    String value = node.getPath().toString();
    marshalValue(dstContext, value);
  }

  @Override
  public DocumentNode unmarshal(PersistUnmarshalContext srcContext) {
    String pathText = srcContext.getValue();
    Path nodePath = Path.of(pathText);
    return new DocumentNode(nodePath);
  }

}
