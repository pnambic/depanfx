package com.pnambic.depanfx.jogl;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

class BasicOglListener implements GLEventListener {

  private final JoglRenderer renderer;

  public BasicOglListener(JoglRenderer renderer) {
    this.renderer = renderer;
  }

  @Override
  public void init(final GLAutoDrawable drawable) {
    renderer.init(drawable);
  }

  @Override
  public void reshape(
      final GLAutoDrawable drawable,
      final int x, final int y, final int width, final int height) {
    renderer.reshape(drawable, x, y, width, height);
  }

  @Override
  public void display(final GLAutoDrawable drawable) {
    renderer.display(drawable);
  }

  @Override
  public void dispose(final GLAutoDrawable drawable) {
  }
}
