package com.pnambic.depanfx.workspace;

import java.nio.file.Path;

public interface DepanFxProjectMember extends DepanFxWorkspaceMember {

  Path getMemberPath();

  DepanFxProjectTree getProject();
}
