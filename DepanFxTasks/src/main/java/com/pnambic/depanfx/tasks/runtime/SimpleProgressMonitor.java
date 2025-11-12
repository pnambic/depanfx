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
package com.pnambic.depanfx.tasks.runtime;

import com.pnambic.depanfx.tasks.ProgressMonitor;

public class SimpleProgressMonitor implements ProgressMonitor {

  private final SimpleTaskController<?> controller;

  public SimpleProgressMonitor(SimpleTaskController<?> controller) {
    this.controller = controller;
  }

  @Override
  public void advance(int stepDelta, String progressMessage) {
    controller.advance(stepDelta, progressMessage);
  }

  @Override
  public void updateMessage(String progressMessage) {
    controller.updateMessage(progressMessage);
    // No listener notification
  }

  @Override
  public void complete() {
    controller.complete();
  }

  @Override
  public boolean isCancelled() {
    return controller.isCancelled();
  }
}
