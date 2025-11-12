package com.pnambic.depanfx.tasks;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Core execution service responsible for running deferred tasks.
 */
public interface TaskExecutorService {

  <T> TaskSubmission<T> submitTask(DeferredTask<T> task);

  boolean cancelTask(UUID taskId);

  Optional<TaskSnapshot> getTaskSnapshot(UUID taskId);

  Collection<TaskSnapshot> getActiveTasks();

  Collection<TaskSnapshot> getCompletedTasks();

  void addListener(TaskListener listener);

  void removeListener(TaskListener listener);
}
