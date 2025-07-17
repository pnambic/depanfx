package com.pnambic.depanfx.jogl;

import com.jogamp.opengl.math.Quaternion;
import com.jogamp.opengl.math.VectorUtil;

public class JoglTransforms {

  public static final int COLOR_BYTE_MIN = 0;

  public static final int COLOR_BYTE_MAX = 255;

  private JoglTransforms() {
    // Prevent instantiation.
  }

  public static double colorByte(int color) {
    if (color <= COLOR_BYTE_MIN) {
      return 0.0f;
    }
    if (color >= COLOR_BYTE_MAX) {
      return 1.0f;
    }
    return ((float) color) / ((float) COLOR_BYTE_MAX);
  }

  public static float[] direction(float[] lookAtV3, float[] cameraV3) {
    float[] result = VectorUtil.subVec3(createVector3(), lookAtV3, cameraV3);
    return normalizeV3(result);
  }

  /**
   * Provide the direction vector from camera to the target for movement.
   */
  public static float[] cameraMoveDirectionV3(
      JoglCamera.CameraData cameraData) {
    float[] resultV3 = new float[] {
        (float) (cameraData.moveToX - cameraData.cameraX),
        (float) (cameraData.moveToY - cameraData.cameraY),
        (float) (cameraData.moveToZ - cameraData.cameraZ) };
    return normalizeV3(resultV3);
  }

  /**
   * Provide the direction vector from camera to the target for movement.
   */
  public static float[] cameraLookDirectionV3(
      JoglCamera.CameraData cameraData) {
    float[] resultV3 = new float[] {
        (float) (cameraData.lookAtX - cameraData.cameraX),
        (float) (cameraData.lookAtY - cameraData.cameraY),
        (float) (cameraData.lookAtZ - cameraData.cameraZ) };
    return normalizeV3(resultV3);
  }

  public static float[] normalizeV3(float[] resultV3) {
    return VectorUtil.normalizeVec3(createVector3(), resultV3);
  }

  /**
   * Provide a unit vector (e.g. |v| = 1) for the supplied direction vector.
   */
  public static float[] toUnitV3(float[] directionV3) {
    float distance = VectorUtil.normVec3(directionV3);
    return VectorUtil.divVec3(createVector3(), directionV3, distance);
  }

  public static float[] addV3(float[] oneV3, float[] twoV3) {
    return VectorUtil.addVec3(createVector3(), oneV3, twoV3);
  }

  public static float[] subtractV3(float[] minuendV3, float[] subtrahendV3) {
    return VectorUtil.subVec3(createVector3(), minuendV3, subtrahendV3);
  }

  public static float lengthV3(float[] vectorV3) {
    return VectorUtil.normVec3(vectorV3);
  }

  public static float[] scaleV3(float[] vectorV3, float scale) {
    return VectorUtil.scaleVec3(createVector3(), vectorV3, scale);
  }

  public static float[] crossV3(float[] oneV3, float[] twoV3) {
    return VectorUtil.crossVec3(createVector3(), oneV3, twoV3);
  }

  public static class Gimbel {

    private Quaternion quat = new Quaternion();

    public Gimbel(float radians, float[] normalV3) {
      quat.setFromAngleNormalAxis(radians, normalV3);
    }

    public float[] rotate(float[] vectorV3) {
      return quat.rotateVector(createVector3(), 0, vectorV3, 0);
    }
  }

  private static float[] createVector3() {
    return new float[3];
  }
}
