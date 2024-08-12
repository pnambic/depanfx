package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.opengl.GL2;
import com.pnambic.depanfx.jogl.JoglColor;

public class SimpleShape extends NodeShape {

  /////////////////////////////////////
  // Cached rendering entities

  protected TextureLoader labelTexture;

  protected SimpleShape(
      boolean isVisible,
      JoglColor fillColor, JoglColor borderColor, JoglColor highlightColor,
      float borderWidth,
      double shapeX, double shapeY, double shapeZ,
      double targetX, double targetY, double targetZ,
      boolean showLabel, String labelText, Object pickObject) {

    super(isVisible, fillColor, borderColor, highlightColor,
        borderWidth,
        shapeX, shapeY, shapeZ, targetX, targetY, targetZ,
        showLabel, labelText, pickObject);
  }

  public SimpleShape(
      boolean isVisible,
      JoglColor fillColor, JoglColor borderColor, JoglColor highlightColor,
      float borderWidth,
      double initialX, double initialY, double initialZ,
      boolean showLabel, String labelText, Object pickObject) {
    this(isVisible,
        fillColor, borderColor, highlightColor,
        borderWidth,
        initialX, initialY, initialZ,
        initialX, initialY, initialZ,
        showLabel, labelText, pickObject);
  }

  @Override
  public SimpleShape forUpdate() {
    SimpleShape result = new SimpleShape(
        isVisible,
        fillColor, borderColor, highlightColor,
        borderWidth,
        shapeX, shapeY, shapeZ,
        targetX, targetY, targetZ,
        showLabel, labelText, pickObject);
    super.fillUpdate(result);
    return result;
  }

  @Override
  protected void renderShape(GL2 gl) {
    setColor(gl, fillColor);
    gl.glBegin(GL2.GL_TRIANGLE_FAN);
    renderVertices(gl);
    gl.glEnd();
  }

  @Override
  protected void renderBorder(GL2 gl) {
    setColor(gl, borderColor);
    gl.glLineWidth(borderWidth);
    gl.glBegin(GL2.GL_LINE_LOOP);
    renderVertices(gl);
    gl.glEnd();
  }

  private void renderVertices(GL2 gl) {
    gl.glVertex3f(-1.0f, 1.0f, 0.0f);
    gl.glVertex3f( 1.0f, 1.0f, 0.0f);
    gl.glVertex3f( 1.0f,-1.0f, 0.0f);
    gl.glVertex3f(-1.0f,-1.0f, 0.0f);
  }
}
