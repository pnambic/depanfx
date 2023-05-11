package com.pnambic.depanfx.workspace.basic;

import java.nio.file.Path;

import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxProjectBadMember;

public class BasicDepanFxProjectBadMember extends BasicDepanFxProjectMember
    implements DepanFxProjectBadMember {

  public BasicDepanFxProjectBadMember(DepanFxProjectTree project, Path path) {
    super(project, path);
  }
}
