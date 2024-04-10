package com.pnambic.depanfx.nodeview.tooldata;

public class DepanFxNodeViewSceneData {

  private final DepanFxJoglColor backgroundColor;

  private final DepanFxNodeViewCameraData cameraInfo;

  public DepanFxNodeViewSceneData(
      DepanFxJoglColor backgroundColor,
      DepanFxNodeViewCameraData cameraInfo) {
    this.backgroundColor = backgroundColor;
    this.cameraInfo = cameraInfo;
  }

  public DepanFxJoglColor getBackgroundColor() {
    return backgroundColor;
  }

  public DepanFxNodeViewCameraData getCameraInfo() {
    return cameraInfo;
  }
}
