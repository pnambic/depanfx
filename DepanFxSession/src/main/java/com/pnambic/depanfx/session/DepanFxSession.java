/*
 * Copyright 2024 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.session;

import com.pnambic.depanfx.perspective.scene.tooldata.DepanFxSceneData;
import com.pnambic.depanfx.scene.DepanFxAppIcons;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import net.rgielen.fxweaver.core.FxWeaver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Top of the document for the user's session.  All persisted data (projects)
 * and UX elements (JavaFX scenes) are contained here.
 */
@Component
public class DepanFxSession {

  public static DepanFxSceneData EMPTY_SESSION_SCENE =
      new DepanFxSceneData("Empty Session", "Empty", Collections.emptyList());

  private final FxWeaver fxWeaver;

  private final DepanFxWorkspace workspace;

  private final Map<DepanFxSceneData, Scene> sceneMap = new HashMap<>();

  private Closeable onClose;

  public static void startSession(
      Stage stage, DepanFxSession session, DepanFxSessionData sessionInfo)
      throws Exception {

    List<DepanFxSceneData> scenes = sessionInfo.getScenes();

    if (scenes.isEmpty()) {
      session.addScene(stage, EMPTY_SESSION_SCENE);
      return;
    }

    // Start the first scene on the initial stage.
    session.addScene(stage, scenes.get(0));

    // Start additional scenes on secondary stages.
    int sceneCnt = scenes.size();
    for (DepanFxSceneData sceneInfo : scenes.subList(1, sceneCnt)) {
      Stage sceneStage = new Stage();
      session.addScene(sceneStage, sceneInfo);
    }
  }

  @Autowired
  public DepanFxSession(FxWeaver fxWeaver, DepanFxWorkspace workspace) {
    this.fxWeaver = fxWeaver;
    this.workspace = workspace;
  }

  public void setOnClose(Closeable onClose) {
    this.onClose = onClose;
  }

  public DepanFxWorkspace getWorkspace() {
    return workspace;
  }

  public Collection<Scene> getScenes() {
    return sceneMap.values();
  }

  public void addScene(Stage stage, DepanFxSceneData sceneInfo)
      throws Exception {
    Scene scene = DepanFxSceneController.createDepanScene(
        fxWeaver, sceneInfo.getViewers(), () -> sceneClose(sceneInfo));
    sceneMap.put(sceneInfo, scene);

    stage.setTitle("DepanFX");
    DepanFxAppIcons.installDepanIcons(stage.getIcons());
    stage.setScene(scene);
    stage.show();
  }

  public void addScene(DepanFxSceneData sceneInfo)
      throws Exception {
    Stage sceneState = new Stage();
    addScene(sceneState, sceneInfo);
  }

  private void sceneClose(DepanFxSceneData sceneInfo) {
    sceneMap.remove(sceneInfo);
    if (sceneMap.isEmpty()) {
      closeParent();
    }
  }

  private void closeParent() {
    try {
      onClose.close();
    } catch (IOException errIo) {
      // Something better ..
    }
  }
}
