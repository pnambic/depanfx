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

import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistryContribution;
import com.pnambic.depanfx.tasks.DeferredTask;
import com.pnambic.depanfx.tasks.ProgressMonitor;
import com.pnambic.depanfx.tasks.TaskStatus;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicReference;

public class ResourceLoadTask
    implements DeferredTask<Optional<DepanFxWorkspaceResource<?>>> {

  private final DepanFxResourceRegistryContribution<?> contribution;

  private final DepanFxWorkspace workspace;

  private final DepanFxProjectDocument document;

  private final AtomicReference<TaskStatus> status =
      new AtomicReference<>(TaskStatus.READY);

  private final AtomicReference<Optional<DepanFxWorkspaceResource<?>>> result =
      new AtomicReference<>(Optional.empty());

  private final AtomicReference<Exception> failure = new AtomicReference<>();

  private volatile String message = "";

  public ResourceLoadTask(
      DepanFxResourceRegistryContribution<?> contribution,
      DepanFxWorkspace workspace,
      DepanFxProjectDocument document) {
    this.contribution = contribution;
    this.workspace = workspace;
    this.document = document;
  }

  @Override
  public String getTaskTitle() {
    return MessageFormat.format(
        "Load {0}", document.getMemberPath().getFileName());
  }

  @Override
  public int getTotalSteps() {
    return 1;
  }

  @Override
  public void start(ProgressMonitor monitor) throws Exception {
    if (!status.compareAndSet(TaskStatus.READY, TaskStatus.RUNNING)) {
      if (status.get() == TaskStatus.CANCELLED) {
        throw cancelled();
      }
    }

    String beginMessage = MessageFormat.format(
        "Loading {0}", document.getMemberPath());
    updateMessage(monitor, beginMessage);

    try {
      checkCancelled(monitor);

      Optional<DepanFxWorkspaceResource<?>> loaded =
          contribution.loadResource(workspace, document)
              .map(resource -> (DepanFxWorkspaceResource<?>) resource);

      checkCancelled(monitor);

      result.set(loaded);
      if (loaded.isPresent()) {
        String successMessage = MessageFormat.format(
            "Loaded {0}", document.getMemberPath());
        monitor.advance(1, successMessage);
        markComplete(monitor);
      } else {
        String missingMessage = MessageFormat.format(
            "No resource produced for {0}", document.getMemberPath());
        monitor.advance(1, missingMessage);
        markComplete(monitor);
      }
    } catch (CancellationException cancel) {
      updateStatus(monitor, TaskStatus.CANCELLED, cancellationMessage());
      throw cancel;
    } catch (Exception error) {
      String errorMessage = MessageFormat.format(
          "Failed to load {0}: {1}",
          document.getMemberPath(),
          error.getMessage());
      updateStatus(monitor, TaskStatus.FAILED, errorMessage);
      failure.set(error);
      throw error;
    }
  }

  @Override
  public TaskStatus getStatus() {
    return status.get();
  }

  @Override
  public Optional<DepanFxWorkspaceResource<?>> getResult() throws Exception {
    Exception error = failure.get();
    if (error != null) {
      throw error;
    }
    if (status.get() == TaskStatus.CANCELLED) {
      throw cancelled();
    }
    return result.get();
  }

  @Override
  public void cancel() {
    TaskStatus current;
    do {
      current = status.get();
      if (current.isTerminal()) {
        return;
      }
    } while (!status.compareAndSet(current, TaskStatus.CANCELLED));
    message = cancellationMessage();
  }

  public String getMessage() {
    return message;
  }

  public DepanFxResourceRegistryContribution<?> getContribution() {
    return contribution;
  }

  public DepanFxWorkspace getWorkspace() {
    return workspace;
  }

  public DepanFxProjectDocument getDocument() {
    return document;
  }

  private void checkCancelled(ProgressMonitor monitor) {
    if (status.get() == TaskStatus.CANCELLED || monitor.isCancelled()) {
      throw cancelled();
    }
  }

  private CancellationException cancelled() {
    return new CancellationException(cancellationMessage());
  }

  private String cancellationMessage() {
    return MessageFormat.format(
        "Cancelled load for {0}", document.getMemberPath());
  }

  private void markComplete(ProgressMonitor monitor) {
    monitor.complete();
    status.set(TaskStatus.COMPLETED);
  }

  private void updateStatus(
      ProgressMonitor monitor, TaskStatus taskStatus, String message) {

    status.set(taskStatus);
    updateMessage(monitor, message);
  }

  private void updateMessage(ProgressMonitor monitor, String message) {
    this.message = message;
    monitor.updateMessage(message);
  }
}
