package com.pnambic.depanfx.session.tooldata;

import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.util.Collection;
import java.util.Collections;

public class DepanFxSessionData extends DepanFxBaseToolData {

  public static final DepanFxSessionData EMPTY_SESSION_DATA =
      new DepanFxSessionData(
          "Empty Session", "Empty DepanFx session", Collections.emptyList());

  private final Collection<DepanFxSceneData> scenes;

  public DepanFxSessionData(
      String toolName, String toolDescription,
      Collection<DepanFxSceneData> scenes) {
    super(toolName, toolDescription);
    this.scenes = scenes;
  }

  public Collection<DepanFxSceneData> getScenes() {
    return scenes;
  }
}
