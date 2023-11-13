package com.pnambic.depanfx.workspace;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The project tree provides an abstraction for the storage of project
 * resources.  The project's storage is partitioned into a hierarchy of
 * containers.  Container members may be other containers or docu ments.
 */
public interface DepanFxProjectTree extends DepanFxProjectContainer {

  public static interface ProjectTreeListener {
    void onContainerAdded(DepanFxProjectContainer projDir);

    void onContainerRegistered(DepanFxProjectContainer projDir);

    void onContainerDeleted(DepanFxProjectContainer projDir);

    void onDocumentAdded(DepanFxProjectDocument projDoc);

    void onDocumentDeleted(DepanFxProjectDocument projDoc);
  }

  DepanFxProjectBadMember asBadMember(Path childPath);

  Optional<DepanFxProjectContainer> asProjectContainer(Path path);

  Optional<DepanFxProjectDocument> asProjectDocument(Path path);

  void createContainer(DepanFxProjectContainer projDir);

  void deleteContainer(DepanFxProjectContainer projDir);

  /** Ensure externally created paths are known */
  void registerContainer(DepanFxProjectContainer dstDir);

  void deleteDocument(DepanFxProjectDocument projDoc);

  void registerDocument(DepanFxProjectDocument projDoc);

  void addListener(ProjectTreeListener listener);

  void removeListener(ProjectTreeListener listener);

  /**
   * Provide the children, if any, for any descendant member. 
   */
  Stream<DepanFxProjectMember> getMembers(
      DepanFxProjectMember basicDepanFxProjectContainer);
}
