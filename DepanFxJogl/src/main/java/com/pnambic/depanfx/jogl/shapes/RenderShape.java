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

import java.awt.Shape;

/**
 * Bundles a {@link Shape} with a cached {@link VboShapeRender} so that
 * multiple node shapes can share the same renderable geometry.
 */
public class RenderShape {

  private final Shape awtShape;

  private final VboShapeRender shapeRender;

  public RenderShape(Shape awtShape, VboShapeRender shapeRender) {
    this.awtShape = awtShape;
    this.shapeRender = shapeRender;
  }

  public static RenderShape build(Shape awtShape) {
    VboShapeRender shapeRender = VboShapeRender.build(
        awtShape.getPathIterator(null, NodeShape.SHAPE_FLATNESS));
    return new RenderShape(awtShape, shapeRender);
  }

  public Shape getAwtShape() {
    return awtShape;
  }

  public void drawShape(GL2 gl) {
    shapeRender.drawShape(gl);
  }

  public void drawBorder(GL2 gl) {
    shapeRender.drawBorder(gl);
  }

  /** Release any OpenGL buffers held by this shape. */
  public void dispose(GL2 gl) {
    shapeRender.dispose(gl);
  }
}
