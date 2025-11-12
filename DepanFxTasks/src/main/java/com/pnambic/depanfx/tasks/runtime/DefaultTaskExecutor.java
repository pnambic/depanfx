package com.pnambic.depanfx.tasks.runtime;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.pnambic.depanfx.tasks.DeferredSubTask;
import com.pnambic.depanfx.tasks.DeferredTask;
import com.pnambic.depanfx.tasks.ProgressMonitor;
import com.pnambic.depanfx.tasks.TaskExecutorService;
import com.pnambic.depanfx.tasks.TaskListener;
import com.pnambic.depanfx.tasks.TaskSnapshot;
import com.pnambic.depanfx.tasks.TaskStatus;
import com.pnambic.depanfx.tasks.TaskSubmission;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link TaskExecutorService}. Tasks are executed on
 * a dedicated thread pool and their progress is broadcast to registered
 * listeners.
 */
@Component
public class DefaultTaskExecutor implements TaskExecutorService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultTaskExecutor.class);

  private final ExecutorService executor;

  private final ConcurrentMap<UUID, TaskController<?>> controllers = new ConcurrentHashMap<>();

  private final Map<DeferredTask<?>, UUID> taskIds = Collections.synchronizedMap(new IdentityHashMap<>());

  private final CopyOnWriteArrayList<TaskListener> listeners = new CopyOnWriteArrayList<>();

  public DefaultTaskExecutor() {
    ThreadFactory threadFactory = new ThreadFactoryBuilder()
        .setNameFormat("depan-task-%d")
        .setDaemon(true)
        .build();
    executor = Executors.newCachedThreadPool(threadFactory);
  }

  @Override
  public <T> TaskSubmission<T> submitTask(DeferredTask<T> task) {
    Objects.requireNonNull(task, "task");
    UUID taskId = UUID.randomUUID();
    UUID parentId = resolveParentId(task);
    TaskController<T> controller = new TaskController<>(taskId, parentId, task);
    controllers.put(taskId, controller);
    taskIds.put(task, taskId);
    notifyListeners(listener -> listener.onTaskScheduled(controller.snapshot()));
    Future<?> future = executor.submit(() -> executeTask(controller));
    controller.setExecutorFuture(future);
    return new TaskSubmission<>(taskId, task, controller.getResultFuture());
  }

  @Override
  public boolean cancelTask(UUID taskId) {
    TaskController<?> controller = controllers.get(taskId);
    if (controller == null) {
      return false;
    }
    if (!controller.requestCancel()) {
      return false;
    }
    controller.cancelTask();
    return true;
  }

  @Override
  public Optional<TaskSnapshot> getTaskSnapshot(UUID taskId) {
    TaskController<?> controller = controllers.get(taskId);
    if (controller == null) {
      return Optional.empty();
    }
    return Optional.of(controller.snapshot());
  }

  @Override
  public Collection<TaskSnapshot> getActiveTasks() {
    return controllers.values().stream()
        .map(TaskController::snapshot)
        .filter(snapshot -> !snapshot.isTerminal())
        .sorted((left, right) -> left.createdAt().compareTo(right.createdAt()))
        .collect(Collectors.toUnmodifiableList());
  }

  @Override
  public Collection<TaskSnapshot> getCompletedTasks() {
    return controllers.values().stream()
        .map(TaskController::snapshot)
        .filter(TaskSnapshot::isTerminal)
        .sorted((left, right) -> {
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
        })
        .collect(Collectors.toUnmodifiableList());
  }

  @Override
  public void addListener(TaskListener listener) {
    listeners.add(Objects.requireNonNull(listener, "listener"));
  }

  @Override
  public void removeListener(TaskListener listener) {
    listeners.remove(listener);
  }

  private <T> void executeTask(TaskController<T> controller) {
    if (controller.isCancelledBeforeStart()) {
      controller.getResultFuture().cancel(false);
      taskIds.remove(controller.task);
      return;
    }
    controller.markStarted();
    notifyListeners(listener -> listener.onTaskStarted(controller.snapshot()));
    try {
      int initialSteps = controller.initialTotalSteps();
      if (initialSteps != ProgressMonitor.UNKNOWN_TOTAL) {
        controller.monitor.begin(initialSteps);
      }
      controller.task.start(controller.monitor);
      if (controller.isCancellationRequested()) {
        handleCancellation(controller);
        return;
      }
      T result = controller.task.getResult();
      handleCompletion(controller, result);
    } catch (CancellationException ex) {
      handleCancellation(controller);
    } catch (Exception ex) {
      handleFailure(controller, ex);
    } finally {
      controller.clearRunningThread();
      taskIds.remove(controller.task);
    }
  }

  private <T> void handleCompletion(TaskController<T> controller, T result) {
    if (!controller.markCompleted()) {
      return;
    }
    controller.getResultFuture().complete(result);
    notifyListeners(listener -> listener.onTaskCompleted(controller.snapshot()));
  }

  private void handleCancellation(TaskController<?> controller) {
    if (!controller.markCancelled()) {
      return;
    }
    controller.getResultFuture().cancel(false);
    notifyListeners(listener -> listener.onTaskCancelled(controller.snapshot()));
  }

  private void handleFailure(TaskController<?> controller, Exception error) {
    controller.recordError(error);
    controller.getResultFuture().completeExceptionally(error);
    notifyListeners(listener -> listener.onTaskFailed(controller.snapshot(), error));
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

  private UUID resolveParentId(DeferredTask<?> task) {
    if (task instanceof DeferredSubTask subTask) {
      DeferredTask<?> parent = subTask.getParent();
      if (parent != null) {
        UUID parentId = taskIds.get(parent);
        if (parentId == null) {
          LOG.warn("Parent task for subtask '{}' has not been registered", task.getTaskTitle());
        }
        return parentId;
      }
    }
    return null;
  }

  private final class TaskController<T> {

    private final UUID taskId;
    private final UUID parentTaskId;
    private final DeferredTask<T> task;
    private final CompletableFuture<T> resultFuture = new CompletableFuture<>();
    private final AtomicReference<TaskStatus> status = new AtomicReference<>(TaskStatus.READY);
    private final AtomicInteger totalSteps = new AtomicInteger(ProgressMonitor.UNKNOWN_TOTAL);
    private final AtomicInteger completedSteps = new AtomicInteger();
    private final AtomicBoolean cancelRequested = new AtomicBoolean();
    private final ProgressMonitor monitor = new TaskProgressMonitor();

    private final Instant createdAt = Instant.now();
    private volatile Instant startedAt;
    private volatile Instant completedAt;
    private volatile String message = "";
    private volatile Throwable error;
    private volatile Future<?> executorFuture;
    private volatile Thread runningThread;

    private TaskController(UUID taskId, UUID parentTaskId, DeferredTask<T> task) {
      this.taskId = taskId;
      this.parentTaskId = parentTaskId;
      this.task = task;
      totalSteps.set(task.getTotalSteps());
    }

    private CompletableFuture<T> getResultFuture() {
      return resultFuture;
    }

    private void setExecutorFuture(Future<?> executorFuture) {
      this.executorFuture = executorFuture;
    }

    private int initialTotalSteps() {
      return totalSteps.get();
    }

    private boolean requestCancel() {
      return cancelRequested.compareAndSet(false, true);
    }

    private void cancelTask() {
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
        completedAt = Instant.now();
        resultFuture.cancel(false);
        notifyListeners(listener -> listener.onTaskCancelled(snapshot()));
      }
    }

    private boolean isCancellationRequested() {
      return cancelRequested.get();
    }

    private boolean isCancelledBeforeStart() {
      return status.get() == TaskStatus.CANCELLED;
    }

    private void markStarted() {
      runningThread = Thread.currentThread();
      startedAt = Instant.now();
      status.set(TaskStatus.RUNNING);
    }

    private boolean markCompleted() {
      if (!status.compareAndSet(TaskStatus.RUNNING, TaskStatus.COMPLETED)) {
        return false;
      }
      completedAt = Instant.now();
      return true;
    }

    private boolean markCancelled() {
      if (!status.compareAndSet(TaskStatus.RUNNING, TaskStatus.CANCELLED)) {
        return false;
      }
      completedAt = Instant.now();
      return true;
    }

    private void recordError(Exception ex) {
      error = ex;
      status.set(TaskStatus.FAILED);
      completedAt = Instant.now();
    }

    private void clearRunningThread() {
      runningThread = null;
    }

    private TaskSnapshot snapshot() {
      return new TaskSnapshot(
          taskId,
          parentTaskId,
          task.getTaskTitle(),
          status.get(),
          totalSteps.get(),
          completedSteps.get(),
          message,
          createdAt,
          startedAt,
          completedAt,
          cancelRequested.get(),
          error);
    }

    private final class TaskProgressMonitor implements ProgressMonitor {

      @Override
      public void begin(int totalSteps) {
        TaskController.this.totalSteps.set(totalSteps);
        notifyProgress();
      }

      @Override
      public void advance(int stepDelta, String progressMessage) {
        if (stepDelta != 0) {
          completedSteps.updateAndGet(current -> Math.max(0, current + stepDelta));
        }
        if (progressMessage != null) {
          message = progressMessage;
        }
        notifyProgress();
      }

      @Override
      public void updateMessage(String progressMessage) {
        message = progressMessage == null ? "" : progressMessage;
        notifyProgress();
      }

      @Override
      public void complete() {
        int total = totalSteps.get();
        if (total != UNKNOWN_TOTAL) {
          completedSteps.set(total);
        }
        notifyProgress();
      }

      @Override
      public boolean isCancelled() {
        return cancelRequested.get();
      }
    }

    private void notifyProgress() {
      notifyListeners(listener -> listener.onTaskProgress(snapshot()));
    }
  }
}
