package com.pnambic.depanfx.jogl.shapes;

import com.jogamp.opengl.GL2;

public interface LineRender {

  void prepare(LineShape line, NodeShape sourceShape, NodeShape targetShape);

  void draw(GL2 gl, LineShape line);
}
