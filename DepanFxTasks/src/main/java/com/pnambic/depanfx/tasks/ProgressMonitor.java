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
 * Mechanism tasks to report their progress.
 */
public interface ProgressMonitor {

  /**
   * Sentinel value for tasks that cannot estimate their total step count.
   */
  int UNKNOWN_TOTAL = -1;

  /**
   * Advances the completed step count by {@code stepDelta} and publishes the
   * supplied message.
   */
  void advance(int stepDelta, String message);


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
}
