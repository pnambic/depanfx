/*
 * Copyright 2025 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Synthesized by Codex.
 */
package com.pnambic.depanfx.nodeview.gui;

import static com.pnambic.depanfx.jogl.JoglKeySymbols.*;

import com.pnambic.depanfx.jogl.JoglModule;
import com.pnambic.depanfx.jogl.JoglTransforms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pnambic.depanfx.jogl.JoglCamera;
import com.pnambic.depanfx.jogl.JoglKeyListener.SymbolAction;
import com.pnambic.depanfx.jogl.JoglKeyListener.CharAction;
import javafx.animation.AnimationTimer;

/**
 * Provide basic flight style controls over the camera.
 */
public class FlightController {

  public static final double UNIT_TURN_D = 2.0d; // degrees per key press

  public static final double UNIT_MOVE_D = 1.0d; // distance per key press

  public static final double THROTTLE_STEP = 1.0d; // units per second

  public static final double ZOOM_IN_D = 0.9d;

  public static final double ZOOM_OUT_D = 1.10d;

  public static final char ZOOM_IN_CHAR = '+';

  private static final Logger LOG =
      LoggerFactory.getLogger(FlightController.class);

  private final JoglModule jogl;

  private final CameraControl cameraControl;

  private double throttle = 0.0d;

  private AnimationTimer flightTimer;

  public FlightController(JoglModule jogl, CameraControl cameraControl) {
    this.jogl = jogl;
    this.cameraControl = cameraControl;
  }

  public static void addActions(JoglModule jogl, FlightController flight) {
    jogl.addPressAction(new SymbolAction(KS_HOME, EMPTY_MASK,
            (s, m) -> flight.home()));
    jogl.addPressAction(new SymbolAction(KS_HOME, CTRL_MASK,
        (s, m) -> flight.lookAtMoveTo()));

    // RotateMoveTo: pitch, roll, yaw
    jogl.addPressAction(new SymbolAction(KS_W, EMPTY_MASK,
        (s, m) -> flight.pitchMoveToDown()));
    jogl.addPressAction(new SymbolAction(KS_S, EMPTY_MASK,
        (s, m) -> flight.pitchMoveToUp()));
    jogl.addPressAction(new SymbolAction(KS_A, EMPTY_MASK,
        (s, m) -> flight.rollMoveToLeft()));
    jogl.addPressAction(new SymbolAction(KS_D, EMPTY_MASK,
        (s, m) -> flight.rollMoveToRight()));
    jogl.addPressAction(new SymbolAction(KS_Q, EMPTY_MASK,
        (s, m) -> flight.yawMoveToLeft()));
    jogl.addPressAction(new SymbolAction(KS_E, EMPTY_MASK,
        (s, m) -> flight.yawMoveToRight()));

    // RotateMoveTo: Extended Keys pitch, roll, yaw
    jogl.addPressAction(new SymbolAction(KS_UP, CTRL_MASK,
        (s, m) -> flight.pitchMoveToDown()));
    jogl.addPressAction(new SymbolAction(KS_DOWN, CTRL_MASK,
        (s, m) -> flight.pitchMoveToUp()));
    jogl.addPressAction(new SymbolAction(KS_PAGE_UP, CTRL_MASK,
        (s, m) -> flight.rollMoveToLeft()));
    jogl.addPressAction(new SymbolAction(KS_PAGE_DOWN, CTRL_MASK,
        (s, m) -> flight.rollMoveToRight()));
    jogl.addPressAction(new SymbolAction(KS_LEFT, CTRL_MASK,
        (s, m) -> flight.yawMoveToLeft()));
    jogl.addPressAction(new SymbolAction(KS_RIGHT, CTRL_MASK,
        (s, m) -> flight.yawMoveToRight()));

    // RotateLookAt: pitch, roll, yaw
    jogl.addPressAction(new SymbolAction(KS_W, CTRL_MASK,
        (s, m) -> flight.pitchLookAtDown()));
    jogl.addPressAction(new SymbolAction(KS_S, CTRL_MASK,
        (s, m) -> flight.pitchLookAtUp()));
    jogl.addPressAction(new SymbolAction(KS_A, CTRL_MASK,
        (s, m) -> flight.rollLookAtLeft()));
    jogl.addPressAction(new SymbolAction(KS_D, CTRL_MASK,
        (s, m) -> flight.rollLookAtRight()));
    jogl.addPressAction(new SymbolAction(KS_Q, CTRL_MASK,
        (s, m) -> flight.yawLookAtLeft()));
    jogl.addPressAction(new SymbolAction(KS_E, CTRL_MASK,
        (s, m) -> flight.yawLookAtRight()));
    jogl.addPressAction(new SymbolAction(KS_X, CTRL_MASK,
        (s, m) -> flight.lookAtMoveTo()));

    // Movement: left, right, up, down, forward, reverse
    // along line of movement (moveTo0
    jogl.addPressAction(new SymbolAction(KS_LEFT, EMPTY_MASK,
        (s, m) -> flight.moveLeft()));
    jogl.addPressAction(new SymbolAction(KS_RIGHT, EMPTY_MASK,
        (s, m) -> flight.moveRight()));
    jogl.addPressAction(new SymbolAction(KS_UP, EMPTY_MASK,
        (s, m) -> flight.moveUp()));
    jogl.addPressAction(new SymbolAction(KS_DOWN, EMPTY_MASK,
        (s, m) -> flight.moveDown()));
    jogl.addPressAction(new SymbolAction(KS_PAGE_UP, EMPTY_MASK,
        (s, m) -> flight.moveForward()));
    jogl.addPressAction(new SymbolAction(KS_PAGE_DOWN, EMPTY_MASK,
        (s, m) -> flight.moveReverse()));

    // Throttle Control
    jogl.addPressAction(new SymbolAction(KS_R, EMPTY_MASK,
        (s, m) -> flight.increaseThrottle()));
    jogl.addPressAction(new SymbolAction(KS_F, EMPTY_MASK,
        (s, m) -> flight.decreaseThrottle()));
    jogl.addPressAction(new SymbolAction(KS_X, EMPTY_MASK,
        (s, m) -> flight.cutThrottle()));

    // Zoom: narrow or widen the field of view.
    jogl.addPressAction(new SymbolAction(KS_PLUS, EMPTY_MASK,
        (s, m) -> flight.zoom(ZOOM_IN_D)));
    jogl.addPressAction(new CharAction(ZOOM_IN_CHAR,
        (c, m) -> flight.zoom(ZOOM_IN_D)));
    jogl.addPressAction(new SymbolAction(KS_MINUS, EMPTY_MASK,
        (s, m) -> flight.zoom(ZOOM_OUT_D)));
  }

  public void addActions() {
    addActions(jogl, this);
  }

  public void start() {
    if (flightTimer != null) {
      return;
    }
    flightTimer = new AnimationTimer() {
      private long last = 0;
      @Override
      public void handle(long now) {
        if (last != 0 && throttle != 0.0d) {
          double delta = (now - last) / 1_000_000_000.0d;
          cameraControl.move(throttle * delta);
        }
        last = now;
      }
    };
    flightTimer.start();
  }

  public void stop() {
    if (flightTimer != null) {
      flightTimer.stop();
      flightTimer = null;
    }
  }

  /////////////////////////////////////
  // Camera basics

  public void home() {
    cameraControl.home();
    cutThrottle();
  }

  public void zoom(double zooom) {
    cameraControl.zoom(zooom);
  }

  /////////////////////////////////////
  // MoveTo rotations

  public void pitchMoveToDown() {
    JoglCamera.CameraData camera = jogl.getCurrentCamera();
    rotateMoveToOnAxis(-UNIT_TURN_D, getMoveRightAxis(camera));
  }

  public void pitchMoveToUp() {
    JoglCamera.CameraData camera = jogl.getCurrentCamera();
    rotateMoveToOnAxis(UNIT_TURN_D, getMoveRightAxis(camera));
  }

  public void rollMoveToLeft() {
    JoglCamera.CameraData camera = jogl.getCurrentCamera();
    rotateMoveToOnAxis(-UNIT_TURN_D, getMoveForwardAxis(camera));
  }

  public void rollMoveToRight() {
    JoglCamera.CameraData camera = jogl.getCurrentCamera();
    rotateMoveToOnAxis(UNIT_TURN_D, getMoveForwardAxis(camera));
  }

  public void yawMoveToLeft() {
    JoglCamera.CameraData camera = jogl.getCurrentCamera();
    rotateMoveToOnAxis(-UNIT_TURN_D, getMoveUpAxis(camera));
  }

  public void yawMoveToRight() {
    JoglCamera.CameraData camera = jogl.getCurrentCamera();
    rotateMoveToOnAxis(UNIT_TURN_D, getMoveUpAxis(camera));
  }

  /////////////////////////////////////
  // LookAt rotations

  public void pitchLookAtDown() {
    JoglCamera.CameraData camera = jogl.getCurrentCamera();
    rotateLookAtOnAxis(-UNIT_TURN_D, getLookRightAxis(camera));
  }

  public void pitchLookAtUp() {
    JoglCamera.CameraData camera = jogl.getCurrentCamera();
    rotateLookAtOnAxis(UNIT_TURN_D, getLookRightAxis(camera));
  }

  public void rollLookAtLeft() {
    JoglCamera.CameraData camera = jogl.getCurrentCamera();
    rotateLookAtOnAxis(-UNIT_TURN_D, getLookForwardAxis(camera));
  }

  public void rollLookAtRight() {
    JoglCamera.CameraData camera = jogl.getCurrentCamera();
    rotateLookAtOnAxis(UNIT_TURN_D, getLookForwardAxis(camera));
  }

  public void yawLookAtLeft() {
    JoglCamera.CameraData camera = jogl.getCurrentCamera();
    rotateLookAtOnAxis(-UNIT_TURN_D, getLookUpAxis(camera));
  }

  public void yawLookAtRight() {
    JoglCamera.CameraData camera = jogl.getCurrentCamera();
    rotateLookAtOnAxis(UNIT_TURN_D, getLookUpAxis(camera));
  }

  public void lookAtMoveTo() {
    cameraControl.lookAtMoveTo();
  }

  /////////////////////////////////////
  // MoveTo movements

  public void moveLeft() { // nose down
    JoglCamera.CameraData camera = jogl.getCurrentCamera();
    moveMoveToOnAxis(-UNIT_MOVE_D, getMoveRightAxis(camera));
  }

  public void moveRight() {
    JoglCamera.CameraData camera = jogl.getCurrentCamera();
    moveMoveToOnAxis(UNIT_MOVE_D, getMoveRightAxis(camera));
  }

  public void moveForward() {
    JoglCamera.CameraData camera = jogl.getCurrentCamera();
    moveMoveToOnAxis(UNIT_MOVE_D, getMoveForwardAxis(camera));
  }

  public void moveReverse() {
    JoglCamera.CameraData camera = jogl.getCurrentCamera();
    moveMoveToOnAxis(-UNIT_MOVE_D, getMoveForwardAxis(camera));
  }

  public void moveUp() {
    JoglCamera.CameraData camera = jogl.getCurrentCamera();
    moveMoveToOnAxis(-UNIT_MOVE_D, getMoveUpAxis(camera));
  }

  public void moveDown() {
    JoglCamera.CameraData camera = jogl.getCurrentCamera();
    moveMoveToOnAxis(UNIT_MOVE_D, getMoveUpAxis(camera));
  }

  public void dolly(double dollyX, double dollyY, double dollyZ) {
    JoglCamera.CameraData camera = jogl.getCurrentCamera();
    float[] moveFwdAxis = getMoveForwardAxis(camera);
    float[] moveUpAxis = getMoveUpAxis(camera);
    float[] moveRightAxis = JoglTransforms.crossV3(moveFwdAxis, moveUpAxis);

    float[] moveFwd = JoglTransforms.scaleV3(moveFwdAxis, (float) dollyZ);
    float[] moveRight = JoglTransforms.scaleV3(moveRightAxis, (float) dollyX);
    float[] moveUp = JoglTransforms.scaleV3(moveUpAxis, (float) dollyY);

    float[] dollyTo = JoglTransforms.addV3(moveRight, moveUp);
    dollyTo = JoglTransforms.addV3(dollyTo, moveFwd);

    LOG.debug("dolly {} on ({}, {}, {})",
        dollyTo[0], dollyTo[1], dollyTo[2]);
    cameraControl.dolly(dollyTo[0], dollyTo[1], dollyTo[2]);
  }

  /////////////////////////////////////
  // Throttle controls

  public void increaseThrottle() {
    throttle += THROTTLE_STEP;
  }

  public void decreaseThrottle() {
    throttle -= THROTTLE_STEP;
  }

  public void cutThrottle() {
    throttle = 0.0d;
  }

  /////////////////////////
  // Helpers

  private void rotateMoveToOnAxis(double angle, float[] axis) {
    LOG.debug("rotateMoveToOnAxis {} on ({}, {}, {})",
        angle, axis[0], axis[1], axis[2]);
    cameraControl.rotateMoveTo(angle, axis[0], axis[1], axis[2]);
  }

  private void rotateLookAtOnAxis(double angle, float[] axis) {
    LOG.debug("rotateLookAtOnAxis {} on ({}, {}, {})",
        angle, axis[0], axis[1], axis[2]);
    cameraControl.rotateLookAt(angle, axis[0], axis[1], axis[2]);
  }

  private void moveMoveToOnAxis(double dist, float[] axis) {
    LOG.debug("moveMoveToOnAxis {} on ({}, {}, {})",
        dist, axis[0], axis[1], axis[2]);
    float[] moveToV3 = JoglTransforms.scaleV3(axis, (float) dist);
    cameraControl.dolly(moveToV3[0], moveToV3[1], moveToV3[2]);
  }

  private float[] getMoveForwardAxis(JoglCamera.CameraData camera) {
    return JoglTransforms.cameraMoveDirectionV3(camera);
  }

  private float[] getMoveRightAxis(JoglCamera.CameraData camera) {
    float[] forward = getMoveForwardAxis(camera);
    float[] up = getMoveUpAxis(camera);
    return JoglTransforms.normalizeV3(JoglTransforms.crossV3(forward, up));
  }

  private float[] getMoveUpAxis(JoglCamera.CameraData camera) {
    return camera.captureMoveUp();
  }

  private float[] getLookForwardAxis(JoglCamera.CameraData camera) {
    return JoglTransforms.cameraLookDirectionV3(camera);
  }

  private float[] getLookRightAxis(JoglCamera.CameraData camera) {
    float[] forward = getLookForwardAxis(camera);
    float[] up = getLookUpAxis(camera);
    return JoglTransforms.normalizeV3(JoglTransforms.crossV3(forward, up));
  }

  private float[] getLookUpAxis(JoglCamera.CameraData camera) {
    return camera.captureLookUp();
  }
}
