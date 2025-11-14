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

import java.time.Instant;

/**
 * Immutable representation of a task's current state. Snapshots are generated
 * by the executor whenever listeners need to be informed of state changes.
 */
public record TaskSnapshot(
    DeferredTask<?> task,
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
