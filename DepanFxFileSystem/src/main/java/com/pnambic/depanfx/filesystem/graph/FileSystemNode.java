package com.pnambic.depanfx.filesystem.graph;

import java.nio.file.Path;

import com.pnambic.depanfx.graph.context.ContextNodeKindId;
import com.pnambic.depanfx.graph.model.GraphNode;

/**
 * Base class for all things file system nodes.
 */
public abstract class FileSystemNode extends GraphNode {

  private final Path path;

  public FileSystemNode(ContextNodeKindId id, Path path) {
    super(id);
    this.path = path;
  }

  public Path getPath() {
    return path;
  }
}
