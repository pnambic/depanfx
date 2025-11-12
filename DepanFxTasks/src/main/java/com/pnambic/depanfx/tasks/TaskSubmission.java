package com.pnambic.depanfx.tasks;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Handle returned when a task is submitted for execution.
 */
public record TaskSubmission<T>(
    UUID taskId,
    DeferredTask<T> task,
    CompletableFuture<T> resultFuture) {

  public TaskSubmission {
    Objects.requireNonNull(taskId, "taskId");
    Objects.requireNonNull(task, "task");
    Objects.requireNonNull(resultFuture, "resultFuture");
  }
}
