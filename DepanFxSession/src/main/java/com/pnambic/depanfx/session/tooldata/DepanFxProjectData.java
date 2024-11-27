package com.pnambic.depanfx.session.tooldata;

import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.nio.file.Path;

public class DepanFxProjectData extends DepanFxBaseToolData {

  private final Path projectPath;

  public DepanFxProjectData(
      String toolName, String toolDescription, Path projectPath) {
    super(toolName, toolDescription);
    this.projectPath = projectPath;
  }

  public Path getProjectPath() {
    return projectPath;
  }
}
