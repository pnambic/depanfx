package com.pnambic.depanfx.workspace.basic;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
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

  @Override
  public Optional<DepanFxProjectContainer> getParent() {
    Path parentPath = path.getParent();
    if (parentPath.startsWith(project.getMemberPath())) {;
      return Optional.of(
          new BasicDepanFxProjectContainer(project, parentPath));
    }
    return Optional.empty();
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder(project.getMemberName());
    result.append(':');
    result.append(project.getRelativePath(path).toString());
    return result.toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(path, project);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    BasicDepanFxProjectMember other = (BasicDepanFxProjectMember) obj;
    return Objects.equals(path, other.path)
        && Objects.equals(project, other.project);
  }
}
