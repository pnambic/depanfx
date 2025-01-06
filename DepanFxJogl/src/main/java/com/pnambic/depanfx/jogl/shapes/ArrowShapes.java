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
 */
package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.math.Matrix4;

public class ArrowShapes {

  private ArrowShapes() {
    // Prevent instantiation.
  }

  public static final ArrowShape NONE = new ArrowShape() {

    @Override
    public void draw(GL2 gl) {
      // Nothing.
    }
  };

  public static abstract class LineArrow implements ArrowShape {

    private final int mode;

    private final LinePoints arrowPoints;

    private final float[] transformationMatrix;

    protected LineArrow(
        int mode, LinePoints arrowPoints, float[] transformationMatrix) {
      this.mode = mode;
      this.arrowPoints = arrowPoints;
      this.transformationMatrix = transformationMatrix;
    }

    @Override
    public void draw(GL2 gl) {
      gl.glMultMatrixf(transformationMatrix, 0);
      arrowPoints.glVertex(gl, mode);
    }
  }

  /////////////////////////////////////
  // Arrows by rendering mode.

  public static class OpenLineArrow extends LineArrow {

    private OpenLineArrow(
        LinePoints arrowPoints, float[] transformationMatrix) {
      super(GL2.GL_LINE_STRIP, arrowPoints, transformationMatrix);
    }
  }

  public static class ClosedLineArrow extends LineArrow {

    protected ClosedLineArrow(
        LinePoints arrowPoints, float[] transformationMatrix) {
      super(GL2.GL_LINE_LOOP, arrowPoints, transformationMatrix);
    }
  }

  public static class FilledLineArrow extends LineArrow {

    protected FilledLineArrow(
        LinePoints arrowPoints, float[] transformationMatrix) {
      super(GL2.GL_TRIANGLE_FAN, arrowPoints, transformationMatrix);
    }
  }

  /////////////////////////////////////
  // Concrete Arrow Shapes

  public static class Artistic extends FilledLineArrow {

    protected Artistic(float[] transformationMatrix) {
      super(ArrowLinePoints.ARTISTIC_ARROW_POINTS, transformationMatrix);
    }
  }

  public static class Chevron extends ClosedLineArrow {

    protected Chevron(float[] transformationMatrix) {
      super(ArrowLinePoints.ARTISTIC_ARROW_POINTS, transformationMatrix);
    }
  }

  public static class Filled extends FilledLineArrow {

    protected Filled(float[] transformationMatrix) {
      super(ArrowLinePoints.TRIANGLE_ARROW_POINTS, transformationMatrix);
    }
  }

  public static class Triangle extends ClosedLineArrow {

    protected Triangle(float[] transformationMatrix) {
      super(ArrowLinePoints.TRIANGLE_ARROW_POINTS, transformationMatrix);
    }
  }

  public static class Open extends OpenLineArrow {

    protected Open(float[] transformationMatrix) {
      super(ArrowLinePoints.TRIANGLE_ARROW_POINTS, transformationMatrix);
    }
  }

  /////////////////////////////////////////////
  // Build an placement transformation matrix.

  public static float[] buildTransform(
      float sourceX, float sourceY, float sourceZ,
      float targetX, float targetY, float targetZ) {

    float deltaX = targetX - sourceX;
    float deltaY = targetY - sourceY;
    float deltaZ = targetZ - sourceZ;

    Matrix4 matrix = new Matrix4();
    matrix.loadIdentity();

    // Translate to the target position
    matrix.translate(targetX, targetY, targetZ);

    // Rotate to align with the target direction
    double rotateAngle = Math.atan2(deltaY, deltaX);
    matrix.rotate((float) rotateAngle, 0.0f, 0.0f, 1.0f);

    // Tilt the line to match the target z-coordinate.
    if (deltaZ != 0.0f) {
      double planarDist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
      double tiltAngle = Math.atan2(-deltaZ, planarDist);

      // Tilt via the y-azis, since arrows are drawn pointing right,
      // with the nose at (0, 0) and the tail near (-1, 0).
      matrix.rotate((float) tiltAngle, 0.0f, 1.0f, 0.0f);
    }

    return matrix.getMatrix();
  }
}
