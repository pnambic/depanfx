package com.pnambic.depanfx.tasks;

/**
 * Listener interface for parties interested in observing task execution.
 */
public interface TaskListener {

  default void onTaskScheduled(TaskSnapshot snapshot) {
    // Default no-op.
  }

  default void onTaskStarted(TaskSnapshot snapshot) {
    // Default no-op.
  }

  default void onTaskProgress(TaskSnapshot snapshot) {
    // Default no-op.
  }

  default void onTaskCompleted(TaskSnapshot snapshot) {
    // Default no-op.
  }

  default void onTaskCancelled(TaskSnapshot snapshot) {
    // Default no-op.
  }

  default void onTaskFailed(TaskSnapshot snapshot, Throwable error) {
    // Default no-op.
  }
}
