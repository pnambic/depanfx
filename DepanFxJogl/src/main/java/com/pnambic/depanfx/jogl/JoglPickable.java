package com.pnambic.depanfx.jogl;

import com.jogamp.opengl.GL2;

public interface JoglPickable {

  /**
   * Draw the existing shape with a pickable name.
   */
  void draw(GL2 gl, JoglRenderer renderer, int name);

  /**
   * Provides the {@code Object} that is picked with this shape.
   */
  Object getObject();
}
