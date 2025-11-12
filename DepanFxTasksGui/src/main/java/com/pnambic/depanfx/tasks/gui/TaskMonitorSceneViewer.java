/*
 * Copyright 2025 The Depan Project Authors
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
package com.pnambic.depanfx.tasks.gui;

import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

/**
 * Scene viewer that provides an overview of background task execution. Active
 * tasks are displayed as a hierarchy and the bottom section lists completed
 * work. A small toolbar gives quick access to clearing completed entries and
 * opening task results when available.
 */
public class TaskMonitorSceneViewer implements DepanFxSceneViewer {

  public static final String TAB_TITLE = "Tasks";

  private final TaskMonitorService monitorService;

  private final DepanFxSceneService sceneService;

  private Tab sceneTab;

  private TaskMonitorSceneViewerController controller;

  public TaskMonitorSceneViewer(
      TaskMonitorService monitorService,
      DepanFxSceneService sceneService) {
    this.monitorService = monitorService;
    this.sceneService = sceneService;
  }

  @Override
  public Tab getSceneTab(DepanFxSceneService sceneSrvc) {
    if (sceneTab != null) {
      return sceneTab;
    }

    FXMLLoader loader = new FXMLLoader(
        TaskMonitorSceneViewer.class.getResource("TaskMonitorSceneViewer.fxml"));
    loader.setControllerFactory(param -> {
      if (param == TaskMonitorSceneViewerController.class) {
        return new TaskMonitorSceneViewerController(monitorService, sceneService);
      }
      try {
        return param.getDeclaredConstructor().newInstance();
      } catch (Exception err) {
        throw new IllegalStateException(
            "Unable to instantiate controller: " + param.getName(), err);
      }
    });

    Parent content;
    try {
      content = loader.load();
    } catch (IOException err) {
      throw new IllegalStateException(
          "Unable to load TaskMonitorSceneViewer.fxml", err);
    }

    controller = loader.getController();
    sceneTab = new Tab(TAB_TITLE, content);
    return sceneTab;
  }

  @Override
  public void closeTab() {
    if (controller != null) {
      controller.dispose();
      controller = null;
    }
  }
}
