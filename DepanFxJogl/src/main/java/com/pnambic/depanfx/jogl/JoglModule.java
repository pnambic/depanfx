package com.pnambic.depanfx.jogl;

import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Display;
import com.jogamp.newt.Screen;
import com.jogamp.newt.javafx.NewtCanvasJFX;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.FPSCounter;
import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;
import com.pnambic.depanfx.jogl.JoglCamera.CameraData;
import com.pnambic.depanfx.jogl.shapes.DemoShape;

import javafx.scene.canvas.Canvas;

public class JoglModule {

  private static final int FRAME_CNT =
      FPSCounter.DEFAULT_FRAMES_PER_INTERVAL;  // 300 .. ~ 5 seconds.

  private GLWindow glWindow;

  private NewtCanvasJFX canvas;

  private JoglCamera camera;

  private JoglRenderer renderer;

  private JoglDrawListener drawListener;

  private JoglKeyListener keyListener;

  public JoglModule(CameraData cameraData) {
    camera = new JoglCamera(cameraData);
    renderer = new JoglRenderer(camera);
    drawListener = new JoglDrawListener(renderer);
    keyListener = new JoglKeyListener();
  }

  /**
   * Create the GLWindow and add it to the JavaFx Group.
   */
  public Canvas createCanvas() {
    Display jfxNewtDisplay = NewtFactory.createDisplay(null, false);
    Screen screen = NewtFactory.createScreen(jfxNewtDisplay, 0);
    GLCapabilities caps =
        new GLCapabilities(GLProfile.getMaxFixedFunc(true));

    glWindow = GLWindow.create(screen, caps);
    glWindow.addGLEventListener(drawListener);
    glWindow.addKeyListener(keyListener);

    canvas = new NewtCanvasJFX(glWindow);
    return canvas;
  }

  public void start() {
    if (glWindow.getAnimator() == null) {
      // registers with window as a side effect
      Animator animator = new Animator(glWindow);
      animator.setUpdateFPSFrames(FRAME_CNT, null);
    }
    glWindow.getAnimator().start();
  }

  public double getFps() {
    GLAnimatorControl animator = glWindow.getAnimator();
    if (animator != null) {
      return animator.getLastFPS();
    }
    return 0.0d;
  }

  public void stop() {
    glWindow.getAnimator().stop();
  }

  /**
   * Release system resources, primarily the window draw thread.
   */
  public void destroy() {
    stop();
    glWindow.removeGLEventListener(drawListener);
    glWindow.removeKeyListener(keyListener);
    glWindow.destroy();
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

  public void updateCamera(CameraData updateInfo) {
     camera.updateCamera(updateInfo);
   }

  public CameraData getCurrentCamera() {
     return camera.getCurrent();
   }

  public void demoDisplay() {
    updateShape(this, new DemoShape());
  }
}
