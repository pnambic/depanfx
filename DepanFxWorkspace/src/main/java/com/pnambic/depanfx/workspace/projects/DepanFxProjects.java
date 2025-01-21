package com.pnambic.depanfx.workspace.projects;

import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectSpi;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DepanFxProjects {

  public static final String BUILT_IN = "Built-in";

  public static final String ANALYZES_CONTAINER = "Analyzes";

  public static final String GRAPHS_CONTAINER = "Graphs";

  public static final String TOOLS_CONTAINER = "Tools";

  public static final Path ANALYZES_PATH =
      new File(ANALYZES_CONTAINER).toPath();

  public static final Path GRAPHS_PATH =
      new File(GRAPHS_CONTAINER).toPath();

  public static final Path TOOLS_PATH =
      new File(TOOLS_CONTAINER).toPath();

  /**
   * Use the project spi during initial project construction to avoid any
   * possible listeners.
   */
  public static void createProjectStructure(
      DepanFxProjectTree projectTree, DepanFxProjectSpi projectSpi) {
    try {
      projectSpi.checkProjectForNew();
      createChildContainer(projectTree, projectSpi, ANALYZES_CONTAINER);
      createChildContainer(projectTree, projectSpi, GRAPHS_CONTAINER);
      createChildContainer(projectTree, projectSpi, TOOLS_CONTAINER);
    } catch (Exception errAny) {
      throw new RuntimeException(
          "Unable to create structure for new project "
              + projectSpi.getProjectName(), errAny);
    }
  }

  /////////////////////////////////////
  // Resources by Container, preferred

  public static Optional<DepanFxProjectContainer> getCurrentGraphsDir(
      DepanFxWorkspace workspace) {
    return getCurrentDir(workspace, GRAPHS_CONTAINER);
  }

  public static Optional<DepanFxProjectContainer> getCurrentAnalyzesDir(
      DepanFxWorkspace workspace) {
    return getCurrentDir(workspace, ANALYZES_CONTAINER);
  }

  public static Optional<DepanFxProjectContainer> getCurrentToolsDir(
      DepanFxWorkspace workspace) {
    return getCurrentDir(workspace, TOOLS_CONTAINER);
  }

  public static Optional<DepanFxProjectContainer> getCurrentDir(
      DepanFxWorkspace workspace, String rsrcContainer) {
    Optional<Path> rsrcPath =
        DepanFxProjects.getCurrentPath(workspace, rsrcContainer);
    return workspace.getCurrentProject()
        .flatMap(p -> p.asProjectContainer(rsrcPath.get()));
  }

  /////////////////////////////////////
  // Resources by Path, preferred

  public static Optional<Path> getCurrentGraphsPath(
      DepanFxWorkspace workspace) {
    return getCurrentPath(workspace, GRAPHS_CONTAINER);
  }

  public static Optional<Path> getCurrentAnalyzesPath(
      DepanFxWorkspace workspace) {
    return getCurrentPath(workspace, ANALYZES_CONTAINER);
  }

  public static Optional<Path> getCurrentToolsPath(
      DepanFxWorkspace workspace) {
    return getCurrentPath(workspace, TOOLS_CONTAINER);
  }

  public static Optional<Path> getCurrentPath(
      DepanFxWorkspace workspace, String container) {
    return workspace.getCurrentProject()
        .map(t -> t.getMemberPath())
        .map(p -> p.resolve(container));
  }

  /////////////////////////////////////
  // "Active" locations never fail .. may be out of workspace

  public static Path getActiveGraphsPath(
      DepanFxWorkspace workspace) {
    return getActivePath(workspace, GRAPHS_PATH);
  }

  public static Path getActiveAnalyzesPath(
      DepanFxWorkspace workspace) {
    return getActivePath(workspace, ANALYZES_PATH);
  }

  public static Path getActiveToolsPath(
      DepanFxWorkspace workspace) {
    return getActivePath(workspace, TOOLS_PATH);
  }

  public static Path getActivePath(
      DepanFxWorkspace workspace, Path resourcePath) {
    return getCurrentPath(workspace, resourcePath.toString())
        .orElse(resourcePath);
  }

  /////////////////////////////////////
  // Resources by File, deprecated

  public static File getCurrentGraphs(DepanFxWorkspace workspace) {
    return getCurrent(workspace, GRAPHS_CONTAINER);
  }

  public static File getCurrentAnalyzes(DepanFxWorkspace workspace) {
    return getCurrent(workspace, ANALYZES_CONTAINER);
  }

  public static File getCurrentTools(DepanFxWorkspace workspace) {
    return getCurrent(workspace, TOOLS_CONTAINER);
  }

  public static File getCurrent(DepanFxWorkspace workspace, String container) {
    return workspace.getCurrentProject()
        .map(t -> t.getMemberPath())
        .map(p -> p.resolve(container))
        .map(p -> p.toFile())
        .orElse(null);
  }

  public static <T> Stream<DepanFxBuiltInContribution<T>> streamBuiltIns(
      DepanFxWorkspace workspace, Class<T> type) {
    DepanFxBuiltInProject project =
        (DepanFxBuiltInProject) workspace.getBuiltInProject();
    return project.getContributions(type);
  }

  public static <T> Optional<DepanFxWorkspaceResource<T>> getBuiltIn(
      DepanFxWorkspace workspace, Class<T> type, Path builtInPath) {
    return
        workspace.getBuiltInProjectTree().asProjectDocument(builtInPath)
        .flatMap(d ->
            ((DepanFxBuiltInProject) workspace.getBuiltInProject())
                .getResource(d));
  }

  public static String getResourceLabel(DepanFxWorkspaceResource<?> resource) {
    return getDocumentLabel(resource.getDocument());
  }

  public static String getDocumentLabel(DepanFxProjectDocument doc) {
    DepanFxProjectTree proj = doc.getProject();
    Path docPath = proj.getMemberPath().relativize(doc.getMemberPath());
    return proj.getMemberName() + ":" + docPath.toString();
  }

  /**
   * By the time a candidate built in hits the filter,
   * it has already been filtered to be of the requested type.
   */
  public static <T> Optional<DepanFxWorkspaceResource<T>> getBuiltIn(
      DepanFxWorkspace workspace, Class<T> type,
      Predicate<DepanFxBuiltInContribution<T>> contribFilter) {

    DepanFxBuiltInProject project =
        (DepanFxBuiltInProject) workspace.getBuiltInProject();
    return streamBuiltIns(workspace, type)
        .filter(contribFilter)
        .findFirst()
        .flatMap(c -> buildContributionRsrc(c, project));
  }

  private static void createChildContainer(
      DepanFxProjectTree projectTree,
      DepanFxProjectSpi projectSpi,
      String dirName) {
    buildChildContainer(projectTree, dirName)
        .ifPresent(projectSpi::createContainer);
  }

  private static Optional<DepanFxProjectContainer> buildChildContainer(
      DepanFxProjectTree projectTree, String dirName) {
    Path dirPath = Paths.get(dirName);
    return projectTree.asProjectContainer(dirPath);
  }

  private static <T> Optional<DepanFxWorkspaceResource<T>> buildContributionRsrc(
      DepanFxBuiltInContribution<T> contrib, DepanFxBuiltInProject project) {
    Optional<DepanFxProjectDocument> optProjDoc =
        project.getProjectTree().asProjectDocument(contrib.getPath());
    return optProjDoc
        .map(p -> DepanFxWorkspaceResource.forSource(p, contrib.getDocument()));
  }
}
