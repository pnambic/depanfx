package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.opengl.GL2;

/**
 * Provide a sequence of line coordinates as a vector of float values.
 * Each point is accessed with a stride of 3.
 *
 * Each triplet of floats is a 3D point (x, y, z).  The pointCount value
 * is 1/3 the size of linePoints.
 */
public class LinePoints {

  public static final int STRIDE = 3;

  public final int pointCount;

  public final float[] linePoints;

  public static final LinePoints EMPTY = new LinePoints(0, null) {

    @Override
    public void drawPoints(GL2 gl, int mode) {
      // do nothing - avoid glBegin and glEnd calls.
    }
  };

  LinePoints(int pointCount, float[] linePoints) {
    this.pointCount = pointCount;
    this.linePoints = linePoints;
  }

  public void drawPoints(GL2 gl, int mode) {
    gl.glBegin(mode);
    drawPoints(gl);
    gl.glEnd();
  }

  public static int getOffset(int index) {
    return index * STRIDE;
  }

  public boolean hasEndpoints() {
    return pointCount >= 2;
  }

  public boolean hasPoints() {
    return pointCount > 0;
  }

  private void drawPoints(GL2 gl) {
    int index = 0;  // Stride by 3
    for (int count = 0; count < pointCount; ++count) {
      gl.glVertex3fv(linePoints, index);
      index += STRIDE;
    }
  }
}
