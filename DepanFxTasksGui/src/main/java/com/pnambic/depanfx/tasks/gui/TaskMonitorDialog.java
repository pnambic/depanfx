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

import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.tasks.TaskStatus;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Controller for the background task monitor dialog.
 */
@DepanFxFxmlDialog
@FxmlView("task-monitor-dialog.fxml")
public class TaskMonitorDialog {

  private final TaskMonitorService monitorService;

  @FXML
  private ListView<TaskMonitorItem> activeTasksView;

  @Autowired
  public TaskMonitorDialog(TaskMonitorService monitorService) {
    this.monitorService = monitorService;
  }

  @FXML
  public void initialize() {
    activeTasksView.setItems(monitorService.getActiveTasks());
    activeTasksView.setCellFactory(list -> new TaskMonitorCell(monitorService));

    Label placeholder = new Label("No active tasks.");
    placeholder.getStyleClass().add("placeholder-text");
    activeTasksView.setPlaceholder(placeholder);
  }

  private static class TaskMonitorCell extends ListCell<TaskMonitorItem> {

    private final TaskMonitorService monitorService;

    private final Label titleLabel = new Label();

    private final Label statusLabel = new Label();

    private final ProgressBar progressBar = new ProgressBar();

    private final Text messageLabel = new Text();

    private final Button cancelButton = new Button("Cancel");

    private final VBox container = new VBox(6.0);

    private TaskMonitorItem boundItem;

    TaskMonitorCell(TaskMonitorService monitorService) {
      this.monitorService = monitorService;

      titleLabel.getStyleClass().add("task-monitor-title");
      statusLabel.getStyleClass().add("task-monitor-status");
      messageLabel.getStyleClass().add("task-monitor-message");
      messageLabel.setWrappingWidth(500);

      Region spacer = new Region();
      HBox.setHgrow(spacer, Priority.ALWAYS);
      HBox header = new HBox(8.0, titleLabel, spacer, statusLabel, cancelButton);
      header.setFillHeight(false);

      container.getChildren().addAll(header, progressBar, messageLabel);
      container.getStyleClass().add("task-monitor-cell");
    }

    @Override
    protected void updateItem(TaskMonitorItem item, boolean empty) {
      super.updateItem(item, empty);

      if (boundItem != null) {
        unbind(boundItem);
      }

      if (empty || item == null) {
        boundItem = null;
        setGraphic(null);
        return;
      }

      boundItem = item;
      bind(item);
      setGraphic(container);
    }

    private void bind(TaskMonitorItem item) {
      titleLabel.textProperty().bind(item.titleProperty());
      statusLabel.textProperty().bind(
          Bindings.createStringBinding(
              () -> buildStatusText(item),
              item.statusProperty(),
              item.completedStepsProperty(),
              item.totalStepsProperty(),
              item.cancelRequestedProperty()));
      progressBar.progressProperty().bind(item.progressProperty());
      messageLabel.textProperty().bind(item.messageProperty());
      messageLabel.visibleProperty().bind(
          Bindings.createBooleanBinding(
              () -> !item.messageProperty().get().isBlank(),
              item.messageProperty()));
      messageLabel.managedProperty().bind(messageLabel.visibleProperty());

      cancelButton.disableProperty().bind(
          Bindings.createBooleanBinding(
              () -> !isCancelable(item),
              item.statusProperty(),
              item.cancelRequestedProperty()));
      cancelButton.setOnAction(event -> monitorService.cancelTask(item));
    }

    private void unbind(TaskMonitorItem item) {
      titleLabel.textProperty().unbind();
      statusLabel.textProperty().unbind();
      progressBar.progressProperty().unbind();
      messageLabel.textProperty().unbind();
      messageLabel.visibleProperty().unbind();
      messageLabel.managedProperty().unbind();
      cancelButton.disableProperty().unbind();
      cancelButton.setOnAction(null);
    }

    private boolean isCancelable(TaskMonitorItem item) {
      TaskStatus status = item.statusProperty().get();
      return !status.isTerminal() && !item.cancelRequestedProperty().get();
    }

    private String buildStatusText(TaskMonitorItem item) {
      TaskStatus status = item.statusProperty().get();
      if (item.cancelRequestedProperty().get() && !status.isTerminal()) {
        return "Cancelling";
      }

      int total = item.totalStepsProperty().get();
      int complete = item.completedStepsProperty().get();
      if (total > 0) {
        return String.format("%s (%d/%d)", status, complete, total);
      }
      if (complete > 0) {
        return String.format("%s (%d)", status, complete);
      }
      return status.toString();
    }
  }
}
