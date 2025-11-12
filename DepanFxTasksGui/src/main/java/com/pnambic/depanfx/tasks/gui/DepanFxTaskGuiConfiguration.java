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
import com.pnambic.depanfx.scene.plugins.DepanFxSceneMenuContribution;
import com.pnambic.depanfx.scene.plugins.DepanFxSceneViewPanelRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javafx.event.ActionEvent;

@Configuration
public class DepanFxTaskGuiConfiguration {

  @Bean
  public DepanFxSceneMenuContribution viewActiveTasksItem(
      TaskMonitorDialogService dialogService) {
    return new TaskMonitorSceneMenuContribution(dialogService);
  }

  @Bean
  public DepanFxSceneViewPanelRegistry.Contribution taskMonitorPanel(
      TaskMonitorService monitorService) {
    return new TaskMonitorViewPanelContribution(monitorService);
  }

  private class TaskMonitorSceneMenuContribution
      extends DepanFxSceneMenuContribution.Simple {

    public static final String MENU_ITEM_KEY = "viewActiveTasksItem";

    private final TaskMonitorDialogService dialogService;

    @Autowired
    public TaskMonitorSceneMenuContribution(
        TaskMonitorDialogService dialogService) {
      super(MENU_ITEM_KEY);
      this.dialogService = dialogService;
    }

    @Override
    public boolean forViewer(DepanFxSceneViewer viewer) {
      return true;
    }

    @Override
    public void handleEvent(DepanFxSceneService sceneSrvc, ActionEvent event) {
      dialogService.showTaskMonitor();
    }
  }

  private static class TaskMonitorViewPanelContribution
      implements DepanFxSceneViewPanelRegistry.Contribution {

    private final TaskMonitorService monitorService;

    public TaskMonitorViewPanelContribution(TaskMonitorService monitorService) {
      this.monitorService = monitorService;
    }

    @Override
    public String getLabel() {
      return TaskMonitorSceneViewer.TAB_TITLE;
    }

    @Override
    public String getOrderKey() {
      return TaskMonitorSceneViewer.TAB_TITLE;
    }

    @Override
    public DepanFxSceneViewer getSceneViewer(DepanFxSceneService sceneSrvc) {
      return new TaskMonitorSceneViewer(monitorService, sceneSrvc);
    }
  }
}
