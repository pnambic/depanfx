package com.pnambic.depanfx.tasks;

/**
 * Represents a unit of work that can be executed asynchronously by the task
 * executor. Tasks expose metadata that is useful for user interfaces and other
 * observers while they are running.
 */
public interface DeferredTask<T> {

  /**
   * Human-readable title that summarizes the work being performed.
   */
  String getTaskTitle();

  /**
   * Estimated number of steps the task will report. If the value is unknown,
   * implementers should return {@link ProgressMonitor#UNKNOWN_TOTAL}.
   */
  int getTotalSteps();

  /**
   * Performs the task's work. Implementations should use the supplied
   * {@link ProgressMonitor} to report progress and respond to cancellation
   * requests.
   */
  void start(ProgressMonitor monitor) throws Exception;

  /**
   * Current lifecycle status for the task.
   */
  TaskStatus getStatus();

  /**
   * Returns the task's result after the execution completes successfully.
   */
  T getResult() throws Exception;

  /**
   * Requests cancellation of the task. Implementations should attempt to stop
   * work quickly and release resources.
   */
  void cancel();
}
