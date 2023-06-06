package com.pnambic.depanfx.filesystem.graph;

import com.pnambic.depanfx.filesystem.context.FileSystemContextDefinition;
import com.pnambic.depanfx.filesystem.context.FileSystemRelationId;
import com.pnambic.depanfx.graph.api.Relation;

public enum FileSystemRelation implements Relation<FileSystemRelationId> {
  CONTAINS_DIR(FileSystemContextDefinition.DIRECTORY_RELID,
      "directory", "subdirectory"),
  CONTAINS_FILE(FileSystemContextDefinition.DOCUMENT_RELID,
      "directory", "file"),
  SYMBOLIC_LINK(FileSystemContextDefinition.LINK_RELID,
      "link", "file");

  /**
   * identifier for the relation
   */
  private final FileSystemRelationId relationId;

  /**
   * name of the element on the left of the relation.
   */
  public final String forwardName;
  /**
   * name of the element on the right side of the relation.
   */
  public final String reverseName;

  /**
   * constructor for a new Relation.
   * 
   * @param forwardName name of the left hand side element.
   * @param reverseName name of the right hand side element.
   */
  private FileSystemRelation(FileSystemRelationId relationId,
      String reverseName, String forwardName) {
    this.relationId = relationId;
    this.forwardName = forwardName;
    this.reverseName = reverseName;
  }

  @Override
  public FileSystemRelationId getId() {
    return relationId;
  }

  @Override
  public String getForwardName() {
    return forwardName;
  }

  @Override
  public String getReverseName() {
    return reverseName;
  }
}
