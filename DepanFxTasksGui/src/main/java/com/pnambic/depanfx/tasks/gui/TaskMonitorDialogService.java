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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.stage.Stage;

/**
 * Service that presents a modeless dialog listing active background tasks.
 */
@Service
public class TaskMonitorDialogService implements DisposableBean {

  public static final int MONITOR_STALL_MS = 1500;

  private static final Logger LOG =
      LoggerFactory.getLogger(TaskMonitorDialogService.class);

  private final DepanFxDialogRunner dialogRunner;

  private final TaskMonitorService monitorService;

  private Stage dialogStage = null;

  // Delayed pop-up task monitor
  private final ScheduledExecutorService dialogDelayExecutor =
      Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "task-monitor-dialog-delay");
        thread.setDaemon(true);
        return thread;
      });

  private ScheduledFuture<?> pendingPopup;

  private final Object pendingLock = new Object();

  @Autowired
  public TaskMonitorDialogService(
      DepanFxDialogRunner dialogRunner,
      TaskMonitorService monitorService) {
    this.dialogRunner = dialogRunner;
    this.monitorService = monitorService;

    monitorService.addActiveListener(this::handleActiveTaskChange);
  }

  @Override
  public void destroy() {
    dialogDelayExecutor.shutdownNow();
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

    // Nothing active, hide the monitor window, exit early.
    if (!hasActiveElseHidden()) {
      LOG.debug("Hid monitor, nothing active");
      return;
    }
    if (dialogStage != null && dialogStage.isShowing()) {
      LOG.debug("Dialog already visible, no delay scheduling");
      return;
    }

    scheduleDelayedPopup();
  }

  /**
   * Hide the monitor if there is no active task
   * and the monitor currently showing.
   *
   * @return {@code true} if there is an active task.
   */
  private boolean hasActiveElseHidden() {
    if (monitorService.hasActiveTask()) {
      return true;
    }

    cancelPendingPopup();
    if (dialogStage != null && dialogStage.isShowing()) {
      runOnFxThread(() -> dialogStage.hide());
    }
    return false;
  }

  private void scheduleDelayedPopup() {
    synchronized (pendingLock ) {
      if (pendingPopup != null && !pendingPopup.isDone()) {
        return;
      }
      pendingPopup = dialogDelayExecutor.schedule(
          this::runDelayedPopup,
          MONITOR_STALL_MS,
          TimeUnit.MILLISECONDS);
    }
  }

  private void runDelayedPopup() {
    clearPendingPopup();
    if (!hasActiveElseHidden()) {
      return;
    }
    LOG.debug("Active task after delay, show dialog");
    runOnFxThread(() -> showTaskMonitor());
  }

  private void cancelPendingPopup() {
    synchronized (pendingLock) {
      if (pendingPopup != null) {
        pendingPopup.cancel(true);
        pendingPopup = null;
      }
    }
  }

  private void clearPendingPopup() {
    synchronized (pendingLock) {
      pendingPopup = null;
    }
  }

  private void runOnFxThread(Runnable action) {
    if (Platform.isFxApplicationThread()) {
      action.run();
    } else {
      Platform.runLater(action);
    }
  }
}
