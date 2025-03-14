package com.pnambic.depanfx.workspace;

import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInProject;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

public interface DepanFxProjectResource<T> {

  Optional<DepanFxWorkspaceResource<T>> getResource(
      DepanFxWorkspace workspace, Class<T> docType);

  public static class BuiltIn<T> implements DepanFxProjectResource<T> {

    private Path builtInPath;

    public BuiltIn(Path builtInPath) {
      this.builtInPath = builtInPath;
    }

    @Override
    public Optional<DepanFxWorkspaceResource<T>> getResource(
        DepanFxWorkspace workspace, Class<T> docType) {
      return
          DepanFxProjects.getBuiltIn(workspace, docType, builtInPath);
    }

    public Path getBuiltInPath() {
      return builtInPath;
    }
  }

  public static class FileSystem<T> implements DepanFxProjectResource<T> {

    private DepanFxProjectDocument projDoc;

    public FileSystem(DepanFxProjectDocument projDoc) {
      this.projDoc = projDoc;
    }

    @Override
    public Optional<DepanFxWorkspaceResource<T>> getResource(
        DepanFxWorkspace workspace, Class<T> docType) {
      return workspace.getWorkspaceResource(
          projDoc, docType, Collections.emptyMap());
    }
  }

  public static <T> DepanFxProjectResource<T> fromWorkspaceResource(
      DepanFxWorkspaceResource<T> wkspRsrc) {
    DepanFxProjectDocument rsrcDoc = wkspRsrc.getDocument();
    if (rsrcDoc.getProject().getMemberPath().equals(
        DepanFxBuiltInProject.BUILT_IN_PROJECT_PATH)) {
      return new BuiltIn<T>(rsrcDoc.getMemberPath());
    }
    return new FileSystem<T>(rsrcDoc);
  }
}
