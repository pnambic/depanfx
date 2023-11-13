package com.pnambic.depanfx.filesystem.graph;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.graph.model.GraphRelation;

public class FileSystemRelation {

  public static final GraphRelation CONTAINS_DIR =
      new GraphRelation(FileSystemContextDefinition.DIRECTORY_RELID,
          "directory", "subdirectory");

  public static final GraphRelation CONTAINS_FILE =
      new GraphRelation(FileSystemContextDefinition.DOCUMENT_RELID,
          "directory", "file");

  public static final GraphRelation SYMBOLIC_LINK =
      new GraphRelation(FileSystemContextDefinition.LINK_RELID,
          "link", "file");

}
