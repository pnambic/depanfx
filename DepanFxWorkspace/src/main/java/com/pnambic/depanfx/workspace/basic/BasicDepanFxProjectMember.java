package com.pnambic.depanfx.workspace.basic;

import java.nio.file.Path;

import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;

/**
 * Support folders and files internal to the project, but not the top level
 * project itself.
 */
public class BasicDepanFxProjectMember implements DepanFxProjectMember {

  private final DepanFxProjectTree project;

  /** Full file system path, not relative to the project */
  private final Path path;

  public BasicDepanFxProjectMember(DepanFxProjectTree project, Path path) {
    this.project = project;
    this.path = path;
  }

  @Override
  public DepanFxProjectTree getProject() {
    return project;
  }

  @Override
  public String getMemberName() {
    return path.getFileName().toString();
  }

  @Override
  public Path getMemberPath() {
    return path;
  }
}
