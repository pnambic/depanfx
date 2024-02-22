package com.pnambic.depanfx.jogl;

import com.jogamp.opengl.math.Quaternion;
import com.jogamp.opengl.math.VectorUtil;

public class JoglTransforms {

  public static final int COLOR_BYTE_MIN = 0;

  public static final int COLOR_BYTE_MAX = 255;

  private JoglTransforms() {
    // Prevent instantiation.
  }

  public static float colorByte(int color) {
    if (color <= COLOR_BYTE_MIN) {
      return 0.0f;
    }
    if (color >= COLOR_BYTE_MAX) {
      return 0.0f;
    }
    return ((float) color) / ((float) COLOR_BYTE_MAX);
  }

  public static float[] direction(float[] lookAtV3, float[] cameraV3) {
    float[] result = VectorUtil.subVec3(createVector3(), lookAtV3, cameraV3);
    return VectorUtil.normalizeVec3(result);
  }

  /**
   * Provide the direction vector from camera to lookAt.
   */
  public static float[] directionV3(JoglCamera.CameraData cameraData) {
    float[] resultV3 = new float[] {
        (float) (cameraData.lookAtX - cameraData.cameraX),
        (float) (cameraData.lookAtY - cameraData.cameraY),
        (float) (cameraData.lookAtZ - cameraData.cameraZ) };
    return VectorUtil.normalizeVec3(resultV3);
  }

  /**
   * Provide a unit vector (e.g. |v| = 1) for the supplied direction vector.
   */
  public static float[] toUnitV3(float[] directionV3) {
    float distance = VectorUtil.normVec3(directionV3);
    return VectorUtil.divVec3(createVector3(), directionV3, distance);
  }

  public static float[] scaleV3(float[] directionV3, float scale) {
    return VectorUtil.scaleVec3(createVector3(), directionV3, scale);
  }

  /**
   * Provide a lookAt point that is rotated on the rotation axis by the
   * supplied degrees.
   */
  public static float[] rotate(
      JoglCamera.CameraData cameraData, double degrees,
      float rotateX, float rotateY, float rotateZ) {
    float[] cameraV3 = cameraData.captureCamera();
    float[] lookAtV3 = cameraData.captureLookAt();
    float[] rotateV3 = new float[] { rotateX, rotateY, rotateZ };

    float[] directionV3 =
        VectorUtil.subVec3(createVector3(), lookAtV3, cameraV3);
    float distance = VectorUtil.normVec3(directionV3);
    directionV3 = VectorUtil.normalizeVec3(directionV3);

    // Convert degrees to radians for the rotation
    float radians = (float) Math.toRadians(degrees);
    Quaternion quat = new Quaternion();
    quat.setFromAngleNormalAxis(radians, rotateV3);

    // Calculate the new direction vector by rotating it around the up vector
    float[] rotatedV3x = quat.rotateVector(createVector3(), 0, directionV3, 0);

    float[] newTargetV3 =
        VectorUtil.scaleVec3(createVector3(), rotatedV3x, distance);
    return VectorUtil.addVec3(newTargetV3, cameraV3, newTargetV3);
  }

  private static float[] createVector3() {
    return new float[3];
  }
}
