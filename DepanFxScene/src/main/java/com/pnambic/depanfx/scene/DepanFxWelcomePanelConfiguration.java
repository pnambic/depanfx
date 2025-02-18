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
package com.pnambic.depanfx.scene;

import com.pnambic.depanfx.scene.plugins.DepanFxSceneStarterContribution;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneViewPanelRegistry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Welcome panel installation for scene and UX menus.
 */
@Configuration
public class DepanFxWelcomePanelConfiguration {

  @Bean
  public DepanFxSceneStarterContribution welcomeSceneStarterContribution(
      DepanFxDialogRunner dialogRunner) {
    return new WelcomeSceneStarterContribution(dialogRunner);
  }

  @Bean
  public DepanFxSceneViewPanelRegistry.Contribution
  welcomeViewPanelsContribution(DepanFxDialogRunner dialogRunner) {
    return new WelcomeViewPanelsContribution(dialogRunner);
  }

  private class WelcomeSceneStarterContribution
      implements DepanFxSceneStarterContribution {

    private final DepanFxDialogRunner dialogRunner;

    public WelcomeSceneStarterContribution(DepanFxDialogRunner dialogRunner) {
      this.dialogRunner = dialogRunner;
    }

    @Override
    public String getLabel() {
      return DepanFxWelcomeViewer.WELCOME_TAB;
    }

    @Override
    public DepanFxSceneViewer getSceneViewer() {
      return new DepanFxWelcomeViewer(dialogRunner);
    }
  }

  private class WelcomeViewPanelsContribution
      implements DepanFxSceneViewPanelRegistry.Contribution {

    private final DepanFxDialogRunner dialogRunner;

    public WelcomeViewPanelsContribution(DepanFxDialogRunner dialogRunner) {
      this.dialogRunner = dialogRunner;
    }

    @Override
    public String getLabel() {
      return DepanFxWelcomeViewer.WELCOME_TAB;
    }

    @Override
    public String getOrder() {
      return DepanFxWelcomeViewer.WELCOME_TAB;
    }

    @Override
    public DepanFxSceneViewer getSceneViewer() {
      return new DepanFxWelcomeViewer(dialogRunner);
    }
  }
}
