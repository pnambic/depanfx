package com.pnambic.depanfx.workspace;

import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.nio.file.Path;
import java.util.Optional;

public interface DepanFxProjectResource {

  Optional<DepanFxWorkspaceResource> getResource(
      DepanFxWorkspace workspace, Class<?> docType);

  public static class BuiltIn implements DepanFxProjectResource {

    private Path builtInPath;

    public BuiltIn(Path builtInPath) {
      this.builtInPath = builtInPath;
    }

    @Override
    public Optional<DepanFxWorkspaceResource> getResource(
        DepanFxWorkspace workspace, Class<?> docType) {
      return
          DepanFxProjects.getBuiltIn(workspace, docType, builtInPath);
    }

    public Path getBuiltInPath() {
      return builtInPath;
    }
  }

  public static class FileSystem implements DepanFxProjectResource {

    private DepanFxProjectDocument projDoc;

    public FileSystem(DepanFxProjectDocument projDoc) {
      this.projDoc = projDoc;
    }

    @Override
    public Optional<DepanFxWorkspaceResource> getResource(
        DepanFxWorkspace workspace, Class<?> docType) {
      return workspace.getWorkspaceResource(projDoc, docType);
    }
  }

  public static DepanFxProjectResource fromWorkspaceResource(
      DepanFxWorkspaceResource wkspRsrc) {
    DepanFxProjectDocument rsrcDoc = wkspRsrc.getDocument();
    if (rsrcDoc.getProject().getMemberPath().equals(
        DepanFxBuiltInProject.BUILT_IN_PROJECT_PATH)) {
      return new BuiltIn(rsrcDoc.getMemberPath());
    }
    return new FileSystem(rsrcDoc);
  }
}
