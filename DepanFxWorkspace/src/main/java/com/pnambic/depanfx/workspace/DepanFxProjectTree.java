package com.pnambic.depanfx.workspace;

import java.nio.file.Path;
import java.util.Optional;

/**
 * The project tree provides an abstraction for the storage of project
 * resources.  The project's storage is partitioned into a hierarchy of
 * containers.  Container members may be other containers or docu ments.
 */
public interface DepanFxProjectTree extends DepanFxProjectMember {

  public static interface ProjectTreeListener {
    void onContainerAdded(DepanFxProjectContainer projDir);

    void onContainerDeleted(DepanFxProjectContainer projDir);

    void onDocumentAdded(DepanFxProjectDocument projDoc);

    void onDocumentDeleted(DepanFxProjectDocument projDoc);
  }

  Optional<DepanFxProjectContainer> asProjectContainer(Path path);

  Optional<DepanFxProjectDocument> asProjectDocument(Path path);

  void createContainer(DepanFxProjectContainer projDir);

  void deleteContainer(DepanFxProjectContainer projDir);

  void deleteDocument(DepanFxProjectDocument projDoc);

  void registerDocument(DepanFxProjectDocument projDoc);

  void addListener(ProjectTreeListener listener);

  void removeListener(ProjectTreeListener listener);
}
