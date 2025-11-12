package com.pnambic.depanfx.tasks;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable representation of a task's current state. Snapshots are generated
 * by the executor whenever listeners need to be informed of state changes.
 */
public record TaskSnapshot(
    UUID taskId,
    UUID parentTaskId,
    String title,
    TaskStatus status,
    int totalSteps,
    int completedSteps,
    String message,
    Instant createdAt,
    Instant startedAt,
    Instant completedAt,
    boolean cancelRequested,
    Throwable error) {

  public TaskSnapshot {
    Objects.requireNonNull(taskId, "taskId");
    Objects.requireNonNull(title, "title");
    Objects.requireNonNull(status, "status");
    message = message == null ? "" : message;
  }

  /**
   * Returns the fraction of progress that has been completed or {@code 0.0}
   * when the total step count is unknown.
   */
  public double progressFraction() {
    if (totalSteps <= 0 || completedSteps < 0) {
      return 0.0d;
    }
    if (completedSteps >= totalSteps) {
      return 1.0d;
    }
    return (double) completedSteps / (double) totalSteps;
  }

  public boolean isTerminal() {
    return status.isTerminal();
  }
}
