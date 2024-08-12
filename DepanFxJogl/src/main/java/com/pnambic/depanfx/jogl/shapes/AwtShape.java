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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Shape;
import java.awt.geom.PathIterator;

public class AwtShape extends NodeShape {

  public static final float FLOAT_ZERO = 0.0f;

  public static final double DOUBLE_ZERO = 0.0d;

  public static final double SHAPE_FLATNESS = 0.05d;

  private static final Logger LOG = LoggerFactory.getLogger(AwtShape.class);

  private Shape shapeAwt;

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
  protected void renderShape(GL2 gl) {
    float[] lastMoveTo = new float[6];
    float[] currSegment = new float[6];

    setShapeColor(gl);

    PathIterator it = shapeAwt.getPathIterator(null, SHAPE_FLATNESS);
    int opened = 0;
    int closed = 0;
    while (!it.isDone()) {
      int res = it.currentSegment(currSegment);
      switch (res) {
        case PathIterator.SEG_CLOSE:
          // gl.glVertex3f(lastMoveTo[0], lastMoveTo[1], FLOAT_ZERO);
          gl.glEnd();
          closed++;
          break;
        case PathIterator.SEG_MOVETO:
          gl.glBegin(GL2.GL_TRIANGLE_FAN);
          opened++;
          gl.glVertex3f(currSegment[0], currSegment[1], FLOAT_ZERO);
          System.arraycopy(currSegment, 0, lastMoveTo, 0, lastMoveTo.length);
          break;
        case PathIterator.SEG_LINETO:
          gl.glVertex3f(currSegment[0], currSegment[1], FLOAT_ZERO);
          break;
        default:
          throw new Error("Error while drawing AWT shape. "
              + "Path iterator setment not handled:" + res);
      }
      it.next();
    }
    while (closed < opened) {
      LOG.warn("missed a close on shape {}", labelText);
      gl.glEnd();
      closed++;
    }
  }

  @Override
  protected void renderBorder(GL2 gl) {
    float[] lastMoveTo = new float[6];
    float[] currSegment = new float[6];

    setEdgeColor(gl);
    gl.glLineWidth(borderWidth);

    PathIterator it = shapeAwt.getPathIterator(null, SHAPE_FLATNESS);
    int opened = 0;
    int closed = 0;
    while (!it.isDone()) {
      int res = it.currentSegment(currSegment);
      switch (res) {
        case PathIterator.SEG_CLOSE:
          // gl.glVertex3f(lastMoveTo[0], lastMoveTo[1], FLOAT_ZERO);
          gl.glEnd();
          closed++;
          break;
        case PathIterator.SEG_MOVETO:
          gl.glBegin(GL2.GL_LINE_LOOP);
          opened++;
          gl.glVertex3f(currSegment[0], currSegment[1], FLOAT_ZERO);
          System.arraycopy(currSegment, 0, lastMoveTo, 0, lastMoveTo.length);
          break;
        case PathIterator.SEG_LINETO:
          gl.glVertex3f(currSegment[0], currSegment[1], FLOAT_ZERO);
          break;
        default:
          throw new Error("Error while drawing AWT border. "
              + "Path iterator setment not handled:" + res);
      }
      it.next();
    }
    while (closed < opened) {
      LOG.warn("missed a close on border {}", labelText);
      gl.glEnd();
      closed++;
    }
  }
}
