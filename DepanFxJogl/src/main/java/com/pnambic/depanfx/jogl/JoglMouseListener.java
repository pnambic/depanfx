/*
 * Copyright 2008, 2024 The Depan Project Authors
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
 */
package com.pnambic.depanfx.jogl;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.opengl.GLWindow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

public class JoglMouseListener implements MouseListener {

  // The mouse button setup for a Microsoft Sculpt Ergonomic Mouse.
  // Many alternatives are effectively compatible.
  public static final int PRIMARY_BUTTON = 1;

  public static final int WHEEL_BUTTON = 2;

  public static final int CONTEXT_BUTTON = 3;

  private static final Logger LOG =
      LoggerFactory.getLogger(JoglMouseListener.class);

  private GLWindow glWindow;

  private final JoglRenderer renderer;

  /**
   * Mouse position at last mouseDown event.
   * The basis for drag and selection rectangles.
   */
  int anchorX = -1;

  int anchorY = -1;

  /**
   * Mouse position at last actionable mouse event.
   * The basis for incremental move actions.
   */
  int priorX = -1;

  int priorY = -1;

  private enum State {
    None, Moving, MovingObject, RectangleSelection
  }

  private State state = State.None;

  private JoglMouseActionListener actionListener;

  public JoglMouseListener(JoglRenderer renderer) {
    this.renderer = renderer;
  }

  public void setWindow(GLWindow glWindow) {
    this.glWindow = glWindow;
  }

  public void addMouseActionListener(JoglMouseActionListener actionListener) {
    // TODO: allow multiple action listeners?  When the need arises.
    this.actionListener = actionListener;
  }

  @Override
  public void mouseClicked(MouseEvent event) {
    LOG.debug("mouse clicked {} type {}",
        event.getClass().getName(), event.getEventType());
  }

  @Override
  public void mouseEntered(MouseEvent event) {
    LOG.debug("mouse entered event {} type {}",
        event.getClass().getName(), event.getEventType());
  }

  @Override
  public void mouseExited(MouseEvent event) {
    LOG.debug("mouse exited {} type {}",
        event.getClass().getName(), event.getEventType());
  }

  @Override
  public void mouseMoved(MouseEvent event) {
    LOG.debug("mouse moved to x:{}, y:{}", event.getX(), event.getY());
  }

  @Override
  public void mouseDragged(MouseEvent event) {
    LOG.debug("mouse dragged to x:{}, y:{} state {}",
        event.getX(), event.getY(), state);

    int deltaX = priorX - event.getX();
    int deltaY = priorY - event.getY();
    double scaleX = renderer.scaleMouseX(deltaX);
    double scaleY = renderer.scaleMouseY(deltaY);

    if (event.getButton() == PRIMARY_BUTTON) { // button1 pressed
      switch (state) {
      case Moving:
        actionListener.mouseDolly(scaleX, -scaleY, 0);

        priorX = event.getX();
        priorY = event.getY();
        return;
      case MovingObject:
        actionListener.moveSelection(-scaleX, scaleY, 0);

        priorX = event.getX();
        priorY = event.getY();
        return;
      case RectangleSelection:
        int viewportHeight = renderer.getViewportHeight();

        renderer.activateSelectionRectangle(
            renderer.scaleMouseX(anchorX),
            renderer.scaleMouseY(viewportHeight - anchorY),
            renderer.scaleMouseX(event.getX()),
            renderer.scaleMouseY(viewportHeight - event.getY()));
        return;
      default:
      }
    }
    if (event.getButton() == CONTEXT_BUTTON) {
      switch (state) {
      case Moving:
        actionListener.rotateCamera(-deltaY / 10f, 0.0f, deltaX / 10f);
        return;
      default:
        // Explicitly ignore other state
        return;
      }
    }
  }

  @Override
  public void mousePressed(MouseEvent event) {
    LOG.debug("mouse pressed button {}", event.getButton());

    anchorX = priorX = event.getX();
    anchorY = priorY = event.getY();

    Collection<Object> hits = getMouseHits(anchorX, anchorY);
    boolean hasHits = !hits.isEmpty();

    // The user clicked on an object without control or shift
    // Entry move mode, and make the picked node the select node if it
    // is not part of the current selection.
    if (hasHits && !event.isControlDown() && !event.isShiftDown()) {
      actionListener.reviseSelection(hits);
      state = State.MovingObject;
    }

    // Start a rectangle selection
    else if (hasHits && event.isControlDown()) {
      state = State.RectangleSelection;
    }

    // Clicked with shift: start selection area
    else if (event.isShiftDown()) {
      state = State.RectangleSelection;

    // Clicked on the background: start moving camera
    } else {
      state = State.Moving;
    }
    LOG.debug("mouse state is {}", state);
  }

  @Override
  public void mouseReleased(MouseEvent event) {
    LOG.debug("mouse release button {} state {}", event.getButton(), state);

    int eventX = event.getX();
    int eventY = event.getY();

    if (event.getButton() == PRIMARY_BUTTON) {
      renderer.releaseSelectionRectangle();
      if (anchorX == eventX && anchorY == eventY
          && state == State.MovingObject) {
        // instead of moving the node, the mouse stayed at the same place.
        // we replace the selection.
        Collection<Object> hits = getMouseHits(anchorX, anchorY);
        actionListener.setSelection(hits);
      }
      else if (anchorX == eventX && anchorY == eventY
          && state == State.RectangleSelection) {
        // a rectangle when the mouse hasn't moved... select nothing
        if (event.isControlDown()) {
          Collection<Object> hits = getMouseHits(anchorX, anchorY);
          actionListener.extendSelection(hits);
        }
        else if (event.isAltDown()) {
          Collection<Object> hits = getMouseHits(anchorX, anchorY);
          actionListener.reduceSelection(hits);
        }
        else {
          actionListener.setSelection(Collections.emptyList());
        }
      }
      else if (state == State.RectangleSelection) {
        Collection<Object> hits = getRectangleHits(anchorX, anchorY, eventX, eventY);
        if (event.isControlDown()) {
          actionListener.extendSelection(hits);
        }
        else if (event.isAltDown()) {
          actionListener.reduceSelection(hits);
        }
        else
          actionListener.setSelection(hits);
      }
    }
    state = State.None;
  }

  @Override
  public void mouseWheelMoved(MouseEvent event) {
    LOG.debug("mouse wheel {}", event.getEventType());

    // A wheel is a y-axis rotation, so use the second element in the array.
    float amount = event.getRotation()[1] * event.getRotationScale();
    actionListener.mouseDolly(0, 0, amount);
  }

  /////////////////////////////////////
  // Hit tests

  private Collection<Object> getMouseHits(float mouseX, float mouseY) {
    int viewportHeight = renderer.getViewportHeight();
    return renderer.getHits(
        glWindow, mouseX, viewportHeight - mouseY,
        1.0f, 1.0f);
  }

  private Collection<Object> getRectangleHits(
      float anchorX, float anchorY, float eventX, float eventY) {
    int viewportHeight = renderer.getViewportHeight();
    float selectX = (float) renderer.scaleMouseX(anchorX);
    float selectY = (float) renderer.scaleMouseY(viewportHeight - anchorY);
    float width = (float) renderer.scaleMouseX(eventX) - selectX;
    float height = (float) renderer.scaleMouseX(viewportHeight - eventY) - selectY;

    // Prevent negative width
    if (anchorX < eventX) {
      selectX = anchorX;
      width = eventX - anchorX;
    }
    else {
      selectX = eventX;
      width = anchorX - eventX;
    }

    // Prevent negative height
    if (anchorY > eventY) {
      selectY = viewportHeight - anchorY;
      height = anchorY - eventY;
    }
    else {
      selectY = viewportHeight - eventY;
      height = eventY - anchorY;
    }

    return renderer.getHits(glWindow, selectX, selectY, width, height);
  }
}
