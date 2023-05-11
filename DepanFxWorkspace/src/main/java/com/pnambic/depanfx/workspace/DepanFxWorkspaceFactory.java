package com.pnambic.depanfx.workspace;

import com.pnambic.depanfx.workspace.basic.BasicDepanFxWorkspace;

public class DepanFxWorkspaceFactory {

  public static DepanFxWorkspace createDepanFxWorkspace(String workspaceName) {
    return new BasicDepanFxWorkspace(workspaceName);
  }
}
