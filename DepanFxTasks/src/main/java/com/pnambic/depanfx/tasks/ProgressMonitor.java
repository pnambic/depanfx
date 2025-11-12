package com.pnambic.depanfx.tasks;

import java.util.concurrent.CancellationException;

/**
 * A simple callback contract that long running tasks use to report their
 * progress back to the task executor. Implementations are responsible for
 * forwarding the updates to interested listeners (for example, GUI panels or
 * logging services).
 */
public interface ProgressMonitor {

  /**
   * Sentinel value for tasks that cannot estimate their total step count.
   */
  int UNKNOWN_TOTAL = -1;

  /**
   * Signals the start of the task and provides (or updates) the total number of
   * expected steps.
   */
  void begin(int totalSteps);

  /**
   * Advances the completed step count by {@code stepDelta}.
   */
  default void advance(int stepDelta) {
    advance(stepDelta, null);
  }

  /**
   * Advances the completed step count by {@code stepDelta} and publishes the
   * supplied message.
   */
  void advance(int stepDelta, String message);

  /**
   * Convenience method that advances the progress by a single step and sets the
   * supplied message.
   */
  default void advance(String message) {
    advance(1, message);
  }

  /**
   * Convenience method that advances the progress by a single step.
   */
  default void advance() {
    advance(1, null);
  }

  /**
   * Updates the current message without changing the progress counters.
   */
  void updateMessage(String message);

  /**
   * Forces the monitor to treat the task as fully progressed. This is useful
   * when the task completes before all of the expected steps are reported.
   */
  void complete();

  /**
   * Returns {@code true} if cancellation has been requested for the owning
   * task.
   */
  boolean isCancelled();

  /**
   * Throws a {@link CancellationException} when cancellation has been
   * requested. Tasks can use this helper to abort their execution without
   * manually inspecting {@link #isCancelled()}.
   */
  default void checkCancelled() {
    if (isCancelled()) {
      throw new CancellationException("Task cancelled");
    }
  }
}
