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
import com.pnambic.depanfx.jogl.JoglCamera;
import com.pnambic.depanfx.jogl.JoglKeyListener.SymbolAction;
import javafx.animation.AnimationTimer;

/**
 * Provide basic flight style controls over the camera.
 */
public class FlightController {

  public static final double UNIT_TURN_D = 2.0d; // degrees per key press

  public static final double THROTTLE_STEP = 1.0d; // units per second

  private final JoglModule jogl;

  private final CameraControl cameraControl;

  private double throttle = 0.0d;

  private AnimationTimer flightTimer;

  public FlightController(JoglModule jogl, CameraControl cameraControl) {
    this.jogl = jogl;
    this.cameraControl = cameraControl;
  }

  public static void addActions(JoglModule jogl, FlightController flight) {
    jogl.addPressAction(new SymbolAction(KS_W, EMPTY_MASK,
        (s, m) -> flight.pitchDown()));
    jogl.addPressAction(new SymbolAction(KS_S, EMPTY_MASK,
        (s, m) -> flight.pitchUp()));
    jogl.addPressAction(new SymbolAction(KS_A, EMPTY_MASK,
        (s, m) -> flight.rollLeft()));
    jogl.addPressAction(new SymbolAction(KS_D, EMPTY_MASK,
        (s, m) -> flight.rollRight()));
    jogl.addPressAction(new SymbolAction(KS_Q, EMPTY_MASK,
        (s, m) -> flight.yawLeft()));
    jogl.addPressAction(new SymbolAction(KS_E, EMPTY_MASK,
        (s, m) -> flight.yawRight()));
    jogl.addPressAction(new SymbolAction(KS_R, EMPTY_MASK,
        (s, m) -> flight.increaseThrottle()));
    jogl.addPressAction(new SymbolAction(KS_F, EMPTY_MASK,
        (s, m) -> flight.decreaseThrottle()));
    jogl.addPressAction(new SymbolAction(KS_X, EMPTY_MASK,
        (s, m) -> flight.cutThrottle()));
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

  /////////////////
  // Flight actions

  public void pitchDown() { // nose down
    rotateOnAxis(-UNIT_TURN_D, getRightAxis());
  }

  public void pitchUp() {
    rotateOnAxis(UNIT_TURN_D, getRightAxis());
  }

  public void rollLeft() {
    rotateOnAxis(-UNIT_TURN_D, getForwardAxis());
  }

  public void rollRight() {
    rotateOnAxis(UNIT_TURN_D, getForwardAxis());
  }

  public void yawLeft() {
    rotateOnAxis(-UNIT_TURN_D, new float[] {0f, 1f, 0f});
  }

  public void yawRight() {
    rotateOnAxis(UNIT_TURN_D, new float[] {0f, 1f, 0f});
  }

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

  private void rotateOnAxis(double angle, float[] axis) {
    cameraControl.rotate(angle, axis[0], axis[1], axis[2]);
  }

  private float[] getForwardAxis() {
    JoglCamera.CameraData data = jogl.getCurrentCamera();
    return JoglTransforms.directionV3(data);
  }

  private float[] getRightAxis() {
    float[] forward = getForwardAxis();
    float[] up = new float[] {0f, 1f, 0f};
    return normalize(cross(forward, up));
  }

  private static float[] cross(float[] a, float[] b) {
    return new float[] {
        a[1] * b[2] - a[2] * b[1],
        a[2] * b[0] - a[0] * b[2],
        a[0] * b[1] - a[1] * b[0]
    };
  }

  private static float[] normalize(float[] v) {
    float len = (float) Math.sqrt(v[0]*v[0] + v[1]*v[1] + v[2]*v[2]);
    return new float[] { v[0]/len, v[1]/len, v[2]/len };
  }
}
