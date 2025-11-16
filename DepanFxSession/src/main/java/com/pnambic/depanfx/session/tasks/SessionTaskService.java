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
package com.pnambic.depanfx.session.tasks;

import com.pnambic.depanfx.session.core.DepanFxSessionConfig;
import com.pnambic.depanfx.session.core.DepanFxSessionDataTransport;
import com.pnambic.depanfx.tasks.TaskExecutorService;
import com.pnambic.depanfx.tasks.TaskSubmission;
import com.pnambic.depanfx.tasks.gui.TaskMonitorDialogService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.concurrent.CancellationException;
import java.util.function.Consumer;

import javafx.application.Platform;

@Service
public class SessionTaskService {

  // Interval to stall so the load can complete first.
  // Just a half second head start before starting the session.
  public static final int LOAD_STALL_MS = 500;

  private static final Logger LOG =
      LoggerFactory.getLogger(SessionTaskService.class);

  private final TaskExecutorService taskExecutorService;

  private final TaskMonitorDialogService taskDialogService;

  public SessionTaskService(
      TaskExecutorService taskExecutorService,
      TaskMonitorDialogService taskDialogService) {
    this.taskExecutorService = taskExecutorService;
    this.taskDialogService = taskDialogService;
  }

  public void showActiveTasks() {
    taskDialogService.showActiveTasks();
  }

  public TaskSubmission<DepanFxSessionConfig>
  submitLoadSession(
      Path sessionPath,
      DepanFxSessionDataTransport transport,
      Consumer<DepanFxSessionConfig> onResourceLoad) {

    SessionLoadTask loadTask = new SessionLoadTask(sessionPath, transport);
    TaskSubmission<DepanFxSessionConfig> result =
        taskExecutorService.submitTask(loadTask);

    result.resultFuture().whenComplete((config, error) -> {
      if (error != null) {
        if (error instanceof CancellationException) {
          return;
        }
        LOG.warn("Unable to load session: {}", sessionPath.getFileName(), error);
        return;
      }
      if (config != null) {
        runOnFxThread(() -> onResourceLoad.accept(config));
        return;
      }
      LOG.warn("Unable to load document: {}", sessionPath.getFileName());
    });

    // Give the session load task a brief head start on its consumers.
    taskExecutorService.awaitTask(loadTask, LOAD_STALL_MS);
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
