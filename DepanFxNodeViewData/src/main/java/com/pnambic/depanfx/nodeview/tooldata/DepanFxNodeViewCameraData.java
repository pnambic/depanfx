package com.pnambic.depanfx.nodeview.tooldata;

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
}
