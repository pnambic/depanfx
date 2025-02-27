package com.pnambic.depanfx.jogl;

import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.javafx.NewtCanvasJFX;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.FPSCounter;
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
import com.pnambic.depanfx.jogl.JoglCamera.CameraData;
import com.pnambic.depanfx.jogl.shapes.DemoShape;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;

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

  private GLWindow glWindow;

  private NewtCanvasJFX canvas;

  private JoglCamera camera;

  private JoglRenderer renderer;

  private JoglDrawListener drawListener;

  private JoglKeyListener keyListener;

  private JoglMouseListener mouseListener;

  private NewtCanvasPane canvasPane;

  public JoglModule(CameraData cameraData) {
    camera = new JoglCamera(cameraData);
    renderer = new JoglRenderer(camera);
    keyListener = new JoglKeyListener();
    drawListener = new JoglDrawListener(renderer);
    mouseListener = new JoglMouseListener(renderer);
    canvas = createCanvas();
  }

  public Pane getCanvasPane() {
    if (canvasPane == null) {
      canvasPane = new NewtCanvasPane();
    }

    // A recycled canvasPanes needs to repeat the layout step.
    canvasPane.enableCanvas();
    return canvasPane;
  }

  public double getFps() {
    GLAnimatorControl animator = glWindow.getAnimator();
    if (animator != null) {
      return animator.getLastFPS();
    }
    return 0.0d;
  }

  public void stop() {
    LOG.info("Stopping jogl pane");
    GLAnimatorControl animator = glWindow.getAnimator();
    if (animator != null) {
      animator.stop();
    }
    if (canvasPane != null) {
      canvasPane.disableCanvas();
    }
  }

  /**
   * Release system resources, primarily the window draw thread.
   */
  public void destroy() {
    LOG.info("Destroy jogl pane");
    stop();
    glWindow.removeGLEventListener(drawListener);
    glWindow.removeKeyListener(keyListener);
    canvas.destroy();
    glWindow.destroy();
  }

  public JoglShape getShape(Object key) {
    return renderer.getShape(key);
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
    GLContext context = glWindow.getContext();
    context.makeCurrent();
    int height = (int) canvas.getHeight();
    int width = (int) canvas.getWidth();
    BufferedImage image = readToBufferedImage(0, 0, width, height, false);
    context.release();
    return image;
  }

  public void demoDisplay() {
    updateShape(this, new DemoShape());
  }

  /**
   * Create the GLWindow and add it to the JavaFx Group.
   */
  private NewtCanvasJFX createCanvas() {
    Display jfxNewtDisplay = NewtFactory.createDisplay(null, false);
    Screen screen = NewtFactory.createScreen(jfxNewtDisplay, 0);
    GLCapabilities caps =
        new GLCapabilities(GLProfile.getMaxFixedFunc(true));

    glWindow = GLWindow.create(screen, caps);
    glWindow.addGLEventListener(drawListener);
    glWindow.addKeyListener(keyListener);
    glWindow.addMouseListener(mouseListener);
    mouseListener.setWindow(glWindow);

    return new NewtCanvasJFX(glWindow);
  }

  private void startAnimation() {
    if (glWindow.getAnimator() == null) {
      // registers with window as a side effect
      Animator animator = new Animator(glWindow);
      animator.setUpdateFPSFrames(FRAME_CNT, null);
    }
    glWindow.getAnimator().start();
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

  /**
   * Handles details of packaging the NewtCanvas.
   *
   * Encapsulates much of the work around for JogAmp Bug #1504.
   */
  private class NewtCanvasPane extends Pane {

    /**
     * A new canvas pane is started enabled, as a convenience.
     * Refresh that enabled state with {@link #enableCanvas()}.
     */
    private boolean firstLayout = true;

    public NewtCanvasPane() {
      setPrefSize(0.0d, 0.0d);
      setMinSize(0.0d, 0.0d);
    }

    public void enableCanvas() {
      firstLayout = true;
      setNeedsLayout(true);
    }

    public void disableCanvas() {
      try {
        LOG.info("Remove canvas {} from pane", canvas.getId());
        getChildren().clear();
      } catch (Exception errAny) {
        LOG.warn("Trouble disabling JOGL: {}", errAny.getMessage());
      }
    }

    @Override
    protected void layoutChildren() {
      super.layoutChildren();

      if (firstLayout) {
        firstLayout = false;
        layoutJogAmpBug1504();
      }
    }

    private void layoutJogAmpBug1504() {
      double width = getWidth();
      double height = getHeight();

      canvas.setWidth(width);
      canvas.setHeight(height);

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
  }
}
