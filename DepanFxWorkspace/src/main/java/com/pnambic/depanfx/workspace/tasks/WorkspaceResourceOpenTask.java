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
package com.pnambic.depanfx.workspace.tasks;

import com.pnambic.depanfx.tasks.DeferredSubTask;
import com.pnambic.depanfx.tasks.DeferredTask;
import com.pnambic.depanfx.tasks.ProgressMonitor;
import com.pnambic.depanfx.tasks.TaskStatus;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.function.Consumer;

class WorkspaceResourceOpenTask<T>
    implements DeferredTask<Void>, DeferredSubTask {

  private final DeferredTask<?> parent;
  private final DepanFxWorkspaceResource<T> resource;
  private final String taskTitle;
  private final Consumer<DepanFxWorkspaceResource<T>> openOperation;
  private final Consumer<Throwable> failureHandler;

  private volatile TaskStatus status = TaskStatus.READY;
  private volatile boolean cancelled;
  private volatile Throwable failure;

  WorkspaceResourceOpenTask(
      DeferredTask<?> parent,
      DepanFxWorkspaceResource<T> resource,
      String taskTitle,
      Consumer<DepanFxWorkspaceResource<T>> openOperation,
      Consumer<Throwable> failureHandler) {
    this.parent = parent;
    this.resource = resource;
    this.taskTitle = taskTitle;
    this.openOperation = openOperation;
    this.failureHandler = failureHandler;
  }

  @Override
  public DeferredTask<?> getParent() {
    return parent;
  }

  @Override
  public String getTaskTitle() {
    return taskTitle;
  }

  @Override
  public int getTotalSteps() {
    return 1;
  }

  @Override
  public void start(ProgressMonitor monitor) throws Exception {
    status = TaskStatus.RUNNING;
    monitor.updateMessage(taskTitle);

    if (monitor.isCancelled() || cancelled) {
      status = TaskStatus.CANCELLED;
      return;
    }

    try {
      openOperation.accept(resource);
      if (monitor.isCancelled() || cancelled) {
        status = TaskStatus.CANCELLED;
        return;
      }
      monitor.complete();
      status = TaskStatus.COMPLETED;
    } catch (Throwable errAny) {
      failure = errAny;
      status = TaskStatus.FAILED;
      if (failureHandler != null) {
        failureHandler.accept(errAny);
      }
      if (errAny instanceof Exception errEx) {
        throw errEx;
      }
      throw new Exception(errAny);
    }
  }

  @Override
  public TaskStatus getStatus() {
    return status;
  }

  @Override
  public Void getResult() throws Exception {
    if (failure != null) {
      if (failure instanceof Exception errEx) {
        throw errEx;
      }
      throw new Exception(failure);
    }
    return null;
  }

  @Override
  public void cancel() {
    cancelled = true;
  }
}
