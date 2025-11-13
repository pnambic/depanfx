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

import com.pnambic.depanfx.tasks.DeferredTask;
import com.pnambic.depanfx.tasks.ProgressMonitor;
import com.pnambic.depanfx.tasks.TaskStatus;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.util.Optional;

class WorkspaceResourceLoadTask<T>
    implements DeferredTask<Optional<DepanFxWorkspaceResource<T>>> {

  private final DepanFxWorkspace workspace;
  private final DepanFxProjectDocument document;
  private final Class<T> resourceType;
  private final String taskTitle;

  private volatile TaskStatus status = TaskStatus.READY;
  private volatile boolean cancelled;
  private volatile Optional<DepanFxWorkspaceResource<T>> result = Optional.empty();
  private volatile Exception failure;

  WorkspaceResourceLoadTask(
      DepanFxWorkspace workspace,
      DepanFxProjectDocument document,
      Class<T> resourceType,
      String taskTitle) {
    this.workspace = workspace;
    this.document = document;
    this.resourceType = resourceType;
    this.taskTitle = taskTitle;
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
      result = workspace.getWorkspaceResource(document, resourceType);
      if (monitor.isCancelled() || cancelled) {
        status = TaskStatus.CANCELLED;
        return;
      }
      monitor.complete();
      status = TaskStatus.COMPLETED;
    } catch (Exception errAny) {
      failure = errAny;
      status = TaskStatus.FAILED;
      throw errAny;
    }
  }

  @Override
  public TaskStatus getStatus() {
    return status;
  }

  @Override
  public Optional<DepanFxWorkspaceResource<T>> getResult() throws Exception {
    if (failure != null) {
      throw failure;
    }
    return result;
  }

  @Override
  public void cancel() {
    cancelled = true;
  }
}
