package com.pnambic.depanfx.tasks;

/**
 * Enumerates the life-cycle states for deferred tasks.
 */
public enum TaskStatus {
  READY,
  RUNNING,
  CANCELLED,
  FAILED,
  COMPLETED;

  /**
   * Returns {@code true} when the status represents a terminal state.
   */
  public boolean isTerminal() {
    return switch (this) {
      case CANCELLED, FAILED, COMPLETED -> true;
      default -> false;
    };
  }
}
