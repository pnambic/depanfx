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
import com.pnambic.depanfx.tasks.TaskExecutorService;
import com.pnambic.depanfx.tasks.TaskSubmission;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.function.Consumer;

import javafx.application.Platform;

@Service
public class WorkspaceTaskService {

  private static final Logger LOG =
      LoggerFactory.getLogger(WorkspaceTaskService.class);

  private final TaskExecutorService taskExecutorService;

  public WorkspaceTaskService(TaskExecutorService taskExecutorService) {
    this.taskExecutorService = taskExecutorService;
  }

  public TaskSubmission<Optional<DepanFxWorkspaceResource<?>>>
  submitFetchResource(
      DepanFxWorkspace workspace,
      DepanFxProjectDocument document,
      DepanFxResourceRegistryContribution<?> contribution,
      Consumer<DepanFxWorkspaceResource<?>> onResourceLoad) {

    ResourceLoadTask loadTask =
        new ResourceLoadTask(contribution, workspace, document);
    TaskSubmission<Optional<DepanFxWorkspaceResource<?>>> result =
        taskExecutorService.submitTask(loadTask);

    result.resultFuture().whenComplete((resource, error) -> {
      if (error != null) {
        if (error instanceof CancellationException) {
          return;
        }
        LOG.warn("Unable to load document: {}", document, error);
        return;
      }
      resource.ifPresentOrElse(
          r -> runOnFxThread(() -> onResourceLoad.accept(r)),
          () -> LOG.warn("Unable to load document: {}", document));
    });

    return result;
  }

  private void runOnFxThread(Runnable action) {
    if (Platform.isFxApplicationThread()) {
      action.run();
    } else {
      Platform.runLater(action);
    }
  }
}
