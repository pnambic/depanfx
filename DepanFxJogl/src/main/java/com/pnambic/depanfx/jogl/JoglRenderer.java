/*
 * Copyright 2024 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.jogl;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.pnambic.depanfx.jogl.shapes.ArrowShape;
import com.pnambic.depanfx.jogl.shapes.ArrowShapes;
import com.pnambic.depanfx.jogl.shapes.LineShape;
import com.pnambic.depanfx.jogl.shapes.NodeFactory;
import com.pnambic.depanfx.jogl.shapes.NodeKind;
import com.pnambic.depanfx.jogl.shapes.RenderShape;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JoglRenderer {

  private static final Logger LOG =
      LoggerFactory.getLogger(JoglRenderer.class);

  // A nice light gray background for the canvas.
  private static final int BACKGROUND_RED = 240;

  private static final int BACKGROUND_GREEN = 240;

  private static final int BACKGROUND_BLUE = 240;

  private static final double TARGET_SIZE = 0.5d;

  private static final float BACKGROUND_ALPHA_FLT = 1.0f;

  private static final int RGB_PARTS_PER_PIXEL = 3;

  private final JoglCamera camera;

  private List<JoglShape> shapes = new ArrayList<>();

  private final Map<Object, JoglShape> renders = new HashMap<>();

  private Map<Object, JoglShape> updates = new HashMap<>();

  private int viewportWidth;

  private int viewportHeight;

  /////////////////////////////////////
  // Factories for shared shapes with managed resources.

  private ArrowShapes.ArrowFactory arrowFactory =
      new ArrowShapes.ArrowFactory();

  private NodeFactory nodeFactory =
      new NodeFactory();

  /////////////////////////////////////
  // Object picking
  private JoglPickBuffer pickBuffer = new JoglPickBuffer();

  private JoglSelectRectangle selectionRect;

  private Color pickBackground = Color.BLACK;

  /////////////////////////////////////
  // Potential user options

  private Color drawBackground;

  private Color targetHorzColor = Color.YELLOW;

  private Color targetVertColor = Color.ORANGE;

  public JoglRenderer(JoglCamera camera) {
    this.camera = camera;
  }

  public void init(final GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();

    setBackgroundColor(gl, BACKGROUND_RED, BACKGROUND_GREEN, BACKGROUND_BLUE);
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
    pickBuffer.dispose(gl);
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
    GLContext glContext = drawable.getContext();
    glContext.makeCurrent();

    gl.glClear(GL.GL_COLOR_BUFFER_BIT);
    gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

    camera.prepareCamera(gl);

    shapes.stream().forEach(s ->s.step(gl, this));
    shapes.stream().forEach(s -> drawShape(gl, s));

    if (selectionRect != null) {
      selectionRect.drawSelectRectangle(gl);
    }

    drawTarget(gl);
    gl.glFlush();
    glContext.release();
  }

  public Collection<Object> getHits(GLAutoDrawable drawable,
      float mouseX, float mouseY, float selectWidth, float selectHeight) {
    LOG.debug("hit test x:{}, y:{}, w:{}, h:{}",
        mouseX, mouseY, selectWidth, selectHeight);

    // Lock down list to ensure repeatable order.
    List<JoglPickable> pickables = getPickableShapes();

    GL2 gl = drawable.getGL().getGL2();
    GLContext glContext = drawable.getContext();
    glContext.makeCurrent();

    // Render into off screen buffer so that selection render does not disturb
    // the visible frame.  This prepares for color based picking.
    pickBuffer.use(gl, viewportWidth, viewportHeight);

    installBackgroundColor(gl, pickBackground);

    gl.glClear(GL.GL_COLOR_BUFFER_BIT);
    gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

    camera.preparePicker(gl, mouseX, mouseY, selectWidth, selectHeight);

    // draw stuff
    drawPickables(gl, pickables);
    installBackgroundColor(gl, drawBackground);

    int mouseXPx = (int) mouseX;
    int mouseYPx = (int) mouseY;
    int selectWidthPx = (int) selectWidth;
    int selectHeightPx = (int) selectHeight;

    int pixelAlloc = selectWidthPx * selectHeightPx * RGB_PARTS_PER_PIXEL;
    FloatBuffer mousePixels = FloatBuffer.allocate(pixelAlloc);
    gl.glReadPixels(mouseXPx, mouseYPx, selectWidthPx, selectHeightPx,
        GL.GL_RGB, GL.GL_FLOAT, mousePixels);

    pickBuffer.release(gl);
    glContext.release();

    return processHits(pickables, mousePixels);
  }

  public void dispose(final GLAutoDrawable drawable) {
    LOG.info("disposing drawable");
    GL2 gl = drawable.getGL().getGL2();

    // Dispose other resource allocations
    pickBuffer.dispose(gl);
    arrowFactory.dispose(gl);
    nodeFactory.dispose(gl);

    // Dispose resources owned by individual shapes.
    shapes.forEach(s -> s.dispose(gl));

    shapes.clear();
    renders.clear();
    updates.clear();
  }

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

  public JoglShape buildShape(
      NodeKind shape,
      boolean isVisible,
      JoglColor fillColor, JoglColor borderColor, JoglColor highlightColor,
      double xPos, double yPos, double zPos,
      String nodeName, Object pickNode) {

    return nodeFactory.buildShape(
        shape,
        isVisible,
        fillColor, borderColor, highlightColor,
        1.0f,
        xPos, yPos, zPos,
        true, nodeName, pickNode);
  }

  public RenderShape getNodeShape(NodeKind renderKey) {
    return nodeFactory.getRenderShape(renderKey);
  }

  public ArrowShape buildArrow(LineShape.Arrow sourceArrow, float[] transform) {
    return arrowFactory.buildArrow(sourceArrow, transform);
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

  private void setBackgroundColor(GL gl, int red, int green, int blue) {
    this.drawBackground = new Color(red, green, blue);
    installBackgroundColor(gl, drawBackground);
  }

  private void installBackgroundColor(GL gl, Color backgroundColor) {
    gl.glClearColor(
        colorByteF(backgroundColor.getRed()),
        colorByteF(backgroundColor.getGreen()),
        colorByteF(backgroundColor.getBlue()),
        BACKGROUND_ALPHA_FLT);
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
  private void drawPickables(GL2 gl, List<JoglPickable> pickables) {
    // Zero is the background, so we start at 1.
    int name = 1;
    for (JoglPickable pickable : pickables) {
      gl.glPushMatrix();
      JoglColor indexColor = indexToColor(name);
      LOG.debug("Pickable {} with RGB {}, {}, {}", name,
          indexColor.red, indexColor.green, indexColor.blue);
      pickable.draw(gl, this, indexColor);
      gl.glPopMatrix();
      name++;
    }
    gl.glFlush();
  }

  /**
   * Provide the pickable set of shapes.
   */
  private List<JoglPickable> getPickableShapes() {
    return shapes.stream()
        .filter(s -> s instanceof JoglPickable)
        .map(JoglPickable.class::cast)
        .toList();
  }

  private Collection<Object> processHits(
      List<JoglPickable> pickables, FloatBuffer mousePixels) {

    int bkgrndColor = fromColor(pickBackground);
    int pickLimit = pickables.size();

    Set<Object> result = new HashSet<>();
    while (mousePixels.hasRemaining()) {
      int pickColor = nextPixel(mousePixels);

      // Skip the background color.
      if (pickColor == bkgrndColor) {
        continue;
      }
      int pickIndex = pickColor - 1;
      if (pickIndex >= pickLimit) {
        LOG.warn("Pick index {} exceeds limit {}", pickIndex, pickLimit);
        continue;
      }
      if (pickIndex < 0 ) {
        LOG.warn("Negative pick index {}", pickIndex, pickLimit);
        continue;
      }
      Object pickObject = pickables.get(pickIndex).getObject();
      if (pickObject == null) {
        LOG.warn("Pick index {} returned null", pickIndex);
        continue;
      }
      result.add(pickObject);
    }
    return result;
  }

  private JoglColor indexToColor(int pickIndex) {
    double red = JoglTransforms.colorByte((pickIndex >> 16) & 0xFF);
    double green = JoglTransforms.colorByte((pickIndex >> 8) & 0xFF);
    double blue = JoglTransforms.colorByte(pickIndex & 0xFF);
    return new JoglColor(red, green, blue);
  }

  private int nextPixel(FloatBuffer mousePixels) {
    float[] pixelColor = new float[RGB_PARTS_PER_PIXEL];
    mousePixels.get(pixelColor);

    return (cleanByte(pixelColor[0]) << 16) |
        (cleanByte(pixelColor[1]) << 8) |
        cleanByte(pixelColor[2]);
  }

  private int fromColor(Color color) {
    // Ignore alpha in high byte.
    return color.getRGB() & 0x00FFFFFF;
  }

  private int cleanByte(Float value) {
    int intValue = (int) (value * 255);
    // Byte values greater than 127 are negative in Java,
    // due to two's complement representation.
    return intValue & 0xFF;
  }

  /////////////////////////////////////
  // Move To target indicator

  private void drawTarget(GL2 gl) {
    JoglCamera.CameraData data = camera.getCurrent();

    float[] camV3 = data.captureCamera();
    float[] moveToV3 = data.captureMoveTo();
    float[] moveUpV3 = data.captureMoveUp();
    float[] moveDirV3 = JoglTransforms.direction(moveToV3, camV3);

    float[] rightPosV3 = calcAxisPos(moveDirV3, moveUpV3);
    float[] perpPosV3 = calcAxisPos(moveDirV3, rightPosV3);

    gl.glLineWidth(2.0f);
    gl.glBegin(GL2.GL_LINES);
    drawAxis(gl, moveToV3, rightPosV3, targetHorzColor);
    drawAxis(gl, moveToV3, perpPosV3, targetVertColor);
    gl.glEnd();
  }

  protected float[] calcAxisPos(float[] forwardV3, float[] axisV3) {
    return JoglTransforms.scaleV3(
        JoglTransforms.normalizeV3(
            JoglTransforms.crossV3(forwardV3, axisV3)),
        (float) TARGET_SIZE);
  }

  protected void drawAxis(
      GL2 gl, float[] centerV3, float[] axisV3, Color axisColor) {
    gl.glColor3d(
        colorByteF(axisColor.getRed()),
        colorByteF(axisColor.getGreen()),
        colorByteF(axisColor.getBlue()));
    gl.glVertex3f(
        centerV3[0] - axisV3[0],
        centerV3[1] - axisV3[1],
        centerV3[2] - axisV3[2]);
    gl.glVertex3f(
        centerV3[0] + axisV3[0],
        centerV3[1] + axisV3[1],
        centerV3[2] + axisV3[2]);
  }

  private float colorByteF(int colorByte) {
    return (float) JoglTransforms.colorByte(colorByte);
  }
}
