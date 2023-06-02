package com.pnambic.depanfx.graph.filesystem.graph;

import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.model.GraphNode;

/**
 * Base class for all things file system nodes.
 */
public abstract class FileSystemNode extends GraphNode {

  public FileSystemNode(ContextNodeId id) {
    super(id);
  }
}
