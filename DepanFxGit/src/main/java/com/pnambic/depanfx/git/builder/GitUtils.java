package com.pnambic.depanfx.git.builder;

import com.pnambic.depanfx.filesystem.graph.DirectoryNode;
import com.pnambic.depanfx.filesystem.graph.DocumentNode;

import java.io.File;

public class GitUtils {

  private GitUtils() {
    // Prevent instantiation.
  }

  public static DirectoryNode createDirectory(String docName) {
    return createDirectory(new File(docName));
  }

  public static DirectoryNode createDirectory(File docFile) {
    return new DirectoryNode(docFile.toPath());
  }

  public static DocumentNode createDocument(String docName) {
    return new DocumentNode(new File(docName).toPath());
  }
}
