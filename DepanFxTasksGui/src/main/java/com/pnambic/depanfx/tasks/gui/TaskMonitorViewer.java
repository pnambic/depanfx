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

import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;

import net.rgielen.fxweaver.core.FxControllerAndView;

import javafx.scene.Node;
import javafx.scene.control.Tab;

/**
 * View panel that provides an overview of background task execution.
 * Active tasks are displayed as a hierarchy and the bottom section lists
 * completed work.
 *
 * The task toolbar gives quick access to clearing completed entries and
 * opening task results when available.
 */
public class TaskMonitorViewer implements DepanFxSceneViewer {

  public static final String TAB_TITLE = "Tasks";

  private final DepanFxDialogRunner dialogRunner;

  private Tab sceneTab;

  private TaskMonitorController controller;

  public TaskMonitorViewer(DepanFxDialogRunner dialogRunner) {
    this.dialogRunner = dialogRunner;
  }

  @Override
  public Tab getSceneTab(DepanFxSceneService sceneSrvc) {
    if (sceneTab != null) {
      return sceneTab;
    }

    FxControllerAndView<TaskMonitorController, Node> view =
        dialogRunner.weaveFxmlView(TaskMonitorController.class);

    sceneTab = new Tab(TAB_TITLE, view.getView().get());
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
