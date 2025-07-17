package com.pnambic.depanfx.nodeview.gui;

public interface CameraChangeListener {

  // Defined by underlying camera
  void home();

  double getFps();

  // Absolute camera actions
  void setCamera(double cameraToX, double cameraToY, double cameraToZ);

  void setMoveTo(double lookAtToX, double lookAtToY, double lookAtToZ);

  void setMoveUp(double lookUpToX, double lookUpToY, double lookUpToZ);

  void setZoom(double zoomTo);

  // Relative camera actions
  void dolly(double dollyX, double dollyY, double dollyZ);

  void rotateMoveTo(double angle, double rotateX, double rotateY, double rotateZ);

  void move(double moveDistance);

  void zoom(double zoomBy);
}
