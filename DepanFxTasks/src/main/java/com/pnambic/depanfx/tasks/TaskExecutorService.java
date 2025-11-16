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

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Core execution service responsible for running deferred tasks.
 */
public interface TaskExecutorService {

  <T> TaskSubmission<T> submitTask(DeferredTask<T> task);

  boolean cancelTask(DeferredTask<?> task);

  Optional<TaskSnapshot> getTaskSnapshot(DeferredTask<?> task);

  Stream<TaskSnapshot> getActiveTasks();

  Stream<TaskSnapshot> getCompletedTasks();

  void addListener(TaskListener listener);

  void removeListener(TaskListener listener);

  boolean awaitTask(DeferredTask<?> task);

  boolean awaitTask(DeferredTask<?> task, int waitMs);
}
