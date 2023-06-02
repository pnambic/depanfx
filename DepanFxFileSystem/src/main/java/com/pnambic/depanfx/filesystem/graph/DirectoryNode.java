package com.pnambic.depanfx.filesystem.graph;

import java.nio.file.Path;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;

public class DirectoryNode extends FileSystemNode {

  public DirectoryNode(Path path) {
    super(FileSystemContextDefinition.DIRECTORY_NKID, path);
  }
}
