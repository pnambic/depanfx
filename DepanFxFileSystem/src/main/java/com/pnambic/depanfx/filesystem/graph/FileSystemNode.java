package com.pnambic.depanfx.filesystem.graph;

import java.nio.file.Path;

import com.pnambic.depanfx.filesystem.context.FileSystemNodeId;
import com.pnambic.depanfx.filesystem.context.FileSystemNodeKindId;
import com.pnambic.depanfx.graph.model.GraphNode;

/**
 * Base class for all things file system nodes.
 */
public abstract class FileSystemNode extends GraphNode {

  public FileSystemNode(FileSystemNodeKindId id, Path path) {
    super(new FileSystemNodeId(id, path));
  }

  public Path getPath() {
    FileSystemNode id = (FileSystemNode) getId();
    return id.getPath();
  }
}
