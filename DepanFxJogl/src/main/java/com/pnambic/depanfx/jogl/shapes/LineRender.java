package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.opengl.GL2;
import com.pnambic.depanfx.jogl.JoglRenderer;

public interface LineRender {

  void prepare(
      LineShape line,
      NodeShape sourceShape,
      NodeShape targetShape,
      JoglRenderer renderer);

  void draw(GL2 gl, LineShape line);

  /** Release any OpenGL resources held by this renderer. */
  void dispose(GL2 gl);
}
