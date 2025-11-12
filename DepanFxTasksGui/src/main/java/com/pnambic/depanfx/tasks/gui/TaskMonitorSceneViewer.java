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
import com.pnambic.depanfx.tasks.TaskStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Scene viewer that provides an overview of background task execution. Active
 * tasks are displayed as a hierarchy and the bottom section lists completed
 * work. A small toolbar gives quick access to clearing completed entries and
 * opening task results when available.
 */
public class TaskMonitorSceneViewer implements DepanFxSceneViewer {

  public static final String TAB_TITLE = "Tasks";

  private static final Logger LOG =
      LoggerFactory.getLogger(TaskMonitorSceneViewer.class);

  private final TaskMonitorService monitorService;

  private final DepanFxSceneService sceneService;

  private TaskListTreeItem activeRootItem;

  private TaskListTreeItem completedRootItem;

  private TreeTableView<TaskMonitorItem> completedTree;

  private Tab sceneTab;

  public TaskMonitorSceneViewer(
      TaskMonitorService monitorService,
      DepanFxSceneService sceneService) {
    this.monitorService = monitorService;
    this.sceneService = sceneService;
  }

  @Override
  public Tab getSceneTab(DepanFxSceneService sceneSrvc) {
    if (sceneTab != null) {
      return sceneTab;
    }

    BorderPane content = new BorderPane();
    content.setPadding(new Insets(12.0));

    TreeTableView<TaskMonitorItem> activeTree = createTaskTree(
        "No active tasks.");
    activeTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    activeRootItem = new TaskListTreeItem(monitorService.getActiveTasks());
    activeTree.setRoot(activeRootItem);

    completedTree = createTaskTree("No completed tasks.");
    completedTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    completedTree.setRowFactory(this::buildCompletedRowFactory);
    completedRootItem = new TaskListTreeItem(monitorService.getCompletedTasks());
    completedTree.setRoot(completedRootItem);

    Button openResultButton = new Button("Open Result");
    openResultButton.setOnAction(event ->
        openSelectedTaskResult(completedTree));
    openResultButton.disableProperty().bind(buildOpenResultDisabledBinding());

    Button clearCompletedButton = new Button("Clear Completed");
    clearCompletedButton.setOnAction(event -> monitorService.clearCompletedTasks());
    clearCompletedButton.disableProperty().bind(
        Bindings.isEmpty(monitorService.getCompletedTasks()));

    HBox completedToolbar = new HBox(8.0, openResultButton, clearCompletedButton);
    completedToolbar.setAlignment(Pos.CENTER_RIGHT);

    VBox activeSection = new VBox(8.0,
        new Label("Active Tasks"),
        activeTree);
    VBox.setVgrow(activeTree, Priority.ALWAYS);

    VBox completedSection = new VBox(8.0,
        new Label("Completed Tasks"),
        completedTree,
        completedToolbar);
    VBox.setVgrow(completedTree, Priority.ALWAYS);

    SplitPane splitPane = new SplitPane(activeSection, completedSection);
    splitPane.setOrientation(Orientation.VERTICAL);
    splitPane.setDividerPositions(0.6);

    content.setCenter(splitPane);

    sceneTab = new Tab(TAB_TITLE, content);
    return sceneTab;
  }

  @Override
  public void closeTab() {
    if (activeRootItem != null) {
      activeRootItem.dispose();
      activeRootItem = null;
    }
    if (completedRootItem != null) {
      completedRootItem.dispose();
      completedRootItem = null;
    }
  }

  private TreeTableView<TaskMonitorItem> createTaskTree(String placeholderText) {
    TreeTableView<TaskMonitorItem> tree = new TreeTableView<>();
    tree.setShowRoot(false);
    tree.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

    TreeTableColumn<TaskMonitorItem, String> titleColumn =
        new TreeTableColumn<>("Task");
    titleColumn.setCellValueFactory(param -> {
      TaskMonitorItem item = param.getValue().getValue();
      return item != null
          ? item.titleProperty()
          : new ReadOnlyStringWrapper("");
    });

    TreeTableColumn<TaskMonitorItem, TaskStatus> statusColumn =
        new TreeTableColumn<>("Status");
    statusColumn.setPrefWidth(120.0);
    statusColumn.setCellValueFactory(param -> {
      TaskMonitorItem item = param.getValue().getValue();
      return item != null
          ? item.statusProperty()
          : new ReadOnlyObjectWrapper<>(null);
    });

    TreeTableColumn<TaskMonitorItem, Number> remainingColumn =
        new TreeTableColumn<>("Remaining");
    remainingColumn.setPrefWidth(120.0);
    remainingColumn.setCellValueFactory(param -> {
      TreeItem<TaskMonitorItem> treeItem = param.getValue();
      if (treeItem instanceof TaskTreeItem taskTreeItem) {
        return taskTreeItem.remainingProperty();
      }
      return new ReadOnlyIntegerWrapper(0).getReadOnlyProperty();
    });
    remainingColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

    tree.getColumns().setAll(titleColumn, statusColumn, remainingColumn);
    tree.setPlaceholder(new Label(placeholderText));
    return tree;
  }

  private TreeTableRow<TaskMonitorItem> buildCompletedRowFactory(
      TreeTableView<TaskMonitorItem> tree) {
    TreeTableRow<TaskMonitorItem> row = new TreeTableRow<>();
    row.setOnMouseClicked(event -> {
      if (event.getClickCount() == 2 && !row.isEmpty()) {
        TaskMonitorItem item = row.getTreeItem().getValue();
        if (item != null && item.statusProperty().get() == TaskStatus.COMPLETED) {
          openTaskResult(item);
        }
      }
    });
    return row;
  }

  private BooleanBinding buildOpenResultDisabledBinding() {
    return Bindings.createBooleanBinding(() -> {
      TreeItem<TaskMonitorItem> selected =
          completedTree.getSelectionModel().getSelectedItem();
      if (selected == null) {
        return true;
      }
      TaskMonitorItem item = selected.getValue();
      if (item == null) {
        return true;
      }
      return item.statusProperty().get() != TaskStatus.COMPLETED;
    }, completedTree.getSelectionModel().selectedItemProperty());
  }

  private void openSelectedTaskResult(TreeTableView<TaskMonitorItem> tree) {
    TreeItem<TaskMonitorItem> selected = tree.getSelectionModel().getSelectedItem();
    if (selected == null || selected.getValue() == null) {
      return;
    }
    openTaskResult(selected.getValue());
  }

  private void openTaskResult(TaskMonitorItem item) {
    try {
      Object result = item.getTask().getResult();
      if (result instanceof DepanFxSceneViewer viewer) {
        sceneService.addViewer(viewer);
        return;
      }
      if (result instanceof Runnable runnable) {
        runnable.run();
        return;
      }
      showInformationDialog(
          item,
          result == null
              ? "Task completed without a result."
              : result.toString());
    } catch (Exception err) {
      LOG.warn("Unable to open task result for {}", item.getTask(), err);
      showErrorDialog(item, err);
    }
  }

  private void showInformationDialog(TaskMonitorItem item, String message) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("Task Result");
    alert.setHeaderText(item.titleProperty().get());
    alert.setContentText(message);
    alert.showAndWait();
  }

  private void showErrorDialog(TaskMonitorItem item, Exception error) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Task Result");
    alert.setHeaderText("Unable to open result for " + item.titleProperty().get());
    String message = error.getMessage();
    if (message == null || message.isBlank()) {
      message = error.toString();
    }

    StringWriter traceWriter = new StringWriter();
    error.printStackTrace(new PrintWriter(traceWriter));

    alert.setContentText(message);
    Node expandable = buildTraceContent(traceWriter.toString());
    alert.getDialogPane().setExpandableContent(expandable);
    alert.showAndWait();
  }

  private Node buildTraceContent(String trace) {
    javafx.scene.control.TextArea area = new javafx.scene.control.TextArea(trace);
    area.setEditable(false);
    area.setWrapText(false);
    area.setPrefRowCount(10);
    return area;
  }

  private static class TaskListTreeItem extends TreeItem<TaskMonitorItem> {

    private final ObservableList<TaskMonitorItem> backingList;

    private final ListChangeListener<TaskMonitorItem> listener = change -> rebuild();

    TaskListTreeItem(ObservableList<TaskMonitorItem> backingList) {
      super(null);
      this.backingList = backingList;
      setExpanded(true);
      backingList.addListener(listener);
      rebuild();
    }

    void dispose() {
      backingList.removeListener(listener);
      for (TreeItem<TaskMonitorItem> child : new ArrayList<>(getChildren())) {
        ((TaskTreeItem) child).dispose();
      }
      getChildren().clear();
    }

    private void rebuild() {
      for (TreeItem<TaskMonitorItem> child : new ArrayList<>(getChildren())) {
        ((TaskTreeItem) child).dispose();
      }
      getChildren().clear();
      for (TaskMonitorItem item : backingList) {
        getChildren().add(new TaskTreeItem(item));
      }
    }
  }

  private static class TaskTreeItem extends TreeItem<TaskMonitorItem> {

    private final ReadOnlyIntegerWrapper remaining =
        new ReadOnlyIntegerWrapper(this, "remaining", 0);

    private final ReadOnlyIntegerWrapper outstandingInclusive =
        new ReadOnlyIntegerWrapper(this, "outstandingInclusive", 0);

    private final ChangeListener<TaskStatus> statusListener =
        (obs, oldValue, newValue) -> updateCounts();

    private final ListChangeListener<TaskMonitorItem> childListener =
        change -> rebuildChildren();

    private final Map<TaskTreeItem, ChangeListener<Number>> childOutstandingListeners =
        new IdentityHashMap<>();

    TaskTreeItem(TaskMonitorItem item) {
      super(item);
      setExpanded(true);
      item.statusProperty().addListener(statusListener);
      item.getChildren().addListener(childListener);
      rebuildChildren();
      updateCounts();
    }

    void dispose() {
      TaskMonitorItem item = getValue();
      if (item == null) {
        return;
      }
      item.statusProperty().removeListener(statusListener);
      item.getChildren().removeListener(childListener);
      childOutstandingListeners.forEach((child, listener) ->
          child.outstandingInclusiveProperty().removeListener(listener));
      childOutstandingListeners.clear();
      for (TreeItem<TaskMonitorItem> child : new ArrayList<>(getChildren())) {
        ((TaskTreeItem) child).dispose();
      }
      getChildren().clear();
    }

    ReadOnlyIntegerProperty remainingProperty() {
      return remaining.getReadOnlyProperty();
    }

    ReadOnlyIntegerProperty outstandingInclusiveProperty() {
      return outstandingInclusive.getReadOnlyProperty();
    }

    private void rebuildChildren() {
      childOutstandingListeners.forEach((child, listener) ->
          child.outstandingInclusiveProperty().removeListener(listener));
      childOutstandingListeners.clear();
      for (TreeItem<TaskMonitorItem> child : new ArrayList<>(getChildren())) {
        ((TaskTreeItem) child).dispose();
      }
      getChildren().clear();
      for (TaskMonitorItem childValue : getValue().getChildren()) {
        TaskTreeItem childItem = new TaskTreeItem(childValue);
        getChildren().add(childItem);
        ChangeListener<Number> listener = (obs, oldValue, newValue) -> updateCounts();
        childItem.outstandingInclusiveProperty().addListener(listener);
        childOutstandingListeners.put(childItem, listener);
      }
      updateCounts();
    }

    private void updateCounts() {
      int childOutstanding = 0;
      for (TreeItem<TaskMonitorItem> child : getChildren()) {
        childOutstanding += ((TaskTreeItem) child)
            .outstandingInclusiveProperty().get();
      }
      boolean selfOutstanding =
          !getValue().statusProperty().get().isTerminal();
      outstandingInclusive.set(childOutstanding + (selfOutstanding ? 1 : 0));
      remaining.set(childOutstanding);
    }
  }
}
