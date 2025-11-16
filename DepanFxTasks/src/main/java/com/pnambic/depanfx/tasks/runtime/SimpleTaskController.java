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
package com.pnambic.depanfx.tasks.runtime;

import com.pnambic.depanfx.tasks.DeferredTask;
import com.pnambic.depanfx.tasks.ProgressMonitor;
import com.pnambic.depanfx.tasks.TaskSnapshot;
import com.pnambic.depanfx.tasks.TaskStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleTaskController<T> {

  private static final int NANOS_PER_MILLIS = 1_000_000;

  private static final Logger LOG =
      LoggerFactory.getLogger(SimpleTaskController.class);

  private final SimpleTaskExecutor executor;

  private final DeferredTask<T> task;

  private final int totalSteps;

  private final CompletableFuture<T> resultFuture = new CompletableFuture<>();

  private final AtomicReference<TaskStatus> status =
      new AtomicReference<>(TaskStatus.READY);

  private final AtomicInteger completedSteps = new AtomicInteger();

  private final AtomicBoolean cancelRequested = new AtomicBoolean();

  private final Instant createdAt;

  private volatile Instant startedAt;

  private volatile Instant completedAt;

  private volatile String message = "";

  private volatile Throwable error;

  private volatile Future<?> executorFuture;

  private volatile Thread runningThread;

  public SimpleTaskController(
      SimpleTaskExecutor executor, DeferredTask<T> task, Instant createdAt) {
    this.executor = executor;
    this.task = task;
    this.createdAt = createdAt;
    totalSteps = task.getTotalSteps();
  }

  public TaskSnapshot takeSnapshot() {
    return new TaskSnapshot(
        task,
        status.get(),
        totalSteps,
        completedSteps.get(),
        message,
        createdAt,
        startedAt,
        completedAt,
        cancelRequested.get(),
        error);
  }

  public void start() throws Exception {
    ProgressMonitor monitor = new SimpleProgressMonitor();
    task.start(monitor);
    notifyProgress();
  }

  public T getResult() throws Exception {
    return task.getResult();
  }

  public CompletableFuture<T> getResultFuture() {
    return resultFuture;
  }

  public void setExecutorFuture(Future<?> executorFuture) {
    this.executorFuture = executorFuture;
  }

  /**
   * Waits for the result, returning {@code true} if the result became available.
   *
   * A {@code false} value indicates some other outcome.
   */
  public boolean await() {
    long start = System.nanoTime();
    try {
      getResultFuture().get();
      long avail = System.nanoTime();
      LOG.info("Task {} result available after {}ms",
          task.getTaskTitle(), reportMs(start, avail));
      return true;
    } catch (InterruptedException e) {
      LOG.info("Task interrupted, continueing");
    } catch (ExecutionException e) {
      LOG.info("Error during wait, continueing");
    }
    return false;
  }

  /**
   * Waits the provided number of milliseconds for the result,
   * returning {@code true} if the result became available.
   *
   * A {@code false} value indicates some other outcome.
   */
  public boolean await(int waitMs) {
    long start = System.nanoTime();
    try {
      getResultFuture().get(waitMs, TimeUnit.MILLISECONDS);
      long avail = System.nanoTime();
      LOG.info("Task {} result available after {}ms",
          task.getTaskTitle(), reportMs(start, avail));
      return true;
    } catch (TimeoutException e) {
      long timeout = System.nanoTime();
      LOG.info("Task {} wait for {} milliseconds expired after {}ms, continueing",
          task.getTaskTitle(), waitMs, reportMs(start, timeout));
    } catch (InterruptedException e) {
      LOG.info("Task {} interrupted, continueing", task.getTaskTitle());
    } catch (ExecutionException e) {
      LOG.info("Error during wait for task {}, continueing", task.getTaskTitle());
    }
    return false;
  }

  public boolean requestCancel() {
    return cancelRequested.compareAndSet(false, true);
  }

  public void cancelTask(Instant cancelledAt) {
    task.cancel();
    Future<?> future = executorFuture;
    if (future != null) {
      future.cancel(true);
    }
    Thread thread = runningThread;
    if (thread != null) {
      thread.interrupt();
    }
    if (status.compareAndSet(TaskStatus.READY, TaskStatus.CANCELLED)) {
      completedAt = cancelledAt;
      resultFuture.cancel(false);
      executor.notifyCancelled(takeSnapshot());
    }
  }

  public boolean isCancellationRequested() {
    return cancelRequested.get();
  }

  public boolean isCancelledBeforeStart() {
    return status.get() == TaskStatus.CANCELLED;
  }

  public void markStarted(Instant startedAt) {
    runningThread = Thread.currentThread();
    this.startedAt = startedAt;
    status.set(TaskStatus.RUNNING);
  }

  public boolean markCompleted(Instant completedAt) {
    if (!status.compareAndSet(TaskStatus.RUNNING, TaskStatus.COMPLETED)) {
      return false;
    }
    this.completedAt = completedAt;
    return true;
  }

  public boolean markCancelled(Instant cancelledAt) {
    if (!status.compareAndSet(TaskStatus.RUNNING, TaskStatus.CANCELLED)) {
      return false;
    }
    completedAt = cancelledAt;
    return true;
  }

  public void recordError(Instant failedAt, Exception error) {
    this.error = error;
    this.completedAt = failedAt;
    status.set(TaskStatus.FAILED);
  }

  public void clearRunningThread() {
    runningThread = null;
  }

  private void notifyProgress() {
    executor.notifyProgress(takeSnapshot());
  }

  private long reportMs(long start, long timed) {
    return (timed - start) / NANOS_PER_MILLIS;
  }

  private void advance(int stepDelta, String progressMessage) {
    if (stepDelta != 0) {
      completedSteps.updateAndGet(current -> Math.max(0, current + stepDelta));
    }
    if (progressMessage != null) {
      message = progressMessage;
    }
    notifyProgress();
  }

  private void updateMessage(String progressMessage) {
    message = progressMessage == null ? "" : progressMessage;
    notifyProgress();
  }

  private void complete() {
    int total = totalSteps;
    if (total != ProgressMonitor.UNKNOWN_TOTAL) {
      completedSteps.set(total);
    }
    notifyProgress();
  }

  private boolean isCancelled() {
    return cancelRequested.get();
  }

  private class SimpleProgressMonitor implements ProgressMonitor {

    @Override
    public void advance(int stepDelta, String progressMessage) {
      SimpleTaskController.this.advance(stepDelta, progressMessage);
    }

    @Override
    public void updateMessage(String progressMessage) {
      SimpleTaskController.this.updateMessage(progressMessage);
      // No listener notification
    }

    @Override
    public void complete() {
      SimpleTaskController.this.complete();
    }

    @Override
    public boolean isCancelled() {
      return SimpleTaskController.this.isCancelled();
    }
  }
}
