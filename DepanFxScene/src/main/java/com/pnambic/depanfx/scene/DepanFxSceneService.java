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
package com.pnambic.depanfx.scene;

import java.util.Optional;
import java.util.stream.Stream;

import javafx.geometry.Rectangle2D;

/**
 * Encapsulate access to the containing scene
 * and allow access to a limited set of scene capabilities.
 */
public interface DepanFxSceneService {

  String getLabel();

  String getDescription();

  Rectangle2D getDisplayRectangle();

  DepanFxDialogRunner getDialogRunner();

  /**
   * Provide the current viewer in the scene.
   *
   * Only a viewer of the requested type will be returned.
   * A different requested type may return with a non-empty {@link Optional}.
   */
  <T extends DepanFxSceneViewer> Optional<T> getViewer(Class<T> viewerClass);

  Stream<DepanFxSceneViewer> streamViewers();

  /**
   * Add a viewer (a.k.a. panel) to the screen.
   */
  void addViewer(DepanFxSceneViewer viewer);

  /**
   * Close the screen and all panels.
   */
  void closeScene();
}
