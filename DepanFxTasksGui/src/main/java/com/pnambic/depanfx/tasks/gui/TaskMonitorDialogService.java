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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pnambic.depanfx.scene.DepanFxDialogRunner;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.stage.Stage;

/**
 * Service that presents a modeless dialog listing active background tasks.
 */
@Service
public class TaskMonitorDialogService {

  public static final int MONITOR_STALL_MS = 300;

  private static final Logger LOG =
      LoggerFactory.getLogger(TaskMonitorDialogService.class);

  private final DepanFxDialogRunner dialogRunner;

  private final TaskMonitorService monitorService;

  private Stage dialogStage = null;

  @Autowired
  public TaskMonitorDialogService(
      DepanFxDialogRunner dialogRunner,
      TaskMonitorService monitorService) {
    this.dialogRunner = dialogRunner;
    this.monitorService = monitorService;

    monitorService.addActiveListener(this::handleActiveTaskChange);
  }

  /**
   * Ensure the dialog is visible to the user.
   */
  public void showTaskMonitor() {
    runOnFxThread(() -> {
      Stage stage = ensureStage();
      if (stage != null) {
        if (!stage.isShowing()) {
          stage.show();
        }
        stage.toFront();
        stage.requestFocus();
      }
    });
  }

  public void showActiveTasks() {
    if (monitorService.hasActiveTask()) {
      showTaskMonitor();
    }
  }

  /**
   * Hide the dialog if it is currently visible.
   */
  public void hideTaskMonitor() {
    runOnFxThread(() -> {
      if (dialogStage != null) {
        dialogStage.hide();
      }
    });
  }

  private Stage ensureStage() {
    if (dialogStage != null) {
      return dialogStage;
    }

    DepanFxDialogRunner.Dialog<TaskMonitorDialog> dialog =
        dialogRunner.createDialogAndParent(TaskMonitorDialog.class);
    Stage stage = dialog.runModeless("Background Tasks");
    stage.setOnHidden(event -> dialogStage = null);
    stage.setOnCloseRequest(event -> dialogStage = null);
    dialogStage = stage;
    return dialogStage;
  }

  private void handleActiveTaskChange(
      ListChangeListener.Change<? extends TaskMonitorItem> change) {
    LOG.debug("Monitor service notified of change");
    runOnFxThread(() -> handleMonitorPopup());
  }

  private void handleMonitorPopup() {
    // Nothing active, hide the monitor window, exit early.
    if (!hasACtiveElseHidden()) {
      LOG.debug("Hid monitor, nothing active");
      return;
    }

    // A brief pause for the task to maybe complete.
    LOG.debug("preparing stall");
    monitorService.getFirstActive()
        .ifPresent(
            i -> monitorService.awaitTaskMs(i.getTask(), MONITOR_STALL_MS));

    // After the pause, confirm no reason to show monitor
    if (!hasACtiveElseHidden()) {
      LOG.info("Hid monitor, nothing active now");
      return;
    }

    // No choice but to show the pask monitor
    if (dialogStage == null || !dialogStage.isShowing()) {
      LOG.debug("Active task, show task monitor");
      showTaskMonitor();
    }
  }

  /**
   * Hide the monitor if there is no active task
   * and the monitor currently showing.
   *
   * @return {@code true} if there is an active task.
   */
  private boolean hasACtiveElseHidden() {
    if (monitorService.hasActiveTask()) {
      return true;
    }

    if (dialogStage != null && dialogStage.isShowing()) {
      dialogStage.hide();
    }
    return false;
  }

  private void runOnFxThread(Runnable action) {
    if (Platform.isFxApplicationThread()) {
      action.run();
    } else {
      Platform.runLater(action);
    }
  }
}
