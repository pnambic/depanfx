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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
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

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxSession.class);

  public static DepanFxSceneConfig EMPTY_SESSION_SCENE =
      new DepanFxSceneConfig("Empty Session", "Empty",
          -1.0d, -1.0d, -1.0d, -1.0d,
          Collections.emptyList());

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final Map<DepanFxSceneController, DepanFxSceneConfig> sceneMap =
      new HashMap<>();

  private Closeable onClose;

  private Path sessionPath;

  private DepanFxSessionConfig sessionConfig;

  public static void startSession(Stage stage, DepanFxSession session)
      throws Exception {
    session.startSession(stage);
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

  public Path getSessionPath() {
    return sessionPath;
  }

  public void setSessionPath(Path sessionPath) {
    this.sessionPath = sessionPath;
  }

  public void setSessionConfig(DepanFxSessionConfig sessionConfig) {
    this.sessionConfig = sessionConfig;
    setCurrentProject(sessionConfig.getCurrentProjectName());

    // Populate sceneMap when session is started.
    sceneMap.clear();
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

  /**
   * Start the UX session using a {@link Stage} that is provided by
   * the framework.  If additional {@link Stage}s are needed for additional
   * scenes, they are created as needed.
   */
  public void startSession(Stage stage) throws Exception {

    Iterator<DepanFxSceneConfig> sceneSeq =
        sessionConfig.getSceneConfigs().iterator();

    if (!sceneSeq.hasNext()) {
      addScene(stage, EMPTY_SESSION_SCENE);
      return;
    }

    // Start the first scene on the initial stage.
    DepanFxSceneConfig baseScene = sceneSeq.next();
    addScene(stage, baseScene);

    // Start additional scenes on secondary stages.
    while (sceneSeq.hasNext()) {
      DepanFxSceneConfig sceneInfo = sceneSeq.next();
      addScene(sceneInfo);
    }
  }

  public void stopSession() {
    sceneMap.keySet().forEach(c -> c.closeScene());
  }

  public void addScene(DepanFxSceneConfig sceneInfo)
      throws Exception {
    Stage sceneState = new Stage();
    addScene(sceneState, sceneInfo);
  }

  @Override // DepanFxSceneController.SceneOwner
  public void closeScene(DepanFxSceneController scene) {
    scene.closeScene();
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
      // Shutting down the application context that started this session.
      onClose.close();
    } catch (Exception errAny) {
      LOG.warn("Trouble closing down the session", errAny);
    }
  }

  private void addScene(Stage stage, DepanFxSceneConfig sceneConfig)
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
