package com.pnambic.depanfx.git.tooldata;

import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.io.File;
import java.nio.file.Path;

public class DepanFxGitRepoData extends DepanFxBaseToolData{

  public static final String GIT_REPOS_TOOL_DIR = "Git Repos";

  public static final String GIT_REPO_TOOL_EXT = "dgrti";

  public static final Path GIT_REPOS_TOOL_PATH =
      DepanFxProjects.TOOLS_PATH.resolve(GIT_REPOS_TOOL_DIR);

  private final String gitExe;

  private final String gitRepoName;

  private final String gitRepoPath;

  public DepanFxGitRepoData(String toolName, String toolDescription,
      String gitExe, String gitRepoName, String gitRepoPath) {
    super(toolName, toolDescription);
    this.gitExe = gitExe;
    this.gitRepoName = gitRepoName;
    this.gitRepoPath = gitRepoPath;
  }

  public String getGitExe() {
    return gitExe;
  }

  public String getGitRepoName() {
    return gitRepoName;
  }

  public String getGitRepoPath() {
    return gitRepoPath;
  }

  public static File buildCurrentToolDir(DepanFxWorkspace workspace) {
     return workspace.getCurrentProject()
        .map(t -> t.getMemberPath())
        .map(p -> p.resolve(GIT_REPOS_TOOL_PATH))
        .map(p -> p.toFile())
        .get();
  }

  public static File buildCurrentToolFile(
      DepanFxWorkspace workspace, String dataLabel) {
    String toolName = DepanFxWorkspaceFactory.buildDocumentTimestampName(
        dataLabel, GIT_REPO_TOOL_EXT);
    return new File(buildCurrentToolDir(workspace), toolName);
  }
}
