package com.pnambic.depanfx.filesystem.graph;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.graph.model.GraphRelation;

public class FileSystemRelation {

  public static GraphRelation CONTAINS_DIR =
      new GraphRelation(FileSystemContextDefinition.DIRECTORY_RELID,
          "directory", "subdirectory");

  public static GraphRelation CONTAINS_FILE =
      new GraphRelation(FileSystemContextDefinition.DOCUMENT_RELID,
          "directory", "file");

  public static GraphRelation SYMBOLIC_LINK =
      new GraphRelation(FileSystemContextDefinition.LINK_RELID,
          "link", "file");

}
