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

import com.pnambic.depanfx.tasks.TaskSubmission;
import com.pnambic.depanfx.tasks.TaskExecutorService;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Coordinates the asynchronous loading of workspace resources so that file IO
 * can be tracked by the task monitor UI.
 */
@Component
public class DepanFxWorkspaceTaskService {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxWorkspaceTaskService.class);

  private final TaskExecutorService executorService;

  @Autowired
  public DepanFxWorkspaceTaskService(TaskExecutorService executorService) {
    this.executorService = executorService;
  }

  public TaskExecutorService getExecutorService() {
    return executorService;
  }

  public <T> void loadAndOpenResource(
      DepanFxWorkspace workspace,
      DepanFxProjectDocument document,
      Class<T> resourceType,
      String loadTaskTitle,
      String openTaskTitle,
      Consumer<DepanFxWorkspaceResource<T>> openOperation,
      Consumer<Throwable> failureHandler) {

    WorkspaceResourceLoadTask<T> loadTask =
        new WorkspaceResourceLoadTask<>(
            workspace, document, resourceType, loadTaskTitle);

    TaskSubmission<Optional<DepanFxWorkspaceResource<T>>> submission =
        executorService.submitTask(loadTask);

    submission.resultFuture().whenComplete((result, error) -> {
      if (error != null) {
        handleFailure(document, failureHandler, error);
        return;
      }
      if (result.isEmpty()) {
        handleFailure(
            document,
            failureHandler,
            new IllegalStateException(
                "Workspace did not provide resource for "
                + document.getMemberPath()));
        return;
      }

      WorkspaceResourceOpenTask<T> openTask = new WorkspaceResourceOpenTask<>(
          loadTask, result.get(), openTaskTitle, openOperation, failureHandler);
      executorService.submitTask(openTask);
    });
  }

  private void handleFailure(
      DepanFxProjectDocument document,
      Consumer<Throwable> failureHandler,
      Throwable error) {

    LOG.error("Failed to load workspace resource {}", document, error);
    if (failureHandler != null) {
      failureHandler.accept(error);
    }
  }
}
