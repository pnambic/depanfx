/*
 * Copyright 2025 The Depan Project Authors
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

import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.javafx.NewtCanvasJFX;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLPixelStorageModes;
import com.jogamp.opengl.util.awt.ImageUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;

import javafx.scene.layout.Pane;

/**
 * Handles details of packaging the NewtCanvas.
 *
 * Encapsulates much of the work around for JogAmp Bug #1504.
 */
public class NewtCanvasPane extends Pane {

  private static final Logger LOG =
      LoggerFactory.getLogger(NewtCanvasPane.class);

  private final JoglDrawListener drawListener;

  private final JoglKeyListener keyListener;

  private final JoglMouseListener mouseListener;

  private final NewtCanvasJFX canvas;

  private final GLWindow glWindow;

  /**
   * A new canvas pane is started enabled, as a convenience.
   * Refresh that enabled state with {@link #enableCanvas()}.
   */
  private boolean firstLayout = true;

  public NewtCanvasPane(
      JoglDrawListener drawListener,
      JoglKeyListener keyListener,
      JoglMouseListener mouseListener) {
    this.drawListener = drawListener;
    this.keyListener = keyListener;
    this.mouseListener = mouseListener;

    setPrefSize(0.0d, 0.0d);
    setMinSize(0.0d, 0.0d);

    glWindow = createWindow();
    canvas = new NewtCanvasJFX(glWindow);

    glWindow.addGLEventListener(drawListener);
    glWindow.addKeyListener(keyListener);
    glWindow.addMouseListener(mouseListener);
    mouseListener.setWindow(glWindow);
  }

  public void enableCanvas() {
    firstLayout = true;
    setNeedsLayout(true);
  }

  public double getFps() {
    GLAnimatorControl animator = glWindow.getAnimator();
    if (animator != null) {
      return animator.getLastFPS();
    }
    return 0.0d;
  }

  public void stop() {
    LOG.info("Stopping canvas pane");
    stopAnimation();
    disableCanvas();
  }

  /**
   * Release system resources, primarily the window draw thread.
   */
  public void destroy() {
    LOG.info("Destroy canvas pane");
    stopAnimation();
    glWindow.removeGLEventListener(drawListener);
    glWindow.removeKeyListener(keyListener);
    glWindow.removeMouseListener(mouseListener);
    glWindow.destroy();
    canvas.destroy();
  }

  /**
   * Read the last image out of the graphics context.
   */
  public BufferedImage takeScreenshot() {
    GLContext context = glWindow.getContext();
    context.makeCurrent();
    int height = (int) canvas.getHeight();
    int width = (int) canvas.getWidth();
    BufferedImage image = readToBufferedImage(0, 0, width, height, false);
    context.release();
    return image;
  }

  @Override
  protected void layoutChildren() {
    super.layoutChildren();

    if (firstLayout) {
      firstLayout = false;
      layoutJogAmpBug1504();
    }
  }

  private void disableCanvas() {
    try {
      LOG.info("Removing canvas {} from pane", canvas.getId());
      getChildren().clear();
    } catch (Exception errAny) {
      LOG.warn("Trouble disabling JOGL: {}", errAny.getMessage());
    }
  }

  private void layoutJogAmpBug1504() {
    canvas.setWidth(getWidth());
    canvas.setHeight(getHeight());

    widthProperty().addListener((obs, oldVal, newVal) ->
        canvas.setWidth(newVal.doubleValue()));

    heightProperty().addListener((obs, oldVal, newVal) ->
        canvas.setHeight(newVal.doubleValue()));

    // Work around #1504 with a late reparent of the NewtCanvas pane.
    getChildren().add(canvas);

    // Without JogAmp Bug #1504, this should happen in activate().
    // Canvas canvas = prepareCanvasBug1504(jogl);
    startAnimation();
  }

  /**
   * Create the GLWindow and add it to the JavaFx Group.
   */
  private GLWindow createWindow() {
    LOG.info("Creating canvas");
    Display jfxNewtDisplay = NewtFactory.createDisplay(null, false);
    Screen screen = NewtFactory.createScreen(jfxNewtDisplay, 0);
    GLCapabilities caps =
        new GLCapabilities(GLProfile.getMaxFixedFunc(true));

    return GLWindow.create(screen, caps);
  }

  private void startAnimation() {
    if (glWindow.getAnimator() == null) {
      // registers with window as a side effect
      Animator animator = new Animator(glWindow);
      animator.setUpdateFPSFrames(JoglModule.FRAME_CNT, null);
    }
    glWindow.getAnimator().start();
  }

  private void stopAnimation() {
    GLAnimatorControl animator = glWindow.getAnimator();
    if (animator != null) {
      animator.stop();
    }
  }

  /**
   * Stolen from com.jogamp.opengl.util.awt.Screenshot.readToBufferedImage()
   *
   * JOGL 2.1.2
   */
  private static BufferedImage readToBufferedImage(
      int x,int y, int width, int height, boolean alpha) throws GLException {

    int bufImgType =
        (alpha ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR);
    int readbackType =
        (alpha ? GL2.GL_ABGR_EXT : GL2ES3.GL_BGR);

    // Allocate necessary storage
    BufferedImage image = new BufferedImage(width, height, bufImgType);

    GLContext glc = GLContext.getCurrent();
    GL gl = glc.getGL();

    // Set up pixel storage modes
    GLPixelStorageModes psm = new GLPixelStorageModes();
    psm.setPackAlignment(gl, 1);

    // read the BGR values into the image
    gl.glReadPixels(x, y, width, height, readbackType,
        GL.GL_UNSIGNED_BYTE,
        ByteBuffer.wrap(((DataBufferByte) image.getRaster().getDataBuffer()).getData()));

    // Restore pixel storage modes
    psm.restore(gl);

    if( glc.getGLDrawable().isGLOriented() ) {
      // Must flip BufferedImage vertically for correct results
      ImageUtil.flipImageVertically(image);
    }
    return image;
  }
}
