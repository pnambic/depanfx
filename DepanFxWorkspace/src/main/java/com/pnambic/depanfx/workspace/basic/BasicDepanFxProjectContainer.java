package com.pnambic.depanfx.workspace.basic;

import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxProjectTree;

import java.nio.file.Path;
import java.util.stream.Stream;

public class BasicDepanFxProjectContainer extends BasicDepanFxProjectMember
    implements DepanFxProjectContainer {

  public BasicDepanFxProjectContainer(DepanFxProjectTree project, Path path) {
    super(project, path);
  }

  @Override
  public Stream<DepanFxProjectMember> getMembers() {
    return getProject().getMembers(this);
  }
}
