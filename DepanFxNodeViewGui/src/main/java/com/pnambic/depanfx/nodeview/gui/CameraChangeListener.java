package com.pnambic.depanfx.nodeview.gui;

public interface CameraChangeListener {

  // Defined by underlying camera
  void home();

  double getFps();

  // Absolute camera actions
  void setCamera(double cameraToX, double cameraToY, double cameraToZ);

  void setLookAt(double lookAtToX, double lookAtToY, double lookAtToZ);

  void setZoom(double zoomTo);

  // Relative camera actions
  void dolly(double dollyX, double dollyY, double dollyZ);

  void rotate(double angle, double rotateX, double rotateY, double rotateZ);

  void move(double moveDistance);

  void zoom(double zoomBy);
}
