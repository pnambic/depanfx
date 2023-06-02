package com.pnambic.depanfx.filesystem.graph;

import java.nio.file.Path;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;

public class DocumentNode extends FileSystemNode {

  public DocumentNode(Path path) {
    super(FileSystemContextDefinition.DOCUMENT_NKID, path);
  }
}
