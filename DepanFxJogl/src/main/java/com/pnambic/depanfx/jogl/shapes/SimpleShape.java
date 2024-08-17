package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.opengl.GL2;
import com.pnambic.depanfx.jogl.JoglColor;

public class SimpleShape extends NodeShape {

  /////////////////////////////////////
  // Cached rendering entities

  protected TextureLoader labelTexture;

  protected SimpleShape(
      boolean isVisible,
      JoglColor shapeColor, JoglColor edgeColor,
      JoglColor borderColor, JoglColor highlightColor,
      float borderWidth,
      double shapeX, double shapeY, double shapeZ,
      double targetX, double targetY, double targetZ,
      boolean showLabel, String labelText, Object pickObject) {

    super(isVisible, shapeColor, edgeColor, borderColor, highlightColor,
        borderWidth,
        shapeX, shapeY, shapeZ, targetX, targetY, targetZ,
        showLabel, labelText, pickObject);
  }

  public SimpleShape(
      boolean isVisible,
      JoglColor shapeColor, JoglColor borderColor, JoglColor highlightColor,
      float borderWidth,
      double initialX, double initialY, double initialZ,
      boolean showLabel, String labelText, Object pickObject) {
    this(isVisible,
        shapeColor, borderColor, borderColor, highlightColor,
        borderWidth,
        initialX, initialY, initialZ,
        initialX, initialY, initialZ,
        showLabel, labelText, pickObject);
  }

  @Override
  public SimpleShape forUpdate() {
    SimpleShape result = new SimpleShape(
        isVisible,
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
    setShapeColor(gl);
    gl.glBegin(GL2.GL_TRIANGLE_FAN);
    renderVertices(gl);
    gl.glEnd();
  }

  @Override
  protected void renderBorder(GL2 gl) {
    setEdgeColor(gl);
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

  @Override
  public boolean contains(double posX, double posY) {
    if (Math.abs(posX) > 1.0d) {
      return false;
    }
    if (Math.abs(posY) > 1.0d) {
      return false;
    }
    return true;
  }
}
