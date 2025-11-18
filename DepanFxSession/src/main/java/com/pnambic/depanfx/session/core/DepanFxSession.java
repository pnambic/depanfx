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
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneStarterRegistry;
import com.pnambic.depanfx.session.gui.DepanFxSessionSaveDialog;
import com.pnambic.depanfx.session.plugins.DepanFxSceneViewerRegistry;
import com.pnambic.depanfx.session.tooldata.DepanFxSceneData;
import com.pnambic.depanfx.session.viewdata.DepanFxBaseViewerData;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javafx.stage.Stage;

/**
 * Top of the document for the user's session.  All persisted data (projects)
 * and UX elements (JavaFX scenes) are contained here.
 */
@Component
public class DepanFxSession implements DepanFxSceneController.SceneOwner {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxSession.class);

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxSceneViewerRegistry viewerRegistry;

  private final DepanFxSceneStarterRegistry starterRegistry;

  // Unlikely to be a big number of windows;
  private record StageInfo(Stage stage, DepanFxSceneService sceneSrvc) {
  }

  private final List<StageInfo> stages = new ArrayList<>(2);

  private Closeable onClose;

  private Stage sessionStage;

  private Path sessionPath;

  private DepanFxSessionConfig sessionConfig;

  private DepanFxSessionConfig activeConfig;

  @Autowired
  public DepanFxSession(
      DepanFxDialogRunner dialogRunner,
      DepanFxWorkspace workspace,
      DepanFxSceneViewerRegistry viewerRegistry,
      DepanFxSceneStarterRegistry starterRegistry) {
    this.dialogRunner = dialogRunner;
    this.workspace = workspace;
    this.viewerRegistry = viewerRegistry;
    this.starterRegistry = starterRegistry;
  }

  @Override // SceneOwner
  public boolean isStage(Stage sessionStage) {
    return this.sessionStage.equals(sessionStage);
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
  }

  public DepanFxWorkspace getWorkspace() {
    return workspace;
  }

  public Stream<DepanFxSceneService> streamScenes() {
    return stages.stream().map(i -> i.sceneSrvc());
  }

  /**
   * Start the UX session using a {@link Stage} that is provided by
   * the framework.  If additional {@link Stage}s are needed for additional
   * scenes, they are created as needed.
   */
  public void startSession(Stage stage) throws Exception {
    sessionStage = stage;

    if (sessionConfig.getSceneConfigs() == null) {
      startStarterSession(stage);
      return;
    }

    initConfigSession();
  }

  public void resetSessionConfig(
      Path sessionPath, DepanFxSessionConfig sessionConfig) {
    this.sessionPath = sessionPath;
    this.sessionConfig = sessionConfig;
  }

  public void activateConfig() throws Exception {
    if (sessionStage == null) {
      return;
    }
    // Some other launcher beat us to the punch

    if (activeConfig == sessionConfig) {
      return;
    }
    clearSession();

    initConfigSession();
  }


  @Override // DepanFxSceneController.SceneOwner
  public void saveSession() throws IOException {
    DepanFxSessionSaveDialog.runSaveSessionDialog(dialogRunner);
  }

  public void stopSession() {
    streamScenes()
        .forEach(c -> c.closeScene());
    activeConfig = null;
  }

  public void addScene(DepanFxSceneData sceneInfo)
      throws Exception {
    Stage sceneStage = new Stage();
    addScene(sceneStage, sceneInfo);
  }

  @Override // DepanFxSceneController.SceneOwner
  public void closeScene(DepanFxSceneService sceneSrvc) {
    sceneSrvc.closeScene();
    stages.removeIf(i -> i.sceneSrvc.equals(sceneSrvc));
    if (stages.isEmpty()) {
      closeParent();
    }
  }

  private void closeParent() {
    try {
      // Shutting down the application context that started this session.
      onClose.close();
    } catch (Exception errAny) {
      LOG.warn("Trouble closing down the session", errAny);
    }
  }

  /**
   * Close all scenes except the primary stage,
   * and close all viewers in the primary stage.
   */
  private void clearSession() {
    LOG.info("Clearing session with {} stages", stages.size());
    streamScenes()
        // Don't close the primary stage
        .filter(s -> !s.isStage(sessionStage))
        .forEach(c -> c.closeScene());

    // clear all viewers on the primary scene
    streamScenes()
        .filter(s -> s.isStage(sessionStage))
        .forEach(s -> s.streamViewers().close());

    stages.clear();
  }

  private void initConfigSession() throws Exception {
    LOG.info("Initializing config {}",
        sessionPath == null ? "blank" : sessionPath.toString());

    activeConfig = sessionConfig;
    setCurrentProject(sessionConfig.getCurrentProjectName());

    Iterator<DepanFxSceneData> sceneSeq =
        sessionConfig.getSceneConfigs().iterator();

    if (!sceneSeq.hasNext()) {
      addScene(sessionStage, DepanFxSceneData.EMPTY_SESSION_SCENE);
      return;
    }

    // Start the first scene on the initial stage.
    DepanFxSceneData baseScene = sceneSeq.next();
    addScene(sessionStage, baseScene);

    // Start additional scenes on secondary stages.
    while (sceneSeq.hasNext()) {
      DepanFxSceneData sceneInfo = sceneSeq.next();
      addScene(sceneInfo);
    }
  }

  private void addScene(Stage stage, DepanFxSceneData sceneInfo)
      throws Exception {

    DepanFxSceneController scene =
        DepanFxSceneController.createDepanScene(dialogRunner, this);

    positionScreen(stage, sceneInfo);
    DepanFxSceneService sceneSrvc = installScene(stage, scene);

    List<DepanFxBaseViewerData> viewers = sceneInfo.getViewers();
    if (viewers != null ) {
      viewers.stream()
          .flatMap(d -> toSceneViewer(sceneSrvc, d).stream())
          .forEach(sceneSrvc::addViewer);
      return;
    }

    // If viewers are null (not empty), use the starter views.
    starterRegistry.getStarterViews(sceneSrvc)
        .forEach(sceneSrvc::addViewer);
  }

  private void startStarterSession(Stage stage) throws Exception {
    LOG.info("Launching starter session");
    DepanFxSceneController scene =
        DepanFxSceneController.createDepanScene(dialogRunner, this);

    DepanFxSceneService sceneSrvc = installScene(stage, scene);

    starterRegistry.getStarterViews(sceneSrvc)
        .forEach(sceneSrvc::addViewer);
  }

  private DepanFxSceneService installScene(
      Stage stage, DepanFxSceneController scene)
      throws Exception {

    stage.setTitle("DepanFX");
    DepanFxAppIcons.installDepanIcons(stage.getIcons());
    stage.setScene(scene.getScene());
    stage.show();

    DepanFxSceneService result = scene.getSceneService();

    stages.add(new StageInfo(stage, result));
    return result;
  }

  /**
   * Handle any number of failures restoring the viewer.
   *
   * Many times, it's a missing value due to a save Scratch document.
   */
  private Optional<DepanFxSceneViewer> toSceneViewer(
      DepanFxSceneService sceneSrvc, DepanFxBaseViewerData viewerInfo) {

    try {
      return viewerRegistry.buildViewer(sceneSrvc, viewerInfo);
    } catch (Exception errAny) {
      LOG.warn("Unable to build viewer {}", viewerInfo.getClass().getName());
      LOG.debug("Unable to build viewer {}",
          viewerInfo.getClass().getName(), errAny);
    }
    return Optional.empty();
  }

  private void positionScreen(Stage stage, DepanFxSceneData sceneInfo) {
    double top = sceneInfo.getTop();
    double left = sceneInfo.getLeft();
    double width = sceneInfo.getWidth();
    double height = sceneInfo.getHeight();

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
