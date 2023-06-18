package com.pnambic.depanfx.workspace;

import java.nio.file.Path;

import com.pnambic.depanfx.workspace.basic.BasicDepanFxProjectBadMember;
import com.pnambic.depanfx.workspace.basic.BasicDepanFxProjectContainer;
import com.pnambic.depanfx.workspace.basic.BasicDepanFxProjectDocument;
import com.pnambic.depanfx.workspace.basic.BasicDepanFxProjectTree;

public class DepanFxWorkspaceFactory {

  private DepanFxWorkspaceFactory() {
    // Prevent instantiation
  }

  public static DepanFxProjectTree createDepanFxProjectTree(
      String projectName, Path projectPath) {
    return new BasicDepanFxProjectTree(projectName, projectPath);
  }

  public static DepanFxProjectContainer createDepanFxProjectContainer(
      DepanFxProjectTree projectTree, Path projectPath) {
    return new BasicDepanFxProjectContainer(projectTree, projectPath);
  }

  public static DepanFxProjectDocument createDepanFxProjectDocument(
      DepanFxProjectTree projectTree, Path projectPath) {
    return new BasicDepanFxProjectDocument(projectTree, projectPath);
  }

  public static DepanFxProjectBadMember createDepanFxProjectBadMember(
      DepanFxProjectTree projectName, Path projectPath) {
    return new BasicDepanFxProjectBadMember(projectName, projectPath);
  }
}
