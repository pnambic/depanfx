package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.Matrix4;

public class ArrowShapes {

  private ArrowShapes() {
    // Prevent instantiation.
  }

  public static ArrowShape NONE = new ArrowShape() {

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

  public static class Open extends FilledLineArrow {

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
    double angle = Math.atan2(deltaY, deltaX) - ( Math.PI / 2.0d);

    Matrix4 matrix = new Matrix4();
    matrix.loadIdentity();
    matrix.translate(targetX, targetY, targetZ);
    matrix.rotate((float) angle, 0.0f, 0.0f, 1.0f);
    return matrix.getMatrix();
  }
}
