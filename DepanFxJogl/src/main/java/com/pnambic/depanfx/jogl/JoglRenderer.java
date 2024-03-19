package com.pnambic.depanfx.jogl;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoglRenderer {

  private static final Logger LOG =
      LoggerFactory.getLogger(JoglRenderer.class);

  private static final float BACKGROUND_RED =
      (float) JoglTransforms.colorByte(240);

  private static final float BACKGROUND_GREEN =
      (float) JoglTransforms.colorByte(240);

  private static final float BACKGROUND_BLUE =
      (float) JoglTransforms.colorByte(240);

  private static final float BACKGROUND_ALPHA = 1.0f;

  private final JoglCamera camera;

  private List<JoglShape> shapes = new ArrayList<>();

  private final Map<Object, JoglShape> renders = new HashMap<>();

  private Map<Object, JoglShape> updates = new HashMap<>();

  public JoglRenderer(JoglCamera camera) {
    this.camera = camera;
  }

  public void init(final GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    gl.glClearColor(
        BACKGROUND_RED, BACKGROUND_GREEN, BACKGROUND_BLUE, BACKGROUND_ALPHA);
    gl.glClearDepth(1.0f);

    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glEnable(GL2.GL_BLEND);
    gl.glEnable(GL2.GL_TEXTURE_2D);

    gl.glDepthFunc(GL.GL_LEQUAL);
    gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
    gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

    // Other options to review
    // enhance(gl);

    LOG.info("initialized drawable");
  }

  public void reshape(
      final GLAutoDrawable drawable,
      final int x, final int y, final int width, final int height) {
    GL2 gl = drawable.getGL().getGL2();
    camera.reshapeCanvas(gl, x, y, width, height);
    LOG.info("reshape to {}x{} @ ({}, {})", width, height, x, y);
  }

  /**
   * See frames per second for display details.
   */
  public void display(final GLAutoDrawable drawable) {
    if (installUpdates()) {
      updateShapes();
    };

    final GL2 gl = drawable.getGL().getGL2();
    gl.glClear(GL.GL_COLOR_BUFFER_BIT);
    gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

    camera.prepareCamera(gl);

    shapes.stream().forEach(s ->s.step(gl, this));
    shapes.stream().forEach(s -> drawShape(gl, s));
  }

  public void dispose(final GLAutoDrawable drawable) {
    LOG.info("disposing drawable");
  }

  private void drawShape(GL2 gl, JoglShape shape) {
    gl.glPushMatrix();
    shape.draw(gl, this);
    gl.glPopMatrix();
  }

  public void updateShape(Object key, JoglShape shape) {
    updates.put(key, shape);
  }

  public JoglShape getRenderShape(Object renderKey) {
    return renders.get(renderKey);
  }

  @SuppressWarnings("unused")
  private void enhance(GLAutoDrawable drawable) {
    /*
    //TODO: add options for this parameters: it looks way nicer,
    //but is way slower ;)
    gl.glEnable(GL.GL_LINE_SMOOTH);
    gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
    gl.glEnable(GL.GL_POLYGON_SMOOTH);
    gl.glHint(GL.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
    */
  }

  private void updateShapes() {
    Collection<JoglShape> source = renders.values();
    List<JoglShape> result = new ArrayList<>(source.size());
    source.stream().forEach(result::add);
    shapes = result;
  }

  /** Indicates if any updates were applied. */
  private boolean installUpdates() {
    if (!updates.isEmpty()) {
      Map<Object, JoglShape> installs = updates;
      updates = new HashMap<>();

      installs.entrySet().stream()
          .forEach(e -> renders.put(e.getKey(), e.getValue()));
      return true;
    }
    return false;
  }
}
