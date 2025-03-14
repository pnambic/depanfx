package com.pnambic.depanfx.workspace;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
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

  DepanFxProjectTree getScratchProjectTree();

  void addProject(DepanFxProjectTree project);

  <T> DepanFxWorkspaceResource<T> addScratchResource(T resource);

  /**
   * Save the document in the persistent store.  The saved document is added
   * to the document cache.
   *
   */
  <T> Optional<DepanFxWorkspaceResource<T>> saveDocument(
      DepanFxProjectDocument projDoc, T item)
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
  <T> Optional<DepanFxWorkspaceResource<T>> loadDocument(
      DepanFxProjectDocument projDoc,
      String expectedLabel,
      Map<?, ?> context);

  /**
   * Provide the resource identified by the project document.
   * The contents may be loaded from storage or provided by the cache.
   */
  <T> Optional<DepanFxWorkspaceResource<T>> getWorkspaceResource(
      DepanFxProjectDocument resourceDoc,
      String expectedContent,
      Map<?, ?> context);

  /**
   * Documents that do not match the supplied {@code docType} are quietly
   * dropped.
   */
  <T> Optional<DepanFxWorkspaceResource<T>> getWorkspaceResource(
      DepanFxProjectDocument resourceDoc,
      Class<T> type,
      Map<?, ?> context);

  Optional<DepanFxProjectContainer> toProjectContainer(URI uri);

  Optional<DepanFxProjectDocument> toProjectDocument(URI uri);

  Optional<DepanFxProjectDocument> toProjectDocument(
      String projectName, String resourcePath);

  void addListener(WorkspaceListener listener);

  void removeListener(WorkspaceListener listener);
}
