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

import com.pnambic.depanfx.tasks.DeferredTask;
import com.pnambic.depanfx.tasks.TaskSnapshot;
import com.pnambic.depanfx.tasks.TaskStatus;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class TaskMonitorItem {

  private final DeferredTask<?> task;

  private TaskMonitorItem parent;

  private final ObservableList<TaskMonitorItem> mutableChildren = FXCollections.observableArrayList();

  private final ObservableList<TaskMonitorItem> children =
      FXCollections.unmodifiableObservableList(mutableChildren);

  private final ReadOnlyStringWrapper title =
      new ReadOnlyStringWrapper(this, "title");

  private final ReadOnlyObjectWrapper<TaskStatus> status =
      new ReadOnlyObjectWrapper<>(this, "status", TaskStatus.READY);

  private final ReadOnlyIntegerWrapper totalSteps =
      new ReadOnlyIntegerWrapper(this, "totalSteps", 0);

  private final ReadOnlyIntegerWrapper completedSteps =
      new ReadOnlyIntegerWrapper(this, "completedSteps", 0);

  private final ReadOnlyDoubleWrapper progress =
      new ReadOnlyDoubleWrapper(this, "progress", 0.0d);

  private final ReadOnlyStringWrapper message =
      new ReadOnlyStringWrapper(this, "message", "");

  private final ReadOnlyBooleanWrapper cancelRequested =
      new ReadOnlyBooleanWrapper(this, "cancelRequested", false);

  private final ReadOnlyObjectWrapper<Throwable> error =
      new ReadOnlyObjectWrapper<>(this, "error", null);

  private final ReadOnlyObjectWrapper<Instant> createdAt =
      new ReadOnlyObjectWrapper<>(this, "createdAt", null);

  private final ReadOnlyObjectWrapper<Instant> startedAt =
      new ReadOnlyObjectWrapper<>(this, "startedAt", null);

  private final ReadOnlyObjectWrapper<Instant> completedAt =
      new ReadOnlyObjectWrapper<>(this, "completedAt", null);

  private final ReadOnlyObjectWrapper<TaskSnapshot> snapshot =
      new ReadOnlyObjectWrapper<>(this, "snapshot", null);

  public TaskMonitorItem(DeferredTask<?> task) {
    this.task = task;
    title.set(task.getTaskTitle());
  }

  public void updateFromSnapshot(TaskSnapshot value) {
    snapshot.set(value);
    status.set(value.status());
    totalSteps.set(value.totalSteps());
    completedSteps.set(value.completedSteps());
    progress.set(value.progressFraction());
    message.set(value.message() == null ? "" : value.message());
    cancelRequested.set(value.cancelRequested());
    error.set(value.error());
    createdAt.set(value.createdAt());
    startedAt.set(value.startedAt());
    completedAt.set(value.completedAt());
  }

  public List<TaskMonitorItem> getMutableChildren() {
    return mutableChildren;
  }

  public void removeChild(TaskMonitorItem child) {
    mutableChildren.remove(child);
  }

  public void setParent(TaskMonitorItem newParent) {
    if (Objects.equals(parent, newParent)) {
      return;
    }
    if (parent != null) {
      parent.mutableChildren.remove(this);
    }
    parent = newParent;
    if (parent != null && !parent.mutableChildren.contains(this)) {
      parent.mutableChildren.add(this);
    }
  }

  public DeferredTask<?> getTask() {
    return task;
  }

  public TaskMonitorItem getParent() {
    return parent;
  }

  public boolean isRoot() {
    return parent == null;
  }

  public ObservableList<TaskMonitorItem> getChildren() {
    return children;
  }

  public ReadOnlyStringProperty titleProperty() {
    return title.getReadOnlyProperty();
  }

  public ReadOnlyObjectProperty<TaskStatus> statusProperty() {
    return status.getReadOnlyProperty();
  }

  public ReadOnlyIntegerProperty totalStepsProperty() {
    return totalSteps.getReadOnlyProperty();
  }

  public ReadOnlyIntegerProperty completedStepsProperty() {
    return completedSteps.getReadOnlyProperty();
  }

  public ReadOnlyDoubleProperty progressProperty() {
    return progress.getReadOnlyProperty();
  }

  public ReadOnlyStringProperty messageProperty() {
    return message.getReadOnlyProperty();
  }

  public ReadOnlyBooleanProperty cancelRequestedProperty() {
    return cancelRequested.getReadOnlyProperty();
  }

  public ReadOnlyObjectProperty<Throwable> errorProperty() {
    return error.getReadOnlyProperty();
  }

  public ReadOnlyObjectProperty<Instant> createdAtProperty() {
    return createdAt.getReadOnlyProperty();
  }

  public ReadOnlyObjectProperty<Instant> startedAtProperty() {
    return startedAt.getReadOnlyProperty();
  }

  public ReadOnlyObjectProperty<Instant> completedAtProperty() {
    return completedAt.getReadOnlyProperty();
  }

  public ReadOnlyObjectProperty<TaskSnapshot> snapshotProperty() {
    return snapshot.getReadOnlyProperty();
  }
}
