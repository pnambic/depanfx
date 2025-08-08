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
package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.opengl.GL2;
import com.pnambic.depanfx.jogl.JoglRenderer;

import java.util.Collections;
import java.util.List;

/**
 * Handle rendering of secondary node shapes.  Useful for overlays, sprites,
 * and other effects that are not the primary node shape.
 */
public interface NodeOverlay {

  static final List<NodeOverlay> EMPTY_OVERLAYS = Collections.emptyList();

  /**
   * Handle the rendering of the node overlay.
   */
  void draw(GL2 gl, JoglRenderer renderer, NodeShape nodeShape);
}
