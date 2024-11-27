package com.pnambic.depanfx.session.tooldata;

import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DepanFxSessionData extends DepanFxBaseToolData {

  public static final DepanFxSessionData EMPTY_SESSION_DATA =
      new DepanFxSessionData(
          "Empty Session", "Empty DepanFx session",
          Collections.emptyList(), Collections.emptyList());

  private final List<DepanFxProjectData> projects;

  private final Collection<DepanFxSceneData> scenes;

  public DepanFxSessionData(
      String toolName, String toolDescription,
      List<DepanFxProjectData> projects,
      Collection<DepanFxSceneData> scenes) {
    super(toolName, toolDescription);
    this.projects = projects;
    this.scenes = scenes;
  }

  public List<DepanFxProjectData> getProjects() {
    return projects;
  }

  public Collection<DepanFxSceneData> getScenes() {
    return scenes;
  }
}
