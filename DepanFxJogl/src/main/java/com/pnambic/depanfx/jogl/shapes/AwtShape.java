/*
 * Copyright 2024 The Depan Project Authors
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
 *
 * Components based on the legacy Depan's AWTShape.java
 * {@code com/google/devtools/depan/eclipse/visualization/ogl/AWTShape.java}
 */
package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.opengl.GL2;
import com.pnambic.depanfx.jogl.JoglColor;

public class AwtShape extends NodeShape {

  // The render shape is typically shared, and should not disposed
  // by the AWT shape.
  private RenderShape renderShape;

  public AwtShape(
      RenderShape renderShape,
      boolean isVisible,
      JoglColor fillColor, JoglColor edgeColor,
      JoglColor borderColor, JoglColor highlightColor,
      float borderWidth,
      double shapeX, double shapeY, double shapeZ,
      double targetX, double targetY, double targetZ,
      boolean showLabel, String labelText, Object pickObject) {

    super(isVisible, fillColor, edgeColor, borderColor, highlightColor,
        borderWidth,
        shapeX, shapeY, shapeZ, targetX, targetY, targetZ,
        showLabel, labelText, pickObject);

    this.renderShape = renderShape;
  }

  public AwtShape(
      RenderShape renderShape,
      boolean isVisible,
      JoglColor fillColor, JoglColor borderColor, JoglColor highlightColor,
      float borderWidth,
      double initialX, double initialY, double initialZ,
      boolean showLabel, String labelText, Object pickObject) {
    this(renderShape, isVisible,
        fillColor, borderColor, borderColor, highlightColor,
        borderWidth,
        initialX, initialY, initialZ,
        initialX, initialY, initialZ,
        showLabel, labelText, pickObject);
  }

  public void setRenderShape(RenderShape renderShape) {
    this.renderShape = renderShape;
  }

  @Override
  public AwtShape forUpdate() {
    AwtShape result = new AwtShape(
        renderShape, isVisible,
        shapeColor, edgeColor, borderColor, highlightColor,
        borderWidth,
        shapeX, shapeY, shapeZ,
        targetX, targetY, targetZ,
        showLabel, labelText, pickObject);
    super.fillUpdate(result);
    return result;
  }

  @Override
  public boolean contains(double posX, double posY) {
    return renderShape.getAwtShape().contains(posX, posY);
  }

  @Override
  protected void renderShape(GL2 gl) {
    renderShape.drawShape(gl);
  }

  @Override
  protected void renderBorder(GL2 gl) {
    gl.glLineWidth(borderWidth);
    renderShape.drawBorder(gl);
  }

  @Override
  public void dispose(GL2 gl) {
    super.dispose(gl);
  }
}
