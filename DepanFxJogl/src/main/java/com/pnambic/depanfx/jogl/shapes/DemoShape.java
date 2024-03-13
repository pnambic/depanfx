package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.opengl.GL2;
import com.pnambic.depanfx.jogl.JoglRenderer;
import com.pnambic.depanfx.jogl.JoglShape;

public class DemoShape implements JoglShape {

  private float rotateT;

  @Override
  public void draw(GL2 gl, JoglRenderer renderer) {
    gl.glTranslatef(0.0f, 0.0f, -5.0f);
    gl.glRotatef(rotateT, 1.0f, 0.0f, 0.0f);
    gl.glRotatef(rotateT, 0.0f, 1.0f, 0.0f);
    gl.glRotatef(rotateT, 0.0f, 0.0f, 1.0f);
    gl.glBegin(GL2.GL_QUADS);
    gl.glColor3f(0.0f, 1.0f, 1.0f);
    gl.glVertex3f(-1.0f, 1.0f, 0.0f);
    gl.glVertex3f( 1.0f, 1.0f, 0.0f);
    gl.glVertex3f( 1.0f,-1.0f, 0.0f);
    gl.glVertex3f(-1.0f,-1.0f, 0.0f);
    gl.glEnd();
  }

  @Override
  public void step(GL2 gl, JoglRenderer renderer) {
    rotateT += 0.2f;
  }
}
