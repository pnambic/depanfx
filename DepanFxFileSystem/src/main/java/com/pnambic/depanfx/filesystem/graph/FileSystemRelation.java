package com.pnambic.depanfx.filesystem.graph;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.graph.context.ContextRelationId;
import com.pnambic.depanfx.graph.model.GraphRelation;

public class FileSystemRelation extends GraphRelation {

  private FileSystemRelation(
      ContextRelationId id, String forwardName, String reverseName) {
    super(id, forwardName, reverseName);
  }

  public static final FileSystemRelation CONTAINS_DIR =
      new FileSystemRelation(FileSystemContextDefinition.DIRECTORY_RELID,
          "directory", "subdirectory");

  public static final FileSystemRelation CONTAINS_FILE =
      new FileSystemRelation(FileSystemContextDefinition.DOCUMENT_RELID,
          "directory", "file");

  public static final FileSystemRelation SYMBOLIC_LINK =
      new FileSystemRelation(FileSystemContextDefinition.LINK_RELID,
          "link", "file");

  public static final GraphRelation[] RELATIONS = {
      CONTAINS_DIR, CONTAINS_FILE, SYMBOLIC_LINK
    };
}
