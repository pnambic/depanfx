package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.opengl.GL2;
import com.pnambic.depanfx.jogl.JoglColor;
import com.pnambic.depanfx.jogl.JoglPickable;
import com.pnambic.depanfx.jogl.JoglRenderer;
import com.pnambic.depanfx.jogl.JoglShape;

public class NodeShape implements JoglShape, JoglPickable {

  public static final float SPEED = 10.0f;

  public static final float STEP_TOLERANCE = 10.0f;

  public JoglColor fillColor;

  public JoglColor borderColor;

  public float borderWidth;

  public double shapeX;

  public double shapeY;

  public double shapeZ;

  public double targetX;

  public double targetY;

  public double targetZ;

  public boolean showLabel;

  public String labelText;

  public Object pickObject;

  /////////////////////////////////////
  // Cached rendering entities

  private TextureLoader labelTexture;

  private NodeShape(
      JoglColor fillColor, JoglColor borderColor, float borderWidth,
      double shapeX, double shapeY, double shapeZ,
      double targetX, double targetY, double targetZ,
      boolean showLabel, String labelText, Object pickObject) {
    this.fillColor = fillColor;
    this.borderColor = borderColor;
    this.borderWidth = borderWidth;
    this.shapeX = shapeX;
    this.shapeY = shapeY;
    this.shapeZ = shapeZ;
    this.targetX = targetX;
    this.targetY = targetY;
    this.targetZ = targetZ;
    this.showLabel = showLabel;
    this.labelText = labelText;
    this.pickObject = pickObject;
  }

  public NodeShape(
      JoglColor fillColor, JoglColor borderColor, float borderWidth,
      double initialX, double initialY, double initialZ,
      boolean showLabel, String labelText, Object pickObject) {
    this(fillColor, borderColor, borderWidth,
        initialX, initialY, initialZ,
        initialX, initialY, initialZ,
        showLabel, labelText, pickObject);
  }

  @Override
  public NodeShape forUpdate() {
    NodeShape result = new NodeShape(
        fillColor, borderColor, borderWidth,
        shapeX, shapeY, shapeZ,
        targetX, targetY, targetZ,
        showLabel, labelText, pickObject);
    result.labelTexture = labelTexture;
    return result;
  }

  @Override
  public void draw(GL2 gl, JoglRenderer renderer) {
    gl.glTranslated(shapeX, shapeY, shapeZ);
    renderShape(gl);
    renderBorder(gl);

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

  @Override // Pickable
  public void draw(GL2 gl, JoglRenderer renderer, int name) {
    gl.glPushName(name);
    gl.glTranslated(shapeX, shapeY, shapeZ);
    renderShape(gl);
    renderBorder(gl);

    // Don't draw the label
    gl.glPushName(name);
  }

  @Override // Pickable
  public Object getObject() {
    return pickObject;
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

  private void renderShape(GL2 gl) {
    gl.glBegin(GL2.GL_TRIANGLE_FAN);
    gl.glColor3d(fillColor.red, fillColor.green, fillColor.blue);
    renderVertices(gl);
    gl.glEnd();
  }

  private void renderBorder(GL2 gl) {
    gl.glBegin(GL2.GL_LINE_LOOP);
    gl.glColor3d(borderColor.red, borderColor.green, borderColor.blue);
    gl.glLineWidth(borderWidth);
    renderVertices(gl);
    gl.glEnd();
  }

  private void renderVertices(GL2 gl) {
    gl.glVertex3f(-1.0f, 1.0f, 0.0f);
    gl.glVertex3f( 1.0f, 1.0f, 0.0f);
    gl.glVertex3f( 1.0f,-1.0f, 0.0f);
    gl.glVertex3f(-1.0f,-1.0f, 0.0f);
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
