package com.pnambic.depanfx.nodeview.jogl;

import com.pnambic.depanfx.jogl.JoglCamera;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewCameraData;

/**
 * Transforms betwixt rendering and persistence cameras.
 */
public class JoglCameras {

  public static DepanFxNodeViewCameraData getHome() {
    return new DepanFxNodeViewCameraData(
        JoglCamera.HOME_CAMERA_X,
        JoglCamera.HOME_CAMERA_Y,
        JoglCamera.HOME_CAMERA_Z,
        JoglCamera.HOME_LOOKAT_X,
        JoglCamera.HOME_LOOKAT_Y,
        JoglCamera.HOME_LOOKAT_Z,
        DepanFxNodeViewCameraData.HOME_ZOOM_100_PCT);
  }

  public static DepanFxNodeViewCameraData of(JoglCamera.CameraData joglData) {
    return new DepanFxNodeViewCameraData(
        joglData.cameraX, joglData.cameraY, joglData.cameraZ,
        joglData.lookAtX, joglData.lookAtY, joglData.lookAtZ,
        joglData.zoom);
  }

  public static JoglCamera.CameraData of(
      DepanFxNodeViewCameraData cameraData) {
    return new JoglCamera.CameraData(
        cameraData.cameraX, cameraData.cameraY, cameraData.cameraZ,
        cameraData.lookAtX, cameraData.lookAtY, cameraData.lookAtZ,
        cameraData.zoom);
  }
}
