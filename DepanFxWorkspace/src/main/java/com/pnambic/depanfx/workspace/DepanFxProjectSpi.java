package com.pnambic.depanfx.workspace;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Interact directly with the underlying storage for the project.
 * None of the actions create notifications.
 */
public interface DepanFxProjectSpi {

  String getProjectName();

  Path getProjectPath();

  void checkProjectForNew() throws IOException;

  boolean isDirectoryEmpty() throws IOException;

  /**
   * Provide a path for the member.  If the path is absent, the proposed path is
   * not a member of the project.
   *
   * In general, relative paths are members of the project.
   */
  Optional<Path> getMemberPath(Path memberPath);

  void createContainer(DepanFxProjectContainer projDir);

  void deleteContainer(DepanFxProjectContainer projDir);

  void deleteDocument(DepanFxProjectDocument projDoc);
}
