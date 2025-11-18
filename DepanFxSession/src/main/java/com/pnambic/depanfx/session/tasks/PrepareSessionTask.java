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

import com.pnambic.depanfx.session.core.DepanFxSession;
import com.pnambic.depanfx.session.core.DepanFxSessionConfig;
import com.pnambic.depanfx.session.core.DepanFxSessionDataTransport;
import com.pnambic.depanfx.tasks.DeferredTask;
import com.pnambic.depanfx.tasks.ProgressMonitor;
import com.pnambic.depanfx.tasks.TaskStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Platform;

/**
 * Prepare the session with the user defined configuration.
 * The task writes the configuration to the session before completion.
 * This allows an waiting action to be sure the results are included
 * in the session before continuing.
 *
 * This task does not activate a configuration this is already running
 * in the session.  If the session has started the loaded configuration,
 * that is left unchanged.
 *
 * It is not intended as a general switch-session task, although its
 * behavior is close.  Outside of the startup case, a new session config
 * should always finish with a session restart.
 */
public class PrepareSessionTask
    implements DeferredTask<DepanFxSessionConfig> {

  private static final Logger LOG =
      LoggerFactory.getLogger(PrepareSessionTask.class);

  private final DepanFxSession session;

  private final Path configPath;

  private final DepanFxSessionDataTransport transport;

  /** Convenience for logging and reporting */
  private final String loadLabel;

  private final AtomicReference<TaskStatus> status =
      new AtomicReference<>(TaskStatus.READY);

  private final AtomicReference<DepanFxSessionConfig> result =
      new AtomicReference<>();

  private final AtomicReference<Exception> failure = new AtomicReference<>();

  private volatile String message = "";

  public PrepareSessionTask(
      DepanFxSession session,
      Path configPath,
      DepanFxSessionDataTransport transport) {
    this.session = session;
    this.configPath = configPath;
    this.transport = transport;
    loadLabel = configPath.getFileName().toString();
  }

  @Override
  public String getTaskTitle() {
    return MessageFormat.format("Loading session {0}", loadLabel);
  }

  @Override
  public int getTotalSteps() {
    // 1) Load the session config
    // 2) Push session config into session
    return 2;
  }

  @Override
  public void start(ProgressMonitor monitor) throws Exception {
    if (!status.compareAndSet(TaskStatus.READY, TaskStatus.RUNNING)) {
      if (status.get() == TaskStatus.CANCELLED) {
        throw cancelled();
      }
    }

    String beginMessage = MessageFormat.format("Loading {0}", loadLabel);
    updateMessage(monitor, beginMessage);

    try {
      checkCancelled(monitor);

      DepanFxSessionConfig config = transport.loadSessionConfig(configPath);

      checkCancelled(monitor);

      monitor.advance(1, "Configuring session");
      LOG.info("reset session to {}", configPath.toString());
      session.resetSessionConfig(configPath, config);
      runOnFxThread(() -> activateConfig());

      // Nobody uses, but we complete the future.
      result.set(config);
      String successMessage = MessageFormat.format("Loaded {0}", loadLabel);
      monitor.advance(1, successMessage);
      markComplete(monitor);

    } catch (CancellationException cancel) {
      updateStatus(monitor, TaskStatus.CANCELLED, cancellationMessage());
      throw cancel;

    } catch (Exception error) {
      String errorMessage = MessageFormat.format(
          "Failed to load {0}: {1}", loadLabel, error.getMessage());
      updateStatus(monitor, TaskStatus.FAILED, errorMessage);
      failure.set(error);
      throw error;
    }
  }

  private void activateConfig() {
    // By the time this runs, the session may have already started with
    // the configuration that was previously loaded.
    try {
      session.activateConfig();
    } catch (Exception err) {
      LOG.warn("Unable to start the session loaded from {}",
          configPath, err);
    }
  }

  @Override
  public TaskStatus getStatus() {
    return status.get();
  }

  @Override
  public DepanFxSessionConfig getResult() throws Exception {
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

  private void checkCancelled(ProgressMonitor monitor) {
    if (status.get() == TaskStatus.CANCELLED || monitor.isCancelled()) {
      throw cancelled();
    }
  }

  private CancellationException cancelled() {
    return new CancellationException(cancellationMessage());
  }

  private String cancellationMessage() {
    return MessageFormat.format("Cancelled load for {0}", loadLabel);
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

  private void runOnFxThread(Runnable action) {
    if (Platform.isFxApplicationThread()) {
      action.run();
    } else {
      Platform.runLater(action);
    }
  }
}
