package com.pnambic.depanfx.git.tooldata;

import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import java.io.File;

public class DepanFxGitRepoData extends DepanFxBaseToolData{

  public static final String GIT_REPOS_TOOL_PATH = "Git Repos";

  public static final String GIT_REPO_TOOL_EXT = "dgrti";

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
    File toolsDir = DepanFxProjects.getCurrentTools(workspace);
    return new File(toolsDir, GIT_REPOS_TOOL_PATH);
  }

  public static File buildCurrentToolFile(DepanFxWorkspace workspace, String dataLabel) {
    String toolName = DepanFxWorkspaceFactory.buildDocumentTimestampName(
        dataLabel, GIT_REPO_TOOL_EXT);
    return new File(buildCurrentToolDir(workspace), toolName);
  }
}
