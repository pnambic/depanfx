package com.pnambic.depanfx.filesystem.context;

import com.pnambic.depanfx.graph.context.ContextModelId;

/**
 * Globally unique identifiers for the file system gragh package.
 */
public class FileSystemContextModelId implements ContextModelId {

  public static final String FILE_SYSTEM_KEY = "FileSystem";

  @Override
  public String getContextModelKey() {
    return FILE_SYSTEM_KEY;
  }

  /**
   * Since there should only be one instance of a FileSystemContextModelId
   * in a correct DepAn execution, just use object equality.
   */
  @Override
  public boolean equals(Object obj) {
    // Explicitly use the inherited equals() method, versus confusing
    // future reviewer with its apparent absence.
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
      return FILE_SYSTEM_KEY.hashCode();
  }
}
