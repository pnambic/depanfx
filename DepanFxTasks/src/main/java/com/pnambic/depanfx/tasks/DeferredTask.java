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
package com.pnambic.depanfx.tasks;

/**
 * Represents a unit of work that can be executed asynchronously by the task
 * executor.
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
