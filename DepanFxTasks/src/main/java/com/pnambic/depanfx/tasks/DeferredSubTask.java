package com.pnambic.depanfx.tasks;

/**
 * Marks a {@link DeferredTask} as a subtask of another deferred task. The task
 * executor uses this metadata to build a hierarchy of running work so that user
 * interfaces can display the relationships between tasks.
 */
public interface DeferredSubTask {

  /**
   * Returns the parent task that spawned this subtask. May be {@code null} when
   * the parent is unknown.
   */
  DeferredTask<?> getParent();
}
