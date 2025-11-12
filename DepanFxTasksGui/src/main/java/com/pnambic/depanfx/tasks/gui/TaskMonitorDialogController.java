package com.pnambic.depanfx.tasks.gui;

import com.pnambic.depanfx.scene.DepanFxFxmlDialog;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import com.pnambic.depanfx.tasks.TaskStatus;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller for the background task monitor dialog.
 */
@DepanFxFxmlDialog
@FxmlView("task-monitor-dialog.fxml")
public class TaskMonitorDialogController {

  private final TaskMonitorService monitorService;

  @FXML
  private ListView<TaskMonitorItem> activeTasksView;

  @Autowired
  public TaskMonitorDialogController(TaskMonitorService monitorService) {
    this.monitorService = monitorService;
  }

  @FXML
  public void initialize() {
    activeTasksView.setItems(monitorService.getActiveTasks());
    activeTasksView.setCellFactory(createCellFactory());

    Label placeholder = new Label("No active tasks.");
    placeholder.getStyleClass().add("placeholder-text");
    activeTasksView.setPlaceholder(placeholder);
  }

  private Callback<ListView<TaskMonitorItem>, ListCell<TaskMonitorItem>> createCellFactory() {
    return list -> new TaskMonitorCell(monitorService);
  }

  private static class TaskMonitorCell extends ListCell<TaskMonitorItem> {

    private final TaskMonitorService monitorService;

    private final Label titleLabel = new Label();

    private final Label statusLabel = new Label();

    private final ProgressBar progressBar = new ProgressBar();

    private final Label messageLabel = new Label();

    private final Button cancelButton = new Button("Cancel");

    private final VBox container = new VBox(6.0);

    private TaskMonitorItem boundItem;

    TaskMonitorCell(TaskMonitorService monitorService) {
      this.monitorService = monitorService;

      titleLabel.getStyleClass().add("task-monitor-title");
      statusLabel.getStyleClass().add("task-monitor-status");
      messageLabel.getStyleClass().add("task-monitor-message");

      progressBar.setPrefWidth(Double.MAX_VALUE);

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
