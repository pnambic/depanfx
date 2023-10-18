package com.pnambic.depanfx.filesystem.context;

import java.nio.file.Path;
import java.util.Objects;

import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.context.ContextNodeKindId;

public class FileSystemNodeId implements ContextNodeId {

  private final FileSystemNodeKindId nodeKindId;

  private final Path nodePath;

  public FileSystemNodeId(FileSystemNodeKindId nodeKindId, Path nodePath) {
    this.nodeKindId = nodeKindId;
    this.nodePath = nodePath;
  }

  @Override
  public ContextNodeKindId getContextNodeKindId() {
    return nodeKindId;
  }

  @Override
  public String getNodeKey() {
    return nodePath.toString();
  }

  public Path getNodePath() {
    return nodePath;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    FileSystemNodeId other = (FileSystemNodeId) obj;
    return Objects.equals(nodeKindId, other.nodeKindId)
        && Objects.equals(nodePath, other.nodePath);
  }

  @Override
  public int hashCode() {
      return Objects.hash(nodeKindId, nodePath);
  }
}
