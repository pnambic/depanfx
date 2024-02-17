package com.pnambic.depanfx.workspace.persistence;

import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Very lightweight class to moderate between in memory workspace
 * resources and something that can be written to storage.
 */
public class PersistWorkspaceResource {

  public String projectName;

  public String resourcePath;

  public PersistWorkspaceResource(String projectName, String resourcePath) {
    this.projectName = projectName;
    this.resourcePath = resourcePath;
  }

  public static PersistWorkspaceResource of(
      DepanFxWorkspaceResource wkspRsrc) {
    DepanFxProjectDocument projectDoc = wkspRsrc.getDocument();
    DepanFxProjectTree rsrcProj = projectDoc.getProject();
    Path pathValue = rsrcProj.getRelativePath(projectDoc.getMemberPath());
    String projectName = rsrcProj.getMemberName();
    String resourcePath = pathValue.toString();

    return new PersistWorkspaceResource(projectName, resourcePath);
  }

  public static Optional<DepanFxWorkspaceResource> forWksp(
      DepanFxWorkspace workspace, PersistWorkspaceResource persistWkspRsrc) {
    Optional<DepanFxProjectDocument> optProjDoc =
        workspace.toProjectDocument(
            persistWkspRsrc.projectName, persistWkspRsrc.resourcePath);
    return optProjDoc.flatMap(d ->
        workspace.getWorkspaceResource(d, "PersistWorkspaceResource"));
  }
}
