package com.pnambic.depanfx.session;

import com.pnambic.depanfx.perspective.scene.tooldata.DepanFxSceneData;
import com.pnambic.depanfx.workspace.tooldata.DepanFxBaseToolData;

import java.util.Collections;
import java.util.List;

public class DepanFxSessionData extends DepanFxBaseToolData {

  public static final DepanFxSessionData EMPTY_SESSION_DATA =
      new DepanFxSessionData(
          "Empty Session", "Empty DepanFx session", Collections.emptyList());

  private final List<DepanFxSceneData> scenes;

  public DepanFxSessionData(
      String toolName, String toolDescription,
      List<DepanFxSceneData> scenes) {
    super(toolName, toolDescription);
    this.scenes = scenes;
  }

  public List<DepanFxSceneData> getScenes() {
    return scenes;
  }
}
