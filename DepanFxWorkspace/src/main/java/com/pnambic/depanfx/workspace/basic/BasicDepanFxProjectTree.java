package com.pnambic.depanfx.workspace.basic;

import java.nio.file.Path;
import java.util.Optional;

import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
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

  @Override
  public Optional<DepanFxProjectDocument> asProjectDocument(Path docPath) {
    if (docPath.startsWith(projectPath)) {
      BasicDepanFxProjectDocument result = new BasicDepanFxProjectDocument(this, docPath);
      return Optional.of(result);
    }
    return Optional.empty();
  }
}
