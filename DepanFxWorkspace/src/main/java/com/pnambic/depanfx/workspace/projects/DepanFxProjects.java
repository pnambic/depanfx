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

  public static final String ANALYSES_CONTAINER = "Analyses";

  public static final String GRAPHS_CONTAINER = "Graphs";

  public static final String TOOLS_CONTAINER = "Tools";

  public static final Path ANALYSES_PATH =
      new File(ANALYSES_CONTAINER).toPath();

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
      createChildContainer(projectTree, projectSpi, ANALYSES_CONTAINER);
      createChildContainer(projectTree, projectSpi, GRAPHS_CONTAINER);
      createChildContainer(projectTree, projectSpi, TOOLS_CONTAINER);
    } catch (Exception errAny) {
      throw new RuntimeException(
          "Unable to create structure for new project "
              + projectSpi.getProjectName(), errAny);
    }
  }

  public static File getCurrentGraphs(DepanFxWorkspace workspace) {
    return getCurrent(workspace, GRAPHS_CONTAINER);
  }

  public static File getCurrentAnalyzes(DepanFxWorkspace workspace) {
    return getCurrent(workspace, ANALYSES_CONTAINER);
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

  public static Stream<DepanFxBuiltInContribution> streamBuiltIns(
      DepanFxWorkspace workspace, Class<?> type) {
    DepanFxBuiltInProject project =
        (DepanFxBuiltInProject) workspace.getBuiltInProject();
    return project.getContributions(type);
  }

  public static Optional<DepanFxWorkspaceResource> getBuiltIn(
      DepanFxWorkspace workspace, Class<?> type, Path builtInPath) {
    return
        workspace.getBuiltInProjectTree().asProjectDocument(builtInPath)
        .flatMap(d ->
            ((DepanFxBuiltInProject) workspace.getBuiltInProject())
                .getResource(d));
  }

  public static Optional<DepanFxWorkspaceResource> getBuiltIn(
      DepanFxWorkspace workspace, Class<?> type,
      Predicate<DepanFxBuiltInContribution> contribFilter) {

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

  private static Optional<DepanFxWorkspaceResource> buildContributionRsrc(
      DepanFxBuiltInContribution contrib, DepanFxBuiltInProject project) {
    Optional<DepanFxProjectDocument> optProjDoc =
        project.getProjectTree().asProjectDocument(contrib.getPath());
    return optProjDoc
        .map(p -> new DepanFxWorkspaceResource.StaticWorkspaceResource(
            p, contrib.getDocument()));
  }
}
