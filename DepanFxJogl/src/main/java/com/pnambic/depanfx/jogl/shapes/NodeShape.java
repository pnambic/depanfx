package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.opengl.GL2;
import com.pnambic.depanfx.jogl.JoglColor;
import com.pnambic.depanfx.jogl.JoglPickable;
import com.pnambic.depanfx.jogl.JoglRenderer;
import com.pnambic.depanfx.jogl.JoglShape;
import com.pnambic.depanfx.jogl.overlays.NodeOverlay;

import java.util.List;

/**
 * Basic node properties.
 *
 * Handles placement (translation), animation, and picking.
 */
public abstract class NodeShape implements JoglShape, JoglPickable {

  public static final float SPEED = 10.0f;

  public static final float STEP_TOLERANCE = 10.0f;

  public boolean isVisible;

  public JoglColor shapeColor;

  public JoglColor edgeColor;

  public JoglColor borderColor;

  public JoglColor highlightColor;

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

  public List<NodeOverlay> overlays = NodeOverlay.EMPTY_OVERLAYS;

  /**
   * The nest object when this node is folded.
   */
  private Object nodeNestKey;

  /////////////////////////////////////
  // Cached rendering entities

  protected TextureLoader labelTexture;

  protected NodeShape(
      boolean isVisible,
      JoglColor shapeColor, JoglColor edgeColor,
      JoglColor borderColor, JoglColor highlightColor,
      float borderWidth,
      double shapeX, double shapeY, double shapeZ,
      double targetX, double targetY, double targetZ,
      boolean showLabel, String labelText, Object pickObject) {
    this.isVisible = isVisible;
    this.shapeColor = shapeColor;
    this.edgeColor = edgeColor;
    this.borderColor = borderColor;
    this.highlightColor = highlightColor;
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

    this.nodeNestKey = pickObject;
  }

  public NodeShape(
      boolean isVisible,
      JoglColor fillColor, JoglColor borderColor, JoglColor highlightColor,
      float borderWidth,
      double initialX, double initialY, double initialZ,
      boolean showLabel, String labelText, Object pickObject) {
    this(isVisible,
        fillColor, borderColor, borderColor, highlightColor,
        borderWidth,
        initialX, initialY, initialZ,
        initialX, initialY, initialZ,
        showLabel, labelText, pickObject);
  }

  @Override
  public void draw(GL2 gl, JoglRenderer renderer) {
    if (!isVisible) {
      return;
    }
    if (getApparentShape(renderer) != this) {
      return;
    }

    gl.glTranslated(shapeX, shapeY, shapeZ);

    setShapeColor(gl);
    renderShape(gl);

    setEdgeColor(gl);
    renderBorder(gl);

    if (showLabel) {
      renderText(gl);
    }
    overlays.forEach(overlay -> overlay.draw(gl, renderer, this));
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
  public void draw(GL2 gl, JoglRenderer renderer, JoglColor pickColor) {
    if (!isVisible) {
      return;
    }
    if (getApparentShape(renderer) != this) {
      return;
    }

    gl.glTranslated(shapeX, shapeY, shapeZ);
    setPickColor(gl, pickColor);
    renderShape(gl);
    renderBorder(gl);
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

  public NodeShape getApparentShape(JoglRenderer renderer) {
    JoglShape nodeNestShape = renderer.getRenderShape(nodeNestKey);
    if (nodeNestShape != this) {
      if (nodeNestShape instanceof NodeShape nodeShape) {
        return nodeShape.getApparentShape(renderer);
      }
    }
    return this;
  }

  public NodeShape getNestNodeShape(JoglRenderer renderer) {
    if (isBelowTolerance()) {
      if (renderer.getRenderShape(nodeNestKey) instanceof
          NodeShape nestNodeShape) {
        return nestNodeShape;
      }
    }
    return this;
  }

  public void setNodeNestKey(Object nodeNestKey, NodeShape nestNodeShape) {
    this.nodeNestKey = nodeNestKey;

    this.targetX = nestNodeShape.shapeX;
    this.targetY = nestNodeShape.shapeY;
    this.targetZ = nestNodeShape.shapeZ;
  }

  public void clearApparentShape() {
    this.nodeNestKey = pickObject;
  }

  abstract public boolean contains(double posX, double posY);

  /////////////////////////////////////
  // Hook methods for derived types

  abstract protected void renderShape(GL2 gl);

  abstract protected void renderBorder(GL2 gl);

  /**
   * Allow derived types to create update clones.
   */
  protected void fillUpdate(NodeShape updateShape) {
    updateShape.nodeNestKey = nodeNestKey;
    updateShape.labelTexture = labelTexture;
    updateShape.overlays = overlays;
  }

  protected void setShapeColor(GL2 gl) {
    setColor(gl, shapeColor);
  }

  protected void setEdgeColor(GL2 gl) {
    setColor(gl, edgeColor);
  }

  protected void setHighlightColor(GL2 gl) {
    setColor(gl, highlightColor);
  }

  protected void setPickColor(GL2 gl, JoglColor pickColor) {
    setColor(gl, pickColor);
  }

  private void setColor(GL2 gl, JoglColor toColor) {
    gl.glColor3d(toColor.red, toColor.green, toColor.blue);
  }

  private void renderText(GL2 gl) {
    if (labelTexture == null) {
      labelTexture = new TextureLoader(gl);
      labelTexture.loadTexture(labelText);
    }
    setHighlightColor(gl);
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
