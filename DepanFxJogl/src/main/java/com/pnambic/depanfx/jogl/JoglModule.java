package com.pnambic.depanfx.jogl;

import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.javafx.NewtCanvasJFX;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;
import com.pnambic.depanfx.jogl.JoglCamera.CameraData;
import com.pnambic.depanfx.jogl.shapes.DemoShape;

import javafx.scene.canvas.Canvas;

public class JoglModule {

  private GLWindow glWindow;

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
  public Canvas createCanvas(double height, double width) {
    com.jogamp.newt.Display jfxNewtDisplay =
        NewtFactory.createDisplay(null, false);
    final Screen screen = NewtFactory.createScreen(jfxNewtDisplay, 0);
    final GLCapabilities caps =
        new GLCapabilities(GLProfile.getMaxFixedFunc(true));

    glWindow = GLWindow.create(screen, caps);
    glWindow.addGLEventListener(drawListener);
    glWindow.addKeyListener(keyListener);

    final NewtCanvasJFX result = new NewtCanvasJFX(glWindow);
    result.setHeight(height);
    result.setWidth(width);
    return result;
  }

  public void start() {
    if (glWindow.getAnimator() == null) {
      new Animator(glWindow);  // registers with window as a side effect
    }
    glWindow.getAnimator().start();
  }

  public void stop() {
    glWindow.getAnimator().stop();
  }

  /**
   * Release system resources, primarily the window draw thread.
   */
  public void destroy() {
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

  public void dollyCamera(double dollyX, double dollyY, double dollyZ) {
    camera.dollyCamera(dollyX, dollyY, dollyZ);
  }

  public CameraData getCurrentCamera() {
     return camera.getCurrent();
   }

  public void demoDisplay() {
    updateShape(this, new DemoShape());
  }
}
