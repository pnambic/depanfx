package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.jogl.JoglModule;
import com.pnambic.depanfx.jogl.JoglCamera;
import com.pnambic.depanfx.jogl.JoglTransforms;

import javafx.beans.property.SimpleDoubleProperty;

/**
 * Connect the Jogl rendering engine to the JavaFX ReactiveUI model.
 */
public class CameraControl implements CameraChangeListener {

  public final SimpleDoubleProperty cameraX = new SimpleDoubleProperty();

  public final SimpleDoubleProperty cameraY = new SimpleDoubleProperty();

  public final SimpleDoubleProperty cameraZ = new SimpleDoubleProperty();

  public final SimpleDoubleProperty lookAtX = new SimpleDoubleProperty();

  public final SimpleDoubleProperty lookAtY = new SimpleDoubleProperty();

  public final SimpleDoubleProperty lookAtZ = new SimpleDoubleProperty();

  public final SimpleDoubleProperty zoom = new SimpleDoubleProperty();

  private final JoglModule jogl;

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

  /**
   * In the current implementation, lookAt always tracks moveTo.
   */
  @Override
  public void setMoveTo(double moveToX, double moveToY, double moveToZ) {
    JoglCamera.CameraData updateData = jogl.getCurrentCamera();

    updateData.moveToX = moveToX;
    updateData.moveToY = moveToY;
    updateData.moveToZ = moveToZ;
    trackMoveTo(updateData);

    jogl.updateCamera(updateData);

    lookAtX.set(updateData.lookAtX);
    lookAtY.set(updateData.lookAtY);
    lookAtZ.set(updateData.lookAtZ);
  }

  @Override
  public void setMoveUp(double moveUpX, double moveUpY, double moveUpZ) {
    JoglCamera.CameraData updateData = jogl.getCurrentCamera();
    updateData.moveUpX = moveUpX;
    updateData.moveUpY = moveUpY;
    updateData.moveUpZ = moveUpZ;
    // LookUp remains (0,1,0) unless explicitly changed
    jogl.updateCamera(updateData);
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

    updateData.moveToX += dollyX;
    updateData.moveToY += dollyY;
    updateData.moveToZ += dollyZ;
    trackMoveTo(updateData);

    jogl.updateCamera(updateData);

    cameraX.set(updateData.cameraX);
    cameraY.set(updateData.cameraY);
    cameraZ.set(updateData.cameraZ);

    lookAtX.set(updateData.lookAtX);
    lookAtY.set(updateData.lookAtY);
    lookAtZ.set(updateData.lookAtZ);
  }

  @Override
  public void rotateMoveTo(
      double angle, double rotateX, double rotateY, double rotateZ) {

    // Prepare the gimble for rotations
    float[] rotateV3 = new float[] {
        (float) rotateX, (float) rotateY, (float) rotateZ };
    float radians = (float) Math.toRadians(angle);
    JoglTransforms.Gimbel gimble =
        new JoglTransforms.Gimbel(radians, rotateV3);

    JoglCamera.CameraData cameraData = jogl.getCurrentCamera();

    float[] rotatedMoveUp = gimble.rotate(cameraData.captureMoveUp());
    setMoveUp(rotatedMoveUp[0], rotatedMoveUp[1], rotatedMoveUp[2]);

    float[] cameraV3 = cameraData.captureCamera();
    float[] dirToV3 =
        JoglTransforms.subtractV3(cameraData.captureMoveTo(), cameraV3);
    float[] rotateToV3 = gimble.rotate(JoglTransforms.normalizeV3(dirToV3));

    float[] rotDirToV3 =
        JoglTransforms.scaleV3(rotateToV3, JoglTransforms.lengthV3(dirToV3));
    float[] rotMoveToV3 = JoglTransforms.addV3(cameraV3, rotDirToV3);
    setMoveTo(rotMoveToV3[0], rotMoveToV3[1], rotMoveToV3[2]);
  }

  @Override
  public void move(double moveDistance) {
    JoglCamera.CameraData joglCamera = jogl.getCurrentCamera();
    float[] directionV3 = JoglTransforms.cameraMoveDirectionV3(joglCamera);
    float[] dollyV3 = JoglTransforms.scaleV3(directionV3, (float) moveDistance);

    dolly(dollyV3[0], dollyV3[1], dollyV3[2]);
  }

  @Override
  public void zoom(double zoomRatio) {
    JoglCamera.CameraData updateData = jogl.getCurrentCamera();
    double newZoom = updateData.zoom * zoomRatio;
    setZoom(newZoom);
  }

  private void trackMoveTo(JoglCamera.CameraData updateData) {
    updateData.lookAtX = updateData.moveToX;
    updateData.lookAtY = updateData.moveToY;
    updateData.lookAtZ = updateData.moveToZ;
  }
}
