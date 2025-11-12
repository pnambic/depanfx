package com.pnambic.depanfx.tasks.gui;

import com.pnambic.depanfx.scene.DepanFxDialogRunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

/**
 * Service that presents a modeless dialog listing active background tasks.
 */
@Service
public class TaskMonitorDialogService {

  private final DepanFxDialogRunner dialogRunner;

  private final ObservableList<TaskMonitorItem> activeTasks;

  private Stage dialogStage;

  @Autowired
  public TaskMonitorDialogService(
      DepanFxDialogRunner dialogRunner,
      TaskMonitorService monitorService) {
    this.dialogRunner = dialogRunner;
    this.activeTasks = monitorService.getActiveTasks();
    this.activeTasks.addListener(this::handleActiveTaskChange);
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

    DepanFxDialogRunner.Dialog<TaskMonitorDialogController> dialog =
        dialogRunner.createDialogAndParent(TaskMonitorDialogController.class);
    Stage stage = dialog.runModeless("Background Tasks");
    stage.setOnHidden(event -> dialogStage = null);
    stage.setOnCloseRequest(event -> dialogStage = null);
    dialogStage = stage;
    return dialogStage;
  }

  private void handleActiveTaskChange(
      ListChangeListener.Change<? extends TaskMonitorItem> change) {
    runOnFxThread(() -> {
      if (activeTasks.isEmpty()) {
        if (dialogStage != null && dialogStage.isShowing()) {
          dialogStage.hide();
        }
      } else if (dialogStage == null || !dialogStage.isShowing()) {
        showTaskMonitor();
      }
    });
  }

  private void runOnFxThread(Runnable action) {
    if (Platform.isFxApplicationThread()) {
      action.run();
    } else {
      Platform.runLater(action);
    }
  }
}
