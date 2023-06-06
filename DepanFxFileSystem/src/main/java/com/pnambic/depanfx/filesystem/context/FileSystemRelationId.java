package com.pnambic.depanfx.filesystem.context;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.context.ContextRelationId;

public class FileSystemRelationId implements ContextRelationId {

  private final ContextModelId contextId;

  private final String relationKey;

  public FileSystemRelationId(
      FileSystemContextModelId contextId, String relationKey) {
    this.contextId = contextId;
    this.relationKey = relationKey;
  }

  @Override
  public String getRelationKey() {
    return relationKey;
  }

  @Override
  public ContextModelId getContextModelId() {
    return contextId;
  }
}
