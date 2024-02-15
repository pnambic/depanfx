package com.pnambic.depanfx.nodeview.tooldata;

import com.pnambic.depanfx.jogl.JoglCamera;

public class DepanFxNodeViewCameraData {

  public static final double HOME_ZOOM_100_PCT = 1.0d;

  public double cameraX;

  public double cameraY;

  public double cameraZ;

  public double lookAtX;

  public double lookAtY;

  public double lookAtZ;

  public double zoom;

  public DepanFxNodeViewCameraData(
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

  public static DepanFxNodeViewCameraData getHome() {
    return new DepanFxNodeViewCameraData(
        JoglCamera.HOME_CAMERA_X,
        JoglCamera.HOME_CAMERA_Y,
        JoglCamera.HOME_CAMERA_Z,
        JoglCamera.HOME_LOOKAT_X,
        JoglCamera.HOME_LOOKAT_Y,
        JoglCamera.HOME_LOOKAT_Z,
        HOME_ZOOM_100_PCT);
  }

  public static DepanFxNodeViewCameraData ofJoglCamera(
      JoglCamera.CameraData joglData) {
    return new DepanFxNodeViewCameraData(
        joglData.cameraX, joglData.cameraY, joglData.cameraZ,
        joglData.lookAtX, joglData.lookAtY, joglData.lookAtZ,
        joglData.zoom);
  }

  public JoglCamera.CameraData getJoglCameraData() {
    return new JoglCamera.CameraData(
        cameraX, cameraY, cameraZ,
        lookAtX, lookAtY, lookAtZ,
        zoom);
  }
}
