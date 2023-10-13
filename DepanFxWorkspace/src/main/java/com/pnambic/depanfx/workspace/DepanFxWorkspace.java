package com.pnambic.depanfx.workspace;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

public interface DepanFxWorkspace extends DepanFxWorkspaceMember {

  // Shutdown the workspace.
  void exit();

  List<DepanFxProjectTree> getProjectList();

  void addProject(DepanFxProjectTree project);

  void saveDocument(URI uri, Object item) throws IOException;

  Object importDocument(URI uri) throws IOException;

  Optional<DepanFxProjectDocument> asProjectDocument(URI uri);
}
