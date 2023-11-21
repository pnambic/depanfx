package com.pnambic.depanfx.workspace.projects;

import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectSpi;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class DepanFxProjects {

  public static final String BUILT_IN = "Built-in";

  public static final String ANALYSES_CONTAINER = "Analyses";

  public static final String GRAPHS_CONTAINER = "Graphs";

  public static final String TOOLS_CONTAINER = "Tools";

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

  public static File getCurrent(DepanFxWorkspace workspace, String container) {
    return workspace.getCurrentProject()
        .map(p -> new File(p.getMemberPath().toFile(), container))
        .orElse(null);
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
}
