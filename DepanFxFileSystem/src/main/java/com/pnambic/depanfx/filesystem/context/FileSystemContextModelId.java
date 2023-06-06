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
}
