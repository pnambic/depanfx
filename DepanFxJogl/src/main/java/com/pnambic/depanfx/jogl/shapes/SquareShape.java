package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.opengl.GL2;
import com.pnambic.depanfx.jogl.JoglShape;

public class SquareShape implements JoglShape {

  private float red;

  private float green;

  private float blue;

  public SquareShape(float red, float green, float blue) {
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  @Override
  public void draw(GL2 gl) {
    gl.glTranslatef(0.0f, 0.0f, -5.0f);
    gl.glBegin(GL2.GL_QUADS);
    gl.glColor3f(red, green, blue);
    gl.glVertex3f(-1.0f, 1.0f, 0.0f);
    gl.glVertex3f( 1.0f, 1.0f, 0.0f);
    gl.glVertex3f( 1.0f,-1.0f, 0.0f);
    gl.glVertex3f(-1.0f,-1.0f, 0.0f);
    gl.glEnd();
  }

  @Override
  public void step(GL2 gl) {
    // Not animated.
  }
}
