package com.pnambic.depanfx.workspace.basic;

import java.nio.file.Path;

import com.pnambic.depanfx.workspace.DepanFxProjectTree;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;

public class BasicDepanFxProjectDocument extends BasicDepanFxProjectMember
    implements DepanFxProjectDocument {

  public BasicDepanFxProjectDocument(DepanFxProjectTree project, Path path) {
   super(project, path);
  }
}
