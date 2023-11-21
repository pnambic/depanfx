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

  Optional<DepanFxWorkspaceResource> saveDocument(
      DepanFxProjectDocument projDoc, Object item)
      throws IOException;

  Optional<DepanFxWorkspaceResource> importDocument(
      DepanFxProjectDocument projDoc)
      throws IOException;

  Optional<DepanFxWorkspaceResource> toWorkspaceResource(
      DepanFxProjectDocument projDoc, Object resource);

  /**
   * Provide the resource identified by the project document.
   * The contents may be loaded from storage or provided by the cache.
   */
  Optional<DepanFxWorkspaceResource> getWorkspaceResource(
      DepanFxProjectDocument resourceDoc);

  Optional<DepanFxProjectContainer> toProjectContainer(URI uri);

  Optional<DepanFxProjectDocument> toProjectDocument(URI uri);

  Optional<DepanFxProjectDocument> toProjectDocument(
      String projectName, String resourcePath);

  void addListener(WorkspaceListener listener);

  void removeListener(WorkspaceListener listener);
}
