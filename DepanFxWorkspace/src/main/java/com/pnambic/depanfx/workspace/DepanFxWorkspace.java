package com.pnambic.depanfx.workspace;

import java.util.List;

public interface DepanFxWorkspace extends DepanFxWorkspaceMember {

  // Shutdown the workspace.
  void exit();

  List<DepanFxProjectTree> getProjectList();

  void addProject(DepanFxProjectTree project);
}
