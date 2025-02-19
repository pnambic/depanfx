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
package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuContribution;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuItems;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DepanFxNodeViewPanelConfiguration {

  @Bean
  public DepanFxSceneMenuContribution nodeViewEditSelectAll() {

    return new DepanFxSceneMenuContribution.Action<DepanFxNodeViewPanel>(
        DepanFxSceneMenuItems.SELECTION_ALL_ITEM,
        DepanFxNodeViewPanel.class,
        v -> v.doSelectAllAction());
  }

  @Bean
  public DepanFxSceneMenuContribution nodeViewEditClearSelection() {

    return new DepanFxSceneMenuContribution.Action<DepanFxNodeViewPanel>(
        DepanFxSceneMenuItems.SELECTION_NONE_ITEM,
        DepanFxNodeViewPanel.class,
        v -> v.doClearSelectionAction());
  }

  @Bean
  public DepanFxSceneMenuContribution nodeViewEditInvertSection() {

    return new DepanFxSceneMenuContribution.Action<DepanFxNodeViewPanel>(
        DepanFxSceneMenuItems.SELECTION_INVERT_ITEM,
        DepanFxNodeViewPanel.class,
        v -> v.doInvertSelectionAction());
  }
}
