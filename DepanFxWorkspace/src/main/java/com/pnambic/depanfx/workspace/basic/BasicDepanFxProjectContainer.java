package com.pnambic.depanfx.workspace.basic;

import java.nio.file.Path;

import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;

public class BasicDepanFxProjectContainer extends BasicDepanFxProjectMember
    implements DepanFxProjectContainer {

  public BasicDepanFxProjectContainer(DepanFxProjectTree project, Path path) {
    super(project, path);
  }
}
