package com.pnambic.depanfx.workspace;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

public interface DepanFxWorkspace extends DepanFxWorkspaceMember {

  // Shutdown the workspace.
  void exit();

  List<DepanFxProjectTree> getProjectList();

  DepanFxProjectSpi getBuiltInProject();

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
}
