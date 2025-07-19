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

  @Override
  public void setMoveTo(double moveToX, double moveToY, double moveToZ) {
    JoglCamera.CameraData updateData = jogl.getCurrentCamera();

    double deltaX = updateData.moveToX - moveToX;
    double deltaY = updateData.moveToY - moveToY;
    double deltaZ = updateData.moveToZ - moveToZ;

    updateData.moveToX = moveToX;
    updateData.moveToY = moveToY;
    updateData.moveToZ = moveToZ;

    updateData.lookAtX += deltaX;
    updateData.lookAtY += deltaY;
    updateData.lookAtZ += deltaZ;

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

  public void setLookAt(double lookAtX, double lookAtY, double lookAtZ) {
    JoglCamera.CameraData updateData = jogl.getCurrentCamera();

    updateData.lookAtX = lookAtX;
    updateData.lookAtY = lookAtY;
    updateData.lookAtZ = lookAtZ;

    jogl.updateCamera(updateData);
  }

  public void setLookUp(double lookUpX, double lookUpY, double lookUpZ) {
    JoglCamera.CameraData updateData = jogl.getCurrentCamera();
    updateData.lookUpX = lookUpX;
    updateData.lookUpY = lookUpY;
    updateData.lookUpZ = lookUpZ;
    jogl.updateCamera(updateData);
  }

  public void lookAtMoveTo() {
    JoglCamera.CameraData updateData = jogl.getCurrentCamera();

    updateData.lookAtX = updateData.moveToX;
    updateData.lookAtY = updateData.moveToY;
    updateData.lookAtZ = updateData.moveToZ;

    updateData.lookUpX = updateData.moveUpX;
    updateData.lookUpY = updateData.moveUpY;
    updateData.lookUpZ = updateData.moveUpZ;

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

    updateData.moveToX += dollyX;
    updateData.moveToY += dollyY;
    updateData.moveToZ += dollyZ;

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
    float[] rotatedMoveTo = rotatePosV3(
        gimble, cameraV3, cameraData.captureMoveTo());
    setMoveTo(rotatedMoveTo[0], rotatedMoveTo[1], rotatedMoveTo[2]);

    float[] rotatedLookUp = gimble.rotate(cameraData.captureLookUp());
    setLookUp(rotatedLookUp[0], rotatedLookUp[1], rotatedLookUp[2]);

    float[] rotatedLookAt = rotatePosV3(
        gimble, cameraV3, cameraData.captureLookAt());
    setLookAt(rotatedLookAt[0], rotatedLookAt[1], rotatedLookAt[2]);
  }

  public void rotateLookAt(
      double angle, double rotateX, double rotateY, double rotateZ) {

    // Prepare the gimble for rotations
    float[] rotateV3 = new float[] {
        (float) rotateX, (float) rotateY, (float) rotateZ };
    float radians = (float) Math.toRadians(angle);
    JoglTransforms.Gimbel gimble =
        new JoglTransforms.Gimbel(radians, rotateV3);

    JoglCamera.CameraData cameraData = jogl.getCurrentCamera();

    float[] rotatedLookUp = gimble.rotate(cameraData.captureLookUp());
    setLookUp(rotatedLookUp[0], rotatedLookUp[1], rotatedLookUp[2]);

    float[] cameraV3 = cameraData.captureCamera();
    float[] rotatedLookAt = rotatePosV3(
        gimble, cameraV3, cameraData.captureLookAt());
    setLookAt(rotatedLookAt[0], rotatedLookAt[1], rotatedLookAt[2]);
  }

  private float[] rotatePosV3(
      JoglTransforms.Gimbel gimble, float[] cameraV3, float[] atV3) {
    float[] dirToV3 = JoglTransforms.subtractV3(atV3, cameraV3);
    float[] rotateToV3 = gimble.rotate(JoglTransforms.normalizeV3(dirToV3));

    float[] rotDirToV3 =
        JoglTransforms.scaleV3(rotateToV3, JoglTransforms.lengthV3(dirToV3));
    return JoglTransforms.addV3(cameraV3, rotDirToV3);

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
}
