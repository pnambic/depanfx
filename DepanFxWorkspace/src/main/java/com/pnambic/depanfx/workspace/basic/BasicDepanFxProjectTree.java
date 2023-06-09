package com.pnambic.depanfx.workspace.basic;

import java.nio.file.Path;

import com.pnambic.depanfx.workspace.DepanFxProjectTree;

public class BasicDepanFxProjectTree implements DepanFxProjectTree {

  private final String projectName;

  private final Path projectPath;

  public BasicDepanFxProjectTree(String projectName, Path projectPath) {
    this.projectName = projectName;

    this.projectPath = projectPath;
  }

  @Override
  public String getMemberName() {
    return projectName;
  }

  @Override
  public Path getMemberPath() {
    return projectPath;
  }

  @Override
  public DepanFxProjectTree getProject() {
    return this;
  }
}
