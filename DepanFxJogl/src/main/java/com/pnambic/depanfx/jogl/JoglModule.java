package com.pnambic.depanfx.jogl;

import com.jogamp.opengl.FPSCounter;
import com.pnambic.depanfx.jogl.JoglCamera.CameraData;
import com.pnambic.depanfx.jogl.shapes.DemoShape;
import com.pnambic.depanfx.jogl.shapes.NodeKind;
import com.pnambic.depanfx.jogl.shapes.RenderShape;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

/**
 * A DepanFX wrapper for an OpenGL GLWindow.
 *
 * The {@link Canvas} provides an API to the GLWindow that is compatible
 * with the JavaFX window management behaviors
 * (and almost works - see JogAmp Bug #1504).
 */
public class JoglModule {

  public static final int TARGET_FPS = 60;

  public  static final int FRAME_CNT =
      FPSCounter.DEFAULT_FRAMES_PER_INTERVAL;  // 300 .. ~ 5 seconds.

  public static final Logger LOG = LoggerFactory.getLogger(JoglModule.class);

  private final JoglCamera camera;

  private final JoglRenderer renderer;

  private final JoglDrawListener drawListener;

  private final JoglKeyListener keyListener;

  private final JoglMouseListener mouseListener;

  // Guaranteed to start as null.
  private NewtCanvasPane canvasPane;

  public JoglModule(CameraData cameraData) {
    camera = new JoglCamera(cameraData);
    renderer = new JoglRenderer(camera);
    drawListener = new JoglDrawListener(renderer);
    keyListener = new JoglKeyListener();
    mouseListener = new JoglMouseListener(renderer);
  }

  public Pane getCanvasPane() {
    if (canvasPane == null) {
      canvasPane = new NewtCanvasPane(drawListener, keyListener, mouseListener);
    }

    // A recycled canvasPanes needs to repeat the layout step.
    canvasPane.enableCanvas();
    return canvasPane;
  }

  public double getFps() {
    return canvasPane.getFps();
  }

  public void stop() {
    canvasPane.stop();
  }

  /**
   * Release system resources, primarily the window draw thread.
   */
  public void destroy() {
    canvasPane.destroy();
    canvasPane = null;
  }

  public JoglShape getShape(Object key) {
    return renderer.getShape(key);
  }

  public RenderShape getNodeShape(NodeKind shape) {
    return renderer.getNodeShape(shape);
  }

  public JoglShape buildShape(
      NodeKind shape,
      boolean isVisible,
      JoglColor fillColor,
      JoglColor borderColor,
      JoglColor highlightColor,
      double xPos, double yPos, double zPos,
      String nodeName, Object pickNode) {
    return renderer.buildShape(
        shape, isVisible,
        fillColor, borderColor, highlightColor,
        xPos, yPos, zPos,
        nodeName, pickNode);
  }

  public void updateShape(Object key, JoglShape shape) {
    renderer.updateShape(key, shape);
  }

  public void addPressAction(JoglKeyListener.KeyAction action) {
    keyListener.addPressAction(action);
  }

  public void addReleaseAction(JoglKeyListener.KeyAction action) {
    keyListener.addReleaseAction(action);
  }

  public void addMouseActionListener(JoglMouseActionListener listener) {
    mouseListener.addMouseActionListener(listener);
  }

  public void updateCamera(CameraData updateInfo) {
     camera.updateCamera(updateInfo);
   }

  public CameraData getCurrentCamera() {
     return camera.getCurrent();
   }

  /**
   * Read the last image out of the graphics context.
   */
  public BufferedImage takeScreenshot() {
    return canvasPane.takeScreenshot();
  }

  public void demoDisplay() {
    updateShape(this, new DemoShape());
  }
}
