package com.pnambic.depanfx.filesystem.context;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.context.ContextNodeKindId;

public class FileSystemNodeKindId implements ContextNodeKindId {

  private final ContextModelId contextId;

  private final String nodeKindKey;

  public FileSystemNodeKindId(ContextModelId contextId, String nodeKindKey) {
    this.contextId = contextId;
    this.nodeKindKey = nodeKindKey;
  }

  @Override
  public ContextModelId getContextModelId() {
    return contextId;
  }

  @Override
  public String getNodeKindKey() {
    return nodeKindKey;
  }
}
