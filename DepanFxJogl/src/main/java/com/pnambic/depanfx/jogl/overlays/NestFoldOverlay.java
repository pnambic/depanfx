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

package com.pnambic.depanfx.jogl.overlays;

import com.jogamp.opengl.GL2;
import com.pnambic.depanfx.jogl.JoglColor;
import com.pnambic.depanfx.jogl.JoglRenderer;
import com.pnambic.depanfx.jogl.shapes.NodeShape;

/**
 * Overlay a small double circle near the upper-left corner of the supplied
 * node.  The inner circle is filled for shut nests and hollow for open nests.
 */
public abstract class NestFoldOverlay implements NodeOverlay {

  public static final NestFoldOverlay OPEN_NEST_OVERLAY =
      new NestOpenOverlay();

  public static final NestFoldOverlay SHUT_NEST_OVERLAY =
      new NestShutOverlay();

  private static final int CIRCLE_STEPS = 24;

  private static final double OFFSET_X = -1.2d;

  private static final double OFFSET_Y =  1.2d;

  private static final double OFFSET_Z = 0.0d;

  private static final double INNER_RADIUS = 0.18d;

  private static final double OUTER_RADIUS = 0.25d;

  private static final float SHUT_OUTER_WIDTH = 2.0f;

  private static final float OPEN_OUTER_WIDTH = 4.0f;

  public NestFoldOverlay() {
  }

  @Override
  public void draw(GL2 gl, JoglRenderer renderer, NodeShape base) {
    gl.glPushMatrix();
    gl.glTranslated(OFFSET_X, OFFSET_Y, OFFSET_Z);
    drawOverlay(gl, base);
    gl.glPopMatrix();
  }

  abstract protected void drawOverlay(GL2 gl, NodeShape base);

  protected void drawShape(GL2 gl) {
    gl.glBegin(GL2.GL_TRIANGLE_FAN);
    gl.glVertex3d(0.0d, 0.0d, 0.0d);
    for (int i = 0; i <= CIRCLE_STEPS; i++) {
      double angle = 2.0d * Math.PI * i / CIRCLE_STEPS;
      double x = INNER_RADIUS * Math.cos(angle);
      double y = INNER_RADIUS * Math.sin(angle);
      gl.glVertex3d(x, y, OFFSET_Z);
    }
    gl.glEnd();
  }

  protected void drawBorder(GL2 gl) {
    gl.glBegin(GL2.GL_LINE_LOOP);
    for (int i = 0; i < CIRCLE_STEPS; i++) {
      double angle = 2.0d * Math.PI * i / CIRCLE_STEPS;
      double x = OUTER_RADIUS * Math.cos(angle);
      double y = OUTER_RADIUS * Math.sin(angle);
      gl.glVertex3d(x, y, OFFSET_Z);
    }
    gl.glEnd();
  }

  protected void updateColor(GL2 gl, JoglColor borderColor) {
    gl.glColor3d(borderColor.red, borderColor.green, borderColor.blue);
  }

  private static class NestOpenOverlay extends NestFoldOverlay {

    @Override
    protected void drawOverlay(GL2 gl, NodeShape base) {
      updateColor(gl, base.borderColor);
      gl.glLineWidth(OPEN_OUTER_WIDTH);
      drawBorder(gl);
    }
  }

  private static class NestShutOverlay extends NestFoldOverlay {

    @Override
    protected void drawOverlay(GL2 gl, NodeShape base) {
      updateColor(gl, base.borderColor);
      gl.glLineWidth(SHUT_OUTER_WIDTH);
      drawBorder(gl);

      updateColor(gl, base.shapeColor);
      drawShape(gl);
    }
  }
}
