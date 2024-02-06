package com.pnambic.depanfx.jogl;

import com.jogamp.opengl.GL2;

public interface JoglShape {

  /**
   * Draw the shape with the current values.
   */
  void draw(GL2 gl);

  /**
   * Animate the shape by updating the current values.
   */
  void step(GL2 gl);
}
