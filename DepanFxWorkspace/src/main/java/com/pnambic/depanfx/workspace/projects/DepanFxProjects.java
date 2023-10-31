package com.pnambic.depanfx.workspace.projects;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.List;

import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

public class DepanFxProjects {
  private static LinkOption[] IS_DIRECTORY_LINK_OPTIONS = new LinkOption[0];

  public static final String ANALYSES_CONTAINER = "Analyses";

  public static final String GRAPHS_CONTAINER = "Graphs";

  public static final String TOOLS_CONTAINER = "Tools";

  public static void createProjectStructure(String projectName, Path projectPath) {
    try {
      checkProjectForNew(projectPath);
      createChildDirectory(projectPath, ANALYSES_CONTAINER);
      createChildDirectory(projectPath, GRAPHS_CONTAINER);
      createChildDirectory(projectPath, TOOLS_CONTAINER);
    } catch (Exception err) {
      throw new RuntimeException("Unable to create structure for new project " + projectName, err);
    }
  }

  public static File getCurrentGraphs(DepanFxWorkspace workspace) {
    return getCurrent(workspace, GRAPHS_CONTAINER);
  }

  public static File getCurrentAnalyzes(DepanFxWorkspace workspace) {
    return getCurrent(workspace, ANALYSES_CONTAINER);
  }

  public static File getCurrent(DepanFxWorkspace workspace, String container) {
    List<DepanFxProjectTree> projectList = workspace.getProjectList();
    if (projectList.size() >= 1) {
      return new File(projectList.get(0).getMemberPath().toFile(), container);
    }
    return null;
  }

  private static void checkProjectForNew(Path projectPath) throws IOException {
    if (!Files.isDirectory(projectPath, IS_DIRECTORY_LINK_OPTIONS)) {
      throw new NotDirectoryException(projectPath.toString());
    }
    if (!isDirectoryEmpty(projectPath)) {
      throw new DirectoryNotEmptyException(projectPath.toString());
    }
  }

  private static boolean isDirectoryEmpty(Path path) throws IOException {
    try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
        return !directory.iterator().hasNext();
    }
  }

  private static void createChildDirectory(Path dirPath, String childDir) {
    File childFile = new File(dirPath.toFile(), childDir);
    boolean result = childFile.mkdirs();
  }
}
