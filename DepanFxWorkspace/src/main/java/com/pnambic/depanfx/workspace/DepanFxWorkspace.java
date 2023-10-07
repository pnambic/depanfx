package com.pnambic.depanfx.workspace;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public interface DepanFxWorkspace extends DepanFxWorkspaceMember {

  // Shutdown the workspace.
  void exit();

  List<DepanFxProjectTree> getProjectList();

  void addProject(DepanFxProjectTree project);

  void saveDocument(URI uri, Object item) throws IOException;

  void importDocument(URI uri) throws IOException;
}
