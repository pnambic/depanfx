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

import com.pnambic.depanfx.scene.plugins.DepanFxSceneStarterRegistry;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneViewPanelRegistry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Welcome panel installation for scene and UX menus.
 */
@Configuration
public class DepanFxWelcomePanelConfiguration {

  @Bean
  public DepanFxSceneStarterRegistry.Contribution
  welcomeSceneStarterContribution() {
    return new WelcomeSceneStarterContribution();
  }

  @Bean
  public DepanFxSceneViewPanelRegistry.Contribution
  welcomeViewPanelsContribution() {
    return new WelcomeViewPanelsContribution();
  }

  private class WelcomeSceneStarterContribution
      implements DepanFxSceneStarterRegistry.Contribution {

    @Override
    public String getLabel() {
      return DepanFxWelcomeViewer.WELCOME_TAB;
    }

    @Override
    public DepanFxSceneViewer getSceneViewer(DepanFxSceneService sceneSrvc) {
      return new DepanFxWelcomeViewer(sceneSrvc.getDialogRunner());
    }
  }

  private class WelcomeViewPanelsContribution
      implements DepanFxSceneViewPanelRegistry.Contribution {

    @Override
    public String getLabel() {
      return DepanFxWelcomeViewer.WELCOME_TAB;
    }

    @Override
    public String getOrderKey() {
      return DepanFxWelcomeViewer.WELCOME_TAB;
    }

    @Override
    public DepanFxSceneViewer getSceneViewer(DepanFxSceneService sceneSrvc) {
      return new DepanFxWelcomeViewer(sceneSrvc.getDialogRunner());
    }
  }
}
