package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.opengl.GL2;
import com.pnambic.depanfx.jogl.JoglRenderer;
import com.pnambic.depanfx.jogl.JoglShape;

public class NodeShape implements JoglShape {

  public static final float SPEED = 10.0f;

  public static final float STEP_TOLERANCE = 10.0f;

  public double red;

  public double green;

  public double blue;

  public double shapeX;

  public double shapeY;

  public double shapeZ;

  public double targetX;

  public double targetY;

  public double targetZ;

  public boolean showLabel;

  public String labelText;

  /////////////////////////////////////
  // Cached rendering entites

  private TextureLoader labelTexture;

  private NodeShape(
      double red, double green, double blue,
      double shapeX, double shapeY, double shapeZ,
      double targetX, double targetY, double targetZ,
      boolean showLabel, String labelText) {
    this.shapeX = shapeX;
    this.shapeY = shapeY;
    this.shapeZ = shapeZ;
    this.targetX = targetX;
    this.targetY = targetY;
    this.targetZ = targetZ;
    this.red = red;
    this.green = green;
    this.blue = blue;
    this.showLabel = showLabel;
    this.labelText = labelText;
  }

  public NodeShape(
      double red, double green, double blue,
      double initialX, double initialY, double initialZ,
      boolean showLabel, String labelText) {
    this(red, green, blue,
        initialX, initialY, initialZ,
        initialX, initialY, initialZ,
        showLabel, labelText);
  }

  @Override
  public NodeShape forUpdate() {
    return new NodeShape(
        red, green, blue,
        shapeX, shapeY, shapeZ,
        targetX, targetY, targetZ,
        showLabel, labelText);
  }

  @Override
  public void draw(GL2 gl, JoglRenderer renderer) {
    gl.glTranslated(shapeX, shapeY, shapeZ);
    renderShape(gl);

    if (showLabel) {
      renderText(gl);
    }
  }

  @Override
  public void step(GL2 gl, JoglRenderer renderer) {
    if (isBelowTolerance()) {
      stopAnimation();
      return;
    }
    stepAnimation();
  }

  public void stepAnimation() {
    shapeX += (targetX - shapeX) / SPEED;
    shapeY += (targetY - shapeY) / SPEED;
    shapeZ += (targetZ - shapeZ) / SPEED;
  }

  public void stopAnimation() {
    shapeX = targetX;
    shapeY = targetY;
    shapeZ = targetZ;
  }

  protected void renderShape(GL2 gl) {
    gl.glBegin(GL2.GL_QUADS);
    gl.glColor3d(red, green, blue);
    gl.glVertex3f(-1.0f, 1.0f, 0.0f);
    gl.glVertex3f( 1.0f, 1.0f, 0.0f);
    gl.glVertex3f( 1.0f,-1.0f, 0.0f);
    gl.glVertex3f(-1.0f,-1.0f, 0.0f);
    gl.glEnd();
  }

  private void renderText(GL2 gl) {
    if (labelTexture == null) {
      labelTexture = new TextureLoader(gl);
      labelTexture.loadTexture(labelText);
    }
    labelTexture.draw(gl, 0.5d, -0.9d, -0.5d, 0.9d);
  }

  private boolean isBelowTolerance() {
    if (Math.abs(targetX - shapeX) < STEP_TOLERANCE
        && Math.abs(targetY - shapeY) < STEP_TOLERANCE
        && Math.abs(targetZ - shapeZ) < STEP_TOLERANCE) {
      return true;
    }
    return false;
  }
}
