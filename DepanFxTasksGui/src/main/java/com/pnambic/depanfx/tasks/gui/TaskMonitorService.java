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

import com.pnambic.depanfx.tasks.DeferredSubTask;
import com.pnambic.depanfx.tasks.DeferredTask;
import com.pnambic.depanfx.tasks.TaskExecutorService;
import com.pnambic.depanfx.tasks.TaskListener;
import com.pnambic.depanfx.tasks.TaskSnapshot;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

@Service
public class TaskMonitorService implements DisposableBean {

  private final TaskExecutorService executorService;

  private final Map<DeferredTask<?>, TaskMonitorItem> taskItems = new IdentityHashMap<>();

  private final ObservableList<TaskMonitorItem> activeRoots =
      FXCollections.observableArrayList();

  private final ObservableList<TaskMonitorItem> completedRoots =
      FXCollections.observableArrayList();

  private final TaskListener execListener = new FxTaskListener();

  public TaskMonitorService(TaskExecutorService executorService) {
    this.executorService = executorService;
    executorService.addListener(execListener);
    bootstrapSnapshots();
  }

  @Override // DisposableBean
  public void destroy() {
    executorService.removeListener(execListener);
  }

  public ObservableList<TaskMonitorItem> getActiveTasks() {
    return FXCollections.unmodifiableObservableList(activeRoots);
  }

  public ObservableList<TaskMonitorItem> getCompletedTasks() {
    return FXCollections.unmodifiableObservableList(completedRoots);
  }

  public void addActiveListener(
      ListChangeListener<? super TaskMonitorItem> listener) {
    activeRoots.addListener(listener);
  }

  public boolean hasActiveTask() {
    return !activeRoots.isEmpty();
  }

  public Optional<TaskMonitorItem> getFirstActive() {
    if (activeRoots.isEmpty()) {
      return Optional.empty();
    }

    // Sometimes the active task is empty by the time we grab it.
    return Optional.ofNullable(activeRoots.getFirst());
  }

  public boolean cancelTask(TaskMonitorItem taskItem) {
    return executorService.cancelTask(taskItem.getTask());
  }

  public void clearCompletedTasks() {
    runOnFxThread(() -> {
      List<TaskMonitorItem> items = new ArrayList<>(completedRoots);
      for (TaskMonitorItem item : items) {
        removeSubtree(item);
      }
      completedRoots.clear();
    });
  }

  public boolean awaitTask(DeferredTask<?> task) {
    return executorService.awaitTask(task);
  }

  public boolean awaitTaskMs(DeferredTask<?> task, int waitMs) {
    return executorService.awaitTask(task, waitMs);
  }

  private void bootstrapSnapshots() {
    try (Stream<TaskSnapshot> active = executorService.getActiveTasks()) {
      active.forEach(this::scheduleSnapshotUpdate);
    }
    try (Stream<TaskSnapshot> completed = executorService.getCompletedTasks()) {
      completed.forEach(this::scheduleSnapshotUpdate);
    }
  }

  private void scheduleSnapshotUpdate(TaskSnapshot snapshot) {
    runOnFxThread(() -> applySnapshot(snapshot));
  }

  private void applySnapshot(TaskSnapshot snapshot) {
    TaskMonitorItem item = getOrCreateItem(snapshot.task());
    attachParentIfNecessary(item);
    item.updateFromSnapshot(snapshot);
    if (!snapshot.isTerminal()) {
      promoteToActive(item);
    } else {
      promoteToCompleted(item);
    }
  }

  private TaskMonitorItem getOrCreateItem(DeferredTask<?> task) {
    TaskMonitorItem item = taskItems.get(task);
    if (item != null) {
      return item;
    }
    item = new TaskMonitorItem(task);
    taskItems.put(task, item);
    return item;
  }

  private void attachParentIfNecessary(TaskMonitorItem item) {
    DeferredTask<?> task = item.getTask();
    if (task instanceof DeferredSubTask subTask) {
      TaskMonitorItem parent = getOrCreateItem(subTask.getParent());
      if (!Objects.equals(item.getParent(), parent)) {
        item.setParent(parent);
      }
    } else if (item.getParent() != null) {
      item.setParent(null);
    }
  }

  private void promoteToActive(TaskMonitorItem item) {
    if (!item.isRoot()) {
      return;
    }
    completedRoots.remove(item);
    if (!activeRoots.contains(item)) {
      activeRoots.add(item);
    }
  }

  private void promoteToCompleted(TaskMonitorItem item) {
    if (!item.isRoot()) {
      return;
    }
    activeRoots.remove(item);
    if (!completedRoots.contains(item)) {
      completedRoots.add(item);
    }
  }

  private void removeSubtree(TaskMonitorItem item) {
    for (TaskMonitorItem child : new ArrayList<>(item.getMutableChildren())) {
      removeSubtree(child);
    }
    taskItems.remove(item.getTask());
    TaskMonitorItem parent = item.getParent();
    if (parent != null) {
      parent.removeChild(item);
    }
  }

  private void runOnFxThread(Runnable action) {
    if (Platform.isFxApplicationThread()) {
      action.run();
    } else {
      Platform.runLater(action);
    }
  }

  private class FxTaskListener implements TaskListener {

    @Override
    public void onTaskScheduled(TaskSnapshot snapshot) {
      scheduleSnapshotUpdate(snapshot);
    }

    @Override
    public void onTaskStarted(TaskSnapshot snapshot) {
      scheduleSnapshotUpdate(snapshot);
    }

    @Override
    public void onTaskProgress(TaskSnapshot snapshot) {
      scheduleSnapshotUpdate(snapshot);
    }

    @Override
    public void onTaskCompleted(TaskSnapshot snapshot) {
      scheduleSnapshotUpdate(snapshot);
    }

    @Override
    public void onTaskCancelled(TaskSnapshot snapshot) {
      scheduleSnapshotUpdate(snapshot);
    }

    @Override
    public void onTaskFailed(TaskSnapshot snapshot, Throwable error) {
      scheduleSnapshotUpdate(snapshot);
    }
  }
}
