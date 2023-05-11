package com.pnambic.depanfx.workspace.basic;

import java.nio.file.Path;

import com.pnambic.depanfx.workspace.DepanFxProjectTree;

public class BasicDepanFxProjectTree implements DepanFxProjectTree {

  private final Path projectPath;

  private final String projectName;

  public BasicDepanFxProjectTree(Path projectPath, String projectName) {
    this.projectPath = projectPath;
    this.projectName = projectName;
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
