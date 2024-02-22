package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.jogl.JoglModule;
import com.pnambic.depanfx.jogl.JoglCamera;
import com.pnambic.depanfx.jogl.JoglTransforms;

import javafx.beans.property.SimpleDoubleProperty;

public class CameraControl implements CameraChangeListener {

  public final SimpleDoubleProperty cameraX = new SimpleDoubleProperty();

  public final SimpleDoubleProperty cameraY = new SimpleDoubleProperty();

  public final SimpleDoubleProperty cameraZ = new SimpleDoubleProperty();

  public final SimpleDoubleProperty lookAtX = new SimpleDoubleProperty();

  public final SimpleDoubleProperty lookAtY = new SimpleDoubleProperty();

  public final SimpleDoubleProperty lookAtZ = new SimpleDoubleProperty();

  public final SimpleDoubleProperty zoom = new SimpleDoubleProperty();

  private  final JoglModule jogl;

  public CameraControl(JoglModule jogl) {
    this.jogl = jogl;

    JoglCamera.CameraData updateData = jogl.getCurrentCamera();
    cameraX.set(updateData.cameraX);
    cameraY.set(updateData.cameraY);
    cameraZ.set(updateData.cameraZ);

    lookAtX.set(updateData.lookAtX);
    lookAtY.set(updateData.lookAtY);
    lookAtZ.set(updateData.lookAtZ);

    zoom.set(updateData.zoom);
  }

  @Override
  public double getFps() {
    return jogl.getFps();
  }

  @Override
  public void home() {
    JoglCamera.CameraData updateData = new JoglCamera.CameraData();
    jogl.updateCamera(updateData);

    cameraX.set(updateData.cameraX);
    cameraY.set(updateData.cameraY);
    cameraZ.set(updateData.cameraZ);

    lookAtX.set(updateData.lookAtX);
    lookAtY.set(updateData.lookAtY);
    lookAtZ.set(updateData.lookAtZ);

    zoom.set(updateData.zoom);
  }

  @Override
  public void setCamera(double cameraToX, double cameraToY, double cameraToZ) {
    JoglCamera.CameraData updateData = jogl.getCurrentCamera();
    updateData.cameraX = cameraToX;
    updateData.cameraY = cameraToY;
    updateData.cameraZ = cameraToZ;
    jogl.updateCamera(updateData);

    cameraX.set(updateData.cameraX);
    cameraY.set(updateData.cameraY);
    cameraZ.set(updateData.cameraZ);
  }

  @Override
  public void setLookAt(double lookAtToX, double lookAtToY, double lookAtToZ) {
    JoglCamera.CameraData updateData = jogl.getCurrentCamera();
    updateData.lookAtX = lookAtToX;
    updateData.lookAtY = lookAtToY;
    updateData.lookAtZ = lookAtToZ;
    jogl.updateCamera(updateData);

    lookAtX.set(updateData.lookAtX);
    lookAtY.set(updateData.lookAtY);
    lookAtZ.set(updateData.lookAtZ);
  }

  @Override
  public void setZoom(double zoomTo) {
    JoglCamera.CameraData updateData = jogl.getCurrentCamera();
    updateData.zoom = zoomTo;
    jogl.updateCamera(updateData);

    zoom.set(updateData.zoom);
  }

  @Override
  public void dolly(double dollyX, double dollyY, double dollyZ) {
    JoglCamera.CameraData updateData = jogl.getCurrentCamera();
    updateData.cameraX += dollyX;
    updateData.cameraY += dollyY;
    updateData.cameraZ += dollyZ;

    updateData.lookAtX += dollyX;
    updateData.lookAtY += dollyY;
    updateData.lookAtZ += dollyZ;
    jogl.updateCamera(updateData);

    cameraX.set(updateData.cameraX);
    cameraY.set(updateData.cameraY);
    cameraZ.set(updateData.cameraZ);

    lookAtX.set(updateData.lookAtX);
    lookAtY.set(updateData.lookAtY);
    lookAtZ.set(updateData.lookAtZ);
  }

  @Override
  public void rotate(
      double angle, double rotateX, double rotateY, double rotateZ) {
    JoglCamera.CameraData cameraData = jogl.getCurrentCamera();
    float[] rotatedLookAt = JoglTransforms.rotate(cameraData,
        (float) angle, (float) rotateX, (float) rotateY, (float) rotateZ);
    setLookAt(rotatedLookAt[0], rotatedLookAt[1], rotatedLookAt[2]);
  }

  @Override
  public void move(double moveDistance) {
    JoglCamera.CameraData joglCamera = jogl.getCurrentCamera();
    float[] directionV3 = JoglTransforms.directionV3(joglCamera);
    float[] dollyV3 = JoglTransforms.scaleV3(directionV3, (float) moveDistance);

    dolly(dollyV3[0], dollyV3[1], dollyV3[2]);
  }

  @Override
  public void zoom(double zoomRatio) {
    JoglCamera.CameraData updateData = jogl.getCurrentCamera();
    double newZoom = updateData.zoom * zoomRatio;
    setZoom(newZoom);
  }
}
