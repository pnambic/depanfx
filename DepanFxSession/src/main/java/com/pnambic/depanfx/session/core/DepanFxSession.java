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
package com.pnambic.depanfx.session.core;

import com.pnambic.depanfx.scene.DepanFxAppIcons;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.session.gui.DepanFxSessionSaveDialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javafx.stage.Stage;

/**
 * Top of the document for the user's session.  All persisted data (projects)
 * and UX elements (JavaFX scenes) are contained here.
 */
@Component
public class DepanFxSession implements DepanFxSceneController.SceneOwner {

  public static DepanFxSceneConfig EMPTY_SESSION_SCENE =
      new DepanFxSceneConfig("Empty Session", "Empty",
          -1.0d, -1.0d, -1.0d, -1.0d,
          Collections.emptyList());

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final Map<DepanFxSceneController, DepanFxSceneConfig> sceneMap =
      new HashMap<>();

  private Closeable onClose;

  public static void startSession(
      Stage stage, DepanFxSession session, DepanFxSessionConfig sessionConfig)
      throws Exception {
    // Projects are installed during deserialization.
    // Set the current project.
    session.setCurrentProject(sessionConfig.getCurrentProjectName());

    // Start scenes after full workspace context restored.
    startScenes(stage, session, sessionConfig.getSceneConfigs());
  }

  private static void startScenes(
      Stage stage,
      DepanFxSession session,
      Collection<DepanFxSceneConfig> scenes)
      throws Exception {

    Iterator<DepanFxSceneConfig> sceneSeq = scenes.iterator();

    if (!sceneSeq.hasNext()) {
      session.addScene(stage, EMPTY_SESSION_SCENE);
      return;
    }

    // Start the first scene on the initial stage.
    DepanFxSceneConfig baseScene = sceneSeq.next();
    session.addScene(stage, baseScene);

    // Start additional scenes on secondary stages.
    while (sceneSeq.hasNext()) {
      DepanFxSceneConfig sceneInfo = sceneSeq.next();
      Stage sceneStage = new Stage();
      session.addScene(sceneStage, sceneInfo);
    }
  }

  @Autowired
  public DepanFxSession(
      DepanFxDialogRunner dialogRunner, DepanFxWorkspace workspace) {
    this.dialogRunner = dialogRunner;
    this.workspace = workspace;
  }

  public void setOnClose(Closeable onClose) {
    this.onClose = onClose;
  }

  public DepanFxWorkspace getWorkspace() {
    return workspace;
  }

  public Collection<DepanFxSceneController> getScenes() {
    return sceneMap.keySet();
  }

  public Collection<DepanFxSceneConfig> getSceneConfigs() {
    return sceneMap.values();
  }

  public void addScene(Stage stage, DepanFxSceneConfig sceneConfig)
      throws Exception {
    DepanFxSceneController scene = DepanFxSceneController.createDepanScene(
        dialogRunner, sceneConfig.getViewers(), this);
    sceneMap.put(scene, sceneConfig);

    positionScreen(stage, sceneConfig);

    stage.setTitle("DepanFX");
    DepanFxAppIcons.installDepanIcons(stage.getIcons());
    stage.setScene(scene.getScene());
    stage.show();
  }

  public void addScene(DepanFxSceneConfig sceneInfo)
      throws Exception {
    Stage sceneState = new Stage();
    addScene(sceneState, sceneInfo);
  }

  @Override // DepanFxSceneController.SceneOwner
  public void closeScene(DepanFxSceneController scene) {
    sceneMap.remove(scene);
    if (sceneMap.isEmpty()) {
      closeParent();
    }
  }

  @Override // DepanFxSceneController.SceneOwner
  public void saveSession() throws IOException {
    DepanFxSessionSaveDialog.runSaveSessionDialog(dialogRunner);
  }

  private void closeParent() {
    try {
      onClose.close();
    } catch (IOException errIo) {
      // Something better ..
    }
  }

  private void positionScreen(Stage stage, DepanFxSceneConfig sceneConfig) {
    double top = sceneConfig.getTop();
    double left = sceneConfig.getLeft();
    double width = sceneConfig.getWidth();
    double height = sceneConfig.getHeight();

    if (width < 100 || height < 100) {
      return;
    }

    stage.setX(top);
    stage.setY(left);
    stage.setWidth(width);
    stage.setHeight(height);
  }

  private void setCurrentProject(String currentProjectName) {
    workspace.getProjectList().stream()
        .filter(p -> p.getMemberName().equals(currentProjectName))
        .findFirst()
        .ifPresent(workspace::setCurrentProject);
  }
}
