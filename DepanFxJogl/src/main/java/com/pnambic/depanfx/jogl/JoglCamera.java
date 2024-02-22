package com.pnambic.depanfx.jogl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;

/**
 * Define the camera to render the view.
 */
public class JoglCamera {

  // Home position is 100 above origin
  public static final double HOME_CAMERA_X = 0.0d;

  public static final double HOME_CAMERA_Y = 0.0d;

  public static final double HOME_CAMERA_Z = 10.0d;

  // Home lookat is the origin.
  public static final double HOME_LOOKAT_X = 0.0d;

  public static final double HOME_LOOKAT_Y = 0.0d;

  public static final double HOME_LOOKAT_Z = 0.0d;

  // Full laptop screen vertical space:
  // 7" vertical from 22" is ~ 20 degrees.
  public static final double HOME_FOV = 20.0d;

  // For 45-degree FOV
  private static final double HOME_ZOOM_100 = 0.5d;

  private static final double HOME_Z_NEAR = 1.0d;

  private static final double HOME_Z_FAR = 3000.0d;

  public static class CameraData {

    public double cameraX;
    public double cameraY;
    public double cameraZ;

    public double lookAtX;
    public double lookAtY;
    public double lookAtZ;

    public double zoom;

    public CameraData(
        double cameraX, double cameraY, double cameraZ,
        double lookAtX, double lookAtY, double lookAtZ,
        double zoom) {
      this.cameraX = cameraX;
      this.cameraY = cameraY;
      this.cameraZ = cameraZ;
      this.lookAtX = lookAtX;
      this.lookAtY = lookAtY;
      this.lookAtZ = lookAtZ;
      this.zoom = zoom;
    }

    /**
     * Clone a new camera data from a source.
     */
    public CameraData(CameraData source) {
      this(source.cameraX, source.cameraY, source.cameraZ,
          source.lookAtX, source.lookAtY, source.lookAtZ,
          source.zoom);
    }

    /**
     * Deliver a new camera data for the home position.
     */
    public CameraData() {
      this(HOME_CAMERA_X, HOME_CAMERA_Y, HOME_CAMERA_Z,
          HOME_LOOKAT_X, HOME_LOOKAT_Y, HOME_LOOKAT_Z,
          HOME_ZOOM_100);
    }

    public void capture(CameraData source) {
      cameraX = source.cameraX;
      cameraY = source.cameraY;
      cameraZ = source.cameraZ;
      lookAtX = source.lookAtX;
      lookAtY = source.lookAtY;
      lookAtZ = source.lookAtZ;
      zoom = source.zoom;
    }

    public float[] captureCamera() {
      return new float[] {
          (float) cameraX, (float) cameraY, (float) cameraZ
      };
    }

    public float[] captureLookAt() {
      return new float[] {
          (float) lookAtX, (float) lookAtY, (float) lookAtZ
      };
    }
  }

  /** Allocate one to retrieve snapshot data at rendering time. */
  private CameraData renderCamera;

  /** Intended camera location and setup. */
  private CameraData updateCamera;

  /** Shape of rendering area */
  private double aspect;

  public JoglCamera(CameraData source) {
    this.updateCamera = new CameraData(source);
    this.renderCamera = new CameraData(updateCamera);
  }

  public CameraData getCurrent() {
    return new CameraData(updateCamera);
  }

  public void reshapeCanvas(GL2 gl,
      final int x, final int y,
      final int width, final int height) {

    // Capture the window shape
    aspect = (double) width / (double) height;
  }

  public void prepareCamera(GL2 gl) {
     GLU glu = GLU.createGLU(gl);
    renderCamera.capture(updateCamera);

    gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
    gl.glLoadIdentity();
    double fh = renderCamera.zoom;
    double fw = fh * aspect;
    gl.glFrustum(-fw, fw, -fh, fh, HOME_Z_NEAR, HOME_Z_FAR);

    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    gl.glLoadIdentity();
    glu.gluLookAt(
        renderCamera.cameraX, renderCamera.cameraY, renderCamera.cameraZ,
        renderCamera.lookAtX, renderCamera.lookAtY, renderCamera.lookAtZ,
        0.0f, 1.0f, 0.0f);
  }

  public void updateCamera(CameraData updateData) {
    updateCamera.capture(updateData);
  }
}
