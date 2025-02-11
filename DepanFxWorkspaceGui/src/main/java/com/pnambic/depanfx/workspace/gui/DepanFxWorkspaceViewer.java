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
package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.perspective.plugins.DepanFxResourceMenuRegistry;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneController;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import java.util.Optional;

import javafx.scene.control.Tab;

public class DepanFxWorkspaceViewer implements DepanFxSceneViewer {

  public static final String WORKSPACE_TAB = "Workspace";

  private final DepanFxWorkspace workspace;

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxResourceMenuRegistry rsrcMenuRegistry;

  private final String tabLabel;

  private DepanFxProjectListViewer workspaceViewer;

  public DepanFxWorkspaceViewer(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxResourceMenuRegistry rsrcMenuRegistry,
      String tabLabel) {
    this.workspace = workspace;
    this.dialogRunner = dialogRunner;
    this.rsrcMenuRegistry = rsrcMenuRegistry;
    this.tabLabel = tabLabel;
  }

  @Override
  public Tab getSceneTab(DepanFxSceneController scene) {
    workspaceViewer = new DepanFxProjectListViewer(
          workspace, dialogRunner, rsrcMenuRegistry, scene);
    Tab workspaceTab = workspaceViewer.createWorkspaceTab(tabLabel);

    return workspaceTab;
  }

  @Override // DepanFxSceneViewer
  public void closeTab() {
    // Just JavaFX resources.
  }

  public Optional<DepanFxWorkspaceMember> getCurrentSelection() {
    return workspaceViewer.getCurrentSelection();
  }
}
