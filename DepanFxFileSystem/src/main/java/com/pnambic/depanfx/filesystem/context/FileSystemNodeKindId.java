package com.pnambic.depanfx.filesystem.context;

import com.pnambic.depanfx.graph.context.ContextModelId;
import com.pnambic.depanfx.graph.context.ContextNodeKindId;

import java.util.Objects;

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

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    FileSystemNodeKindId other = (FileSystemNodeKindId) obj;
    return Objects.equals(contextId, other.contextId)
        && Objects.equals(nodeKindKey, other.nodeKindKey);
  }

  @Override
  public int hashCode() {
      return Objects.hash(contextId, nodeKindKey);
  }
}
