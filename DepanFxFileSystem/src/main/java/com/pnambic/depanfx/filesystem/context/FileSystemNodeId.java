package com.pnambic.depanfx.filesystem.context;

import java.nio.file.Path;

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
}
