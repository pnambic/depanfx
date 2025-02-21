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
package com.pnambic.depanfx.scene;

import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.control.Tab;

public class DepanFxWelcomeViewer implements DepanFxSceneViewer {

  public static final String WELCOME_TAB = "Welcome";

  private final DepanFxDialogRunner dialogRunner;

  public DepanFxWelcomeViewer(DepanFxDialogRunner dialogRunner) {
    this.dialogRunner = dialogRunner;
  }

  @Override // DepanFxSceneViewer
  public Tab getSceneTab(DepanFxSceneService sceneSrvc) {
    Optional<Node> welcomePanel =
        dialogRunner.weaveFxmlView(DepanFxWelcomePanel.class).getView();
    return new Tab(DepanFxWelcomeViewer.WELCOME_TAB, welcomePanel.get());
  }

  @Override // DepanFxSceneViewer
  public void closeTab() {
    // Just JavaFX resources.
  }
}
