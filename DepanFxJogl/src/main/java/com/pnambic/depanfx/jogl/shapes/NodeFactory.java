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
import com.pnambic.depanfx.jogl.JoglColor;

import java.util.EnumMap;
import java.util.Map;

public class NodeFactory {

  private final Map<NodeKind, RenderShape> baseShapes =
      new EnumMap<>(NodeKind.class);

  public NodeFactory() {
    // Default constructor
  }

  public AwtShape buildShape(
    NodeKind shape,
    boolean isVisible,
    JoglColor fillColor, JoglColor borderColor, JoglColor highlightColor,
    float borderWidth,
    double xPos, double yPos, double zPos,
    boolean showLabel, String nodeLabel, Object pickNode) {

    return new AwtShape(
        getNodeShape(shape),
        isVisible,
        fillColor, borderColor, highlightColor,
        borderWidth,
        xPos, yPos, zPos,
        showLabel, nodeLabel, pickNode);
  }

  public RenderShape getRenderShape(NodeKind shape) {
    return getNodeShape(shape);
  }

  public void dispose(GL2 gl) {
    baseShapes.values().forEach(s -> s.dispose(gl));
    baseShapes.clear();
  }

  private RenderShape getNodeShape(NodeKind shape) {
    return baseShapes.computeIfAbsent(
        shape,
        s -> RenderShape.build(shape.buildAwtShape()));
  }
}