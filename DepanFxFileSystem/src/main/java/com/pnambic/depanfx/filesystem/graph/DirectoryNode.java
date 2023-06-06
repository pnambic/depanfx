package com.pnambic.depanfx.filesystem.graph;

import java.nio.file.Path;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.graph.context.ContextNodeKindId;

public class DirectoryNode extends FileSystemNode {

  public DirectoryNode(ContextNodeKindId id, Path path) {
    super(FileSystemContextDefinition.DIRECTORY_NKID, path);
  }
}
