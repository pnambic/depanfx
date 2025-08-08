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

import java.awt.Shape;

public class AwtShape extends NodeShape {

  public Shape shapeAwt;

  private PathIteratorRender pathRender;

  public AwtShape(
      Shape shapeAwt,
      boolean isVisible,
      JoglColor fillColor, JoglColor edgeColor, JoglColor borderColor, JoglColor highlightColor,
      float borderWidth,
      double shapeX, double shapeY, double shapeZ,
      double targetX, double targetY, double targetZ,
      boolean showLabel, String labelText, Object pickObject) {

    super(isVisible, fillColor, edgeColor, borderColor, highlightColor,
        borderWidth,
        shapeX, shapeY, shapeZ, targetX, targetY, targetZ,
        showLabel, labelText, pickObject);

    this.shapeAwt = shapeAwt;
  }

  public AwtShape(
      Shape shape,
      boolean isVisible,
      JoglColor fillColor, JoglColor borderColor, JoglColor highlightColor,
      float borderWidth,
      double initialX, double initialY, double initialZ,
      boolean showLabel, String labelText, Object pickObject) {
    this(shape, isVisible,
        fillColor, borderColor, borderColor, highlightColor,
        borderWidth,
        initialX, initialY, initialZ,
        initialX, initialY, initialZ,
        showLabel, labelText, pickObject);
  }

  @Override
  public AwtShape forUpdate() {
    AwtShape result = new AwtShape(
        shapeAwt, isVisible,
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
    return shapeAwt.contains(posX, posY);
  }

  @Override
  protected void renderShape(GL2 gl) {
    if (pathRender == null) {
      pathRender = new PathIteratorRender(shapeAwt);
    }
    pathRender.drawShape(gl);
  }

  @Override
  protected void renderBorder(GL2 gl) {
    gl.glLineWidth(borderWidth);
    if (pathRender == null) {
      pathRender = new PathIteratorRender(shapeAwt);
    }
    pathRender.drawBorder(gl);
  }
}
