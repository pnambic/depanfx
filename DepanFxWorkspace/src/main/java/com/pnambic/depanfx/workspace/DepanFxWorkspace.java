package com.pnambic.depanfx.workspace;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

public interface DepanFxWorkspace extends DepanFxWorkspaceMember {

  public static interface WorkspaceListener {
    void onProjectAdded(DepanFxProjectTree project);

    void onProjectDeleted(DepanFxProjectTree project);

    void onProjectChanged(DepanFxProjectTree project);
  }

  // Shutdown the workspace.
  void exit();

  Optional<DepanFxProjectTree> getCurrentProject();

  void setCurrentProject(DepanFxProjectTree currentProject);

  List<DepanFxProjectTree> getProjectList();

  DepanFxProjectSpi getBuiltInProject();

  DepanFxProjectTree getBuiltInProjectTree();

  void addProject(DepanFxProjectTree project);

  /**
   * Save the document in the persistent store.  The saved document is added
   * to the document cache.
   *
   */
  Optional<DepanFxWorkspaceResource> saveDocument(
      DepanFxProjectDocument projDoc, Object item)
      throws IOException;

  /**
   * Load the document without checking the document cache.  The loaded
   * document is added to the document cache.
   *
   * @param expectedLabel - a label describing the expected content if the
   *    resource cannot be loaded.  Used in error messages.
   * @throws RuntimeException rethrowing a wrapped {@link IOException} after
   *    logging the failure.
   */
  Optional<DepanFxWorkspaceResource> loadDocument(
      DepanFxProjectDocument projDoc, String expectedLabel);

  /**
   * Provide the resource identified by the project document.
   * The contents may be loaded from storage or provided by the cache.
   */
  Optional<DepanFxWorkspaceResource> getWorkspaceResource(
      DepanFxProjectDocument resourceDoc, String expectedContent);

  /**
   * Documents that do not match the supplied {@code docType} are quietly
   * dropped.
   */
  Optional<DepanFxWorkspaceResource> getWorkspaceResource(
      DepanFxProjectDocument resourceDoc, Class<?> type);

  Optional<DepanFxProjectContainer> toProjectContainer(URI uri);

  Optional<DepanFxProjectDocument> toProjectDocument(URI uri);

  Optional<DepanFxProjectDocument> toProjectDocument(
      String projectName, String resourcePath);

  void addListener(WorkspaceListener listener);

  void removeListener(WorkspaceListener listener);
}
