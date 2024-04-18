package com.pnambic.depanfx.jogl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public class JoglSelectRectangle {

  private final double anchorX;

  private final double anchorY;

  private final double mouseX;

  private final double mouseY;

  public JoglSelectRectangle(
      double anchorX, double anchorY, double mouseX, double mouseY) {
    this.anchorX = anchorX;
    this.anchorY = anchorY;
    this.mouseX = mouseX;
    this.mouseY = mouseY;
  }

  /**
   * Draw the 2D selection rectangle on top of everything.
   */
  public void drawSelectRectangle(GL2 gl) {
    go2D(gl);

    gl.glColor4f(0.0f, 0.0f, 0.8f, 0.3f);
    gl.glBegin(GL2.GL_QUADS);
    gl.glVertex2d(anchorX, anchorY);
    gl.glVertex2d(mouseX, anchorY);
    gl.glVertex2d(mouseX, mouseY);
    gl.glVertex2d(anchorX, mouseY);
    gl.glEnd();

    gl.glColor4f(0.0f, 0.0f, 0.6f, 1.0f);
    gl.glLineWidth(1.0f);
    gl.glBegin(GL2.GL_LINE_STRIP);
    gl.glVertex2d(anchorX, anchorY);
    gl.glVertex2d(mouseX, anchorY);
    gl.glVertex2d(mouseX, mouseY);
    gl.glVertex2d(anchorX, mouseY);
    gl.glVertex2d(anchorX, anchorY);
    gl.glEnd();

    end2D(gl);
  }

  protected void go2D(GL2 gl) {
    /* Disable depth testing */
    gl.glDisable(GL2.GL_DEPTH_TEST);

    /* Select The Projection Matrix */
    gl.glMatrixMode(GL2.GL_PROJECTION);
    /* Store The Projection Matrix */
    gl.glPushMatrix();
    /* Reset The Projection Matrix */
    gl.glLoadIdentity();

    /* Set Up An Ortho Screen */
    GLU glu = GLU.createGLU(gl);
    glu.gluOrtho2D(0.0, 1.0d, 0, 1.0d);

    /* Select The Modelview Matrix */
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    /* Store the Modelview Matrix */
    gl.glPushMatrix();
    /* Reset The Modelview Matrix */
    gl.glLoadIdentity();
  }

  /**
   * Return to 3D rendering mode.
   */
  protected void end2D(GL2 gl) {
    /* Select The Projection Matrix */
    gl.glMatrixMode(GL2.GL_PROJECTION);
    /* Restore The Old Projection Matrix */
    gl.glPopMatrix();

    /* Select the Modelview Matrix */
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    /* Restore the Modelview Matrix */
    gl.glPopMatrix();

    /* Re-enable Depth Testing */
    gl.glEnable(GL2.GL_DEPTH_TEST);
  }
}
