package com.pnambic.depanfx.graph.filesystem.graph;

import java.nio.file.Path;

import com.pnambic.depanfx.graph.context.ContextNodeId;
import com.pnambic.depanfx.graph.context.GraphModel;

public class FileSystemNodeId implements ContextNodeId {

  private final Path nodePath;

  public FileSystemNodeId(Path nodePath) {
    this.nodePath = nodePath;
  }

  @Override
  public GraphModel<?, ?> getGraphModel() {
    return new FileSystemGraphModel<>();
  }

  public Path getNodePath() {
    return nodePath;
  }
  
}
