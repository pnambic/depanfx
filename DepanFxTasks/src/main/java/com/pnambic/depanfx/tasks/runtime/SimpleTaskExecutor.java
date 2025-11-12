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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.pnambic.depanfx.tasks.DeferredTask;
import com.pnambic.depanfx.tasks.TaskExecutorService;
import com.pnambic.depanfx.tasks.TaskListener;
import com.pnambic.depanfx.tasks.TaskSnapshot;
import com.pnambic.depanfx.tasks.TaskSubmission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Default implementation of {@link TaskExecutorService}. Tasks are executed on
 * a dedicated thread pool and their progress is broadcast to registered
 * listeners.
 */
@Component
public class SimpleTaskExecutor implements TaskExecutorService {

  private static final Logger LOG =
      LoggerFactory.getLogger(SimpleTaskExecutor.class);

  private final ExecutorService executor;

  private final Clock clock;

  private final ConcurrentMap<DeferredTask<?>, SimpleTaskController<?>> controllers =
      new ConcurrentHashMap<>();

  private final CopyOnWriteArrayList<TaskListener> listeners =
      new CopyOnWriteArrayList<>();

  public SimpleTaskExecutor(Clock clock) {
    this.clock = clock;
    ThreadFactory threadFactory = new ThreadFactoryBuilder()
        .setNameFormat("depan-task-%d")
        .setDaemon(true)
        .build();
    executor = Executors.newCachedThreadPool(threadFactory);
  }

  @Override
  public <T> TaskSubmission<T> submitTask(DeferredTask<T> task) {
    SimpleTaskController<T> controller =
        new SimpleTaskController<T>(this, task, clock.instant());
    controllers.put(task, controller);
    notifyListeners(
        listener -> listener.onTaskScheduled(controller.takeSnapshot()));

    @SuppressWarnings("unchecked")
    Future<T> future = (Future<T>) executor.submit(() -> executeTask(controller));
    controller.setExecutorFuture(future);
    return new TaskSubmission<T>(task, controller.getResultFuture());
  }

  @Override
  public boolean cancelTask(DeferredTask<?> task) {
    SimpleTaskController<?> controller = controllers.get(task);
    if (controller == null) {
      return false;
    }
    if (!controller.requestCancel()) {
      return false;
    }
    controller.cancelTask(clock.instant());
    return true;
  }

  @Override
  public Optional<TaskSnapshot> getTaskSnapshot(DeferredTask<?> task) {
    SimpleTaskController<?> controller = controllers.get(task);
    if (controller == null) {
      return Optional.empty();
    }
    return Optional.of(controller.takeSnapshot());
  }

  @Override
  public Stream<TaskSnapshot> getActiveTasks() {
    return controllers.values().stream()
        .map(SimpleTaskController::takeSnapshot)
        .filter(t -> !t.isTerminal())
        .sorted((left, right) -> left.createdAt().compareTo(right.createdAt()));
  }

  @Override
  public Stream<TaskSnapshot> getCompletedTasks() {
    return controllers.values().stream()
        .map(SimpleTaskController::takeSnapshot)
        .filter(TaskSnapshot::isTerminal)
        .sorted((left, right) -> compareCompleted(left, right));
  }

  @Override
  public void addListener(TaskListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(TaskListener listener) {
    listeners.remove(listener);
  }

  public void notifyProgress(TaskSnapshot snapShot) {
    notifyListeners(
        listener -> listener.onTaskProgress(snapShot));
  }

  public void notifyCancelled(TaskSnapshot snapShot) {
    notifyListeners(
        listener -> listener.onTaskCancelled(snapShot));
  }

  private int compareCompleted(TaskSnapshot left, TaskSnapshot right) {
    Instant leftCompleted = left.completedAt();
    Instant rightCompleted = right.completedAt();
    if (leftCompleted == null && rightCompleted == null) {
      return 0;
    }
    if (leftCompleted == null) {
      return -1;
    }
    if (rightCompleted == null) {
      return 1;
    }
    return leftCompleted.compareTo(rightCompleted);
  }

  private <T> void executeTask(SimpleTaskController<T> controller) {
    if (controller.isCancelledBeforeStart()) {
      controller.getResultFuture().cancel(false);
      return;
    }
    controller.markStarted(clock.instant());
    notifyListeners(
        listener -> listener.onTaskStarted(controller.takeSnapshot()));
    try {
      notifyProgress(controller.takeSnapshot());
      controller.start();
      if (controller.isCancellationRequested()) {
        handleCancellation(controller);
        return;
      }
      T result = controller.getResult();
      handleCompletion(controller, result);
    } catch (CancellationException ex) {
      handleCancellation(controller);
    } catch (Exception ex) {
      handleFailure(controller, ex);
    } finally {
      controller.clearRunningThread();
    }
  }

  private <T> void handleCompletion(SimpleTaskController<T> controller, T result) {
    if (!controller.markCompleted(clock.instant())) {
      return;
    }
    controller.getResultFuture().complete(result);
    notifyListeners(
        listener -> listener.onTaskCompleted(controller.takeSnapshot()));
  }

  private void handleCancellation(SimpleTaskController<?> controller) {
    if (!controller.markCancelled(clock.instant())) {
      return;
    }
    controller.getResultFuture().cancel(false);
    notifyListeners(
        listener -> listener.onTaskCancelled(controller.takeSnapshot()));
  }

  private void handleFailure(
      SimpleTaskController<?> controller, Exception error) {
    controller.recordError(clock.instant(), error);
    controller.getResultFuture().completeExceptionally(error);
    notifyListeners(
        listener -> listener.onTaskFailed(controller.takeSnapshot(), error));
  }

  private void notifyListeners(Consumer<TaskListener> consumer) {
    for (TaskListener listener : listeners) {
      try {
        consumer.accept(listener);
      } catch (RuntimeException ex) {
        LOG.warn("Task listener {} threw an exception", listener, ex);
      }
    }
  }
}
