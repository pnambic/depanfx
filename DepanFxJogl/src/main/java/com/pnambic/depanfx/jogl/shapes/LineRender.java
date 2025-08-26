package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.opengl.GL2;

public interface LineRender {

  void prepare(LineShape line, NodeShape sourceShape, NodeShape targetShape);

  void draw(GL2 gl, LineShape line);

  /** Release any OpenGL resources held by this renderer. */
  default void dispose(GL2 gl) {}
}
