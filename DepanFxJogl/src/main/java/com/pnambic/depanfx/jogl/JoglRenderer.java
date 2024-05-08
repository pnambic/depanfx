package com.pnambic.depanfx.jogl;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

public class JoglRenderer {

  private static final Logger LOG =
      LoggerFactory.getLogger(JoglRenderer.class);

  private static final int BYTES_PER_INT = (Integer.SIZE / Byte.SIZE);

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

  private int viewportWidth;

  private int viewportHeight;

  private JoglSelectRectangle selectionRect;

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

  public int getViewportHeight() {
    return viewportHeight;
  }

  public int getViewportWidth() {
    return viewportWidth;
  }

  public double scaleMouseX(int mouseX) {
    return ((double) mouseX) / (double) viewportWidth;
  }

  public double scaleMouseX(float mouseX) {
    return ((double) mouseX) / (double) viewportWidth;
  }

  public double scaleMouseY(int mouseY) {
    return ((double) mouseY) / (double) viewportHeight;
  }

  public double scaleMouseY(float mouseY) {
    return ((double) mouseY) / (double) viewportHeight;
  }

  public void activateSelectionRectangle(
      double anchorX, double anchorY, double mouseX, double mouseY) {
    LOG.debug("select rectangle ax:{}, ay:{}, mx:{}, my:{}",
        anchorX, anchorY, mouseX, mouseY);
    selectionRect =
        new JoglSelectRectangle(anchorX, anchorY, mouseX, mouseY);
  }

  public void releaseSelectionRectangle() {
    selectionRect = null;
  }

  public void reshape(
      final GLAutoDrawable drawable,
      final int x, final int y, final int width, final int height) {
    GL2 gl = drawable.getGL().getGL2();
    camera.reshapeCanvas(gl, x, y, width, height);
    viewportWidth = width;
    viewportHeight = height;
    LOG.debug("reshape to {}x{} @ ({}, {})", width, height, x, y);
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

    if (selectionRect != null) {
      selectionRect.drawSelectRectangle(gl);
    }
  }

  public List<Object> getHits(GLAutoDrawable drawable,
      float mouseX, float mouseY, float selectWidth, float selectHeight) {
    LOG.info("hit test x:{}, y:{}, w:{}, h:{}",
        mouseX, mouseY, selectWidth, selectHeight);

    // Lock down list to ensure repeatable order.
    List<JoglPickable> pickables =
        getPickableShapes().collect(Collectors.toList());
    IntBuffer selectBuffer = getSelectBuffer(pickables.size());
    selectBuffer.rewind();

    GL2 gl = drawable.getGL().getGL2();
    GLContext glContext = drawable.getContext();
    glContext.makeCurrent();

    gl.glSelectBuffer(selectBuffer.capacity(), selectBuffer);
    gl.glRenderMode(GL2.GL_SELECT);
    gl.glInitNames();

    camera.preparePicker(gl, mouseX, mouseY, selectWidth, selectHeight);

    // draw stuff
    pickHits(gl, pickables);

    // Collect and process hits
    int hits = gl.glRenderMode(GL2.GL_RENDER);
    glContext.release();

    return processHits(hits, selectBuffer, pickables);
  }

  public void dispose(final GLAutoDrawable drawable) {
    LOG.info("disposing drawable");
  }

  @Nullable
  public JoglShape getShape(Object key) {
    JoglShape update = updates.get(key);
    if (update != null) {
      return update;
    }
    return getRenderShape(key).forUpdate();
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

  private void drawShape(GL2 gl, JoglShape shape) {
    gl.glPushMatrix();
    shape.draw(gl, this);
    gl.glPopMatrix();
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

  /////////////////////////////////////
  // Hit testing

  /**
   * @param pickables ordered list of drawable items.
   *   The ids in the GL select buffer are the indexes for this list.
   */
  private void pickHits(GL2 gl, List<JoglPickable> pickables) {

    // Ensure that the index is the name.
    int name = 0;
    while(name < pickables.size() ) {
      gl.glPushMatrix();
      pickables.get(name).draw(gl, this, name);
      gl.glPopMatrix();
      name++;
    }
  }

  /**
   * Provide the pickable set of shapes.
   */
  private Stream<JoglPickable> getPickableShapes() {
    return shapes.stream()
        .filter(s -> s instanceof JoglPickable)
        .map(JoglPickable.class::cast);
  }

  /**
   * Must provide a "direct buffer".
   */
  private IntBuffer getSelectBuffer(int pickMax) {
    pickMax *= 100;
    int allocBytes = pickMax * 6 * BYTES_PER_INT;
    ByteBuffer result = ByteBuffer.allocateDirect(allocBytes);
    result.order(ByteOrder.nativeOrder());
    return result.asIntBuffer();
  }

  /**
   * Provide the objects that were hit during a picking operation.
   */
  private List<Object> processHits(
      int hits, IntBuffer buffer, List<JoglPickable> pickables) {
    if (hits == 0) {
      LOG.info("zero hits");
      return Collections.emptyList();
    }
    if (hits < 0) {
      LOG.warn(
          "Too many hits!! IntBuffer capacity = {}", buffer.capacity());
      return Collections.emptyList();
    }
    // int[] hitsResults = new int[hits];
    List<Object> results = new ArrayList<>(hits);

    int offset = 0;
    int names;
    for (int i = 0; i < hits; i++) {
      names = buffer.get(offset); offset++;
      offset++; // z1 (first z)
      offset++; // z2 (last z)

      for (int j = 0; j < names; j++) {
        if (j == (names - 1)) {
          int hitIndex = buffer.get(offset);
          JoglPickable hitOject = pickables.get(hitIndex);
          results.add(hitOject.getObject());
        }
        offset++;
      }
    }
    LOG.info("hits = {}; offset = {}", hits, offset);
    return results;
  }
}
