package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.opengl.GL2;

import java.awt.Shape;

/**
 * Bundles a {@link Shape} with its cached {@link PathIteratorRender} so that
 * multiple node shapes can share the same renderable geometry.
 */
public class RenderShape {

  private final Shape awtShape;
  private final PathIteratorRender pathRender;

  public RenderShape(Shape awtShape) {
    this.awtShape = awtShape;
    this.pathRender = new PathIteratorRender(awtShape);
  }

  public Shape getAwtShape() {
    return awtShape;
  }

  public void drawShape(GL2 gl) {
    pathRender.drawShape(gl);
  }

  public void drawBorder(GL2 gl) {
    pathRender.drawBorder(gl);
  }
}
