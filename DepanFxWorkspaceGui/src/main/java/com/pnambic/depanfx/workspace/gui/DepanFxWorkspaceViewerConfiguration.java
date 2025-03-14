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
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
import com.pnambic.depanfx.scene.DepanFxSceneService;
import com.pnambic.depanfx.scene.DepanFxSceneViewer;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneStarterRegistry;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneViewPanelRegistry;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * Workspace panel installation for scene and UX menus.
 */
@Configuration
public class DepanFxWorkspaceViewerConfiguration {

  @Bean
  DepanFxWorkspaceSceneStarterContribution workspaceSceneStarterContribution(
      DepanFxWorkspace workspace,
      DepanFxResourceRegistry rsrcRegistry,
      DepanFxResourceMenuRegistry rsrcMenuRegistry) {
    return new DepanFxWorkspaceSceneStarterContribution(
        workspace, rsrcRegistry, rsrcMenuRegistry);
  }

  @Bean
  DepanFxSceneViewPanelRegistry.Contribution workspaceViewPanelsContribution(
      DepanFxWorkspace workspace,
      DepanFxResourceRegistry rsrcRegistry,
      DepanFxResourceMenuRegistry rsrcMenuRegistry) {
    return new WorkspaceViewPanelsContribution(
        workspace, rsrcRegistry, rsrcMenuRegistry);
  }

  private class DepanFxWorkspaceSceneStarterContribution
      implements DepanFxSceneStarterRegistry.Contribution {

    private final DepanFxWorkspace workspace;

    private final DepanFxResourceRegistry rsrcRegistry;

    private final DepanFxResourceMenuRegistry rsrcMenuRegistry;

    public DepanFxWorkspaceSceneStarterContribution(
        DepanFxWorkspace workspace,
        DepanFxResourceRegistry rsrcRegistry,
        DepanFxResourceMenuRegistry rsrcMenuRegistry) {
      this.workspace = workspace;
      this.rsrcRegistry = rsrcRegistry;
      this.rsrcMenuRegistry = rsrcMenuRegistry;
    }

    @Override
    public String getLabel() {
      return DepanFxWorkspaceViewer.WORKSPACE_TAB;
    }

    @Override
    public DepanFxSceneViewer getSceneViewer(DepanFxSceneService sceneSrvc) {

      return new DepanFxWorkspaceViewer(
          workspace,
          rsrcRegistry, rsrcMenuRegistry, Collections.emptyMap(),
          getLabel());
    }
  }

  private static class WorkspaceViewPanelsContribution
      implements DepanFxSceneViewPanelRegistry.Contribution {

    private final DepanFxWorkspace workspace;

    private final DepanFxResourceRegistry rsrcRegistry;

    private final DepanFxResourceMenuRegistry rsrcMenuRegistry;

    public WorkspaceViewPanelsContribution(
        DepanFxWorkspace workspace,
        DepanFxResourceRegistry rsrcRegistry,
        DepanFxResourceMenuRegistry rsrcMenuRegistry) {
      this.workspace = workspace;
      this.rsrcMenuRegistry = rsrcMenuRegistry;
      this.rsrcRegistry = rsrcRegistry;
    }

    @Override
    public String getLabel() {
      return DepanFxWorkspaceViewer.WORKSPACE_TAB;
    }

    @Override
    public String getOrderKey() {
      return DepanFxWorkspaceViewer.WORKSPACE_TAB;
    }

    @Override
    public DepanFxSceneViewer getSceneViewer(DepanFxSceneService screenSrvc) {

      return new DepanFxWorkspaceViewer(
          workspace,
          rsrcRegistry, rsrcMenuRegistry, Collections.emptyMap(),
          getLabel());
    }
  }
}
