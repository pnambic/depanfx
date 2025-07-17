package com.pnambic.depanfx.jogl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Define the camera to render the view.
 */
public class JoglCamera {

  // Home position is 100 above origin
  public static final double HOME_CAMERA_X = 0.0d;

  public static final double HOME_CAMERA_Y = 0.0d;

  public static final double HOME_CAMERA_Z = 100.0d;

  // Home lookat is the origin.
  public static final double HOME_LOOKAT_X = HOME_CAMERA_X;

  public static final double HOME_LOOKAT_Y = HOME_CAMERA_Y;

  public static final double HOME_LOOKAT_Z = 80.0d;

  // When looking down into the Z axis, the Y axis points up.
  public static final double HOME_LOOKUP_X = 0.0d;

  public static final double HOME_LOOKUP_Y = 1.0d;

  public static final double HOME_LOOKUP_Z = 0.0d;

  // Move starts in sync with looking at.
  public static final double HOME_MOVETO_X = HOME_LOOKAT_X;

  public static final double HOME_MOVETO_Y = HOME_LOOKAT_Y;

  public static final double HOME_MOVETO_Z = HOME_LOOKAT_Z;

  public static final double HOME_MOVEUP_X = HOME_LOOKUP_X;

  public static final double HOME_MOVEUP_Y = HOME_LOOKUP_Y;

  public static final double HOME_MOVEUP_Z = HOME_LOOKUP_Z;

  // Full laptop screen vertical space:
  // 7" vertical from 22" is ~ 20 degrees.
  public static final double HOME_FOV = 20.0d;

  // For 45-degree FOV
  public static final double HOME_ZOOM_100 = 0.5d;

  public static final double HOME_Z_NEAR = 1.0d;

  public static final double HOME_Z_FAR = 3000.0d;

  private static final Logger LOG = LoggerFactory.getLogger(JoglCamera.class);

  public static class CameraData {
    public double cameraX;
    public double cameraY;
    public double cameraZ;

    public double lookAtX;
    public double lookAtY;
    public double lookAtZ;

    public double lookUpX;
    public double lookUpY;
    public double lookUpZ;

    public double moveToX;
    public double moveToY;
    public double moveToZ;

    public double moveUpX;
    public double moveUpY;
    public double moveUpZ;

    public double zoom;

    public CameraData(
        double cameraX, double cameraY, double cameraZ,
        double lookAtX, double lookAtY, double lookAtZ,
        double lookUpX, double lookUpY, double lookUpZ,
        double moveToX, double moveToY, double moveToZ,
        double moveUpX, double moveUpY, double moveUpZ,
        double zoom) {
      if (lookAtX != moveToX) {
        LOG.warn("wtf");
      }
      this.cameraX = cameraX;
      this.cameraY = cameraY;
      this.cameraZ = cameraZ;
      this.lookAtX = lookAtX;
      this.lookAtY = lookAtY;
      this.lookAtZ = lookAtZ;
      this.lookUpX = lookUpX;
      this.lookUpY = lookUpY;
      this.lookUpZ = lookUpZ;
      this.moveToX = moveToX;
      this.moveToY = moveToY;
      this.moveToZ = moveToZ;
      this.moveUpX = moveUpX;
      this.moveUpY = moveUpY;
      this.moveUpZ = moveUpZ;
      this.zoom = zoom;
    }

    public CameraData(
        double cameraX, double cameraY, double cameraZ,
        double lookAtX, double lookAtY, double lookAtZ,
        double zoom) {
      this(cameraX, cameraY, cameraZ,
          lookAtX, lookAtY, lookAtZ,
          0.0d, 1.0d, 0.0d,
          lookAtX, lookAtY, lookAtZ,
          0.0d, 1.0d, 0.0d,
          zoom);
    }

    /**
     * Clone a new camera data from a source.
     */
    public CameraData(CameraData source) {
      this(
          source.cameraX, source.cameraY, source.cameraZ,
          source.lookAtX, source.lookAtY, source.lookAtZ,
          source.lookUpX, source.lookUpY, source.lookUpZ,
          source.moveToX, source.moveToY, source.moveToZ,
          source.moveUpX, source.moveUpY, source.moveUpZ,
          source.zoom);
    }

    /**
     * Deliver a new camera data for the home position.
     */
    public CameraData() {
      this(HOME_CAMERA_X, HOME_CAMERA_Y, HOME_CAMERA_Z,
          HOME_LOOKAT_X, HOME_LOOKAT_Y, HOME_LOOKAT_Z,
          HOME_LOOKUP_X, HOME_LOOKUP_Y, HOME_LOOKUP_Z,
          HOME_MOVETO_X, HOME_MOVETO_Y, HOME_MOVETO_Z,
          HOME_MOVEUP_X, HOME_MOVEUP_Y, HOME_MOVEUP_Z,
          HOME_ZOOM_100);
    }

    public void capture(CameraData source) {
      cameraX = source.cameraX;
      cameraY = source.cameraY;
      cameraZ = source.cameraZ;
      lookAtX = source.lookAtX;
      lookAtY = source.lookAtY;
      lookAtZ = source.lookAtZ;
      lookUpX = source.lookUpX;
      lookUpY = source.lookUpY;
      lookUpZ = source.lookUpZ;
      moveToX = source.moveToX;
      moveToY = source.moveToY;
      moveToZ = source.moveToZ;
      moveUpX = source.moveUpX;
      moveUpY = source.moveUpY;
      moveUpZ = source.moveUpZ;
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

    public float[] captureLookUp() {
      return new float[] {
          (float) lookUpX, (float) lookUpY, (float) lookUpZ
      };
    }

    public float[] captureMoveTo() {
      return new float[] {
          (float) moveToX, (float) moveToY, (float) moveToZ
      };
    }

    public float[] captureMoveUp() {
      return new float[] {
          (float) moveUpX, (float) moveUpY, (float) moveUpZ
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
    renderCamera.capture(updateCamera);

    prepareProjection(gl);
    prepareModelView(gl);
  }

  public void preparePicker(
      GL2 gl, float mouseX, float mouseY,
      float selectionWidth, float selectionHeight) {

    prepareProjection(gl);
    prepareModelView(gl);
  }

  public void updateCamera(CameraData updateData) {
    updateCamera.capture(updateData);
  }

  private void prepareProjection(GL2 gl) {
    gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
    gl.glLoadIdentity();
    double fh = renderCamera.zoom;
    double fw = fh * aspect;
    gl.glFrustum(-fw, fw, -fh, fh, HOME_Z_NEAR, HOME_Z_FAR);
  }

  private void prepareModelView(GL2 gl) {
    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    gl.glLoadIdentity();

    // For now, lookAt tracks the moveTo position,
    // and the moveTo up is also the lookAt up.
    GLU glu = GLU.createGLU(gl);
    glu.gluLookAt(
        renderCamera.cameraX, renderCamera.cameraY, renderCamera.cameraZ,
        renderCamera.lookAtX, renderCamera.lookAtY, renderCamera.lookAtZ,
        renderCamera.moveUpX, renderCamera.moveUpY, renderCamera.moveUpZ);
  }
}
