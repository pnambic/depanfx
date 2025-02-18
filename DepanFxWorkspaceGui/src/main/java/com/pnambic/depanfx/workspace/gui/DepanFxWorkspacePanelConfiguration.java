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

package com.pnambic.depanfx.workspace.gui;

import com.pnambic.depanfx.perspective.plugins.DepanFxResourceMenuRegistry;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneStarterContribution;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneViewPanelRegistry;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Workspace panel installation for scene and UX menus.
 */
@Configuration
public class DepanFxWorkspacePanelConfiguration {

  @Bean
  DepanFxWorkspaceSceneStarterContribution workspaceSceneStarterContribution(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxResourceMenuRegistry rsrcMenuRegistry) {
    return new DepanFxWorkspaceSceneStarterContribution(
        workspace, dialogRunner, rsrcMenuRegistry);
  }

  @Bean
  DepanFxSceneViewPanelRegistry.Contribution workspaceViewPanelsContribution(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxResourceMenuRegistry rsrcMenuRegistry) {
    return new WorkspaceViewPanelsContribution(
        workspace, dialogRunner, rsrcMenuRegistry);
  }

  private class DepanFxWorkspaceSceneStarterContribution
      implements DepanFxSceneStarterContribution {

    private final DepanFxWorkspace workspace;

    private final DepanFxDialogRunner dialogRunner;

    private final DepanFxResourceMenuRegistry rsrcMenuRegistry;

    @Autowired
    public DepanFxWorkspaceSceneStarterContribution(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxResourceMenuRegistry rsrcMenuRegistry) {
      this.workspace = workspace;
      this.dialogRunner = dialogRunner;
      this.rsrcMenuRegistry = rsrcMenuRegistry;
    }

    @Override
    public String getLabel() {
      return DepanFxWorkspaceViewer.WORKSPACE_TAB;
    }

    @Override
    public DepanFxSceneViewer getSceneViewer() {

      return new DepanFxWorkspaceViewer(
          workspace, dialogRunner, rsrcMenuRegistry, getLabel());
    }
  }

  private static class WorkspaceViewPanelsContribution
      implements DepanFxSceneViewPanelRegistry.Contribution {

    private final DepanFxWorkspace workspace;

    private final DepanFxDialogRunner dialogRunner;

    private final DepanFxResourceMenuRegistry rsrcMenuRegistry;

    public WorkspaceViewPanelsContribution(
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        DepanFxResourceMenuRegistry rsrcMenuRegistry) {
      this.workspace = workspace;
      this.dialogRunner = dialogRunner;
      this.rsrcMenuRegistry = rsrcMenuRegistry;
    }

    @Override
    public String getLabel() {
      return DepanFxWorkspaceViewer.WORKSPACE_TAB;
    }

    @Override
    public String getOrder() {
      return DepanFxWorkspaceViewer.WORKSPACE_TAB;
    }

    @Override
    public DepanFxSceneViewer getSceneViewer() {

      return new DepanFxWorkspaceViewer(
          workspace, dialogRunner, rsrcMenuRegistry, getLabel());
    }
  }
}
